package co.intentservice.chatui.sample;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class MainActivity extends AppCompatActivity {
    EmojiconEditText emojiconEditText;
    View rootView;
    ImageView emojiButton;
    EmojIconActions emojIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emojiconEditText = (EmojiconEditText) findViewById(co.intentservice.chatui.R.id.emojicon_edit_text);
        rootView = findViewById(co.intentservice.chatui.R.id.root_view);
        emojiButton = (ImageView) findViewById(co.intentservice.chatui.R.id.emoji_btn);
        emojIcon = new EmojIconActions(getBaseContext(), rootView, emojiconEditText, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });
        String time = "2017-06-8 ,11:25 am";
        final ChatView chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.addMessage(new ChatMessage("Message received", time, ChatMessage.Type.RECEIVED));
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
          String date= "04:45 am";
           String msg  =   chatMessage.getMessage();
                chatView.addMessage(new ChatMessage(msg,date, ChatMessage.Type.SENT));
                return false;
            }
        });

        chatView.setTypingListener(new ChatView.TypingListener() {
            @Override
            public void userStartedTyping() {

            }

            @Override
            public void userStoppedTyping() {

            }
        });
    }
}
