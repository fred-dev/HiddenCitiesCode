package com.example.HiddenCities;

import processing.core.*;


public class MouseCircle extends PApplet {
	
	public void setup() {
		orientation(PORTRAIT);
		background(0);
		smooth();
		println("MouseCircle Setup");
			
	}
	
	public void draw(){
		if (mousePressed){
			noStroke();
			fill(255,100);
			float rad = dist(mouseX, mouseY, pmouseX, pmouseY);
			ellipse(mouseX, mouseY, rad, rad);
		}
//		println("Mouse Circle Draw. FPS= " + frameRate);
	}
	
	public void onStop(){
		super.onStop();
		onDestroy();
	}
	
	public int sketchWidth() {
		return displayWidth;
	}
	
	public int sketchHeight() {
		return displayHeight;
	}
	
	public String sketchRenderer(){
		return P3D;
	}

}
