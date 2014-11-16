/*
ImageButton class

Assumes that pixels in the image with an alpha value greater than zero are pixels that are part of the main image
All fully transparent pixels do not apply for collision detection

You must create the function called "buttonEvent(String)" for this code to work
*/

class ImageButton {
  //The parent PApplet
  PApplet parent;
 
  //The image source
  PImage image;
 
  //The location and dimensions
  PVector loc;
  PVector dim;
 
  //The identifier of this ImageButton
  String id;
 
  //Whether or not the ImageButton is pressed
  boolean pressed;
 
  /*
  Creates an new ImageButton
 
  PApplet parent - use "this"
  String  path   - the path to the image file
  String  id     - the identifier of this image, used for firing press events
  */
  ImageButton(PApplet parent, String path, String id) {
    this.parent = parent;
   
    parent.registerDraw(this);
   
    image = loadImage(path);
   
    loc = new PVector(0, 0);
    dim = new PVector(image.width, image.height);
  }
 
  /*
  Creates an new ImageButton
 
  PApplet parent - use "this"
  String path   - the path to the image file
  String id     - the identifier of this image, used for firing press events
  float  x      - the x position of the ImageButton
  float  y      - the y position of the ImageButton
  */
  ImageButton(PApplet parent, String path, String id, float x, float y) {
    this.parent = parent;
   
    parent.registerDraw(this);
   
    image = loadImage(path);
   
    loc = new PVector(x, y);
    dim = new PVector(image.width, image.height);
  }
 
  /*
  Creates an new ImageButton
 
  PApplet parent - use "this"
  String path   - the path to the image file
  String id     - the identifier of this image, used for firing press events
  float  x      - the x position of the ImageButton
  float  y      - the y position of the ImageButton
  float  width  - the width of the image
  float  height - the height of the image
  */
  ImageButton(PApplet parent, String path, String id, float x, float y, float width, float height) {
    this.parent = parent;
   
    parent.registerDraw(this);
   
    image = loadImage(path);
    image.resize((int) width, (int) height);
   
    loc = new PVector(x, y);
    dim = new PVector(width, height);
  }
 
  void draw() {
    update();
    display();
  }
 
  void update() {
    if(mousePressed) {
      if(!pressed) {
        //Fire the button event
        buttonPressEvent(id);
        pressed = true;
      }
    } else pressed = false;
  }
 
  void display() {
    pushStyle();
   
    //Tint the image if it is pressed
    if(pressed) tint(204);
    //Display the image
    image(image, loc.x, loc.y);
   
    popStyle();
  }
 
  //This is the magic mouse detection part
  boolean mouseOver() {
    return alpha(image.get((int) (mouseX - loc.x), (int) (mouseY - loc.y))) > 0;
  }
}

