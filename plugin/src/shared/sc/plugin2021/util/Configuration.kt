package sc.plugin2021.util

import com.thoughtworks.xstream.XStream
import sc.protocol.helpers.LobbyProtocol
import sc.plugin2021.*

object Configuration {
    @JvmStatic
    var xStream: XStream
        private set

    @JvmStatic
    val classesToRegister: List<Class<*>>
        get() = listOf()

    init {
        xStream = XStream()
        xStream.setMode(XStream.NO_REFERENCES)
        xStream.classLoader = Configuration::class.java.classLoader
        LobbyProtocol.registerMessages(xStream)
        LobbyProtocol.registerAdditionalMessages(xStream, classesToRegister)
    }
}
