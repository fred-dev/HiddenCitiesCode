package com.example.HiddenCities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import es.ava.aruco.exceptions.CPException;

/**
 * Class to carry out a board detection. Its only method is detect.
 * 
 * @author Rafael Ortega
 * 
 */
public class MyBoardDetector
{

	/**
	 * Determines whether a set of markers constitutes a board or not.
	 * 
	 * @param detectedMyMarkers
	 *            the markers that possibly are inside a board
	 * @param conf
	 *            the configuration of the board we are looking for
	 * @param bDetected
	 *            the board in case it is detected
	 * @param cp
	 *            the camera parameters for extrinsic parameters
	 * @param markerSizeMeters
	 *            the size of each marker
	 * @return a number representing how likely the markers given are actually a
	 *         board the bigger this number is the higher probability of having
	 *         found the board. This number variates from 0 to 1.
	 * @throws CvException
	 */
	public float detect(Vector<MyMarker> detectedMyMarkers,
			MyBoardConfiguration conf, MyBoard bDetected, MyCameraParameters cp,
			float markerSizeMeters) throws CvException
	{
		bDetected.clear();
		// find among the detected markers those who belong to the board configuration
		int height = conf.height;
		int width = conf.width;
		int[][] detected = new int[width][height];// indices of the markers in the vector detectedMyMarkers
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				detected[i][j] = -1;
		int nMarkInBoard = 0;// number of detected markers
		for (int i = 0; i < detectedMyMarkers.size(); i++) {
			boolean found = false;
			int id = detectedMyMarkers.get(i).id;
			// find it
			for (int j = 0; j < height && !found; j++)
				for (int k = 0; k < width && !found; k++)
					if (conf.markersId[j][k] == id) {
						detected[j][k] = i;
						nMarkInBoard++;
						found = true;
						bDetected.add(detectedMyMarkers.get(i));
						if (markerSizeMeters > 0)
							bDetected.lastElement().ssize = markerSizeMeters;
					}
		}
		bDetected.conf = conf;
		if (markerSizeMeters != -1)
			bDetected.markerSizeMeters = markerSizeMeters;
		// calculate extrinsics
		if (cp.isValid() && markerSizeMeters > 0
				&& detectedMyMarkers.size() > 1) {
			// create necessary matrix
			List<Point3> objPoints = new ArrayList<Point3>();
			List<Point> imgPoints = new ArrayList<Point>();
			// size in meters of the distance between markers
			float markerDistanceMeters = (conf.markerDistancePix)
					* markerSizeMeters / (conf.markerSizePix);

			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++) {
					if (detected[y][x] != -1) {
						imgPoints.addAll(detectedMyMarkers.get(detected[y][x])
								.toList());

						// translation to put the origin in the center
						float TX = -(((detected.length - 1)
								* (markerDistanceMeters + markerSizeMeters) + markerSizeMeters) / 2);
						float TY = -(((detected.length - 1)
								* (markerDistanceMeters + markerSizeMeters) + markerSizeMeters) / 2);
						//points in real reference system. We see the center in the bottom-left corner
						float AY = x
								* (markerDistanceMeters + markerSizeMeters)
								+ TY;
						float AX = y
								* (markerDistanceMeters + markerSizeMeters)
								+ TX;
						objPoints.add(new Point3(AX, AY, 0));
						objPoints.add(new Point3(AX, AY + markerSizeMeters, 0));
						objPoints.add(new Point3(AX + markerSizeMeters, AY
								+ markerSizeMeters, 0));
						objPoints.add(new Point3(AX + markerSizeMeters, AY, 0));
					}
				}
			// TODO get the opencv calls out of the loops
			MatOfPoint3f objPointsMat = new MatOfPoint3f();
			objPointsMat.fromList(objPoints);
			MatOfPoint2f imgPointsMat = new MatOfPoint2f();
			imgPointsMat.fromList(imgPoints);
			Calib3d.solvePnP(objPointsMat, imgPointsMat, cp.getCameraMatrix(),
					cp.getDistCoeff(), bDetected.Rvec, bDetected.Tvec);
			//	        Utils.rotateXAxis(bDetected.Rvec); rotated later, in getModelViewMatrix
		}
		return ((float) nMarkInBoard / (float) (conf.width * conf.height));
	}
	
	
	

}
