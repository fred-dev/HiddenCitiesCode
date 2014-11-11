
   import apwidgets.*; 

APVideoView videoView; 
APWidgetContainer container; 

void setup()
{
        container = new APWidgetContainer(this); //create a new widget container
        videoView = new APVideoView(10, 50, 240, 180, true); //create a new video view, without media controller
        //videoView = new APVideoView(false); //create a new video view that fills the screen, without a media controller
        //videoView = new APVideoView(); //create a new video view that fills the screen, with a media controller
        videoView.setVideoPath("/storage/emulated/0/DCIM/Camera/transit.mov"); //specify the path to the video file
        container.addWidget(videoView); //place the video view in the container
        videoView.start(); //start playing the video
        videoView.setLooping(true); //restart the video when the end of the file is reached


}

void draw()
{
  background(0); //black background
  text(videoView.getDuration(), 10, 10); //display the length of the video in milliseconds
  text(videoView.getCurrentPosition(), 10, 30); //visa hur långt videon spelats 
  

}
void keyPressed() {
  if (key == CODED) {
    if (keyCode == LEFT) {
      videoView.seekTo(0); //"rewind"
    }
    else if (keyCode == RIGHT) {
      videoView.start(); //start playing video file
    }
    else if (keyCode == DPAD) {
      videoView.pause(); //pause the playing of the video file
    }
    else if (keyCode == DOWN) {
      videoView.setVideoPath("sdcard/VIDEO0001.3gp"); //switch to other video file
    }
  }
}
