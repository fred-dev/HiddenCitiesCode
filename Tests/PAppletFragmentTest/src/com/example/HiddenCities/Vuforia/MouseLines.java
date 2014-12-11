package com.example.HiddenCities;

import processing.core.*;


 class MouseLines extends PApplet {
	
	public void setup() {
		orientation(PORTRAIT);
		background(255);
		smooth();
		println("Mouse Lines Draw");
	}
	
	public void draw(){
		if (mousePressed){
			stroke(0,100);
			float rad = dist(mouseX, mouseY, pmouseX, pmouseY);
			strokeWeight(random(1,4));
			translate(mouseX, mouseY);
			rotate(atan2(mouseX-pmouseX, mouseY-pmouseY));
			line(-rad/2, 0, rad/2, 0);
		}
//		println("Mouse Lines Draw. FPS= " + frameRate);
	}
	
	public int sketchWidth() {
		return displayWidth;
	}
	
	public int sketchHeight() {
		return displayHeight;
	}
	
	public void onStop(){
		super.onStop();
		onDestroy();
	}
	
//	public String sketchRenderer(){
//		return P3D;
//	}

}
