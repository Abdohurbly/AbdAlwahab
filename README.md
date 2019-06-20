[![](https://jitpack.io/v/Abdohurbly/AbdAlwahab.svg)](https://jitpack.io/#Abdohurbly/AbdAlwahab)

# Fork Lib.
# Abdulwahab Herbli
# Android Chat UI

A chat UI library with different customization options. Currently it only has a title edit that can be used.

This is still under development, and more changes will gradually come which would include new features such as adding images and files.

![Library used](http://res.cloudinary.com/duswj2lve/image/upload/v1479837904/chatui_k3diqq.png)

### Version
v1.0.10

### Installation

Add this to your build.gradle file's dependencies:

    implementation 'com.github.Abdohurbly:AbdAlwahab:1.0.10'

## Usage
Drop the ChatView in your XML layout as is shown below:

```
<co.intentservice.chatui.ChatView
	android:id="@+id/chat_view"
	android:layout_width="match_parent"
	android:layout_height="match_parents"
	<!-- Insert customisation options here -->
/>
```

You need to add this attribute to your root layout.

```
xmlns:chatview="http://schemes.android.com/apk/res-auto"
```

Currently there aren't many methods that can be utilized within Java code. However, the view can be customized from the XML file. You can still define it in your activity class as follows.

```
ChatView chatView = (ChatView) findViewById(R.id.chat_view);
```

### Attributes

ChatView has the following attributes:

```
chatview:backgroundColor=""
chatview:inputBackgroundColor=""
chatview:inputUseEditorAction="" // true or false
chatview:inputTextAppearance=""
chatview:inputTextSize=""
chatview:inputTextColor=""
chatview:inputHintColor=""
chatview:inputHintText=""

chatview:titleBackgroundColor=""
chatview:titleTextAppearance=""
chatview:titleTextSize=""
chatview:titleTextColor=""
chatview:titleHintColor=""
chatview:titleHintText=""

chatview:sendBtnIcon="" 
chatview:sendBtnIconTint=""
chatview:sendBtnBackgroundTint=""

chatview:bubbleBackgroundRcv="" // color
chatview:bubbleBackgroundSend="" //color
chatview:bubbleElevation="" // "flat" or "elevated"

```

### Sending messages

`OnSentMessageListener` is used to detect sending messages actions.

```
chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener(){
	@Override
	public boolean sendMessage(ChatMessage chatMessage){
		// perform actual message sending 
		return true;
	}
});
```


You can perform your connection in the `sendMessage(ChatMessage chatMessage)` method.

The returned value will determine whether the message is sent or not.

### Receiving messages

You can add received messages by using `chatView.addMessage(ChatMessage message)`, or, for multiple messages, `chatView.addMessages(ArrayList<ChatMessage> messages)`

Messages will appear to the right or the left depending on the `Type` variable in your `ChatMessage` object.

### Deleting messages

You can remove messages using `chatView.removeMessage(int position)` or `chatView.clearMessages()`

### The ChatMessage interface

Your app should implement this interface to define its own implementation of a `Message` model. Your implementation should typically contain a variable for each getter method.

There is an `InternalMessage` class that implements this interface, but it is strongly recommended for you to define your own implementation, as using `InternalMessage` class might cause (at least) complexities in your code.

The interface also contains the Type enum which only contains two values: `SENT`, and `RECEIVED`. I'll leave you to figure out what each means ;) .

### Typing Listener

This listener is used to perform an action during different typing states.

```
chatView.setTypingListener(new ChatView.TypingListener(){
	@Override
	public void userStartedTyping(){
		// will be called when the user starts typing
	}
	
	@Override
	public void userStoppedTyping(){
		// will be called when the user stops typing
	}
});
```

