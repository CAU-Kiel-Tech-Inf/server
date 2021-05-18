package sc.player

import sc.api.plugins.IMove
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.Player
import sc.shared.GameResult

/**
 * Das Interface der Logik.
 * Der GameHandler definiert Reaktionen auf Ereignisse vom Server,
 * insbesondere die Reaktion mit einem Zug auf eine Zuganfrage.
 */
interface IGameHandler {
    
    /** Wird aufgerufen, wenn sich das Spielbrett ändert. */
    fun onUpdate(gameState: TwoPlayerGameState)
    
    /** Wird aufgerufen, um die Zuganfrage des Servers zu beantworten. */
    fun calculateMove(): IMove
    
    /**
     * Wird aufgerufen, wenn das Spiel beendet ist.
     *
     * @param data         Das Spielergebnis
     * @param errorMessage Eventuelle Fehlernachricht
     */
    fun onGameOver(data: GameResult, errorMessage: String?)
    
}
