package co.intentservice.chatui.models;

import android.graphics.Bitmap;
import android.text.format.DateFormat;

import java.util.concurrent.TimeUnit;

public class ChatMessage {
    private String message;
    private long timestamp;
    private Type type;
    private String time;


    private Bitmap bitmap;

    public ChatMessage(String message, long timestamp, Type type, String time, Bitmap bitmap) {
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.time = time;
        this.bitmap = bitmap;
    }

    public ChatMessage(String message, long timestamp, Type type){
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }

    public ChatMessage(String message, String time, Type type){
        this.message = message;
        this.time = time;
        this.type = type;
    }

    public String getTime() {

        return time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public String getFormattedTime(){

        long oneDayInMillis = TimeUnit.DAYS.toMillis(1); // 24 * 60 * 60 * 1000;

        long timeDifference = System.currentTimeMillis() - timestamp;

        return timeDifference < oneDayInMillis
                ? DateFormat.format("hh:mm a", timestamp).toString()
                : DateFormat.format("MMM-d, hh:mm :aa", timestamp).toString();
        //Add Test
    }
    public Bitmap getBitmap() {
        return bitmap;
    }

    public String timeFromString(String s)
    {

     return s   ;
    }

    public enum Type {
        SENT, RECEIVED
    }

}
