import apwidgets.*;

APMediaPlayer player;

void setup() {

  player = new APMediaPlayer(this); //create new APMediaPlayer
  player.setMediaFile("ConductorScene.wav"); //set the file (files are in data folder)
  player.start(); //start play back
  player.setLooping(false); //restart playback end reached
  player.setVolume(1.0, 1.0); //Set left and right volumes. Range is from 0.0 to 1.0

}

void draw() {

  background(0); //set black background
  text(player.getDuration(), 10, 10); //display the duration of the sound
  text(player.getCurrentPosition(), 10, 30); //display how far the sound has played

}

void keyPressed() {

  
}

//The MediaPlayer must be released when the app closes
public void onDestroy() {

  super.onDestroy(); //call onDestroy on super class
  if(player!=null) { //must be checked because or else crash when return from landscape mode
    player.release(); //release the player

  }
}
