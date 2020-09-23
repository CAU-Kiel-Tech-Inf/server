package sc.plugin2021.util

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.converters.Converter
import sc.protocol.helpers.LobbyProtocol
import sc.plugin2021.*
import sc.plugin2021.xstream.BoardConverter

object Configuration {
    @JvmStatic
    var xStream: XStream
        private set

    @JvmStatic
    val classesToRegister: List<Class<*>>
        get() = listOf(Board::class.java, Coordinates::class.java,
                Field::class.java, GameState::class.java,
                Move::class.java, Piece::class.java,
                Color::class.java, Team::class.java)

    init {
        xStream = XStream()
        xStream.setMode(XStream.NO_REFERENCES)
        xStream.classLoader = Configuration::class.java.classLoader
        LobbyProtocol.registerMessages(xStream)
        LobbyProtocol.registerAdditionalMessages(xStream, classesToRegister)

        xStream.registerConverter(BoardConverter())
    }
}
