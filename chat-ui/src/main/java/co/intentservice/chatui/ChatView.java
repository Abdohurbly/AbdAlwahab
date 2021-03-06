package co.intentservice.chatui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;

import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.intentservice.chatui.fab.FloatingActionsMenu;
import co.intentservice.chatui.models.ChatMessage;
import co.intentservice.chatui.models.ChatMessage.Type;
import co.intentservice.chatui.models.InternalMessage;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by timi on 17/11/2015.
 */
public class ChatView extends RelativeLayout {

    private static final int FLAT = 0;
    private static final int ELEVATED = 1;

    private CardView inputFrame;
    private ListView chatListView;
    EmojiconEditText emojiconEditText;
    private EditText titleEditText;
    View rootView;
    ImageView emojiButton;
    EmojIconActions emojIcon;
    private FloatingActionsMenu actionsMenu;
    private boolean previousFocusState = false, useEditorAction, isTyping;

    private Runnable typingTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTyping) {
                isTyping = false;
                if (typingListener != null) typingListener.userStoppedTyping();
            }
        }
    };
    private TypingListener typingListener;
    private OnSentMessageListener onSentMessageListener;
    private ChatViewListAdapter chatViewListAdapter;

    private int inputFrameBackgroundColor, backgroundColor;
    private int inputTextSize, inputTextColor, inputHintColor;
    private int titleTextSize, titleTextColor, titleHintColor, titleVisibility;
    private String inputHintText, titleHintText;
    private int sendButtonBackgroundTint, sendButtonIconTint;

    private float bubbleElevation;

    private int bubbleBackgroundRcv, bubbleBackgroundSend; // Drawables cause cardRadius issues. Better to use background color
    private Drawable sendButtonIcon, buttonDrawable;
    private TypedArray attributes, textAppearanceAttributes, titleAppearanceAttributes;
    private Context context;


    ChatView(Context context) {
        this(context, null);
    }

    public ChatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(getContext()).inflate(R.layout.chat_view, this, true);
        this.context = context;
        initializeViews();
        getXMLAttributes(attrs, defStyleAttr);
        setViewAttributes();
        setListAdapter();
        setButtonClickListeners();
        setUserTypingListener();
        setUserStoppedTypingListener();
    }

    private void initializeViews() {
        chatListView = findViewById(R.id.chat_list);
        inputFrame = findViewById(R.id.input_frame);
        emojiconEditText = findViewById(R.id.emojicon_edit_text);
        actionsMenu = findViewById(R.id.sendButton);
        titleEditText = findViewById(R.id.title_edit_text);
    }


    private void getXMLAttributes(AttributeSet attrs, int defStyleAttr) {
        attributes = context.obtainStyledAttributes(attrs, R.styleable.ChatView, defStyleAttr, R.style.ChatViewDefault);
        getChatViewBackgroundColor();
        getAttributesForBubbles();
        getAttributesForInputFrame();
        getAttributesForInputText();
        getAttributesForTitleText();
        getAttributesForSendButton();
        getUseEditorAction();
        attributes.recycle();
    }

    private void setListAdapter() {
        chatViewListAdapter = new ChatViewListAdapter(context);
        chatListView.setAdapter(chatViewListAdapter);
    }


    private void setViewAttributes() {
        setChatViewBackground();
        setInputFrameAttributes();
        setInputTextAttributes();
        setTitleTextAttributes();
        setSendButtonAttributes();
        setUseEditorAction();
    }

    private void getChatViewBackgroundColor() {
        backgroundColor = attributes.getColor(R.styleable.ChatView_backgroundColor, -1);
    }

    private void getAttributesForBubbles() {
        float dip4 = context.getResources().getDisplayMetrics().density * 4.0f;
        int elevation = attributes.getInt(R.styleable.ChatView_bubbleElevation, ELEVATED);
        bubbleElevation = elevation == ELEVATED ? dip4 : 0;

        bubbleBackgroundRcv = attributes.getColor(R.styleable.ChatView_bubbleBackgroundRcv, ContextCompat.getColor(context, R.color.default_bubble_color_rcv));
        bubbleBackgroundSend = attributes.getColor(R.styleable.ChatView_bubbleBackgroundSend, ContextCompat.getColor(context, R.color.default_bubble_color_send));
    }

    private void getAttributesForInputFrame() {
        inputFrameBackgroundColor = attributes.getColor(R.styleable.ChatView_inputBackgroundColor, -1);
    }

    private void setInputFrameAttributes() {
        inputFrame.setCardBackgroundColor(inputFrameBackgroundColor);
    }

    private void setChatViewBackground() {
        this.setBackgroundColor(backgroundColor);
    }

    private void getAttributesForInputText() {
        setInputTextDefaults();
        if (hasStyleResourceSet()) {
            setTextAppearanceAttributes();
            setInputTextSize();
            setInputTextColor();
            setInputHintColor();
            setInputHintText();
            textAppearanceAttributes.recycle();
        }
        overrideTextStylesIfSetIndividually();
    }

    private void getAttributesForTitleText() {
        setTitleTextDefaults();
        if (hasTitleResourceSet()) {
            setTitleAppearanceAttributes();
            setTitleTextSize();
            setTitleTextColor();
            setTitleHintColor();
            setTitleHintText();
            setTitleVisibility();
            titleAppearanceAttributes.recycle();
        }
        overrideTitleStylesIfSetIndividually();
    }

    private void setTitleAppearanceAttributes() {
        final int titleAppearanceId = attributes.getResourceId(R.styleable.ChatView_titleTextAppearance, 0);
        titleAppearanceAttributes = getContext().obtainStyledAttributes(titleAppearanceId, R.styleable.ChatViewTitleTextAppearance);
    }

    private void setTextAppearanceAttributes() {
        final int textAppearanceId = attributes.getResourceId(R.styleable.ChatView_inputTextAppearance, 0);
        textAppearanceAttributes = getContext().obtainStyledAttributes(textAppearanceId, R.styleable.ChatViewInputTextAppearance);
    }

    private void setTitleTextAttributes() {
        titleEditText.setTextColor(titleTextColor);
        titleEditText.setHintTextColor(titleHintColor);
        titleEditText.setHint(titleHintText);
        titleEditText.setVisibility(titleVisibility);
        titleEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
    }

    private void setInputTextAttributes() {
        emojiconEditText.setTextColor(inputTextColor);
        emojiconEditText.setHintTextColor(inputHintColor);
        emojiconEditText.setHint(inputHintText);
        emojiconEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, inputTextSize);
    }

    private void getAttributesForSendButton() {
        sendButtonBackgroundTint = attributes.getColor(R.styleable.ChatView_sendBtnBackgroundTint, -1);
        sendButtonIconTint = attributes.getColor(R.styleable.ChatView_sendBtnIconTint, Color.WHITE);
        sendButtonIcon = attributes.getDrawable(R.styleable.ChatView_sendBtnIcon);
    }

    private void setSendButtonAttributes() {
        actionsMenu.getSendButton().setColorNormal(sendButtonBackgroundTint);
        actionsMenu.setIconDrawable(sendButtonIcon);

        buttonDrawable = actionsMenu.getIconDrawable();
        actionsMenu.setButtonIconTint(sendButtonIconTint);
    }

    private void getUseEditorAction() {
        useEditorAction = attributes.getBoolean(R.styleable.ChatView_inputUseEditorAction, false);
    }

    private void setUseEditorAction() {
        if (useEditorAction) {
            setupEditorAction();
        } else {
            emojiconEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        }
    }

    private boolean hasStyleResourceSet() {
        return attributes.hasValue(R.styleable.ChatView_inputTextAppearance);
    }

    // Message text

    private void setInputTextDefaults() {
        inputTextSize = context.getResources().getDimensionPixelSize(R.dimen.default_input_text_size);
        inputTextColor = ContextCompat.getColor(context, R.color.black);
        inputHintColor = ContextCompat.getColor(context, R.color.main_color_gray);
        inputHintText = context.getResources().getString(R.string.default_input_hint);
    }

    private void setInputTextSize() {
        if (textAppearanceAttributes.hasValue(R.styleable.ChatView_inputTextSize)) {
            inputTextSize = attributes.getDimensionPixelSize(R.styleable.ChatView_inputTextSize, inputTextSize);
        }
    }

    private void setInputTextColor() {
        if (textAppearanceAttributes.hasValue(R.styleable.ChatView_inputTextColor)) {
            inputTextColor = attributes.getColor(R.styleable.ChatView_inputTextColor, inputTextColor);
        }
    }

    private void setInputHintColor() {
        if (textAppearanceAttributes.hasValue(R.styleable.ChatView_inputHintColor)) {
            inputHintColor = attributes.getColor(R.styleable.ChatView_inputHintColor, inputHintColor);
        }
    }

    private void setInputHintText() {
        if (textAppearanceAttributes.hasValue(R.styleable.ChatView_inputHintText)) {
            inputHintText = attributes.getString(R.styleable.ChatView_inputHintText);
        }
    }

    private boolean hasTitleResourceSet() {
        return attributes.hasValue(R.styleable.ChatView_titleTextAppearance);
    }

    private void overrideTextStylesIfSetIndividually() {
        inputTextSize = (int) attributes.getDimension(R.styleable.ChatView_inputTextSize, inputTextSize);
        inputTextColor = attributes.getColor(R.styleable.ChatView_inputTextColor, inputTextColor);
        inputHintColor = attributes.getColor(R.styleable.ChatView_inputHintColor, inputHintColor);
        inputHintText = attributes.getString(R.styleable.ChatView_inputHintText);
    }

    // TitleView

    private void setTitleTextDefaults() {
        titleTextSize = context.getResources().getDimensionPixelSize(R.dimen.default_input_text_size);
        titleTextColor = ContextCompat.getColor(context, R.color.black);
        titleHintColor = ContextCompat.getColor(context, R.color.main_color_gray);
        titleHintText = context.getResources().getString(R.string.type_title);
        titleVisibility = View.VISIBLE;
    }

    private void setTitleTextSize() {
        if (textAppearanceAttributes.hasValue(R.styleable.ChatView_inputTextSize)) {
            titleTextSize = attributes.getDimensionPixelSize(R.styleable.ChatView_inputTextSize, titleTextSize);
        }
    }

    private void setTitleTextColor() {
        if (textAppearanceAttributes.hasValue(R.styleable.ChatView_inputTextColor)) {
            titleTextColor = attributes.getColor(R.styleable.ChatView_inputTextColor, titleTextColor);
        }
    }

    private void setTitleHintColor() {
        if (textAppearanceAttributes.hasValue(R.styleable.ChatView_inputHintColor)) {
            titleHintColor = attributes.getColor(R.styleable.ChatView_inputHintColor, titleHintColor);
        }
    }

    private void setTitleHintText() {
        if (textAppearanceAttributes.hasValue(R.styleable.ChatView_titleHintText)) {
            titleHintText = attributes.getString(R.styleable.ChatView_titleHintText);
        }
    }

    private void setTitleVisibility() {
        if (textAppearanceAttributes.hasValue(R.styleable.ChatView_titleVisibility)) {
            titleVisibility = attributes.getInt(R.styleable.ChatView_titleVisibility, View.VISIBLE);
        }
    }

    private void overrideTitleStylesIfSetIndividually() {
        titleTextSize = (int) attributes.getDimension(R.styleable.ChatView_titleTextSize, titleTextSize);
        titleTextColor = attributes.getColor(R.styleable.ChatView_inputTextColor, titleTextColor);
        titleHintColor = attributes.getColor(R.styleable.ChatView_inputHintColor, titleHintColor);
        titleHintText = attributes.getString(R.styleable.ChatView_titleHintText);
        titleVisibility = attributes.getInt(R.styleable.ChatView_titleVisibility, titleVisibility);
    }

    private void setupEditorAction() {
        emojiconEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        emojiconEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        emojiconEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    long stamp = System.currentTimeMillis();
                    String message = emojiconEditText.getText().toString();
                    String title = titleEditText.getText().toString();
                    if (!TextUtils.isEmpty(message)) {
                        sendMessage(message, title, stamp);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void setButtonClickListeners() {

        actionsMenu.getSendButton().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (actionsMenu.isExpanded()) {
                    actionsMenu.collapse();
                    return;
                }

                long stamp = System.currentTimeMillis();
                String message = emojiconEditText.getText().toString();
                String title = titleEditText.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    sendMessage(message, title, stamp);
                }

            }
        });

        actionsMenu.getSendButton().setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                actionsMenu.expand();
                return true;
            }
        });
    }

    private void setUserTypingListener() {
        emojiconEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

                    if (!isTyping) {
                        isTyping = true;
                        if (typingListener != null) typingListener.userStartedTyping();
                    }

                    removeCallbacks(typingTimerRunnable);
                    postDelayed(typingTimerRunnable, 1500);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setUserStoppedTypingListener() {
        emojiconEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (previousFocusState && !hasFocus && typingListener != null) {
                    typingListener.userStoppedTyping();
                }
                previousFocusState = hasFocus;
            }
        });
    }


    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
        return super.addViewInLayout(child, index, params);
    }

    public String getTypedMessage() {
        return emojiconEditText.getText().toString();
    }

    public void setTypingListener(TypingListener typingListener) {
        this.typingListener = typingListener;
    }

    public void setOnSentMessageListener(OnSentMessageListener onSentMessageListener) {
        this.onSentMessageListener = onSentMessageListener;
    }

    private void sendMessage(String message, String title, long stamp) {
        ChatMessage chatMessage = new InternalMessage(message, title, stamp, Type.SENT);
        if (onSentMessageListener != null && onSentMessageListener.sendMessage(chatMessage)) {
            chatViewListAdapter.addMessage(chatMessage);
            emojiconEditText.setText("");
            titleEditText.setText("");
        } else {
            emojiconEditText.setText("");
            titleEditText.setText("");
        }
    }

    public void addMessage(ChatMessage chatMessage) {
        chatViewListAdapter.addMessage(chatMessage);
    }

    public void addMessages(ArrayList<ChatMessage> messages) {
        chatViewListAdapter.addMessages(messages);
    }

    public void removeMessage(int position) {
        chatViewListAdapter.removeMessage(position);
    }

    public void clearMessages() {
        chatViewListAdapter.clearMessages();
    }

    public EditText getInputEditText() {
        return emojiconEditText;
    }

    public FloatingActionsMenu getActionsMenu() {
        return actionsMenu;
    }


    public interface TypingListener {

        void userStartedTyping();

        void userStoppedTyping();

    }

    public interface OnSentMessageListener {
        boolean sendMessage(ChatMessage chatMessage);
    }

    private class ChatViewListAdapter extends BaseAdapter {

        public final int STATUS_SENT = 0;
        public final int STATUS_RECEIVED = 1;

        ArrayList<ChatMessage> chatMessages;

        Context context;
        LayoutInflater inflater;

        public ChatViewListAdapter(Context context) {
            this.chatMessages = new ArrayList<>();
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return chatMessages.size();
        }

        @Override
        public Object getItem(int position) {
            return chatMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return chatMessages.get(position).getType().ordinal();
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int type = getItemViewType(position);
            if (convertView == null) {
                switch (type) {
                    case STATUS_SENT:
                        convertView = inflater.inflate(R.layout.chat_item_sent, parent, false);
                        break;
                    case STATUS_RECEIVED:
                        convertView = inflater.inflate(R.layout.chat_item_rcv, parent, false);
                        break;
                }

                assert convertView != null;
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.getMessageTextView().setText(chatMessages.get(position).getMessage());

            if (chatMessages.get(position).getTitle().isEmpty()) {
                holder.getTitleTextView().setVisibility(GONE);
            } else {
                holder.getTitleTextView().setText(chatMessages.get(position).getTitle());
            }
            holder.getTimestampTextView().setText(chatMessages.get(position).getTime());
            holder.getChatBubble().setCardElevation(bubbleElevation);
            holder.setBackground(type);
            holder.seenImage.setImageBitmap(chatMessages.get(position).getBitmap());
            return convertView;
        }

        private void addMessage(ChatMessage message) {
            chatMessages.add(message);
            notifyDataSetChanged();
        }

        private void addMessages(ArrayList<ChatMessage> chatMessages) {
            this.chatMessages.addAll(chatMessages);
            notifyDataSetChanged();
        }

        private void removeMessage(int position) {
            if (this.chatMessages.size() > position) {
                this.chatMessages.remove(position);
            }
        }

        private void clearMessages() {
            this.chatMessages.clear();
            notifyDataSetChanged();
        }

        class ViewHolder {
            View row;
            CardView bubble;
            EmojiconTextView messageTextView;
            TextView timestampTextView;
            TextView titleTextView;
            ImageView seenImage;

            private ViewHolder(View convertView) {
                row = convertView;
                bubble = convertView.findViewById(R.id.bubble);
                seenImage = convertView.findViewById(R.id.imageView);
            }

            private TextView getTitleTextView() {
                if (titleTextView == null) {
                    titleTextView = row.findViewById(R.id.title_text_view);
                }
                return titleTextView;
            }

            private TextView getMessageTextView() {
                if (messageTextView == null) {
                    messageTextView = row.findViewById(R.id.message_text_view);
                }
                return messageTextView;
            }

            private TextView getTimestampTextView() {
                if (timestampTextView == null) {
                    timestampTextView = row.findViewById(R.id.timestamp_text_view);
                }

                return timestampTextView;
            }

            private CardView getChatBubble() {
                if (bubble == null) {
                    bubble = row.findViewById(R.id.bubble);
                }

                return bubble;
            }

            private void setBackground(int messageType) {

                int background = ContextCompat.getColor(context, R.color.cardview_light_background);

                switch (messageType) {
                    case STATUS_RECEIVED:
                        background = bubbleBackgroundRcv;
                        break;
                    case STATUS_SENT:
                        background = bubbleBackgroundSend;
                        break;
                }

                bubble.setCardBackgroundColor(background);
            }
        }
    }
}
