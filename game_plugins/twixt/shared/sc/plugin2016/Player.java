package sc.plugin2016;

import sc.framework.plugins.SimplePlayer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Ein Spieler, identifiziert durch seine Spielerfarbe.<br/>
 * Beeinhaltet auch Informationen zum Punktekonto und der Anzahl der Plättchen
 * des Spielers.
 * 
 */
@XStreamAlias(value = "player")
public class Player extends SimplePlayer implements Cloneable {

	/** Spielerfarbe des spielers
	 
	 */
	@XStreamAsAttribute
	private PlayerColor color;

	/**
	 * aktuelle Fische des Spielers
	 */
	@XStreamAsAttribute
	private int points;

	/**
	 * Anzahl der gesammelten Schollen
	 */
	@XStreamAsAttribute
	private int fields;

	/**
	 * XStream benötigt eventuell einen parameterlosen Konstruktor bei der
	 * Deserialisierung von Objekten aus XML-Nachrichten.
	 */
	public Player() {
		points = 0;
		fields = 0;
	}

	/**
	 * Erstellt einen Spieler mit gegebener Spielerfarbe.
	 * 
	 * @param color
	 *            Spielerfarbe
	 */
	public Player(final PlayerColor color) {
		fields = 0;
		this.color = color;
		points = 0;
	}

	/**
	 * erzeugt eine Deepcopy dieses Objekts
	 * 
	 * @return ein neues Objekt mit gleichen Eigenschaften
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Player clone = new Player(this.color);
		clone.points = this.points;
		clone.fields = this.fields;
		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Player) && ((Player) obj).color == this.color;
	}

	/**
	 * liefert die Spielerfarbe dieses Spielers
	 * 
	 * @return Spielerfarbe
	 */
	public PlayerColor getPlayerColor() {
		return color;
	}
}
