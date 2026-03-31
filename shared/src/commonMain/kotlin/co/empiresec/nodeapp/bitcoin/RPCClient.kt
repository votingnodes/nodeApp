package co.empiresec.nodeapp.bitcoin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class RPCResult<T>(
    val result: T?,
    val error: RPCError?,
    val id: Int
)

data class RPCError(
    val code: Int,
    val message: String
)

sealed class BitcoinRPCResponse {
    data class BoolResponse(val value: Boolean) : BitcoinRPCResponse()
    data class IntResponse(val value: Long) : BitcoinRPCResponse()
    data class StringResponse(val value: String) : BitcoinRPCResponse()
    data class ObjectResponse(val fields: Map<String, JsonElement>) : BitcoinRPCResponse()
    data class ArrayResponse(val items: List<JsonElement>) : BitcoinRPCResponse()
    data class Error(val error: RPCError) : BitcoinRPCResponse()
    data class NullResponse(val value: JsonNull?) : BitcoinRPCResponse()
}

class BitcoinRPCClient(private val config: BitcoinConf) {
    private var id = 0
    
    private fun getUrl(): String {
        val port = when {
            config.testnet -> 18332
            config.rpcPort > 0 -> config.rpcPort
            else -> 8332
        }
        return "http://${config.rpcHost}:$port/"
    }
    
    private fun auth(): String {
        val userPwd = "${URLEncoder.encode(config.rpcUser)}:${URLEncoder.encode(config.rpcPassword)}"
        return "Basic ${java.util.Base64.getEncoder().encodeToString(userPwd.toByteArray())}"
    }
    
    suspend fun <T> call(method: String, vararg params: Any): T = withContext(Dispatchers.IO) {
        id++
        val json = buildJsonObject {
            put("jsonrpc", "2.0")
            put("method", method)
            put("id", id)
            put("params", buildJsonArray {
                params.forEach { param ->
                    when (param) {
                        is String -> add(param)
                        is Number -> add(param)
                        is Boolean -> add(param)
                        else -> add(param.toString())
                    }
                }
            })
        }
        
        val url = URL(getUrl())
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Authorization", auth())
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        connection.outputStream.use { os ->
            os.write(json.toString().toByteArray())
        }
        
        val responseCode = connection.responseCode
        if (responseCode != 200) {
            throw RPCException(responseCode, "HTTP error: $responseCode")
        }
        
        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = reader.readText()
        reader.close()
        
        val jsonResponse = Json.parseToJsonElement(response)
        val result = jsonResponse.jsonObject["result"]
        val error = jsonResponse.jsonObject["error"]
        
        if (error != null && error !is JsonNull) {
            val errorCode = error.jsonObject["code"]?.jsonPrimitive?.int ?: 0
            val errorMsg = error.jsonObject["message"]?.jsonPrimitive?.content ?: "Unknown error"
            throw RPCException(-1, "RPC error: $errorCode - $errorMsg")
        }
        
        @Suppress("UNCHECKED_CAST")
        result as T
    }
    
    suspend fun getBlockCount(): Long = call("getblockcount")
    
    suspend fun getBlockChainInfo(): BlockChainInfo = call("getblockchaininfo")
    
    suspend fun getNetworkInfo(): NetworkInfo = call("getinfo")
    
    suspend fun getPeerInfo(): List<PeerInfo> = call("getpeerinfo")
    
    suspend fun getMempoolInfo(): MempoolInfo = call("getmempoolinfo")
    
    suspend fun stop(): Boolean = call("stop")
    
    suspend fun getMiningInfo(): MiningInfo = call("getmininginfo")
    
    suspend fun getWalletInfo(): WalletInfo? = try {
        call("getwalletinfo")
    } catch (e: RPCException) {
        null
    }
    
    suspend fun listWallets(): List<String> = call("listwallets")
    
    suspend fun getBalance(minimumConfirmations: Int = 0): Double = call("getbalance", minimumConfirmations)
    
    suspend fun getNewAddress(account: String = ""): String = call("getnewaddress", account)
    
    suspend fun getReceivedByAddress(address: String, minimumConfirmations: Int = 0): Double = call("getreceivedbyaddress", address, minimumConfirmations)
    
    suspend fun listUnspent(minimumConfirmations: Int = 0, maximumConfirmations: Int = 9999999): List<Unspent> = call("listunspent", minimumConfirmations, maximumConfirmations)
    
    suspend fun listTransactions(count: Int = 10, skip: Int = 0): List<Transaction> = call("listtransactions", "*", count, skip)
}

data class BlockChainInfo(
    val chain: String,
    val blocks: Long,
    val headers: Long,
    val bestBlockHash: String,
    val difficulty: Double,
    val verificationProgress: Double,
    val chainWork: String,
    val prune: Boolean,
    val pruneHeight: Long?,
    val sizeOnDisk: Long,
    val softForks: List<SoftFork>,
    val bip9SoftForks: Map<String, SoftForkInfo>
)

data class SoftFork(
    val id: String,
    val version: Int,
    val reject: SoftForkStatus
)

data class SoftForkStatus(
    val status: String,
    val found: Int?,
    val progress: Double?
)

data class SoftForkInfo(
    val status: String,
    val startTime: Long,
    val timeout: Long
)

data class NetworkInfo(
    val version: Int,
    val protocolVersion: Int,
    val timeOffset: Long,
    val connections: Int,
    val connectionsIn: Int,
    val connectionsOut: Int,
    val difficulty: Double,
    val testnet: Boolean,
    val relayFee: Double,
    val warnings: String
)

data class PeerInfo(
    val id: Long,
    val addr: String,
    val addrlocal: String?,
    val services: String,
    val lastSend: Long,
    val lastRecv: Long,
    val bytessent: Long,
    val bytesrecv: Long,
    val conntime: Long,
    val timeoffset: Long,
    val pingtime: Double?,
    val pingwait: Double?,
    val version: Long,
    val subver: String,
    val inbound: Boolean,
    val startingHeight: Long,
    val banScore: Int,
    val syncedHeaders: Long?,
    val syncedBlocks: Long?,
    val inflate: Boolean?
)

data class MempoolInfo(
    val size: Long,
    val bytes: Long,
    val usage: Long,
    val maxMempool: Long,
    val mempoolMinFee: Double,
    val mempoolMinRelayFee: Double,
    val unwasteful: Boolean?
)

data class MiningInfo(
    val blocks: Long,
    val currentBlockSize: Long,
    val currentBlockTx: Long,
    val difficulty: Double,
    val blockPriorityThreshold: Long,
    val generate: Boolean,
    val genProcLimit: Int?,
    val hashesPerSec: Int?,
    val networkHashPS: Double,
    val pooledTx: Long,
    val testnet: Boolean,
    val chain: String
)

data class WalletInfo(
    val walletName: String,
    val walletVersion: Int,
    val balance: WalletBalance,
    val txcount: Long,
    val keypoolOldest: Long,
    val keypoolSize: Long,
    val unlockedUntil: Long?,
    val paytxFee: Double
)

data class WalletBalance(
    val trusted: Double,
    val untrustedPending: Double,
    val immature: Double
)

data class Unspent(
    val txid: String,
    val vout: Long,
    val address: String?,
    val scriptPubKey: String,
    val amount: Double,
    val confirmations: Long,
    val spendable: Boolean
)

data class Transaction(
    val address: String?,
    val category: String,
    val amount: Double,
    val fee: Double?,
    val confirmations: Long,
    val blockhash: String?,
    val txid: String,
    val time: Long,
    val timereceived: Long,
    val comment: String?,
    val account: String?
)

class RPCException(val code: Int, message: String) : Exception(message)