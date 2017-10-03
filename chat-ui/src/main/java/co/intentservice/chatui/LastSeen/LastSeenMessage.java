package co.intentservice.chatui.LastSeen;

/**
 * Created by HP on 03/10/2017.
 */

public interface LastSeenMessage {
    void savedInDataBase();
    void sendNotification();
    void receivedMessage();
}
