package com.hiddenCities.main;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler
{
       
           
          List<XmlValuesModel> markerList=null;
          List<XmlValuesModel> waypointList=null;
          List<XmlValuesModel> idList=null;
          List<XmlValuesModel> networkList=null;
          List<XmlValuesModel> messageList=null;
           
          // string builder acts as a buffer
          StringBuilder builder;
       
          XmlValuesModel markerValues=null;
          XmlValuesModel waypointValues=null;
          XmlValuesModel idValues = null;
          XmlValuesModel networkValues = null;
          XmlValuesModel messageValues = null;
           
           
           // Initialize the arraylist
           // @throws SAXException
            
          @Override
          public void startDocument() throws SAXException {
               
        	  markerList = new ArrayList<XmlValuesModel>();
        	  waypointList = new ArrayList<XmlValuesModel>();
        	  idList = new ArrayList<XmlValuesModel>();
        	  networkList= new ArrayList<XmlValuesModel>();
        	  messageList= new ArrayList<XmlValuesModel>();
          }
       
            
          @Override
          public void startElement(String uri, String localName, String qName,
                  Attributes attributes) throws SAXException {
               
              /****  When New XML Node initiating to parse this function called *****/
               
              // Create StringBuilder object to store xml node value
              builder=new StringBuilder();
               
              if(localName.equals("users")){
 
              }
              else if(localName.equals("user")){

            	  idValues= new XmlValuesModel();
              }
       
              else if(localName.equals("markers")){
                   
              }
              else if(localName.equals("marker")){
  
            	  markerValues = new XmlValuesModel();
              }
              else if(localName.equals("waypoints")){
                  
              }
              else if(localName.equals("waypoint")){

            	  waypointValues = new XmlValuesModel();
              }
              if(localName.equals("networkInfos")){
            	  
              }
              else if(localName.equals("networkInfo")){

            	  networkValues= new XmlValuesModel();
              }
              if(localName.equals("messageStrings")){
            	  
              }
              else if(localName.equals("messageString")){

            	  messageValues= new XmlValuesModel();
              }
          }
           
           
           
           // Finished reading the login tag, add it to arraylist
           // @param uri
           // @param localName
           // @param qName
           // @throws SAXException
            
          @Override
          public void endElement(String uri, String localName, String qName)
                  throws SAXException {
              
               
              if(localName.equals("marker")){

                  /** finished reading a job xml node, add it to the arraylist **/
            	  markerList.add( markerValues );
                   
              }
              if(localName.equals("waypoint")){

                  /** finished reading a job xml node, add it to the arraylist **/
            	  waypointList.add( waypointValues );
                   
              }
              if(localName.equals("networkInfo")){

                  /** finished reading a job xml node, add it to the arraylist **/
            	  networkList.add( networkValues );
                   
              }
              if(localName.equals("messageString")){

                  /** finished reading a job xml node, add it to the arraylist **/
            	  messageList.add( messageValues );
                   
              }
       
              else  if(localName.equalsIgnoreCase("user")){
            	  idList.add( idValues );
              }
              
              
              else  if(localName.equalsIgnoreCase("useremail")){
            	  idValues.setUserEmail(builder.toString());
              }
              else  if(localName.equalsIgnoreCase("username")){
            	  idValues.setUsername(builder.toString());
              }
              
              else  if(localName.equalsIgnoreCase("markerId")){  

            	  markerValues.setMarkerId(Integer.parseInt(builder.toString()));
              }
              else if(localName.equalsIgnoreCase("markerLat")){

            	  markerValues.setMarkerLat(Float.parseFloat(builder.toString()));
              }
              else if(localName.equalsIgnoreCase("markerLong")){
            	  markerValues.setMarkerLong(Float.parseFloat(builder.toString()));
              }
              
              else  if(localName.equalsIgnoreCase("waypointId")){  

            	  waypointValues.setWayPointId(Integer.parseInt(builder.toString()));
              }
              else if(localName.equalsIgnoreCase("waypointLat")){

            	  waypointValues.setWayPointLat(Float.parseFloat(builder.toString()));
              }
              else if(localName.equalsIgnoreCase("waypointLong")){
            	  waypointValues.setWayPointLong(Float.parseFloat(builder.toString()));
              }
              
              
              else  if(localName.equalsIgnoreCase("ftpAddress")){  

            	  networkValues.setFtpAddress(builder.toString());
              }
              else if(localName.equalsIgnoreCase("ftpUser")){

            	  networkValues.setFtpUser(builder.toString());
              }
              else if(localName.equalsIgnoreCase("ftpPassword")){
            	  networkValues.setFtpPassword(builder.toString());
              }
              
              else if(localName.equalsIgnoreCase("ftpRemotePath")){
            	  networkValues.setFtpRemotePath(builder.toString());
              }
              
              else if(localName.equalsIgnoreCase("webSocketAddress")){
            	  networkValues.setWebSocketAdress(builder.toString());
              }
              
              else if(localName.equalsIgnoreCase("webSocketUser")){
            	  networkValues.setWebSocketUser(builder.toString());
              }
              
              else if(localName.equalsIgnoreCase("welcomeString")){
            	  messageValues.setWelcomeString(builder.toString());
              }
              
              else if(localName.equalsIgnoreCase("emailString")){
            	  messageValues.setEmailString(builder.toString());
              }
              else if(localName.equalsIgnoreCase("usernameString")){
            	  messageValues.setUsernameString(builder.toString());
              }
              
              else if(localName.equalsIgnoreCase("instructionStringOne")){
            	  messageValues.setInstructionStringOne(builder.toString());
              }
              
              else if(localName.equalsIgnoreCase("instructionStringTwo")){
            	  messageValues.setInstructionStringTwo(builder.toString());
              }
            
              else if(localName.equalsIgnoreCase("instructionStringThree")){
            	  messageValues.setInstructionStringThree(builder.toString());
              }
              
              else if(localName.equalsIgnoreCase("instructionStringFour")){
            	  messageValues.setInstructionStringFour(builder.toString());
              }
              else if(localName.equalsIgnoreCase("instructionStringFive")){
            	  messageValues.setInstructionStringFive(builder.toString());
              }
              
              else if(localName.equalsIgnoreCase("instructionStringSix")){
            	  messageValues.setInstructionStringSix(builder.toString());
              }
              
              else if(localName.equalsIgnoreCase("instructionStringSeven")){
            	  messageValues.setInstructionStringSeven(builder.toString());
              }
            
              else if(localName.equalsIgnoreCase("errorStringOne")){
            	  messageValues.setErrorStringOne(builder.toString());
              }
              else if(localName.equalsIgnoreCase("errorStringTwo")){
            	  messageValues.setErrorStringTwo(builder.toString());
              }
              else if(localName.equalsIgnoreCase("errorStringThree")){
            	  messageValues.setErrorStringThree(builder.toString());
              }
              else if(localName.equalsIgnoreCase("errorStringFour")){
            	  messageValues.setErrorStringFour(builder.toString());
              }	
              
          
            
            

               
           // Log.i("parse",localName.toString()+"========="+builder.toString());
          }
       
          
           // Read the value of each xml NODE
           // @param ch
           // @param start
           // @param length
           // @throws SAXException
            
          @Override
          public void characters(char[] ch, int start, int length)
                  throws SAXException {
                   
              /******  Read the characters and append them to the buffer  ******/
              String tempString=new String(ch, start, length);
               builder.append(tempString);
          }
}