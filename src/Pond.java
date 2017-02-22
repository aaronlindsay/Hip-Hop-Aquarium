// Assignment 5
// lindsay aaron
// aaronlindsay
// lei bowen
// bowenleis

import java.awt.Color;
import java.io.File;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import tester.*;
import javalib.funworld.*;
import javalib.worldimages.*;


// to represent the world of fish
class Pond extends World {

  // WORLD CONSTANTS

  // width, height, and center of pond values
  public static final int WIDTH = 1800;
  public static final int HEIGHT = 1000;
  public static final int HALF_WIDTH = (int) Pond.WIDTH / 2;
  public static final int HALF_HEIGHT = (int) Pond.HEIGHT / 2;
  public static final int initialSize = (int) (Math.min(Pond.HEIGHT, Pond.WIDTH) * 0.02);

  // images
  public static final WorldImage reef = new FromFileImage("reef.jpeg");

  public static double getReefScale() {
    if (WIDTH > HEIGHT) {
      return (WIDTH / reef.getWidth());
    }
    else {
      return (HEIGHT / reef.getHeight());
    }
  }

  public static final WorldImage POND_BG = new ScaleImage(reef, getReefScale());

  public static final WorldImage POND_BG2 = new RectangleImage(Pond.WIDTH,
      Pond.HEIGHT, OutlineMode.SOLID, Color.BLUE);

  public static final WorldImage RED_FISH = new FromFileImage("redFish.png");
  public static final WorldImage RED_FISH_FLIP = new FromFileImage("redFishFlip.png");
  public static final WorldImage YLW_FISH = new FromFileImage("ylwFish.png");
  public static final WorldImage YLW_FISH_FLIP = new FromFileImage("ylwFishFlip.png");
  public static final WorldImage BLU_FISH = new FromFileImage("bluFish.png");
  public static final WorldImage BLU_FISH_FLIP = new FromFileImage("bluFishFlip.png");
  public static final WorldImage MAG_FISH = new FromFileImage("magFish.png");
  public static final WorldImage MAG_FISH_FLIP = new FromFileImage("magFishFlip.png");
  public static final WorldImage YL = new FromFileImage("YL.png");
  public static final WorldImage YL_FLIP = new FromFileImage("YLFlip.png");
  public static final WorldImage YE = new FromFileImage("YE.png");
  public static final WorldImage YE_FLIP = new FromFileImage("YEFlip.png");
  public static final WorldImage KENNY = new FromFileImage("kenny.png");
  public static final WorldImage KENNY_FLIP = new FromFileImage("kennyFlip.png");

  // bools
  public static boolean newRapKing = false;
  public static boolean yeEvent = false;
  public static boolean kennyEvent = false;

  // fields
  Player player;
  ILoSwimmer fishies;

  public Pond() {}

  // construct a pond with n fish
  public Pond(int n) {
    this.player = new Player();
    this.fishies = new ConsLoSwimmer(n);
  }

  public Pond(Player player, ILoSwimmer fishies) {
    super();
    this.player = player;
    this.fishies = fishies;
  }

  public WorldScene makeScene() {
    WorldScene bottom = this.fishies.getImages(this)
        .placeImageXY(this.player.getImage(),
            this.player.posn.x, this.player.posn.y);
    WorldImage text = new TextImage(("Score: " + String.format("%d", 
        this.player.size - initialSize)), 30, FontStyle.BOLD, Color.WHITE);

    int scale = (int) (0.05 * Math.min(Pond.WIDTH, Pond.HEIGHT));
    int textX = (int) (Pond.WIDTH - 0.5 * text.getWidth() - scale);
    int textY = (int) (0.5 * text.getHeight() + scale);

    return bottom.placeImageXY(text, textX, textY);

  }

  // move player on key-press
  public World onKeyEvent(String ke) {
    if (ke.equals("x")) {
      Sound.feedbackLoop.stop();
      Sound.backseatLoop.stop();
      Sound.underwater.stop();
      Sound.soundOfSilence.play();
      return this.endOfWorld("if u can't handle the heat then GET OUT OF THE RAP KITCHEN");
    }
    else if (ke.equals("space")) {
      return new Pond(10);
    }
    else {
      return new Pond(this.player.move(ke), this.fishies);
    }
  }

  // move all swimmers according to their velocities on-tick
  public World onTick() {

    if (yeEvent && !Sound.feedbackLoop.isPlaying) {
      Sound.feedbackLoop.loop();
    }
    else if (!yeEvent && Sound.feedbackLoop.isPlaying) {
      Sound.feedbackLoop.stop();
    }

    if (kennyEvent && !Sound.backseatLoop.isPlaying) {
      Sound.backseatLoop.loop();
    }
    else if (!kennyEvent && Sound.backseatLoop.isPlaying) {
      Sound.backseatLoop.stop();
    }

    return new Pond(this.player.eat(this.fishies).move(),
        this.fishies.checkPopulation(this.player).move());
  }

  // produce end-game image
  public WorldScene lastScene(String s) {
    WorldImage text = new TextImage(s, 30, FontStyle.BOLD, Color.WHITE);
    return this.makeScene()
        .placeImageXY(new RectangleImage(
            (int) text.getWidth() + 10, (int) text.getHeight() + 10,
            OutlineMode.SOLID, new Color(76, 200, 232)),
            Pond.HALF_WIDTH, Pond.HALF_HEIGHT)
        .placeImageXY(new RectangleImage(
            (int) text.getWidth() + 10, (int) text.getHeight() + 10,
            OutlineMode.OUTLINE, Color.WHITE),
            Pond.HALF_WIDTH, Pond.HALF_HEIGHT)
        .placeImageXY(text, Pond.HALF_WIDTH, Pond.HALF_HEIGHT);
  }

  // check whether the player was eaten or is the largest fish in the pond
  public WorldEnd worldEnds() {

    // if one of the fishies can eat the player:
    if (this.fishies.canEat(this.player)) {
      Sound.feedbackLoop.stop();
      Sound.backseatLoop.stop();
      Sound.underwater.stop();
      Sound.soundOfSilence.play();
      return new WorldEnd(true, this.lastScene("You have been eaten :o("));
    }
    // if the player is the largest fish in the pond:
    else if (newRapKing) {
      Sound.feedbackLoop.stop();
      Sound.backseatLoop.stop();
      Sound.underwater.stop();
      Sound.gatorade.play();
      return new WorldEnd(true, this.lastScene(" ~*~* ALL HAIL THE NEW KING OF HIP HOP *~*~ "));
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

}

class PondExamples {

  // player size & smaller & bigger
  int pSize = ((int) (Math.min(Pond.HEIGHT, Pond.WIDTH) * 0.03));
  int pSizeSmall = pSize - 5;
  int pSizeLarge = pSize + 5;

  // acceleration scale for velocities
  int a = ((int) (Math.min(Pond.HEIGHT, Pond.WIDTH) * 0.005));

  // examples of players
  Player p1 = new Player();
  Player bigPlayer = new Player(new Posn(Pond.HALF_WIDTH, Pond.HALF_HEIGHT), new Vel(0, 0), 500, Color.RED);
  Player p2 = new Player(new Posn(100, 100), new Vel(0, 0), pSize, Color.RED);
  Player p2v2 = new Player(new Posn(100 + Pond.WIDTH, 100), new Vel(0, 0),
      pSize, Color.RED);
  Player p2L = new Player(new Posn(100, 100), new Vel(-a, 0), pSize, Color.RED);
  Player p2R = new Player(new Posn(100, 100), new Vel(a, 0), pSize, Color.RED);
  Player p2U = new Player(new Posn(100, 100), new Vel(0, -a), pSize, Color.RED);
  Player p2D = new Player(new Posn(100, 100), new Vel(0, a), pSize, Color.RED);
  Player p1L = new Player(new Posn(95, 100), new Vel(-5, 0), pSize, Color.RED);
  Player p1L2 = new Player(new Posn(90, 100), new Vel(-5, 0), pSize, Color.RED);
  Player p1R = new Player(new Posn(105, 100), new Vel(5, 0), pSize, Color.RED);
  Player p1R2 = new Player(new Posn(110, 100), new Vel(5, 0), pSize, Color.RED);
  Player p1U = new Player(new Posn(100, 95), new Vel(0, -5), pSize, Color.RED);
  Player p1U2 = new Player(new Posn(100, 90), new Vel(0, -5), pSize, Color.RED);
  Player p1D = new Player(new Posn(100, 105), new Vel(0, 5), pSize, Color.RED);
  Player p1D2 = new Player(new Posn(100, 110), new Vel(0, 5), pSize, Color.RED);
  Player p3 = new Player(new Posn(200, 100), new Vel(0, 0), pSize, Color.RED);
  Player p4 = new Player(new Posn(300, 100), new Vel(0, 0), pSize, Color.RED);

  // examples of fish
  ASwimmer f1 = new Fish(new Posn(100, 100), new Vel(0, 0), pSizeSmall, Color.YELLOW);
  ASwimmer f1L = new Fish(new Posn(95, 100), new Vel(-5, 0), pSizeSmall, Color.YELLOW);
  ASwimmer f1L2 = new Fish(new Posn(90, 100), new Vel(-5, 0), pSizeSmall, Color.YELLOW);
  ASwimmer f1R = new Fish(new Posn(105, 100), new Vel(5, 0), pSizeSmall, Color.YELLOW);
  ASwimmer f1U = new Fish(new Posn(100, 95), new Vel(0, -5), pSizeSmall, Color.YELLOW);
  ASwimmer f1D = new Fish(new Posn(100, 105), new Vel(0, 5), pSizeSmall, Color.YELLOW);

  ASwimmer f2 = new Fish(new Posn(200, 100), new Vel(0, 0), pSizeLarge, Color.CYAN);
  ASwimmer f3 = new Fish(new Posn(300, 100), new Vel(10, 10), pSizeSmall, Color.MAGENTA);
  ASwimmer ye = new Fish(new Posn(-200, Pond.HALF_HEIGHT), new Vel(2, 0), (int) (Pond.HEIGHT * 0.495), Color.BLACK);

  // examples of fishies
  ILoSwimmer mt = new MtLoSwimmer();
  ILoSwimmer fishies1 = new ConsLoSwimmer(f1,
      new ConsLoSwimmer(f2,
          new ConsLoSwimmer(f3, mt)));
  ILoSwimmer fishiesYe = new ConsLoSwimmer(ye, mt);

  // examples of data for the BlobWorldFun class:
  Pond tw1 = new Pond(this.p2, this.fishies1);
  Pond tw1L = new Pond(this.p2L, this.fishies1);
  Pond tw1R = new Pond(this.p2R, this.fishies1);
  Pond tw1U = new Pond(this.p2U, this.fishies1);
  Pond tw1D = new Pond(this.p2D, this.fishies1);

  Pond playerEats = new Pond(p3, fishies1);
  Pond playerEaten = new Pond(p2, fishies1);

  // test sameSwimmer()
  boolean testSameSwimmer(Tester t) {
    return t.checkExpect(this.p1.sameSwimmer(this.p1), true)
        && t.checkExpect(this.p1.sameSwimmer(this.p2), false)
        && t.checkExpect(this.p2.sameSwimmer(this.p2v2), true)
        && t.checkExpect(this.p2.sameSwimmer(this.f1), false)
        && t.checkExpect(this.f1.sameSwimmer(this.f1), true);
  }

  // test the move method that computes according to velocity
  boolean testMove(Tester t) {
    return t.checkExpect(this.p1.move().sameSwimmer(this.p1), true)
        && t.checkExpect(this.p1.move().sameSwimmer(this.p2), false)
        && t.checkExpect(this.p1L.move().sameSwimmer(this.p1L2), true)
        && t.checkExpect(this.p1R.move().sameSwimmer(this.p1R2), true)
        && t.checkExpect(this.p1U.move().sameSwimmer(this.p1U2), true)
        && t.checkExpect(this.p1D.move().sameSwimmer(this.p1D2), true)
        && t.checkExpect(this.f1L.move().sameSwimmer(this.f1L2), true);
  }

  // test on key events
  boolean testOnKeyEvent(Tester t) {
    return t.checkExpect(this.tw1.onKeyEvent("left"), this.tw1L)
        && t.checkExpect(this.tw1.onKeyEvent("right"), this.tw1R)
        && t.checkExpect(this.tw1.onKeyEvent("up"), this.tw1U)
        && t.checkExpect(this.tw1.onKeyEvent("down"), this.tw1D)
        && t.checkExpect(
            tw1.onKeyEvent("x").lastWorld,
            new WorldEnd(true, tw1.makeScene().placeImageXY(
                new TextImage("Goodbye", Color.RED), Pond.WIDTH, Pond.HEIGHT)));
  }

  // test isNear()
  boolean testIsNear(Tester t) {
    return t.checkExpect(p2.isNear(f2), false)
        && t.checkExpect(p3.isNear(f2), true);
  }

  /*
  // test the method outsideBounds in the Blob class 
  boolean testOutsideBounds(Tester t) {
    return t.checkExpect(this.b1.outsideBounds(60, 200), true,
        "test outsideBounds on the right")
        &&

        t.checkExpect(this.b1.outsideBounds(100, 90), true,
            "test outsideBounds below")
            &&

            t.checkExpect(new Blob(new Posn(-5, 100), 50, Color.RED)
            .outsideBounds(100, 110), true,
                "test outsideBounds above")
                &&

                t.checkExpect(new Blob(new Posn(80, -5), 50, Color.BLUE)
                .outsideBounds(100, 90), true,
                    "test outsideBounds on the left")
                    &&

                    t.checkExpect(this.b1.outsideBounds(200, 400), false,
                        "test outsideBounds - within bounds");
  }

  // test the method onMOuseClicked in the BlobWorldFun class 
  boolean testOnMouseClicked(Tester t) {
    return t.checkExpect(this.b1w.onMouseClicked(new Posn(50, 50)),
        this.b1mouse50x50w);
  }

  // test the method nearCenter in the Blob class 
  boolean testNearCenter(Tester t) {
    return t.checkExpect(this.b1.nearCenter(200, 200), true,
        "test nearCenter - true")
        && t.checkExpect(this.b1.nearCenter(200, 100), false,
            "test nearCenter - false");
  }

  // the method randomInt in the Blob class 
  boolean testRandomInt(Tester t) {
    return t.checkOneOf("test randomInt", this.b1.randomInt(3), -3, -2, -1,
        0, 1, 2, 3)
        && t.checkNoneOf("test randomInt", this.b1.randomInt(3), -5,
            -4, 4, 5);
  }

  // test the method randomMove in the Blob class 
  boolean testRandomMove(Tester t) {
    return t.checkOneOf("test randomMove", this.b1.randomMove(1), new Blob(
        new Posn(99, 99), 50, Color.RED), new Blob(new Posn(99, 100),
            50, Color.RED), new Blob(new Posn(99, 101), 50, Color.RED),
            new Blob(new Posn(100, 99), 50, Color.RED), new Blob(new Posn(
                100, 100), 50, Color.RED), new Blob(new Posn(100, 101),
                    50, Color.RED), new Blob(new Posn(101, 99), 50,
                        Color.RED),
                        new Blob(new Posn(101, 100), 50, Color.RED), new Blob(new Posn(
                            101, 101), 50, Color.RED));
  }

  // test the method onTick in the BlobWorldFun class 
  boolean testOnTick1(Tester t) {
    boolean result = true;
    for (int i = 0; i < 20; i++) {
      BlobWorldFun bwf = (BlobWorldFun) this.b1w.onTick();
      result = result && t.checkRange(bwf.blob.center.x, 95, 106)
          && t.checkRange(bwf.blob.center.y, 95, 106);
    }
    return result;
  }

  // test the method onTick when the world should end in the BlobWorldFun
  // class
  boolean testOnTick2(Tester t) {
    return
        // to test the world ending, verify the value of the lastWorld
        t.checkExpect(
            this.bwOutOfBounds.testOnTick().lastWorld,
            new WorldEnd(true, this.bwOutOfBounds
                .lastScene("Blob is outside the bounds")))
                &&

                t.checkExpect(
                    this.bwInTheCenter.testOnTick().lastWorld,
                    new WorldEnd(true, this.bwInTheCenter.makeScene()
                        .placeImageXY(
                            new TextImage(
                                "Black hole ate the blob", 13,
                                FontStyle.BOLD_ITALIC, Color.red), 100, 40)));
  }

  // test the method onTick in the BlobWorldFun class
  /*
   * boolean testOnTick2(Tester t){ return
   * 
   * // insufficient number of options ...
   * t.checkOneOf("test onTick2: randomMove", this.b1w.onTick(), new
   * BlobWorldFun(new Blob(new Posn( 99, 99), 50, Color.RED)), new
   * BlobWorldFun(new Blob(new Posn( 99, 100), 50, Color.RED)), new
   * BlobWorldFun(new Blob(new Posn( 99, 101), 50, Color.RED)), new
   * BlobWorldFun(new Blob(new Posn(100, 99), 50, Color.RED)), new
   * BlobWorldFun(new Blob(new Posn(100, 100), 50, Color.RED)), new
   * BlobWorldFun(new Blob(new Posn(100, 101), 50, Color.RED)), new
   * BlobWorldFun(new Blob(new Posn(101, 99), 50, Color.RED)), new
   * BlobWorldFun(new Blob(new Posn(101, 100), 50, Color.RED)), new
   * BlobWorldFun(new Blob(new Posn(101, 101), 50, Color.RED)) ); }
   *

  // test the method worldEnds for the class BlobWorld
  boolean testWorldEnds(Tester t) {
    return t.checkExpect(
        this.bwOutOfBounds.worldEnds(),
        new WorldEnd(true, this.bwOutOfBounds.makeScene().placeImageXY(
            new TextImage("Blob is outside the bounds", Color.red),
            100, 40)))
            &&

            t.checkExpect(
                this.bwInTheCenter.worldEnds(),
                new WorldEnd(true, this.bwInTheCenter.makeScene()
                    .placeImageXY(
                        new TextImage(
                            "Black hole ate the blob", 13,
                            FontStyle.BOLD_ITALIC, Color.red), 100, 40)))
                            &&

                            t.checkExpect(this.b1w.worldEnds(), new WorldEnd(false,
                                this.b1w.makeScene()));
  }

  /** run the animation *
  BlobWorldFun bw1 = new BlobWorldFun(new Blob(new Posn(100, 200), 20,
      Color.RED));
  BlobWorldFun w2 = new BlobWorldFun(new Blob(new Posn(100, 200), 20,
      Color.RED));
  BlobWorldFun w3 = new BlobWorldFun(new Blob(new Posn(100, 200), 20,
      Color.RED));

  // test that we can run three different animations concurrently
  // with the events directed to the correct version of the world
  /*
   * boolean runAnimation = this.w1.bigBang(200, 300, 0.3); boolean
   * runAnimation2 = this.w2.bigBang(200, 300, 0.3); boolean runAnimation3 =
   * this.w3.bigBang(200, 300, 0.3); (/
   * 
   * /** main: an alternative way of starting the world and running the tests
   *
   **/
  public static void main(String[] argv) {

    // run the tests - showing only the failed test results
    PondExamples pe = new PondExamples();
    //Tester.runReport(pe, false, false);

    // run the game
    Pond w = new Pond(new Player(), new ConsLoSwimmer().buildSwimmerList(10));
    w.bigBang(Pond.WIDTH, Pond.HEIGHT, 0.3);

    /*
     * Canvas c = new Canvas(200, 300); c.show();
     * System.out.println(" let's see: \n\n" +
     * Printer.produceString(w.makeImage())); c.drawImage(new
     * OverlayImages(new CircleImage(new Posn(50, 50), 20, Color.RED), new
     * RectangleImage(new Posn(20, 30), 40, 20, Color.BLUE)));
     */
  }

  boolean testRunGame(Tester t) {
    // run the game
    Pond w = new Pond(20);
    if (w.fishies.yeEvent()) {
      w.yeEvent = true;
    }
    if (w.fishies.kennyEvent()) {
      w.kennyEvent = true;
    }
    w.bigBang(Pond.WIDTH, Pond.HEIGHT, 0.04);
    Sound.underwater.loop();
    return true;
  }

}