package co.empiresec.nodeapp.bitcoin

data class BitcoinPackage(
    val name: String,
    val version: String,
    val archSizes: Map<String, Long>,
    val sha256Hashes: Map<String, String>,
    val downloadUrlTemplate: String
)

object Packages {
    const val BITCOIN_CORE_VERSION = "0.19.0.1"
    const val BITCOIN_KNOTS_VERSION = "0.18.1"
    const val BITCOIN_LIQUID_VERSION = "0.18.1.3"

    private val coreArchSizes = mapOf(
        "aarch64-linux-android" to 5527036L,
        "arm-linux-androideabi" to 5226832L,
        "i686-linux-android" to 6176104L,
        "x86_64-linux-android" to 6363016L
    )

    private val coreSha256 = mapOf(
        "aarch64-linux-android" to "7f431841190c276b51318803f7e71e3012726fb7323d7abfebc926e466568575",
        "arm-linux-androideabi" to "81bd282a2c607f62f17a0fb640927ff2d78588b389285de981dda0a6ef2e56f8",
        "i686-linux-android" to "66553fc6a0b3077f70131eb0a246890901ed51e15642dd97be58eb7002490891",
        "x86_64-linux-android" to "3ce9d65b36e8e74ab29a22836149b64680cb749d9dbf7a89a5fb9f5c81de4e22"
    )

    private val knotsArchSizes = mapOf(
        "aarch64-linux-android" to 5368752L,
        "arm-linux-androideabi" to 5091092L,
        "i686-linux-android" to 6033768L,
        "x86_64-linux-android" to 6209312L
    )

    private val knotsSha256 = mapOf(
        "aarch64-linux-android" to "91a2414d3b697502924d13dcdb0fd586994c25b4008547aa012e37bce3b92a21",
        "arm-linux-androideabi" to "17d6a46fe9893a962a2ea4c307be56126b865840dee676452bc931f1d18bdc1b",
        "i686-linux-android" to "b61fbe07604931ea1ff541e6331535fd0d6f94332611abfba92ad3df9b516380",
        "x86_64-linux-android" to "3411ea5bc32d74529d45915a7e4d2376a37a075d56e34c439a1795f81e360b09"
    )

    private val liquidArchSizes = mapOf(
        "aarch64-linux-android" to 5790596L,
        "arm-linux-androideabi" to 5547968L,
        "i686-linux-android" to 6480784L,
        "x86_64-linux-android" to 6656944L
    )

    private val liquidSha256 = mapOf(
        "aarch64-linux-android" to "ed0038610bfd710074a96bab736adf56d4fba9d76132cdd956eaeeb532ebdc35",
        "arm-linux-androideabi" to "13e7f4270c51d7fe75809dd966a407e58ae0095f707b878423060b3a917592b0",
        "i686-linux-android" to "35ebf1aca32caff99d7346ce93775d9fea8f2bd4cf57edcc78f0ff71b087c10c",
        "x86_64-linux-android" to "947f94562018a45cbea9efcc391489fd54f578343eeb8c7dd5b48529d2b93c15"
    )

    val BITCOIN_CORE = BitcoinPackage(
        name = "core",
        version = BITCOIN_CORE_VERSION,
        archSizes = coreArchSizes,
        sha256Hashes = coreSha256,
        downloadUrlTemplate = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.19.0.1/%s_bitcoin%s.tar.xz"
    )

    val BITCOIN_KNOTS = BitcoinPackage(
        name = "knots",
        version = BITCOIN_KNOTS_VERSION,
        archSizes = knotsArchSizes,
        sha256Hashes = knotsSha256,
        downloadUrlTemplate = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.19.0.1/%s_bitcoin%s.tar.xz"
    )

    val BITCOIN_LIQUID = BitcoinPackage(
        name = "liquid",
        version = BITCOIN_LIQUID_VERSION,
        archSizes = liquidArchSizes,
        sha256Hashes = liquidSha256,
        downloadUrlTemplate = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.19.0.1/%s_%s.tar.xz"
    )

    fun getPackage(name: String): BitcoinPackage {
        return when (name) {
            "core" -> BITCOIN_CORE
            "knots" -> BITCOIN_KNOTS
            "liquid" -> BITCOIN_LIQUID
            else -> BITCOIN_CORE
        }
    }

    fun getPackageUrl(packageName: String, arch: String): String {
        val pkg = getPackage(packageName)
        val suffix = if (packageName == "liquid") "liquid" else ""
        return String.format(pkg.downloadUrlTemplate, arch, suffix)
    }

    fun getSupportedArchs(): List<String> = listOf(
        "aarch64-linux-android",
        "arm-linux-androideabi",
        "i686-linux-android",
        "x86_64-linux-android"
    )

    fun getArchFromAndroidAbis(abis: List<String>): String? {
        for (abi in abis) {
            return when (abi) {
                "arm64-v8a" -> "aarch64-linux-android"
                "armeabi-v7a" -> "arm-linux-androideabi"
                "x86" -> "i686-linux-android"
                "x86_64" -> "x86_64-linux-android"
                else -> continue
            }
        }
        return null
    }
}