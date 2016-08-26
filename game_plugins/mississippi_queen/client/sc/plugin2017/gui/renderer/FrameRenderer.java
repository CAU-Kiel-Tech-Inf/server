/**
 *
 */
package sc.plugin2017.gui.renderer;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import processing.core.PApplet;
import sc.plugin2017.Acceleration;
import sc.plugin2017.Action;
import sc.plugin2017.EPlayerId;
import sc.plugin2017.FieldType;
import sc.plugin2017.GameState;
import sc.plugin2017.Move;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.Turn;
import sc.plugin2017.gui.renderer.primitives.Background;
import sc.plugin2017.gui.renderer.primitives.BoardFrame;
import sc.plugin2017.gui.renderer.primitives.GameEndedDialog;
import sc.plugin2017.gui.renderer.primitives.GuiBoard;
import sc.plugin2017.gui.renderer.primitives.GuiConstants;
import sc.plugin2017.gui.renderer.primitives.GuiTile;
import sc.plugin2017.gui.renderer.primitives.HexField;
import sc.plugin2017.gui.renderer.primitives.ProgressBar;
import sc.plugin2017.gui.renderer.primitives.SideBar;
import sc.plugin2017.util.InvalidMoveException;

/**
 * @author soeren
 */

public class FrameRenderer extends PApplet {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final Logger logger = LoggerFactory
      .getLogger(FrameRenderer.class);

  public GameState currentGameState;
  private GameState backUp;
  public Move currentMove;
  public boolean humanPlayer;
  private boolean humanPlayerMaxTurn;
  public int maxTurn;
  private EPlayerId id;


  public GuiBoard guiBoard;

  private Background background;

  private ProgressBar progressBar;
  private SideBar sideBar;
  private BoardFrame boardFrame;

  public LinkedHashMap<HexField, Action> stepPossible;

  public FrameRenderer() {
    super();

    // logger.debug("calling frameRenderer.size()");
    this.humanPlayer = false;
    this.humanPlayerMaxTurn = false;
    this.id = EPlayerId.OBSERVER;

    RenderConfiguration.loadSettings();

    background = new Background(this);
    logger.debug("Dimension when creating board: (" + this.width + ","
        + this.height + ")");
    guiBoard = new GuiBoard(this);
    progressBar = new ProgressBar(this);
    sideBar = new SideBar(this);
    boardFrame = new BoardFrame(this);
    stepPossible = new LinkedHashMap<HexField, Action>();
  }

  @Override
  public void setup() {
    maxTurn = -1;
    // choosing renderer from options - using P2D as default
    if (RenderConfiguration.optionRenderer.equals("JAVA2D")) {
      logger.debug("Using Java2D as Renderer");
      size(this.width, this.height, JAVA2D);
    } else if (RenderConfiguration.optionRenderer.equals("P3D")) {
      logger.debug("Using P3D as Renderer");
      size(this.width, this.height, P3D);
    } else {
      logger.debug("Using P2D as Renderer");
      size(this.width, this.height, P2D);
    }

    smooth(RenderConfiguration.optionAntiAliasing); // Anti Aliasing

    // initial draw
    GuiConstants.generateFonts(this);
    redraw();
    noLoop(); // prevent thread from starving everything else

  }

  @Override
  public void draw() {
    background.draw();
    guiBoard.draw();
    progressBar.draw();
    sideBar.draw();
    boardFrame.draw();
    if (currentGameState != null && currentGameState.gameEnded()) {
      GameEndedDialog.draw(this);
    }
  }

  public void updateGameState(GameState gameState) {
    int lastTurn = -1;
    if (currentGameState != null) {
      lastTurn = currentGameState.getTurn();
    }
    currentGameState = gameState;
    currentMove = new Move();
    // needed for simulation of actions
    currentGameState.getRedPlayer().setMovement(currentGameState.getRedPlayer().getSpeed());
    currentGameState.getBluePlayer().setMovement(currentGameState.getBluePlayer().getSpeed());
    currentGameState.getCurrentPlayer().setFreeTurns(currentGameState.isFreeTurn() ? 2 : 1);
    currentGameState.getCurrentPlayer().setFreeAcc(1);
    // make backup of gameState
    try {
      backUp = currentGameState.clone();
    } catch (CloneNotSupportedException e) {
      // TODO Auto-generated catch block
      System.out.println("Clone of Backup failed");
      e.printStackTrace();
    }

    if (gameState != null && gameState.getBoard() != null)
      guiBoard.update(gameState.getBoard(), gameState.getRedPlayer(), gameState.getBluePlayer(), gameState.getCurrentPlayerColor());
    if ((currentGameState == null || lastTurn == currentGameState.getTurn() - 1)) {

      if (maxTurn == currentGameState.getTurn() - 1) {

        maxTurn++;
        humanPlayerMaxTurn = false;
      }
    }
    humanPlayer = false;
    if (currentGameState != null && maxTurn == currentGameState.getTurn()
        && humanPlayerMaxTurn) {
      humanPlayer = true;
    }
  }

  public void requestMove(int maxTurn, EPlayerId id) {
    int turn = currentGameState.getTurn();
    this.id = id;
    if (turn % 2 == 1) {
      if (id == EPlayerId.PLAYER_ONE) {
        this.id = EPlayerId.PLAYER_TWO;
      }
    }
    // this.maxTurn = maxTurn;
    this.humanPlayer = true;
    humanPlayerMaxTurn = true;
  }

  public Image getImage() {
    // TODO return an Image of the current board
    return null;
  }

  @Override
  public void mouseMoved(MouseEvent e) {

  }

  @Override
  public void mouseDragged(MouseEvent e) {

  }

  private void update(GameState gameState) {
    if (gameState != null && gameState.getBoard() != null) {
      gameState.getRedPlayer().setPoints(gameState.getPointsForPlayer(PlayerColor.RED));
      gameState.getBluePlayer().setPoints(gameState.getPointsForPlayer(PlayerColor.BLUE));
      guiBoard.update(gameState.getBoard(), gameState.getRedPlayer(),
          gameState.getBluePlayer(), gameState.getCurrentPlayerColor());
    }
    redraw();
  }

  @Override
  public void mousePressed(MouseEvent e) {
    draw();
    if(isHumanPlayer() && maxTurn == currentGameState.getTurn()) {
      if(currentGameState.getCurrentPlayer()
        .getField( currentGameState.getBoard()).getType() != FieldType.SANDBANK) {
        guiBoard.left.isClicked();
        guiBoard.right.isClicked();
        if(currentGameState.getCurrentPlayer().getSpeed() != 1) {
          guiBoard.speedDown.isClicked();
        }
        if(currentGameState.getCurrentPlayer().getSpeed()  != 6) {
          guiBoard.speedUp.isClicked();
        }
      }
      guiBoard.send.isClicked();
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if(isHumanPlayer() && maxTurn == currentGameState.getTurn()) {
      // first the gui buttons
      if(currentGameState.getCurrentPlayer()
        .getField( currentGameState.getBoard()).getType() != FieldType.SANDBANK) {
        if(guiBoard.left.isClicked()) {
          Turn turn = new Turn(1);
          currentMove.actions.add(turn);
          try {
            turn.perform(currentGameState, currentGameState.getCurrentPlayer());
          } catch (InvalidMoveException e1) {
            System.out.println("Failed to perform move of user, please report if this happens");
            e1.printStackTrace();
          }
        }
        if(guiBoard.right.isClicked()) {
          Turn turn = new Turn(-1);
          currentMove.actions.add(turn);
          try {
            turn.perform(currentGameState, currentGameState.getCurrentPlayer());
          } catch (InvalidMoveException e1) {
            System.out.println("Failed to perform move of user, please report if this happens");
            e1.printStackTrace();
          }
        }
        if(currentGameState.getCurrentPlayer().getSpeed() != 1) {
          if(guiBoard.speedDown.isClicked()) {
            Acceleration acc = new Acceleration(-1);
            currentMove.actions.add(acc);
            try {
              acc.perform(currentGameState, currentGameState.getCurrentPlayer());
            } catch (InvalidMoveException e1) {
              System.out.println("Failed to perform move of user, please report if this happens");
              e1.printStackTrace();
            }
          }
        }
        if(currentGameState.getCurrentPlayer().getSpeed()  != 6) {
          if(guiBoard.speedUp.isClicked()) {
            Acceleration acc = new Acceleration(1);
            currentMove.actions.add(acc);
            try {
              acc.perform(currentGameState, currentGameState.getCurrentPlayer());
            } catch (InvalidMoveException e1) {
              System.out.println("Failed to perform move of user, please report if this happens");
              e1.printStackTrace();
            }
          }
        }
      }
      if(guiBoard.send.isClicked()) {
        sendMove();
      }
      if(guiBoard.cancel.isClicked()) {
        try {
          currentGameState = backUp.clone();
        } catch (CloneNotSupportedException e1) {
          System.out.println("Clone of backup failed");
          e1.printStackTrace();
        }
        updateGameState(currentGameState);
        redraw();
        return;
      }
      // then field clicks
      HexField clicked = getFieldCoordinates(mouseX, mouseY);
      Action action = stepPossible.get(clicked);
      if(action != null) {
        try {
          action.perform(currentGameState, currentGameState.getCurrentPlayer());
        } catch (InvalidMoveException e1) {
          System.out.println("Failed to perform move of user, please report if this happens");
          e1.printStackTrace();
        }
        currentMove.actions.add(action);
        update(currentGameState);
      }
    }
    update(currentGameState);
    redraw();
  }

  private void sendMove() {
    Move send = new Move();
    // buddle accelerations
    int acc = 0;
    for (Action action : currentMove.actions) {
      if(action instanceof Acceleration) {
        acc += ((Acceleration) action).acc;
      }
    }
    if(acc != 0) {
      send.actions.add(new Acceleration(acc));
    }
    // add rest to send
    for (Action action : currentMove.actions) {
      if(action != null && action.getClass() != Acceleration.class) {
        send.actions.add(action);
      }
    }
    // set order
    send.setOrderInActions();
    RenderFacade.getInstance().sendMove(send);
  }

  private HexField getFieldCoordinates(int x, int y) {
    HexField coordinates;

    for (GuiTile tile : guiBoard.tiles) {
      coordinates = tile.getFieldCoordinates(x,y);
      if(coordinates != null) {
        return coordinates;
      }
    }
    return null;
  }

  @Override
  public void resize(int width, int height) {
    background.resize(width, height);
    guiBoard.resize(width, height);
  }

  /*
   * Hack! wenn das Fenster resized wird, wird setBounds aufgerufen. hier
   * rufen wir resize auf, um die Komponenten auf die richtige Größe zu
   * bringen.
   */
  @Override
  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);
    this.resize(width, height);
  }

  @Override
  public void keyPressed() {
    if (key == 'c' || key == 'C') {
      new RenderConfigurationDialog(FrameRenderer.this);
    }

  }

  public boolean isHumanPlayer() {
    return humanPlayer;
  }

  public EPlayerId getId() {
    return id;
  }

  public void killAll() {
noLoop();

    if(background != null) {
      background.kill();
    }
    if(guiBoard != null) {
      // TODO kill board
      guiBoard.kill();
    }
    if(progressBar != null) {
      progressBar.kill();
    }
    if(sideBar != null) {
      sideBar.kill();
    }
    if(boardFrame != null) {
      boardFrame.kill();
    }
  }
}