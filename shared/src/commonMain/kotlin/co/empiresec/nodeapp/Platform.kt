package co.empiresec.nodeapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform