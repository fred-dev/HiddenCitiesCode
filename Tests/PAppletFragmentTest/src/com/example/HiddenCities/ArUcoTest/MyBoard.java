package com.example.HiddenCities;

import java.util.Vector;

import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import processing.test.ArUcoTest.MyBoardConfiguration;
import processing.test.ArUcoTest.MyUtils;

public class MyBoard extends Vector<MyMarker>
{
	public static final long		serialVersionUID	= 1L;

	// fields
	public MyBoardConfiguration	conf;
	public Mat					Rvec, Tvec;
	public float					markerSizeMeters;

	// constructor
	public MyBoard()
	{
		Rvec = new Mat(3, 1, CvType.CV_64FC1);
		Tvec = new Mat(3, 1, CvType.CV_64FC1);
		markerSizeMeters = -1;
	}

	// other methods

	public Mat createBoardImage(Size gridSize, int markerSize,
			int markerDistance, int firstID, MyBoardConfiguration info)
			throws CvException
	{
		info.markersId = new int[(int) gridSize.width][(int) gridSize.height];
		int nMarkers = (int) (gridSize.width * gridSize.height);
		if (firstID + nMarkers >= 1024)
			throw new CvException(
					"creation of board implies a marker with an incorrect ID");

		int idp = 0;
		int[] data = new int[info.width * info.height];
		for (int i = 0; i < gridSize.height; i++)
			for (int j = 0; j < gridSize.width; j++, idp++)
				info.markersId[i][j] = firstID + idp; //number in the range [0,1023]
		info.markerSizePix = markerSize;
		info.markerDistancePix = markerDistance;

		int sizeY = (int) gridSize.height * markerSize
				+ ((int) gridSize.height - 1) * markerDistance;
		int sizeX = (int) gridSize.width * markerSize
				+ ((int) gridSize.width - 1) * markerDistance;
		Mat tableImage = new Mat(sizeY, sizeX, CvType.CV_8UC1);
		tableImage.setTo(new Scalar(255));

		for (int y = 0; y < gridSize.height; y++)
			for (int x = 0; x < gridSize.width; x++) {
				Mat subrect = tableImage.submat(x
						* (markerDistance + markerSize), x
						* (markerDistance + markerSize) + markerSize, y
						* (markerDistance + markerSize), y
						* (markerDistance + markerSize) + markerSize);
				Mat marker = MyMarker.createMarkerImage(data[y
						* ((int) gridSize.height) + x], markerSize);
				marker.copyTo(subrect);
			}

		return tableImage;
	}

//	public void draw3dAxis(Mat frame, MyCameraParameters cp, Scalar color)
//	{
//		MyUtils.draw3dAxis(frame, cp, color, 2 * this.get(0).ssize, Rvec,
//				Tvec);
//	}
}