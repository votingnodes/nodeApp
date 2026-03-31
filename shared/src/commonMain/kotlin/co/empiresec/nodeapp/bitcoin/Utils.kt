package co.empiresec.nodeapp.bitcoin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.security.MessageDigest

expect object Utils {
    fun getDataDir(): String
    
    fun getArch(): String
    
    fun extractTarXz(input: File, outputDir: File)
    
    fun sha256Hex(filePath: String): String
    
    fun validateSha256(packageName: String, filePath: String): Boolean
    
    fun isDaemonInstalled(): Boolean
    
    fun runDaemon(config: BitcoinConf): Boolean
    
    fun stopDaemon(): Boolean
}

expect class BitcoinConf(
    val rpcUser: String,
    val rpcPassword: String,
    val rpcHost: String,
    val rpcPort: Int,
    val dataDir: String,
    val testnet: Boolean,
    val prune: Int?
) {
    companion object {
        fun load(path: String): BitcoinConf?
        fun default(): BitcoinConf
    }
}

class DownloadProgress(
    val bytesPerSecond: Int,
    val totalBytesDownloaded: Long
)

suspend fun downloadFile(url: String, filePath: String, onUpdate: ((Int, Long) -> Unit)? = null): Unit = withContext(Dispatchers.IO) {
    val fos = FileOutputStream(filePath)
    val startTime = System.currentTimeMillis()
    
    val urlConnection = URL(url).openConnection()
    urlConnection.connect()
    
    val inputStream: InputStream = urlConnection.getInputStream()
    val buffer = ByteArray(1024)
    var bytesRead: Int
    var totalBytesDownloaded = 0L
    var lastUpdate = 0L
    
    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
        fos.write(buffer, 0, bytesRead)
        totalBytesDownloaded += bytesRead
        
        if (onUpdate != null) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastUpdate > 200) {
                val ms = currentTime - startTime
                val rate = (totalBytesDownloaded / (ms / 1000.0)).toInt()
                onUpdate(rate, totalBytesDownloaded)
                lastUpdate = currentTime
            }
        }
    }
    
    fos.close()
    inputStream.close()
}

fun sha256Hex(filePath: String): String {
    val fis = BufferedInputStream(FileInputStream(filePath))
    val md = MessageDigest.getInstance("SHA-256")
    val buffer = ByteArray(1024)
    var bytesRead: Int
    
    while (fis.read(buffer).also { bytesRead = it } != -1) {
        md.update(buffer, 0, bytesRead)
    }
    
    val digest = md.digest()
    val sb = StringBuilder()
    for (b in digest) {
        sb.append(Integer.toString((b.toInt() and 0xff) + 0x100, 16).substring(1))
    }
    
    return sb.toString()
}

fun validateSha256(packageName: String, filePath: String, expectedHash: String, arch: String): Boolean {
    return try {
        val hash = sha256Hex(filePath)
        val expected = expectedHash.substring(expectedHash.indexOf(arch) + arch.length)
        hash == expected
    } catch (e: Exception) {
        false
    }
}