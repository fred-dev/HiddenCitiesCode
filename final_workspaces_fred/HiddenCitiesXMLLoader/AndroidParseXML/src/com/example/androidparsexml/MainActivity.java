package com.example.androidparsexml;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.app.Activity;

public class MainActivity extends Activity {

	public static final String LOG_TAG = "MainActivity.java";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {

			// parse our XML
			new parseXmlAsync().execute();

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * @We are using an AsyncTask to avoid
	 * android.os.NetworkOnMainThreadException when parsing from a URL
	 * 
	 * @If you don't know a thing about AsynTasks, there are a lot of excellent
	 * tutorial out there, see this thread
	 */
	private class parseXmlAsync extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... strings) {

			try {

				/*
				 * You may change the value of x to try different sources of XML
				 * 
				 * @1 = XML from SD Card
				 * 
				 * @2 = XML from URL
				 * 
				 * @3 = XML from assets folder
				 */
				int x = 1;

				// initialize our input source variable
				InputSource inputSource = null;

				// XML from sdcard
				if (x == 1) {
					
					// make sure sample.xml is in your root SD card directory
					File xmlFile = new File(
							Environment.getExternalStorageDirectory()
									+ "/hiddenCities/HCsettings.xml");
					FileInputStream xmlFileInputStream = new FileInputStream(
							xmlFile);
					inputSource = new InputSource(xmlFileInputStream);
				}

				// XML from URL
				else if (x == 2) {
					// specify a URL
					// make sure you are connected to the internet
					URL url = new URL(
							"http://demo.codeofaninja.com/AndroidXml/sample.xml");
					inputSource = new InputSource(url.openStream());
				}

				// XML from assets folder
				else if (x == 3) {
					inputSource = new InputSource(getAssets()
							.open("sample.xml"));
				}

				// instantiate SAX parser
				SAXParserFactory saxParserFactory = SAXParserFactory
						.newInstance();
				SAXParser saxParser = saxParserFactory.newSAXParser();

				// get the XML reader
				XMLReader xmlReader = saxParser.getXMLReader();

				// prepare and set the XML content or data handler before
				// parsing
				XmlContentHandler xmlContentHandler = new XmlContentHandler();
				xmlReader.setContentHandler(xmlContentHandler);

				// parse the XML input source
				xmlReader.parse(inputSource);

				// put the parsed data to a List
				List<ParsedDataSet> parsedDataSet = xmlContentHandler
						.getParsedData();

				// we'll use an iterator so we can loop through the data
				Iterator<ParsedDataSet> i = parsedDataSet.iterator();
				ParsedDataSet dataItem;

				while (i.hasNext()) {

					dataItem = (ParsedDataSet) i.next();

					/*
					 * parentTag can also represent the main type of data, in
					 * our example, "Owners" and "Dogs"
					 */
					String parentTag = dataItem.getParentTag();
					Log.v(LOG_TAG, "parentTag: " + parentTag);

					if (parentTag.equals("Markers")) {
						Log.v(LOG_TAG, "Latitude: " + dataItem.getLat());
						Log.v(LOG_TAG, "Longditude: " + dataItem.getLong());
					}

					else if (parentTag.equals("Waypoints")) {
						Log.v(LOG_TAG, "Latitude: " + dataItem.getLat());
						Log.v(LOG_TAG, "Longditude: " + dataItem.getLong());
					}

				}

			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String lenghtOfFile) {
			// your do stuff after parsing the XML
		}
	}

}
