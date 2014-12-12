package com.example.hiddencitiesmap;

public class XmlValuesModel {
	 
    private  float markerLat;
    private  float markerLong;
    private  int markerId;
    private String installationId;
    
    private  float wayPointLat;
    private  float wayPointLong;
    private  int wayPointId;
    private String wayPointNarrationPlayer;
    
    private String userName;
    private String userEmail;
    
    private String ftpAddress;
    private String ftpUser;
    private String ftpPassword;
    private String ftpRemotePath;
    
    private String webSocketAdress;
    private String webSocketUser;
    
    private String portholeDataDat;
    private String portholeDataXML;
    private String portholeImage;
    private String portholeVideo;
    
    private String colourName;
    private String colourHexValue;
    
    private String welcomeString;
    private String emailString;
    private String usernameString;
    private String instructionStringOne;
    private String instructionStringTwo;
    private String instructionStringThree;
    private String instructionStringFour;
    private String instructionStringFive;
    private String instructionStringSix;
    private String instructionStringSeven;
    private String errorStringOne;
    private String errorStringTwo;
    private String errorStringThree;
    private String errorStringFour;
     
    /************* Define Setter Methods *********/
     
    public void setMarkerLat(Float markerLat)
    {
        this.markerLat = markerLat;
    }
    public void setMarkerLong(Float markerLong)
    {
        this.markerLong = markerLong;
    }
    public void setMarkerId(int markerId)
    {
        this.markerId = markerId;
    }
    public void setMarkerInstallationId(String installationId)
    {
        this.installationId = installationId;
    }

    
    
    public void setWayPointLat(Float wayPointLat)
    {
        this.wayPointLat = wayPointLat;
    }
    public void setWayPointLong(Float wayPointLong)
    {
        this.wayPointLong = wayPointLong;
    }
    public void setWayPointId(int wayPointId)
    {
        this.wayPointId = wayPointId;
    }
    public void setWayPointNarrationPlayer(String wayPointNarrationPlayer)
    {
        this.wayPointNarrationPlayer = wayPointNarrationPlayer;
    }
    
    
    
    public void setUsername(String userName)
    {
        this.userName = userName;
    }
    public void setUserEmail(String userEmail)
    {
        this.userEmail = userEmail;
    }
    
    
    
    public void setPortholeDataXML(String portholeDataXML)
    {
        this.portholeDataXML = portholeDataXML;
    }
    public void setPortholeDataDat(String portholeDataDat)
    {
        this.portholeDataDat = portholeDataDat;
    }
    public void setPortholeImage(String portholeImage)
    {
        this.portholeImage = portholeImage;
    }
    public void setPortholeVideo(String portholeVideo)
    {
        this.portholeVideo = portholeVideo;
    }
   
    
    
    public void setColourName(String colourName)
    {
    	this.colourName = colourName;
    }
    public void setColourHexValue(String colourHexValue)
    {
    	this.colourHexValue = colourHexValue;
    }
   
    
    
    public void setFtpAddress(String ftpAddress)
    {
        this.ftpAddress = ftpAddress;
    }
    public void setFtpUser(String ftpUser)
    {
        this.ftpUser = ftpUser;
    }
    public void setFtpPassword(String ftpPassword)
    {
        this.ftpPassword = ftpPassword;
    }
    public void setFtpRemotePath(String ftpRemotePath)
    {
        this.ftpRemotePath = ftpRemotePath;
    }
    public void setWebSocketAdress(String webSocketAdress)
    {
        this.webSocketAdress = webSocketAdress;
    }
    public void setWebSocketUser(String webSocketUser)
    {
        this.webSocketUser = webSocketUser;
    }
    
    
    
    public void setEmailString(String emailString)
    {
        this.emailString = emailString;
    }
    public void setUsernameString(String usernameString)
    {
        this.usernameString = usernameString;
    }
    
    
    
    public void setWelcomeString(String welcomeString)
    {
        this.welcomeString = welcomeString;
    }
    public void setInstructionStringOne(String instructionStringOne)
    {
        this.instructionStringOne = instructionStringOne;
    }
    public void setInstructionStringTwo(String instructionStringTwo)
    {
        this.instructionStringTwo = instructionStringTwo;
    }
    public void setInstructionStringThree(String instructionStringThree)
    {
        this.instructionStringThree = instructionStringThree;
    }
    public void setInstructionStringFour(String instructionStringFour)
    {
        this.instructionStringFour = instructionStringFour;
    }
    public void setInstructionStringFive(String instructionStringFive)
    {
        this.instructionStringFive = instructionStringFive;
    }
    public void setInstructionStringSix(String instructionStringSix)
    {
        this.instructionStringSix = instructionStringSix;
    }
    public void setInstructionStringSeven(String instructionStringSeven)
    {
        this.instructionStringSeven = instructionStringSeven;
    }
    
    
    
    public void setErrorStringOne(String errorStringOne)
    {
        this.errorStringOne = errorStringOne;
    }
    public void setErrorStringTwo(String errorStringTwo)
    {
        this.errorStringTwo = errorStringTwo;
    }
    public void setErrorStringThree(String errorStringThree)
    {
        this.errorStringThree = errorStringThree;
    }
    public void setErrorStringFour(String errorStringFour)
    {
        this.errorStringFour = errorStringFour;
    }
  

    
     
    /************* Define Getter Methods *********/
     
    public Float getMarkerLat()
    {
        return markerLat;
    }  
    public float getMarkerLong()
    {
        return markerLong;
    }
    public int getMarkerId()
    {
        return markerId;
    }
    public String getMarkerInstallationId()
    {
        return installationId;
    }
    
    
    
    public float getWaypointLat()
    {
        return wayPointLat;
    }
    public float getWaypointLong()
    {
        return this.wayPointLong;
    }
    public int getWaypointId()
    {
        return this.wayPointId;
    }
    public String getWayPointNarrationPlayer()
    {
    	return this.wayPointNarrationPlayer;
    }
    
    
    
    public String getUsername()
    {
    	return this.userName;
    }
    public String getUserEmail()
    {
    	return this.userEmail;
    }
    
    
    
    public String getPortholeDataDat()
    {
    	return this.portholeDataDat;
    }
    public String getPortholeDataXML()
    {
    	return this.portholeDataXML;
    }
    public String getPortholeImage()
    {
    	return this.portholeImage;
    }
    public String getPortholeVideo()
    {
    	return this.portholeVideo;
    }
    
    
    
    public String getColourName()
    {
        return colourName;
    }
    public String getColourHexValue()
    {
        return colourHexValue;
    }
    
    
    
    public String getFtpAddress()
    {
    	return this.ftpAddress;
    }
    public String getFtpUser()
    {
    	return this.ftpUser;
    }
    public String getFtpPassword()
    {
    	return this.ftpPassword;
    }
    public String getFtpRemotePath()
    {
    	return this.ftpRemotePath;
    }
    public String getWebSocketAdress()
    {
    	return this.webSocketAdress;
    }
    public String getWebSocketUser()
    {
    	return this.webSocketUser;
    }
    
    
    
    public String getWelcomeString()
    {
    	return this.welcomeString;
    }
    public String getEmailString()
    {
    	return this.emailString;
    }
    public String getUsernameString()
    {
    	return this.usernameString;
    }
    public String getInstructionStringOne()
    {
    	return this.instructionStringOne;
    }
    public String getInstructionStringTwo()
    {
    	return this.instructionStringTwo;
    }
    public String getInstructionStringThree()
    {
    	return this.instructionStringThree;
    }
    public String getInstructionStringFour()
    {
    	return this.instructionStringFour;
    }
    public String getInstructionStringFive()
    {
    	return this.instructionStringFive;
    }
    public String getInstructionStringSix()
    {
    	return this.instructionStringSix;
    }
    public String getInstructionStringSeven()
    {
    	return this.instructionStringSeven;
    }
    
    
    
    public String getErrorStringOne()
    {
    	return this.errorStringOne;
    }
    public String getErrorStringTwo()
    {
    	return this.errorStringTwo;
    }
    public String getErrorStringThree()
    {
    	return this.errorStringThree;
    }
    public String getErrorStringFour()
    {
    	return this.errorStringFour;
    }

     
} 