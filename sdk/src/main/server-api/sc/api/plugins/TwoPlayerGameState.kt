package sc.api.plugins

abstract class TwoPlayerGameState(
        val startTeam: ITeam
) : IGameState {
    
    abstract val board: IBoard
    
    override val round: Int
        get() = (turn+1)/2
    
    /** @return das Team, das am Zug ist. */
    override val currentTeam: ITeam
        get() = currentTeamFromTurn()
    
    /** @return das Team, das nicht dran ist. */
    val otherTeam: ITeam
        get() = currentTeam.opponent()

    /** Letzter getaetigter Zug. */
    abstract val lastMove: IMove?

    /** Calculates the color of the current player from the [turn] and the [startTeam].
     * Based on the assumption that the current player switches every turn. */
    protected fun currentTeamFromTurn(): ITeam =
            if(turn.rem(2) == 0) startTeam else startTeam.opponent()

    /** Gibt die angezeigte Punktzahl des Spielers zurueck. */
    abstract fun getPointsForTeam(team: ITeam): Int

    override fun toString() =
            "GameState(turn=$turn,currentTeam=$currentTeam)"
    
    open fun longString() =
            "$this\nLast Move: $lastMove\n$board"
    
}