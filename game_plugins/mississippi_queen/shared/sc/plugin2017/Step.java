package sc.plugin2017;

import java.util.LinkedList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.plugin2017.util.InvalidMoveException;

@XStreamAlias(value = "step")
public class Step extends Action {
  /**
   * Anzahl der Felder, die zurückgelegt werden.
   */
  @XStreamAsAttribute
  public int distance;
  /**
   * Das fahren auf eine Sandbank beendet den Zug
   */
  @XStreamOmitField
  protected boolean endsTurn;

  public Step() {
    distance = 0;
    order = 0;
  }

  /**
   * Legt eine neue Laufaktion an
   * @param distance Felder die überwunden werden
   */
  public Step(int distance) {
    this.distance = distance;
    endsTurn = false;
  }

  /**
   * Legt eine neue Laufaktion an
   * @param distance Felder die überwunden werden
   * @param order Reihenfolge
   */
  public Step(int distance, int order) {
    this.distance = distance;
    this.order = order;
    endsTurn = false;
  }

  @Override
  public void perform(GameState state, Player player) throws InvalidMoveException {
    if(player.getMovement() == 0) {
      throw new InvalidMoveException("Keine Bewegunspunkte mehr vorhanden");
    }
    Field start = player.getField(state.getBoard());
    LinkedList<Field> nextFields = new LinkedList<Field>();
    int direction = player.getDirection();
    if(distance == 0 || distance > 6 || distance < -1) {
      throw new InvalidMoveException("Zurückgelegte Distanz ist ungültig.");
    }
    if(distance == -1) { // Fall rückwärts von Sandbank
      if(start.getType() != FieldType.SANDBANK) {
        throw new InvalidMoveException("Rückwärtszug ist nur von Sandbank aus möglich.");
      }
      Field next = start.getFieldInDirection(GameState.getOppositeDirection(direction), state.getBoard());
      if(next == null || next.getType() == FieldType.LOG || !next.isPassable()) {
        throw new InvalidMoveException("Der Weg ist versperrt");
      }
      state.put(next.getX(), next.getY(), player);
      player.setMovement(0);
      return;
    } else {
      if(start.getType() == FieldType.SANDBANK) {
        if(this.distance != 1) {
          throw new InvalidMoveException("Nur eine Bewegung nach vorne auf einer Sandbank möglich");
        }
        player.setMovement(0);
        Field next = start.alwaysGetFieldInDirection(direction, state.getBoard());
        if(!next.isPassable()) {
          throw new InvalidMoveException("Feld ist blockiert. Ungültiger Zug.");
        }
        state.put(next.getX(), next.getY(), player);
        player.setCoal(player.getCoal() - 1);
        return;
      }
      nextFields.add(start);
      // Kontrolliere für die Zurückgelegte Distanz, wie viele Bewegunsgpunkte verbraucht werden und ob es möglich ist, soweit zu ziehen
      for(int i = 0; i < distance; i++) {
        Field next = nextFields.get(i).alwaysGetFieldInDirection(player.getDirection(), state.getBoard());
        if(next != null) {
          nextFields.add(next);
        }
        Field checkField = nextFields.get(i + 1); // get next field
        if(!checkField.isPassable() ||
            (state.getOtherPlayer().getField(state.getBoard()).equals(checkField) && i != distance -1)) {
          throw new InvalidMoveException("Feld ist blockiert. Ungültiger Zug.");
        }
        if(checkField.getType() == FieldType.SANDBANK) {
          // case sandbar
          player.setSpeed(1);
          player.setMovement(0);
          endsTurn = true;
          if(i != distance - 1) {
            // Zug endet hier, also darf nicht weitergelaufen werden
            throw new InvalidMoveException("Zug sollte bereits enden, da auf Sandbank gefahren wurde.");
          }

          state.put(checkField.getX(), checkField.getY(), player);
          return;
        } else if(checkField.getType() == FieldType.LOG) {
          if(player.getMovement() <= 1) {
            throw new InvalidMoveException("Nicht genug Bewegunspunkte vorhanden, um Baumstamm zu überqueren");
          }
          player.setMovement(player.getMovement() - 2);
          player.setSpeed(player.getSpeed() - 1);
        } else {
          player.setMovement(player.getMovement() - 1);
          System.out.println("Verringere Movement zu " + player.getMovement());
        }

      }
      Field target = nextFields.get(distance);
      state.put(target.getX(), target.getY(), player);
    }
    return;
  }

  @Override
  public Step clone() {
    return new Step(this.distance, this.order);
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof Step) {
      return (this.distance == ((Step) o).distance);
    }
    return false;
  }

  @Override
  public String toString() {
    return "Gehe " + distance + " vor";
  }

}