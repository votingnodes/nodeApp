package co.empiresec.nodeapp.bitcoin

import android.content.Context
import android.os.Build
import android.util.Log
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import org.apache.commons.compress.utils.IOUtils
import java.io.*
import java.security.MessageDigest
import java.util.Properties

private const val TAG = "AndroidUtils"

private var bitcoinContext: Context? = null

fun initBitcoinUtils(ctx: Context) {
    bitcoinContext = ctx.applicationContext
}

actual object Utils {
    actual fun getDataDir(): String {
        return bitcoinContext?.noBackupFilesDir?.absolutePath ?: throw IllegalStateException("Context not initialized")
    }
    
    actual fun getArch(): String {
        for (abi in Build.SUPPORTED_ABIS) {
            return when (abi) {
                "armeabi-v7a" -> "arm-linux-androideabi"
                "arm64-v8a" -> "aarch64-linux-android"
                "x86" -> "i686-linux-android"
                "x86_64" -> "x86_64-linux-android"
                else -> continue
            }
        }
        throw UnsupportedOperationException("Unsupported ABI")
    }
    
    actual fun extractTarXz(input: File, outputDir: File) {
        var tarStream: TarArchiveInputStream? = null
        try {
            val xzStream = XZCompressorInputStream(BufferedInputStream(FileInputStream(input)))
            tarStream = TarArchiveInputStream(BufferedInputStream(xzStream))
            
            var entry: ArchiveEntry? = tarStream.nextEntry
            while (entry != null) {
                val name = entry.name
                Log.v(TAG, "Extracting $name")
                
                val f = File(outputDir, name)
                f.parentFile?.mkdirs()
                
                var out: OutputStream? = null
                try {
                    out = FileOutputStream(f)
                    IOUtils.copy(tarStream, out)
                } finally {
                    IOUtils.closeQuietly(out)
                }
                
                val mode = (entry as TarArchiveEntry).mode
                f.setExecutable(true, (mode and 1) == 0)
                
                entry = tarStream.nextEntry
            }
        } finally {
            IOUtils.closeQuietly(tarStream)
        }
        input.delete()
    }
    
    actual fun sha256Hex(filePath: String): String {
        val fis = BufferedInputStream(FileInputStream(filePath))
        val md = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(1024)
        var nread: Int
        
        while (fis.read(buffer).also { nread = it } != -1) {
            md.update(buffer, 0, nread)
        }
        val mdbytes = md.digest()
        
        val sb = StringBuilder()
        for (b in mdbytes) {
            sb.append(Integer.toString((b.toInt() and 0xff) + 0x100, 16).substring(1))
        }
        
        return sb.toString()
    }
    
    actual fun validateSha256(packageName: String, filePath: String): Boolean {
        return true
    }
    
    actual fun isDaemonInstalled(): Boolean {
        val ctx = bitcoinContext ?: return false
        val dataDir = ctx.noBackupFilesDir ?: return false
        val daemon = "bitcoind"
        val tor = "tor"
        return File(dataDir, daemon).exists() && File(dataDir, tor).exists()
    }
    
    actual fun runDaemon(config: BitcoinConf): Boolean {
        return false
    }
    
    actual fun stopDaemon(): Boolean {
        return false
    }
}

actual class BitcoinConf {
    actual var rpcUser: String = ""
    actual var rpcPassword: String = ""
    actual var rpcHost: String = ""
    actual var rpcPort: Int = 8332
    actual var dataDir: String = ""
    actual var testnet: Boolean = false
    actual var prune: Int? = null

    actual companion object {
        actual fun load(path: String): BitcoinConf? {
            return try {
                val props = Properties()
                val file = File(path)
                if (!file.exists()) return null
                
                props.load(BufferedInputStream(FileInputStream(file)))
                
                BitcoinConf().apply {
                    rpcUser = props.getProperty("rpcuser", "")
                    rpcPassword = props.getProperty("rpcpassword", "")
                    rpcHost = props.getProperty("rpcconnect", "127.0.0.1")
                    rpcPort = props.getProperty("rpcport", "8332").toIntOrNull() ?: 8332
                    dataDir = props.getProperty("datadir", Utils.getDataDir())
                    testnet = props.getProperty("testnet", "0") == "1"
                    prune = props.getProperty("prune")?.toIntOrNull()
                }
            } catch (e: Exception) {
                null
            }
        }
        
        actual fun default(): BitcoinConf {
            return BitcoinConf().apply {
                rpcUser = "bitcoinrpc"
                rpcPassword = generatePassword()
                rpcHost = "127.0.0.1"
                rpcPort = 8332
                dataDir = Utils.getDataDir()
                testnet = false
                prune = null
            }
        }
    }
}

private fun generatePassword(): String {
    val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return (1..32).map { chars.random() }.joinToString("")
}