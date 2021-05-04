package sc.server.network

import io.kotest.assertions.timing.eventually
import io.kotest.assertions.until.Interval
import io.kotest.assertions.until.fibonacci
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import sc.framework.plugins.protocol.MoveRequest
import sc.networking.clients.AdminClient
import sc.protocol.ProtocolPacket
import sc.protocol.requests.JoinPreparedRoomRequest
import sc.protocol.responses.ErrorMessage
import sc.protocol.responses.ErrorPacket
import sc.protocol.responses.GamePreparedResponse
import sc.protocol.responses.ObservationResponse
import sc.server.client.PlayerListener
import sc.server.gaming.GameRoom
import sc.server.plugins.TestGame
import sc.server.plugins.TestMove
import sc.server.plugins.TestPlugin
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.seconds

@ExperimentalTime
suspend fun await(clue: String? = null, duration: Duration = 1.seconds, interval: Interval = 20.milliseconds.fibonacci(), f: suspend () -> Unit) =
        withClue(clue) { eventually(duration, interval, f) }

@ExperimentalTime
class LobbyRequestTest: WordSpec({
    "A Lobby with connected clients" When {
        val lobby = autoClose(TestLobby())
    
        val messages = ArrayDeque<ProtocolPacket>()
        suspend fun <T : ProtocolPacket> awaitMessage(clue: String? = null, clazz: KClass<T>): T {
            await(clue) { messages.shouldNotBeEmpty() }
            val msg = messages.removeFirst()
            msg should beInstanceOf(clazz)
            return clazz.cast(msg)
        }
        
        val admin = AdminClient("localhost", lobby.serverPort, PASSWORD, messages::add)
        val players = Array(2) { lobby.connectClient() }
        await("Clients connected") { lobby.clientManager.clients.size shouldBe 3 }
        "a player joined" should {
            players[0].joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
            "create a room for it" {
                await("Room opened") { lobby.games.size shouldBe 1 }
                lobby.games.single().clients shouldHaveSize 1
            }
        }
        "a game is prepared paused" should {
            admin.prepareGame(TestPlugin.TEST_PLUGIN_UUID, true)
            val prepared = awaitMessage("GameRoom prepared", GamePreparedResponse::class)
            lobby.games shouldHaveSize 1
            
            val roomId = prepared.roomId
            val room = lobby.findRoom(roomId)
            withClue("GameRoom is empty and paused") {
                room.clients.shouldBeEmpty()
                room.isPauseRequested shouldBe true
            }
            
            val observer = admin.observeAndControl(roomId, true)
            awaitMessage("Game observed", ObservationResponse::class)
            
            val reservations = prepared.reservations
            players[0].joinPreparedGame(reservations[0])
            await("First player joined") { room.clients shouldHaveSize 1 }
            "not accept a reservation twice" {
                admin.send(JoinPreparedRoomRequest(reservations[0]))
                awaitMessage("Receive error when reusing a reservation", ErrorPacket::class)
                room.clients shouldHaveSize 1
                lobby.games shouldHaveSize 1
            }
            players[1].joinPreparedGame(reservations[1])
            await("Players join, Game start") { room.status shouldBe GameRoom.GameStatus.ACTIVE }
            
            val playerListeners = room.slots.map { slot ->
                PlayerListener().also { listener -> slot.role.player.addPlayerListener(listener) }
            }
            "terminate when a Move is received while still paused" {
                players[0].sendMessageToRoom(roomId, TestMove(0))
                await("Terminates") { room.status shouldBe GameRoom.GameStatus.OVER }
            }
            "play game on unpause" {
                observer.unpause()
                await { room.isPauseRequested shouldBe false }
                val game = room.game as TestGame
                game.isPaused shouldBe false
                withClue("Processes moves") {
                    playerListeners[0].waitForMessage(MoveRequest::class)
                    players[0].sendMessageToRoom(roomId, TestMove(32))
                    await { game.currentState.state shouldBe 32 }
                    playerListeners[1].waitForMessage(MoveRequest::class)
                    players[1].sendMessageToRoom(roomId, TestMove(54))
                    await { game.currentState.state shouldBe 54 }
                }
                players[1].sendMessageToRoom(roomId, TestMove(0))
                await("Terminate after wrong player sent a turn") {
                    room.status shouldBe GameRoom.GameStatus.OVER
                }
            }
        }
    }
})
