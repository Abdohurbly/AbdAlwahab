package co.intentservice.chatui.sample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;
import co.intentservice.chatui.models.InternalMessage;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class MainActivity extends AppCompatActivity {
    EmojiconEditText emojiconEditText;
    EditText titleText;
    View rootView;
    ImageView emojiButton;
    EmojIconActions emojIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emojiconEditText = findViewById(co.intentservice.chatui.R.id.emojicon_edit_text);
        rootView = findViewById(co.intentservice.chatui.R.id.root_view);
        emojiButton = findViewById(co.intentservice.chatui.R.id.emoji_btn);
        titleText = findViewById(co.intentservice.chatui.R.id.title_edit_text);
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
        String time = "2017-06-8, 11:25";
        final ChatView chatView = findViewById(R.id.chat_view);
        String title = "Title goes here";
        chatView.addMessage(new InternalMessage("Message received", title, time, ChatMessage.Type.RECEIVED));
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                Date calendar = Calendar.getInstance().getTime();
                DateFormat format = new SimpleDateFormat("HH:mm");
                String date = format.format(calendar);
                String title = chatMessage.getTitle();
                String msg = chatMessage.getMessage();
                chatView.addMessage(new InternalMessage(msg, title, date, ChatMessage.Type.SENT));
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
