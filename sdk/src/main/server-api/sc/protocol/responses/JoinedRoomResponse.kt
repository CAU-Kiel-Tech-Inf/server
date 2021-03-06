package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.protocol.ResponsePacket

/** Response to client who successfully joined a game. */
@XStreamAlias(value = "joined")
data class JoinedRoomResponse(
        @XStreamAsAttribute
        val roomId: String
): ResponsePacket
