// Assignment 5
// lindsay aaron
// aaronlindsay
// lei bowen
// bowenleis

import java.awt.Color;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.nio.file.*;

import tester.*;
import javalib.funworld.*;
import javalib.worldimages.*;

// to represent a swimmer
abstract class ASwimmer {

  Posn posn;
  Vel vel;
  int size;
  Color col;

  // default constructor
  ASwimmer() {}

  // constructor
  ASwimmer(Posn posn, Vel vel, int size, Color col) {
    this.posn = posn;
    this.vel = vel;
    this.size = size;
    this.col = col;
  }

  // produce the image of this swimmer
  WorldImage getImage() {
    if (this.col.equals(Color.MAGENTA)) {
      return new Utils().modifyImage("magenta", this);
    }
    else if (this.col.equals(Color.YELLOW)) {
      return new Utils().modifyImage("yellow", this);
    }
    else if (this.col.equals(Color.CYAN)) {
      return new Utils().modifyImage("blue", this);
    }
    else if (this.col.equals(Color.BLACK)) {
      return new Utils().modifyImage("ye", this);
    }
    else if (this.col.equals(Color.WHITE)) {
      return new Utils().modifyImage("kenny", this);
    }
    else {
      return new Utils().modifyImage("player", this);
    }
  }

  // moves this swimmer according to its velocity
  public abstract ASwimmer move();

  // is this swimmer bigger than that swimmer?
  public boolean isBiggerThan(ASwimmer that) {
    return (this.size > that.size);
  }

  // is this swimmer near that swimmer?
  public boolean isNear(ASwimmer that) {
    
    // helpful numbers
    double thisR = this.size / 2;
    double thatR = that.size / 2;
    double distX = Math.abs(this.posn.x - that.posn.x);
    double distY = Math.abs(this.posn.y - that.posn.y);
    double totalR = thisR + thatR;
    
    return (distX * distX + distY * distY) < (totalR * totalR);
        
    /*
    // helpful numbers
    int l = ((int) (Math.min(Pond.HEIGHT, Pond.WIDTH) * 0.005));
    double thisRad = (this.size / 3);
    double thatRad = (that.size / 3);
    double thisLB = this.posn.x - thisRad;
    double thisRB = this.posn.x + thisRad;
    double thisUB = this.posn.y - thisRad;
    double thisDB = this.posn.y + thisRad;
    double thatLB = that.posn.x - thatRad;
    double thatRB = that.posn.x + thatRad;
    double thatUB = that.posn.y - thatRad;
    double thatDB = that.posn.y + thatRad;

    // helpful booleans
    // true if the left and right boundaries overlap
    boolean leftRightOverlap = 
        (((thisLB <= thatLB) && (thatLB <= thisRB)) ||
            ((thisLB <= thatRB) && (thatRB <= thisRB)))
        || (((thatLB <= thisLB) && (thisLB <= thatRB)) ||
            ((thatLB <= thisRB) && (thisRB <= thatRB)));
    boolean upDownOverlap = 
        (((thisUB <= thatUB) && (thatUB <= thisDB)) ||
            ((thisUB <= thatDB) && (thatDB <= thisDB)))
        || (((thatUB <= thisUB) && (thisUB <= thatDB)) ||
            ((thatUB <= thisDB) && (thisDB <= thatDB)));

    return leftRightOverlap && upDownOverlap;
    */
  }

  // can this swimmer eat the given swimmer?
  public boolean canEat(ASwimmer that) {
    return this.isBiggerThan(that) && this.isNear(that);
  }

  // is this swimmer the same as the given?
  public boolean sameSwimmer(ASwimmer that) {
    return this.posn.isSameAs(that.posn) &&
        this.vel.isSameAs(that.vel) &&
        this.size == that.size &&
        this.col.equals(that.col);
  }

  // is this swimmer out of bounds?
  public boolean outOfBounds() {

    // boundary definitions
    int lB = (int) (- Pond.WIDTH * 0.2);
    int rB = (int) (Pond.WIDTH + Pond.WIDTH * 0.2);
    int uB = (int) (- Pond.HEIGHT * 0.2);
    int dB = (int) (Pond.HEIGHT + Pond.WIDTH * 0.2);

    return (this.posn.x < lB)
        || (this.posn.x > rB)
        || (this.posn.y < uB)
        || (this.posn.y > dB);
  }
  
  
  public boolean isYe() {
    return this.col.equals(Color.BLACK);
  }
  public boolean isKenny() {
    return this.col.equals(Color.WHITE);
  }

}

// to represent the player
class Player extends ASwimmer {

  // constructor
  Player(Posn posn, Vel vel, int size, Color col) {
    // ensures player stays within the pond
    super(new Posn((new Utils().modulus(posn.x, Pond.WIDTH)),
        new Utils().modulus(posn.y, Pond.HEIGHT)),
        vel, size, col);
  }

  Player() {
    this(new Posn(Pond.HALF_WIDTH, Pond.HALF_HEIGHT), new Vel(0, 0),
        ((int) (Math.min(Pond.HEIGHT, Pond.WIDTH) * 0.02)), Color.RED);
  }

  // moves this player according to its velocity
  public Player move() {
    return new Player(this.posn.move(this.vel), this.vel.drag(), this.size, this.col);
  }

  // moves this player according to key-press
  public Player move(String ke) {
    
    int maxVel = 20; //Math.min(Pond.WIDTH, Pond.HEIGHT) / 40;
    int negMaxVel = -20;
    
    if (ke.equals("right")) {
      return new Player(this.posn, new Vel(Math.min(this.vel.dx + 6, maxVel), this.vel.dy),
          this.size, this.col);
    }
    else if (ke.equals("left")) {
      return new Player(this.posn, new Vel(Math.max(this.vel.dx - 6, negMaxVel), this.vel.dy), 
          this.size, this.col);
    }
    else if (ke.equals("up")) {
      return new Player(this.posn, new Vel(this.vel.dx, Math.max(this.vel.dy - 6, negMaxVel)),
          this.size, this.col);
    }
    else if (ke.equals("down")) {
      return new Player(this.posn, new Vel(this.vel.dx, Math.min(this.vel.dy + 6, maxVel)),
          this.size, this.col);
    }
    else {
      return this;
    }
  }

  // attempt to eat the given swimmer
  public Player eat(ASwimmer that) {
    Sound chomp = new Sound("chomp.wav", -10 + (this.size / 10));
    chomp.play();
    if (that.col.equals(Color.BLACK) || that.col.equals(Color.WHITE)) {
      Pond.newRapKing = true;
    }
    return new Player(this.posn, this.vel,
          ((int) (this.size + 0.5 * Math.sqrt(that.size))), this.col);
  }

  // attempt to eat the swimmer in this ILoSwimmer
  public Player eat(ILoSwimmer fishies) {
    return fishies.areEatenBy(this);
  }

}

// to represent a fish
class Fish extends ASwimmer {

  // regular constructor
  Fish(Posn posn, Vel vel, int size, Color col) {
    super(posn, vel, size, col);
  }

  // constructs a fish with random values (with some QoL constraints)
  Fish() {
    super();
    new Utils().setFish(this);
  }

  // moves this fish according to its velocity
  public Fish move() {
    return new Fish(this.posn.move(this.vel), this.vel, this.size, this.col);
  }

  public Fish eat(ASwimmer that) {
    return new Fish(this.posn, this.vel,
        ((int) (this.size + Math.sqrt(that.size))), this.col);
  }
}

