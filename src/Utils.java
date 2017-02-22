//Assignment 5
//lindsay aaron
//aaronlindsay
//lei bowen
//bowenleis

import java.awt.Color;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.nio.file.*;

import tester.*;
import javalib.funworld.*;
import javalib.worldimages.*;

// utility class
class Utils {

  // setter method for constructor abstraction
  void setFish(Fish f) {
    // places fish
    // in the middle 80% of y values, and either:
    // -- to the left of the pond
    // -- to the right of the pond
    int x = new Utils().chooseInt(- (Pond.WIDTH * 0.1),
        Pond.WIDTH + (Pond.WIDTH * 0.1));
    int y = (int) ((Math.random() * Pond.HEIGHT * 0.8) + (Pond.HEIGHT * 0.1));
    Posn p = new Posn(x, y);
    f.posn = p;

    // dy is random & subtle
    // dx is:
    // -- leftward if fish is on the right
    // -- rightward if fish is on the left
    int dx = (int) ((Math.random() * Pond.WIDTH * 0.006) + (Pond.WIDTH * 0.001));
    int dy = (int) (Math.random() * Pond.HEIGHT * 0.002);
    Vel v;
    if (f.posn.x < 0) {
      v = new Vel(dx, dy);
    } 
    else {
      v = new Vel(-dx, dy);  
    }
    f.vel = v;

    // size is random, scaled to size of pond
    int s = new Utils().randSize();
    f.size = s;

    // color is one of: yellow, magenta, cyan
    // special case, color is one of: black, white
    // translates to Kanye event, Kendrick event, respectively
    if (f.size > ((int) (Math.min(Pond.HEIGHT, Pond.WIDTH) * 0.49))) {
      int rand = chooseInt(0, 1);
      if (rand == 0) {
        f.col = Color.BLACK;
      }
      else {
        f.col = Color.WHITE;
      }
    }
    else {
      f.col = new Utils().randColor();
    }
  }

  // modulus operator
  int modulus(int n, int mod) {
    if (n >= 0) {
      return n % mod;
    }
    else {
      return modulus((mod + n), mod);
    }
  }

  // calculate angle
  double calcAngle(double y, double x) {
    if (x != 0) {
      return Math.atan((y / x));
    }
    else {
      if (y > 0) {
        return Math.PI / 2;
      }
      else if (y < 0) {
        return 3 * Math.PI / 2;
      }
      else {
        return 0;
      }
    }
  }

  // change acceleration depending on size
  double inertia(int size) {
    return Pond.WIDTH / Math.pow(size, 1.5);
  }

  // scale and rotate the image appropriately
  WorldImage modifyImage(String s, ASwimmer sw) {
    // constants for computation
    Vel v = sw.vel;
    WorldImage img = getImage(s, v);
    double scale = (sw.size / Pond.RED_FISH.getHeight());
    double a = calcAngle(v.dy, v.dx);
    double deg = (360 * (a / (2 * Math.PI)));

    return new ScaleImage(new RotateImage(img, deg), scale);
  }

  WorldImage getImage(String s, Vel v) {
    boolean posdx = (v.dx >= 0);
    WorldImage img;

    if (s.equals("magenta")) {
      if (posdx) {
        img = Pond.MAG_FISH;
      } else {
        img = Pond.MAG_FISH_FLIP;
      }
    }
    else if (s.equals("yellow")) {
      if (posdx) {
        img = Pond.YLW_FISH;
      } else {
        img = Pond.YLW_FISH_FLIP;
      }
    }
    else if (s.equals("blue")) {
      if (posdx) {
        img = Pond.BLU_FISH;
      } else {
        img = Pond.BLU_FISH_FLIP;
      }
    }
    else if (s.equals("ye")) {
      if (posdx) {
        img = Pond.YE;
      } else {
        img = Pond.YE_FLIP;
      }
    }
    else if (s.equals("kenny")) {
      if (posdx) {
        img = Pond.KENNY;
      } else {
        img = Pond.KENNY_FLIP;
      }
    }
    else {
      if (posdx) {
        img = Pond.YL;
      } else {
        img = Pond.YL_FLIP;
      }
    }
    
    return img;
  }


  // 'GIMME SOMETHING RANDOM'S:

  // randomly chooses one of two given integers
  int chooseInt(int a, int b) {
    double rand = Math.random();
    if (rand < 0.5) {
      return a;
    }
    else {
      return b;
    }
  }

  // randomly chooses one of two given doubles, converts choice to int
  int chooseInt(double a, double b) {
    double rand = Math.random();
    if (rand < 0.5) {
      return (int) Math.floor(a);
    }
    else {
      return (int) Math.floor(b);
    }
  }

  // return random size, scaled to size of pond
  int randSize() {
    double rand = Math.random() * Math.random() * Math.random();
    double rand2 = Math.random() * rand;
    if ((rand * 10) > 6) {
      return (int) (Math.min(Pond.HEIGHT, Pond.WIDTH) * 0.5);
    }
    else {
      return (int) ((rand2 * Math.min(Pond.HEIGHT, Pond.WIDTH) * 0.44)
          + Math.min(Pond.HEIGHT, Pond.WIDTH) * 0.01);
    }
  }

  // return a random color, one of: yellow, magenta, cyan
  Color randColor() {
    int rand = (int) (Math.random() * 3);
    if (rand == 0) {
      return Color.YELLOW;
    }
    else if (rand == 1) {
      return Color.MAGENTA;
    }
    else {
      return Color.CYAN;
    }
  }
}

// to represent position
class Posn {
  int x;
  int y;

  Posn(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // move this posn according to the given velocity
  Posn move(Vel vel) {
    return new Posn(this.x + vel.dx, this.y + vel.dy);
  }

  // is this posn the same as the given?
  boolean isSameAs(Posn that) {
    return (this.x == that.x) && (this.y == that.y);
  }
}

// to represent velocity
class Vel {
  int dx;
  int dy;

  Vel(int dx, int dy) {
    this.dx = dx;
    this.dy = dy;
  }

  Vel(double dx, double dy) {
    this.dx = (int) dx;
    this.dy = (int) dy;
  }

  // accelerate this velocity in the given direction by the given amount
  Vel accel(String dir, int dV) {
    if (dir.equals("right")) {
      return new Vel(this.dx + dV, this.dy);
    }
    else if (dir.equals("left")) {
      return new Vel(this.dx - dV, this.dy);
    }
    else if (dir.equals("up")) {
      return new Vel(this.dx, this.dy - dV);
    }
    else if (dir.equals("down")) {
      return new Vel(this.dx, this.dy + dV);
    }
    else {
      return this;
    }
  }

  // apply drag to this velocity
  Vel drag() {
    if (this.dx > 0) {
      if (this.dy > 0) {
        return new Vel(this.dx - 1, this.dy - 1);
      }
      else if (this.dy < 0) {
        return new Vel(this.dx - 1, this.dy + 1);
      }
      else {
        return new Vel(this.dx - 1, this.dy);
      }
    }
    else if (this.dx < 0) {
      if (this.dy > 0) {
        return new Vel(this.dx + 1, this.dy - 1);
      }
      else if (this.dy < 0) {
        return new Vel(this.dx + 1, this.dy + 1);
      }
      else {
        return new Vel(this.dx + 1, this.dy);
      }
    }
    else {
      if (this.dy > 0) {
        return new Vel(this.dx, this.dy - 1);
      }
      else if (this.dy < 0) {
        return new Vel(this.dx, this.dy + 1);
      }
      else {
        return new Vel(this.dx, this.dy);
      }
    }
  }

  // apply wobble to this velocity
  Vel wobble() {
    double rand = Math.random() * 1000;
    if (rand >= 996) {
      return new Vel(this.dx, this.dy + 1);
    }
    else if (rand <= 4) {
      return new Vel(this.dx, this.dy - 1);
    }
    else {
      return this;
    }
  }


  // is this posn the same as the given?
  boolean isSameAs(Vel that) {
    return (this.dx == that.dx) && (this.dy == that.dy);
  }
}