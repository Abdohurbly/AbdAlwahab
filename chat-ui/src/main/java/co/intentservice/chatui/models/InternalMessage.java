package co.intentservice.chatui.models;

import android.graphics.Bitmap;

/**
 * A concrete implementation of ChatMessageInterface. This will help displaying sent messages.
 *
 * @author HyldenMan
 */
public class InternalMessage implements ChatMessage {
    private String time;
    private String title;
    private String body;
    private Type type;
    private Bitmap bitmap;
    private long timestamp;

    public InternalMessage(String body, String title, String time, Type type) {
        this.body = body;
        this.title = title;
        this.time = time;
        this.type = type;
    }

    public InternalMessage(String message, String title, long stamp, Type sent) {
        this.body = message;
        this.title = title;
        this.timestamp = stamp;
        this.type = sent;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getMessage() {
        return body;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
