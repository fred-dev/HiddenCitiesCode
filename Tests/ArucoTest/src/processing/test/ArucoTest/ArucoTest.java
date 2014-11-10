package processing.test.ArucoTest;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import es.ava.aruco.*;
import org.opencv.core.*;

public class ArucoTest extends PApplet {

	BoardConfiguration mBoardConfig;
	Board mBoard;
	BoardDetector mBoardDetector;
	Mat mBoardImageMat;

	public void setup() {
		orientation(PORTRAIT);
		imageMode(CENTER);
		smooth();

		mBoardImageMat = mBoard.createBoardImage(new Size(4, 4), 80, 20, 200,
				mBoardConfig);

	}

	public void draw() {

		ellipse(mouseX, mouseY, 20, 20);

	}

	public void mousePressed() {

	}

	public void keyPressed() {

	}

	public int sketchWidth() {
		return displayWidth;
	}

	public int sketchHeight() {
		return displayHeight;
	}

	public String sketchRenderer() {
		return P3D;
	}

}
