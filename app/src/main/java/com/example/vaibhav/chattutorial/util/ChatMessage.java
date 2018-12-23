package com.example.vaibhav.chattutorial.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage {

    private String  msgSender;
    private String  msgText;
    private boolean isSent;
    private String  msgTime;

    public ChatMessage(String msgUser, String msgText, boolean isSent, String msgTime) {
        this.msgSender = msgUser;
        this.msgText = msgText;
        this.isSent = isSent;
        this.msgTime = msgTime;
    }

    public ChatMessage(String user, String text, boolean sent) {
        msgSender = user;
        msgText = text;
        isSent  = sent;
        msgTime = getCurrentTime();
    }

    private String getCurrentTime() {
        Date date = new Date();
        String time = new SimpleDateFormat("dd MM,yyyy, HH:mm").format(date).substring(6);

        String day = getDayOfWeek(date.getDay())+", ";
        String dateStr = date.getDate()+" ";
        String month = getMonthString(date.getMonth())+", ";

        return day+dateStr+month+time;
    }

    private String getMonthString(int month) {
        switch (month){
            case 1:  return "Jan";
            case 2:  return "Feb";
            case 3:  return "Mar";
            case 4:  return "Apr";
            case 5:  return "May";
            case 6:  return "Jun";
            case 7:  return "July";
            case 8:  return "Aug";
            case 9:  return "Sep";
            case 10: return "Oct";
            case 11: return "Nov";
            default: return "Dec";
        }
    }

    private String getDayOfWeek(int day) {
        switch (day){
            case 0:  return "Sunday";
            case 1:  return "Monday";
            case 2:  return "Tuesday";
            case 3:  return "Wednesday";
            case 4:  return "Thursday";
            case 5:  return "Friday";
            default: return "Saturday";
        }
    }

    public String getMsgSender() { return msgSender; }

    public String getMsgText() { return msgText; }

    public String getMsgTime() { return msgTime; }

    public boolean isSent() { return isSent; }

}
