package com.example.androidparsexml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class XmlContentHandler extends DefaultHandler {

	private static final String LOG_TAG = "XmlContentHandler";

	// used to track of what tags are we
	private boolean inMarkers = false;
	private boolean inWaypoints = false;

	// accumulate the values
	private StringBuilder mStringBuilder = new StringBuilder();

	// new object
	private ParsedDataSet mParsedDataSet = new ParsedDataSet();

	// the list of data
	private List<ParsedDataSet> mParsedDataSetList = new ArrayList<ParsedDataSet>();

	/*
	 * Called when parsed data is requested.
	 */
	public List<ParsedDataSet> getParsedData() {
		Log.v(LOG_TAG, "Returning mParsedDataSetList");
		return this.mParsedDataSetList;
	}

	// Methods below are built in, we just have to do the tweaks.

	/*
	 * @Receive notification of the start of an element.
	 * 
	 * @Called in opening tags such as <Owner>
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {

		if (localName.equals("Markers")) {
			// meaning new data object will be made
			this.mParsedDataSet = new ParsedDataSet();
			this.inMarkers = true;
		}

		else if (localName.equals("Waypoints")) {
			this.mParsedDataSet = new ParsedDataSet();
			this.inWaypoints = true;
		}

	}

	/*
	 * @Receive notification of the end of an element.
	 * 
	 * @Called in end tags such as </Owner>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {

		// Owners
		if (this.inMarkers == true && localName.equals("Marker")) {
			this.mParsedDataSetList.add(mParsedDataSet);
			mParsedDataSet.setParentTag("Markers");
			this.inMarkers = false;
		}


		// Dogs
		if (this.inWaypoints == true && localName.equals("Waypoint")) {
			this.mParsedDataSetList.add(mParsedDataSet);
			mParsedDataSet.setParentTag("Waypoints");
			this.inWaypoints = false;
		}

	}

	/*
	 * @Receive notification of character data inside an element.
	 * 
	 * @Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		// append the value to our string builder
		mStringBuilder.append(ch, start, length);
	}
}