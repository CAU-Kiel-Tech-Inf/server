package sc.plugin2022

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.plugin2022.util.Constants
import kotlin.math.abs
import kotlin.math.sqrt

/** Eine 2D Koordinate der Form (x, y). */
@XStreamAlias(value = "coordinates")
data class Coordinates(
        @XStreamAsAttribute val x: Int,
        @XStreamAsAttribute val y: Int,
) {
    
    override fun toString(): String = "[$x|$y]"
    
    /** Addiere den [Vector] auf die [Coordinates] auf. */
    operator fun plus(vector: Vector): Coordinates {
        return Coordinates(x + vector.dx, y + vector.dy)
    }
    /** Berechne die Distanz zweier Koordinaten, als [Vector] */
    operator fun minus(other: Coordinates): Vector {
        return Vector(x - other.x, y - other.y)
    }
    /** Ziehe die Distanz (als [Vector]) von der Koordinate ab. */
    operator fun minus(other: Vector): Coordinates {
        return Coordinates(x - other.dx, y - other.dy)
    }
    /** Wandelt die [Coordinates] in einen entsprechenden [Vector]. */
    operator fun unaryPlus(): Vector = Vector(x, y)
    
    /** Gibt ein Set der vier benachbarten Felder dieser Koordinaten zurück. */
    val neighbors: Set<Coordinates>
        get() = Vector.cardinals.mapTo(HashSet()) { this + it }
    
    /** Whether these coordinates mark a field on the board. */
    val isValid
        get() = x >= 0 && x < Constants.BOARD_SIZE &&
                y >= 0 && y < Constants.BOARD_SIZE
    
    companion object {
        /** Der Ursprung des Koordinatensystems (0, 0). */
        val origin = Coordinates(0, 0)
    }
}

/**
 * Die Strecke zwischen zwei [Coordinates].
 * @property dx die Differenz in x-Richtung
 * @property dy die Differenz in y-Richtung
 */
data class Vector(val dx: Int, val dy: Int): Comparable<Vector> {
    /** Die Fläche des Rechtecks, dessen Diagonale der Vector ist. */
    val area: Int
        get() = abs(dx * dy)
    
    private val comparableLength: Int
        get() = dx * dx + dy * dy
    
    val length: Double
        get() = sqrt(comparableLength.toDouble())
    
    /** Verändert die Länge des Vektors um den gegebenen Faktor, ohne seine Richtung zu ändern. */
    operator fun times(scalar: Int): Vector =
            Vector(scalar * dx, scalar * dy)
    
    /**
     * Vergleicht die Länge dieses Vektors mit einem anderen.
     * @return groesser Null, wenn dieser laenger ist
     */
    override operator fun compareTo(other: Vector): Int =
            comparableLength - other.comparableLength
    
    /** Konvertiert den Vektor zu entsprechenden [Coordinates]. */
    operator fun unaryPlus(): Coordinates = Coordinates(dx, dy)
    
    companion object {
        /** Die vier Vektoren in diagonaler Richtung. */
        val diagonals: Array<Vector> = arrayOf(
                Vector(-1, -1),
                Vector(-1, 1),
                Vector(1, -1),
                Vector(1, 1)
        )
        /** Die vier Vektoren in kardinaler Richtung. */
        val cardinals: Array<Vector> = arrayOf(
                Vector(-1, 0),
                Vector(0, -1),
                Vector(1, 0),
                Vector(0, 1)
        )
    }
}