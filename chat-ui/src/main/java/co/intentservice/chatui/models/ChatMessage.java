package co.intentservice.chatui.models;

import android.graphics.Bitmap;
import android.text.format.DateFormat;

import java.util.concurrent.TimeUnit;

/**
 * This interface must be implemented by your app. Its implementation should hold the logic behind
 * sending and receiving messages. The implementation will also help display previous messages inside
 * your code.
 */
public interface ChatMessage {

    String getTime();

    long getTimestamp();

    String getMessage();

    Type getType();

    Bitmap getBitmap();

    String getTitle();

    enum Type {
        SENT, RECEIVED
    }

}
