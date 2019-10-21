package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Sent to client as response to successfully joining a GameRoom as Observer. */
@XStreamAlias(value = "observed")
data class ObservationProtocolMessage(
        @XStreamAsAttribute
        val roomId: String
): ProtocolMessage