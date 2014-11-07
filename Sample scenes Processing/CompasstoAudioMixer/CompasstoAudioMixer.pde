import apwidgets.*;

CompassManager compass;
float direction;
float directionNorm;
float panner;
APMediaPlayer  snd1;

void setup() {
  compass = new CompassManager(this);
  snd1 = new APMediaPlayer(this); //create new APMediaPlayer
  snd1.setMediaFile("track1.mp3"); //set the file (files are in data folder)
 
  snd1.setLooping(false); //restart playback end reached
  snd1.setVolume(0, 0); //Set left and right volumes. Range is from 0.0 to 1.0
}


void pause() {
  if (compass != null) compass.pause();
}


void resume() {
  if (compass != null) compass.resume();
}


void draw() {
  textSize(20);
 // background(map(directionNorm,0,2,0,255),map(directionNorm,0,2,0,255),map(directionNorm,0,2,0,255));
 
 fill(panner,125,100);
rect(0, 0, width, height);
 text("Accelerometer :" + "\n" 
    + "x: " + nfp(directionNorm
    , 1, 2) + "\n",  20, 0, width, height);
   
}
public void mousePressed() { 
snd1.start();
}

void directionEvent(float newDirection) {
  direction = newDirection;
  
 directionNorm = map(direction,-6.28,0,0,2);
 panner= map(direction,-6.28,0,0,255);
    
  if(directionNorm <1){
    snd1.setVolume(directionNorm,1-directionNorm);
  }
  if(directionNorm> 1){
    snd1.setVolume(2-directionNorm,directionNorm-1);
  }
   
  
}
