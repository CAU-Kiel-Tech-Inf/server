package sc.plugin2017.gui.renderer.primitives;

import processing.core.PApplet;
import processing.core.PFont;

/**
 * @author soeren
 *
 */

public class GuiConstants {

  // ###### Colors
  public static int calcColor(int alpha, int r, int g, int b) {
    int col = 0;
    col |= alpha << 24;
    col |= r << 16;
    col |= g << 8;
    col |= b;
    return col;
  }

  public static final int colorBackGround = calcColor(200, 200, 200, 200);
  public static final int colorRed = calcColor(255, 200, 0, 0);
  public static final int colorBlue = calcColor(255, 0, 0, 200);
  public static final int colorLightRed = calcColor(255, 205, 92, 92);
  public static final int colorLightBlue = calcColor(255, 92, 92, 205);
  public static final int colorGreen = calcColor(255, 0, 255, 0);
  public static final int colorLightGrey = calcColor(255, 100, 100, 100);
  public static final int colorLightLightGrey = calcColor(255, 150, 150, 150);
  public static final int colorGrey = calcColor(255, 50, 68, 70);
  public static final int colorDarkGrey = calcColor(255, 30, 30, 30);
  public static final int colorBlack = calcColor(255, 0, 0, 0);
  public static final int colorSideBarBG = calcColor(200, 255, 255, 255);
  public static final int colorHexFields = calcColor(255, 21, 160, 177);
  public static final int colorHexFieldsHighlight = calcColor(255, 21, 195, 177);
  public static final int colorText = calcColor(255, 0, 0, 0);
  public static final int colorGreyOut = calcColor(100, 30, 30, 30);
  public static final int colorHighLighted = calcColor(255, 40, 205, 50);
  public static final int colorHexFieldIsland = calcColor(255, 50, 150, 50);
  public static final int colorHexFieldSANDBANK = calcColor(255, 240, 230, 140); // khaki
  public static final int colorHexFieldSANDBANKHighlight = calcColor(255, 250, 240, 150);
  public static final int colorHexFieldLOG = calcColor(255, 151, 125, 100);
  public static final int colorHexFieldLOGHighlight = calcColor(255, 161, 135, 110);
  public static final int colorPassenger = calcColor(255, 255, 255, 153);
  public static final int colorWhite = calcColor(255, 255, 255, 255);
  public static final int colorHexFieldGOAL = calcColor(255, 154, 50, 205);
  public static final int colorOrange = calcColor(255, 255, 153, 0);




  public static final int frameBorderSize = 5;

  // not needed
  /*
   * public static final float GUI_BOARD_START_X = 1; public static final
   * float GUI_BOARD_START_Y = 1;
   */


  /**
   * startwert der x-Koordinate der Sidebar mit parent.width zu multiplizieren
   */
  public static final float SIDE_BAR_START_X = 0.8f;
  /**
   * startwert der y-Koordinate der Sidebar zu 0 zu addieren, ist 0
   *
   */
  public static final float SIDE_BAR_START_Y = 0;
  /**
   * Breite der Sidebar mit parent.width zu multiplizieren
   */
  public static final float SIDE_BAR_WIDTH = 0.2f;
  /**
   * Höhe der Sidebar mit parent.heigth zu multiplizieren
   */
  public static final float SIDE_BAR_HEIGHT = 0.875f;

  /**
   * startwert der x-Koordinate der Progressbar zu 0 zu addieren
   */
  public static final float PROGRESS_BAR_START_X = 50;
  /**
   * startwert der y-Koordinate der Progressbar zu parent.height zu addieren
   */
  public static final float PROGRESS_BAR_START_Y = -30;
  /**
   * Breite der Progressbar zu parent.width zu addieren
   */
  public static final float PROGRESS_BAR_WIDTH = -60;
  /**
   * Höhe der Progressbar
   *
   */
  public static final float PROGRESS_BAR_HEIGHT = 1 - SIDE_BAR_HEIGHT;

  /**
   * Sourcepath to Background Image
   */
  public static final String BACKGROUND_IMAGE = "resource/game/background.png";

  public static final String ISLAND_IMAGE_PATH = "resource/game/island.png";
  /**
   * Relative Size of the Game Ended Dialog
   */
  public static final float GAME_ENDED_SIZE = 0.3f;

   /**
   * Breite des GuiBoard mit parent.width zu multiplizieren
   */
  public static final float GUI_BOARD_WIDTH = 0.8f;
  /**
   * Höhe des GuiBoard zu parent.height zu ultiplizieren
   */
  public static final float GUI_BOARD_HEIGHT = SIDE_BAR_HEIGHT;
  public static final float BORDERSIZE = 2f;

  /*
   * Fonts
   * Wird OpenGL als Renderer genutzt, dann wird Text unscharf
   * dargestellt. Für jede benutzte Textgröße einen Font gengerieren löst das
   * Problem.
   */
  public static int[] fontSizes = {18, 25, 30, 10 }; //The needed Font Sizes
  public static PFont[] fonts;

  public static void generateFonts(PApplet parent){
    fonts = new PFont[fontSizes.length];
    for(int i=0; i< fontSizes.length;i++){
      fonts[i] = parent.createFont("Arial", fontSizes[i]);
    }
  }
}