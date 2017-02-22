import java.awt.Color;
import java.io.File;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import tester.*;
import javalib.funworld.*;
import javalib.worldimages.*;

public class Sound {

  // bools
  public boolean isPlaying = false;

  private Clip clip;
  public Sound(String fileName, float gain) {
    // specify the sound to play
    // (assuming the sound can be played by the audio system)
    // from a wave File
    try {
      File file = new File(fileName);
      if (file.exists()) {
        AudioInputStream sound = AudioSystem.getAudioInputStream(file);
        // load the sound into memory (a Clip)
        clip = AudioSystem.getClip();
        
        clip.open(sound);
        
        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        volume.setValue(gain);
      }
      else {
        throw new RuntimeException("Sound: file not found: " + fileName);
      }
    }
    catch (Exception e) {}
    /*
    catch (MalformedURLException e) {
      e.printStackTrace();
      throw new RuntimeException("Sound: Malformed URL: " + e);
    }
    catch (UnsupportedAudioFileException e) {
      e.printStackTrace();
      throw new RuntimeException("Sound: Unsupported Audio File: " + e);
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Sound: Input/Output Error: " + e);
    }
    catch (LineUnavailableException e) {
      e.printStackTrace();
      throw new RuntimeException("Sound: Line Unavailable Exception Error: " + e);
    }
     */
  }

  // play, stop, loop the sound clip
  public void play(){
    clip.setFramePosition(0);  // Must always rewind!
    clip.start();
    isPlaying = true;
  }
  public void loop(){
    clip.loop(Clip.LOOP_CONTINUOUSLY);
    isPlaying = true;
  }
  public void stop(){
    clip.stop();
    isPlaying = false;
  }

  // sounds
  public static final Sound gatorade = new Sound("gatorade.wav", 0);
  public static final Sound feedback = new Sound("05 Feedback.wav", 0);
  public static final Sound feedbackLoop = new Sound("feedbackLoop.wav", 0);
  public static final Sound backseat = new Sound("03 Backstreet Freestyle.wav", 0);
  public static final Sound backseatLoop = new Sound("backseatLoop.wav", 0);
  public static final Sound underwater = new Sound("underwaterSounds.wav", 0);
  public static final Sound chomp = new Sound("chomp.wav", 0);
  public static final Sound soundOfSilence = new Sound("soundOfSilence.wav", 0);
}