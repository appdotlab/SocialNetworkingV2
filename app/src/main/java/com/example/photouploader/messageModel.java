package com.example.photouploader;

public class messageModel  {
    String senderName;
    String SenderID;
    String RecieverName;
    String RecieverID;

    public String getRecieverName() {
        return RecieverName;
    }

    public void setRecieverName(String recieverName) {
        RecieverName = recieverName;
    }

    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }

    String messsage;



    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderID() {
        return SenderID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID;
    }

    public String getRecievernName() {
        return RecieverName;
    }

    public void setRecievernName(String recievernName) {
        RecieverName = recievernName;
    }

    public String getRecieverID() {
        return RecieverID;
    }

    public void setRecieverID(String recieverID) {
        RecieverID = recieverID;
    }
}
