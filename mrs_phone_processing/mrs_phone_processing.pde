import controlP5.*;

ControlP5 cp5;

int myColor = color(0);

int totalButtons;
String[] buttonPaths = new String[12];
String pathRoot;

void setup() {
  size(1080,1920);
  cp5 = new ControlP5(this);
 // pathRoot =  "";
  buttonPaths[0] = "0_dialer_icon.png";
  buttonPaths[1] =  "1_dialer_icon.png";
  buttonPaths[2] =  "2_dialer_icon.png";
  buttonPaths[3] =  "3_dialer_icon.png";
  buttonPaths[4] =  "4_dialer_icon.png";
  buttonPaths[5] =  "5_dialer_icon.png";
  buttonPaths[6] =  "6_dialer_icon.png";
  buttonPaths[7] =  "7_dialer_icon.png";
  buttonPaths[8] =  "8_dialer_icon.png";
  buttonPaths[9] =  "9_dialer_icon.png";
  buttonPaths[10] =  "star_dialer_icon.png";
  buttonPaths[11] =  "hash_dialer_icon.png";
  

  cp5.addButton("button1")
     .setPosition(20,200)
     .setImages(loadImage(buttonPaths[0]),loadImage(buttonPaths[1]),loadImage(buttonPaths[1]))
     .updateSize();
     
    cp5.addButton("button2")
     .setPosition(120,200)
     .setImages(loadImage(buttonPaths[1]),loadImage(buttonPaths[2]),loadImage(buttonPaths[2]))
     .updateSize();
     
     cp5.addButton("button3")
     .setPosition(220,200)
     .setImages(loadImage(buttonPaths[2]),loadImage(buttonPaths[3]),loadImage(buttonPaths[3]))
     .updateSize();
     
     cp5.addButton("button4")
     .setPosition(320,200)
     .setImages(loadImage(buttonPaths[3]),loadImage(buttonPaths[4]),loadImage(buttonPaths[4]))
     .updateSize();
     
     cp5.addButton("button5")
     .setPosition(420,200)
     .setImages(loadImage(buttonPaths[4]),loadImage(buttonPaths[5]),loadImage(buttonPaths[5]))
     .updateSize();
     
     cp5.addButton("button6")
     .setPosition(520,200)
     .setImages(loadImage(buttonPaths[5]),loadImage(buttonPaths[6]),loadImage(buttonPaths[6]))
     .updateSize();
     
     cp5.addButton("button7")
     .setPosition(620,200)
     .setImages(loadImage(buttonPaths[6]),loadImage(buttonPaths[7]),loadImage(buttonPaths[7]))
     .updateSize();
     
     cp5.addButton("button8")
     .setPosition(720,200)
     .setImages(loadImage(buttonPaths[7]),loadImage(buttonPaths[8]),loadImage(buttonPaths[8]))
     .updateSize();
     
     cp5.addButton("button9")
     .setPosition(820,200)
     .setImages(loadImage(buttonPaths[8]),loadImage(buttonPaths[9]),loadImage(buttonPaths[9]))
     .updateSize();
  
}

void draw() {
  background(myColor);
}

public void controlEvent(ControlEvent theEvent) {
  println(theEvent.getController().getName());
  
}

// function buttonA will receive changes from 
// controller with name buttonA
public void buttonA(int theValue) {
  println("a button event from buttonA: "+theValue);
  myColor = color(128);
}

