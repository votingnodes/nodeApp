package co.empiresec.nodeapp.bitcoin

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

enum class DaemonState {
    NOT_INSTALLED,
    INSTALLED,
    STARTING,
    RUNNING,
    STOPPING,
    ERROR
}

data class NodeStatus(
    val state: DaemonState,
    val blockCount: Long = 0,
    val connections: Int = 0,
    val verificationProgress: Double = 0.0,
    val errorMessage: String? = null,
    val version: String? = null,
    val isTestnet: Boolean = false,
    val isPruned: Boolean = false,
    val diskUsage: Long = 0
)

class BitcoinCoreManager(
    private val packageName: String = "core"
) {
    private val _status = MutableStateFlow(NodeStatus(DaemonState.NOT_INSTALLED))
    val status: StateFlow<NodeStatus> = _status.asStateFlow()
    
    private var rpcClient: BitcoinRPCClient? = null
    private var daemonProcess: Process? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    val packageInfo: BitcoinPackage = Packages.getPackage(packageName)
    
    fun isInstalled(): Boolean {
        return Utils.isDaemonInstalled()
    }
    
    suspend fun install(
        dataDir: File,
        onProgress: (DownloadProgress) -> Unit = {}
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val arch = Utils.getArch()
            val url = Packages.getPackageUrl(packageName, arch)
            val fileName = url.substring(url.lastIndexOf("/") + 1)
            val downloadPath = File(dataDir, fileName)
            
            Utils.downloadFile(url, downloadPath.path) { rate, total ->
                onProgress(DownloadProgress(rate, total))
            }
            
            val hash = packageInfo.sha256Hashes[arch]
            if (hash != null && !validateSha256(packageName, downloadPath.path, hash, arch)) {
                downloadPath.delete()
                return@withContext Result.failure(Exception("SHA256 validation failed"))
            }
            
            Utils.extractTarXz(downloadPath, dataDir)
            
            _status.value = _status.value.copy(state = DaemonState.INSTALLED)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    @Suppress("unused")
    suspend fun start(
        config: BitcoinConf = BitcoinConf.default()
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _status.value = _status.value.copy(state = DaemonState.STARTING)
            
            val bitcoinConfFile = File(Utils.getDataDir(), "bitcoin.conf")
            writeBitcoinConf(bitcoinConfFile, config)
            
            val daemonBinary = getDaemonBinary(config)
            if (!daemonBinary.exists()) {
                return@withContext Result.failure(Exception("Daemon binary not found"))
            }
            
            val processBuilder = ProcessBuilder(
                daemonBinary.absolutePath,
                "-conf=${bitcoinConfFile.absolutePath}",
                "-datadir=${Utils.getDataDir()}"
            )
            
            daemonProcess = processBuilder.start()
            
            awaitStart()
            
            _status.value = _status.value.copy(state = DaemonState.RUNNING)
            Result.success(Unit)
        } catch (e: Exception) {
            _status.value = _status.value.copy(
                state = DaemonState.ERROR,
                errorMessage = e.message
            )
            Result.failure(e)
        }
    }
    
    @Suppress("unused")
    suspend fun stop(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _status.value = _status.value.copy(state = DaemonState.STOPPING)
            
            rpcClient?.stop()
            
            daemonProcess?.destroy()
            daemonProcess?.waitFor()
            daemonProcess = null
            
            _status.value = _status.value.copy(state = DaemonState.INSTALLED)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getStatus(): NodeStatus = withContext(Dispatchers.IO) {
        if (daemonProcess == null || daemonProcess?.isAlive != true) {
            return@withContext _status.value.copy(
                state = if (isInstalled()) DaemonState.INSTALLED else DaemonState.NOT_INSTALLED
            )
        }
        
        try {
            val client = getOrCreateRPCClient()
            val blockCount = client.getBlockCount()
            val info = client.getBlockChainInfo()
            
            _status.value.copy(
                state = DaemonState.RUNNING,
                blockCount = blockCount,
                verificationProgress = info.verificationProgress,
                isTestnet = info.chain == "test",
                isPruned = info.prune,
                diskUsage = info.sizeOnDisk
            )
        } catch (e: Exception) {
            _status.value.copy(
                state = DaemonState.ERROR,
                errorMessage = e.message
            )
        }
    }
    
    private suspend fun awaitStart(timeoutMs: Long = 30000) = withTimeout(timeoutMs) {
        var attempts = 0
        while (attempts < 30) {
            try {
                val client = getOrCreateRPCClient()
                client.getBlockCount()
                return@withTimeout
            } catch (e: Exception) {
                delay(1000)
                attempts++
            }
        }
        throw Exception("Timeout waiting for daemon to start")
    }
    
    private fun getOrCreateRPCClient(): BitcoinRPCClient {
        if (rpcClient == null) {
            rpcClient = BitcoinRPCClient(BitcoinConf.default())
        }
        return rpcClient!!
    }
    
    private fun getDaemonBinary(config: BitcoinConf): File {
        val binaryName = when (packageName) {
            "liquid" -> "liquidd"
            else -> "bitcoind"
        }
        return File(Utils.getDataDir(), binaryName)
    }
    
    private fun writeBitcoinConf(confFile: File, config: BitcoinConf) {
        confFile.parentFile?.mkdirs()
        confFile.writeText(buildString {
            appendLine("rpcuser=${config.rpcUser}")
            appendLine("rpcpassword=${config.rpcPassword}")
            appendLine("rpcallowip=127.0.0.1")
            appendLine("rpcbind=127.0.0.1")
            appendLine("port=8333")
            appendLine("listen=1")
            appendLine("server=1")
            appendLine("daemon=0")
            appendLine("txindex=1")
            appendLine("discover=1")
            appendLine("addrlisten=1")
            
            if (config.testnet) {
                appendLine("testnet=1")
                appendLine("port=18333")
                appendLine("rpcport=18332")
            }
            
            config.prune?.let {
                appendLine("prune=$it")
            }
            
            if (config.dataDir.isNotEmpty()) {
                appendLine("datadir=${config.dataDir}")
            }
        })
    }
    
    fun cleanup() {
        scope.cancel()
        daemonProcess?.destroy()
    }
}