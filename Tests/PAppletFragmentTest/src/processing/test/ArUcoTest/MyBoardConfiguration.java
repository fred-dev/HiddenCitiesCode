package processing.test.ArUcoTest;

public class MyBoardConfiguration
{
	protected int		width, height;
	protected int[][]	markersId;
	protected int		markerSizePix, markerDistancePix;

	public MyBoardConfiguration(int width, int height, int[][] markersId,
			int markerSizePix, int markerDistancePix)
	{
		super();
		this.width = width;
		this.height = height;
		this.markersId = markersId;
		this.markerSizePix = markerSizePix;
		this.markerDistancePix = markerDistancePix;
	}
}
