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

// to represent fish
interface ILoSwimmer {

  // return the images of these fish
  WorldScene getImages(Pond w);

  // getImages helper
  WorldScene getImagesHelp(Pond w, ASwimmer a);

  // moves the fish in this list of fish
  ILoSwimmer move();

  // can any of these fish eat the given swimmer?
  boolean canEat(ASwimmer that);

  // the given swimmer attempts to eat these fish
  Player areEatenBy(Player p);

  // is the given swimmer bigger than all of these fish?
  boolean areSmallerThan(ASwimmer that);

  // remove out of bounds swimmers, eaten swimmers,
  // and add new swimmers to maintain population
  ILoSwimmer checkPopulation(Player p);

  // is Ye present?
  boolean yeEvent();
  
  // is Kenny present?
  boolean kennyEvent();
}

// to represent a non-empty list of fish
class ConsLoSwimmer implements ILoSwimmer {
  ASwimmer first;
  ILoSwimmer rest;

  ConsLoSwimmer() {}

  ConsLoSwimmer(int n) {
    this.first = new Fish();
    this.rest = new ConsLoSwimmer().buildSwimmerList(n-1);
  }

  ConsLoSwimmer(ASwimmer first, ILoSwimmer rest) {
    this.first = first;
    this.rest = rest;
  }

  // build fish list to n length
  public ILoSwimmer buildSwimmerList(int n) {
    if (n > 0) {
      return new ConsLoSwimmer(new Fish(), this.buildSwimmerList(n - 1));
    }
    else {
      return new MtLoSwimmer();
    }
  }

  // place the images of the apples in this list
  public WorldScene getImages(Pond w) {
    return this.rest.getImagesHelp(w, this.first);
  }

  // placeApplesXY helper
  public WorldScene getImagesHelp(Pond w, ASwimmer a) {
    return this.rest.getImagesHelp(w, this.first)
        .placeImageXY(a.getImage(), a.posn.x, a.posn.y);
  }

  // moves the fish in this list of fish
  public ConsLoSwimmer move() {
    return new ConsLoSwimmer(this.first.move(), this.rest.move());
  }

  // do any of these fish eat the given swimmer?
  public boolean canEat(ASwimmer that) {
    return this.first.canEat(that) || this.rest.canEat(that);
  }

  // the given swimmer attempts to eat these fish
  public Player areEatenBy(Player sw) {
    if (sw.canEat(this.first)) {
      return sw.eat(this.first);
    }
    else {
      return this.rest.areEatenBy(sw);
    }
  }

  // is the given swimmer bigger than all of these fish?
  public boolean areSmallerThan(ASwimmer that) {
    return that.isBiggerThan(this.first) && this.rest.areSmallerThan(that);
  }

  // remove out of bounds swimmers and add new swimmers to maintain population
  public ConsLoSwimmer checkPopulation(Player p) {
    if (this.first.outOfBounds() || p.canEat(this.first)) {
      Pond.yeEvent = (!this.first.isYe() || this.rest.yeEvent()) && Pond.yeEvent;
      Pond.kennyEvent = (!this.first.isKenny() || this.rest.kennyEvent()) && Pond.kennyEvent;
      Fish newFish = new Fish();
      Pond.yeEvent = Pond.yeEvent || newFish.isYe();
      Pond.kennyEvent = Pond.kennyEvent || newFish.isKenny();
      return new ConsLoSwimmer(newFish, this.rest.checkPopulation(p));
    }
    else {
      return new ConsLoSwimmer(this.first, this.rest.checkPopulation(p));
    }
  }

  // is Ye present?
  public boolean yeEvent() {
    return (this.first.col.equals(Color.BLACK) || this.rest.yeEvent());
  }
  
  // is Kenny present?
  public boolean kennyEvent() {
    return (this.first.col.equals(Color.WHITE) || this.rest.kennyEvent());
  }

}

// to represent an empty list of fish
class MtLoSwimmer implements ILoSwimmer {

  MtLoSwimmer() {}

  // build fish list to n length
  public ILoSwimmer buildSwimmerList(int n) {
    if (n > 0) {
      return new ConsLoSwimmer(new Fish(), this.buildSwimmerList(n - 1));
    }
    else {
      return new MtLoSwimmer();
    }
  }

  // place the images of the apples in this list
  public WorldScene getImages(Pond w) {
    return w.getEmptyScene().placeImageXY(
        Pond.POND_BG, Pond.HALF_WIDTH, Pond.HALF_HEIGHT);
  }

  // placeApplesXY helper
  public WorldScene getImagesHelp(Pond w, ASwimmer a) {
    return w.getEmptyScene()
        .placeImageXY(Pond.POND_BG, Pond.HALF_WIDTH, Pond.HALF_HEIGHT)
        .placeImageXY(a.getImage(), a.posn.x, a.posn.y);
  }

  // moves the fish in this list of fish
  public MtLoSwimmer move() {
    return this;
  }

  // do any of these fish eat the given swimmer?
  public boolean canEat(ASwimmer that) {
    return false;
  }

  // the given swimmer attempts to eat these fish
  public Player areEatenBy(Player sw) {
    return sw;
  }

  // is the given swimmer bigger than all of these fish?
  public boolean areSmallerThan(ASwimmer that) {
    return true;
  }

  // remove out of bounds swimmers and add new swimmers to maintain population
  public MtLoSwimmer checkPopulation(Player p) {
    return this;
  }

  // is Ye present?
  public boolean yeEvent() {
    return false;
  }
  
  // is Kenny present?
  public boolean kennyEvent() {
    return false;
  }

}

