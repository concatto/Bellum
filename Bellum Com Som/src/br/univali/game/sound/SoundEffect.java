package br.univali.game.sound;

import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;



public enum SoundEffect {
	

    EXPLODE("./src/sounds/explosion.wav"),   
    DEAD("./src/sounds/missionfailed.wav"),         
    SHOOT("./src/sounds/shoot.wav"),
    BACKGROUND("./src/sounds/background.wav"),
    CANNON("./src/sounds/Cannon.wav"),
    SPECIALSHOOT("./src/sounds/specialBullet.wav"),
    SHIELD("./src/sounds/shield.wav"),
    LIFEUP("./src/sounds/lifeup.wav"),
    GOTSPECIAL("./src/sounds/gotSpecialBullet.wav"),
    BAT("./src/sounds/bat.wav"),
    ENEMYDEAD("./src/sounds/niceshoot.wav"),
	MENU("./src/sounds/menu.wav"),
	ENEMYSHOT("./src/sounds/enemyshot.wav");
	 
 
   public static enum Volume {
      MUTE, LOW, MEDIUM, HIGH
   }
   
   public static Volume volume = Volume.MEDIUM;
   
   private Clip clip;
   
   SoundEffect(String soundFileName) {
	   
      try {
    	 File soundFile = new File(soundFileName);
         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
         clip = AudioSystem.getClip();
         clip.open(audioInputStream);
      } catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (LineUnavailableException e) {
         e.printStackTrace();
      }
   }
   
   public void play() {
      if (volume != Volume.MUTE) {
         if (clip.isRunning())
         clip.stop();   
         clip.setFramePosition(0); 
         clip.start();    
      }
   }
   
   public void mute(){
	   if (volume != Volume.MUTE) {
		   volume = Volume.MUTE;
	   }
   }
   
   public void stop(){
	   if(clip.isRunning()){
		   clip.stop();
	   }
   }
   public void restart(){
	   if(!clip.isRunning()){
		   clip.setFramePosition(0); 
	         clip.start(); 
	   }
   }
   
   static void init() {
      values(); 
   }
}