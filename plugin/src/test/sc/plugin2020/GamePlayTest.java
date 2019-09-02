package sc.plugin2020;

import com.thoughtworks.xstream.XStream;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import sc.plugin2020.util.Configuration;
import sc.plugin2020.util.CubeCoordinates;
import sc.plugin2020.util.GameRuleLogic;
import sc.plugin2020.util.TestGameUtil;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static sc.plugin2020.util.TestJUnitUtil.assertThrows;

public class GamePlayTest {

  private Game game;
  private GameState state;

  @Before
  public void beforeEveryTest() {
    game = new Game();
    state = game.getGameState();
    state.setCurrentPlayerColor(PlayerColor.RED);
  }

  @Test
  public void boardCreationTest() {
    Board board = new Board();
    assertNotNull(board.getField(0,0,0));
  }

  @Test
  public void invalidBoardStringTest() {

    assertThrows(InvalidParameterException.class, () ->
            TestGameUtil.createCustomBoard("" +
                    "    XY--------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------" )
    );
    assertThrows(InvalidParameterException.class, () ->
            TestGameUtil.createCustomBoard("" +
                    "    BY--------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------" )
    );
  }

  @Test
  public void onlyEndAfterRoundTest() throws InvalidMoveException {
    Board board = TestGameUtil.createCustomBoard("" +
            "    RB--------" +
            "   ------------" +
            "  --------------" +
            " ----------------" +
            "------------------" +
            " ----------------" +
            "  --------------" +
            "   ------------" +
            "    ----------" );
    state.setBoard(board);
    {
      //Move move = new Move();
      //GameRuleLogic.performMove(state, move);
      assertEquals(1, GameRuleLogic.findPiecesOfTypeAndPlayer(state.getBoard(), PieceType.BEETLE, PlayerColor.RED).size());
    }
  }

  @Test
  public void getNeighbourTest() {
    ArrayList<Field> n = GameRuleLogic.getNeighbours(new Board(), new CubeCoordinates(-2, 1));
    CubeCoordinates[] expected = {
            new CubeCoordinates(-2,2),
            new CubeCoordinates(-1,1),
            new CubeCoordinates(-1,0),
            new CubeCoordinates(-2,0),
            new CubeCoordinates(-3,1),
            new CubeCoordinates(-3,2)
    };
    assertArrayEquals(
            Arrays.stream(expected).sorted().toArray(),
            n.stream().map((Field f) -> f.getPosition()).sorted().toArray()
    );
  }

  @Test
  public void gameEndTest() {
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " ----BBBB--------" +
              "----BBRQBB--------" +
              " ----BBBB--------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      assertTrue(GameRuleLogic.isQueenBlocked(board, PlayerColor.RED));
    }
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " ----BBBB--------" +
              "------RQBB--------" +
              " ----BBBB--------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      assertFalse(GameRuleLogic.isQueenBlocked(board, PlayerColor.RED));
    }
  }

  @Test
  public void findFieldsOwnedByPlayerTest() {
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   RG----------" +
              "  --BQ----------" +
              " BGBB------------" +
              "----RQ------------" +
              " ----------------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      state.setBoard(board);
      assertEquals(3, GameRuleLogic.fieldsOwnedByPlayer(board, PlayerColor.BLUE).size());
    }
  }

  @Test
  public void setMoveTest() throws InvalidMoveException {
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " ----------------" +
              "------------------" +
              " ----------------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      state.setBoard(board);
      Move move = new Move(state.getUndeployedPieces(PlayerColor.RED).get(0), new CubeCoordinates(0,0));
      assertTrue(GameRuleLogic.validateMove(state, move));
    }
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " ----------------" +
              "------------------" +
              " ----------------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      state.setBoard(board);
      Move move = new Move(state.getUndeployedPieces(PlayerColor.RED).get(0), new CubeCoordinates(8,0));
      assertThrows(InvalidMoveException.class, () -> GameRuleLogic.validateMove(state, move));
    }
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " --BG------------" +
              "------------------" +
              " ----------------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      state.setBoard(board);
      Move move = new Move(state.getUndeployedPieces(PlayerColor.RED).get(0), new CubeCoordinates(0,0));
      assertThrows(InvalidMoveException.class, () -> GameRuleLogic.validateMove(state, move));
    }
    // TODO test and implement setting on board which already has pieces by current player

  }

  @Test
  @Ignore
  // use to see the generated XML
  public void xmlTest() {
    Board board = TestGameUtil.createCustomBoard("" +
            "    ----------" +
            "   ------------" +
            "  --------------" +
            " --BG------------" +
            "------------------" +
            " ----------------" +
            "  --------------" +
            "   ------------" +
            "    ----------");
    state.setBoard(board);
    Piece[] pieces = new Piece[]{, };
    board.getField(0,0).getPieces().add(new Piece(PlayerColor.RED, PieceType.ANT));
    board.getField(0,0).getPieces().add(new Piece(PlayerColor.BLUE, PieceType.BEE));
    assertEquals(PieceType.BEE, board.getField(0,0).getPieces().lastElement().getPieceType());
    assertEquals(PieceType.BEE, board.getField(0,0).getPieces().peek().getPieceType());
    XStream xstream = Configuration.getXStream();
    String xml = xstream.toXML(state);
    assertEquals("", xml);
  }
}