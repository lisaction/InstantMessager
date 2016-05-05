package com.ucas.xiaol.llim;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.MessageEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaol on 2016/4/29.
 */
public class ChatActivity extends AppCompatActivity implements EMMessageListener {

    //input text
    private EditText cInputEdit;
    //send button
    private Button cSendBtn;
    //chat title: the name of this friend
    private TextView cContentTitle;
    //show the chat content
    private ListView cContent;
    //store chat contents
    private List<String[]> lChat = null;
    //the data adapter of cContent
    private ChatAdapter adapter = null;
    //private String[] new_msg = new String[2];
    //message listener
    private EMMessageListener cMessageListener;
    //the target userID of conversation
    private String chatID;
    //the structure of conversation
    private EMConversation cConversation;

    //the num of message in a page
    private int pagesize = 10;
    //the num of max messages to show
    private int maxMsgcount = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatID = getIntent().getStringExtra("chat_id");
        cMessageListener = this;

        initView();
        initConversation();


    }

    private void initView() {
        cInputEdit = (EditText) findViewById(R.id.edit_message_input);
        cSendBtn = (Button) findViewById(R.id.btn_send);
        cContentTitle = (TextView) findViewById(R.id.chat_title);
        cContent = (ListView) findViewById(R.id.text_content);

        //set title
        cContentTitle.setText(chatID);

        //Scrolling View
        //cContentText.setMovementMethod(new ScrollingMovementMethod());

        //send click event
        cSendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = cInputEdit.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    cInputEdit.setText("");
                    //create new EMMesage(message_content,message_receiver);
                    EMMessage message = EMMessage.createTxtSendMessage(content, chatID);
                    //add the message content and time to the text view
                    String[] new_msg = new String[2];
                    new_msg[0] = "send";
                    new_msg[1] = "(" + message.getMsgTime() + ")\n" + content;
                    //call the method to send message
                    EMClient.getInstance().chatManager().sendMessage(message);
                    updateListView(new_msg);
                    //set callback for the message
                    message.setMessageStatusCallback(new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            Log.d("mesg", "send message onSuccess");
                            //send successfully, update the ListView
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.d("mesg", "send message onError");
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                }
            }
        });
    }

    private void initConversation() {
        //The three parameters are
        //chat target chatId of current conversation
        //the type of the conversation
        //whether to create a new conversation if it not exist
        cConversation = EMClient.getInstance().chatManager().getConversation(chatID, null, true);

        //get the unread message num
        //int unread = cConversation.getUnreadMsgCount();
        //set the number of unread message to 0
        //cConversation.markAllMessagesAsRead();

        // getAllMessages : get message from memory (server?）
        int count = cConversation.getAllMessages().size();
        // getAllMsgCount() : get message from the conversation;
        // if there is no enough to show on the screen then load more message if possible
        if (count < cConversation.getAllMsgCount() && count < pagesize) {
            //get the top messID of the get-list
            String mesId = cConversation.getAllMessages().get(0).getMsgId();
            // ....PAGE????
            cConversation.loadMoreMsgFromDB(mesId, pagesize - count);
        }

        //get the last message the show on the text view
        if (cConversation.getAllMessages().size() > 0) {
            EMMessage message = cConversation.getLastMessage();
            EMTextMessageBody body = (EMTextMessageBody) message.getBody();
            String[] new_msg = new String[2];
            Log.d("chatCon", "initConversation: mssagefrom: " + message.getFrom());
            if (message.getFrom().equals(chatID)){
                new_msg[0] = "receive";
            }
            else new_msg[0] = "send";
            new_msg[1] = "(" + message.getMsgTime() + ")\n" + body.getMessage();
            updateListView(new_msg);
        }
    }

    //A custom Handler to refresh the UI
    Handler cHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    EMMessage message = (EMMessage) msg.obj;
                    //just support only the text content
                    EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                    //add new msg
                    //cContentText.setText(cContentText.getText() + "\n(" + message.getMsgTime() + ")\n" + body.getMessage());
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //add listener
        EMClient.getInstance().chatManager().addMessageListener(cMessageListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //remove listener
        EMClient.getInstance().chatManager().removeMessageListener(cMessageListener);
    }

    //-----------------------------------------------
    //the implements of the listener
    @Override
    public void onMessageReceived(List<EMMessage> list) {
        //look through the message list
        for (EMMessage message : list) {
            if (message.getFrom().equals(chatID)) {
                //mark the message as read
                cConversation.markMessageAsRead(message.getMsgId());
                //the listener callback isn't the thread of ui
                //ust the handler to refresh ui
                //Message msg = cHandler.obtainMessage();
                //msg.what = 0;
                //msg.obj = message;
                //cHandler.sendMessage(msg);
                EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                String[] new_msg = new String[2];
                new_msg[0] = "receive";
                new_msg[1] = "(" + message.getMsgTime() + ")\n" + body.getMessage();
                //receive successfully, update the ListView
                updateListView(new_msg);
            } else {
                //if receive a msg from another people ...
                //i have no idea ! whats the hell!
                //i think it should be transmitted to mainActivity and stored in the local database
                /*
                * */
            }
        }
    }

    public void updateListView(String[] new_msg) {
        if (lChat == null || lChat.isEmpty()) {
            lChat = new ArrayList<>();
            lChat.add(new_msg);
            adapter = new ChatAdapter(getApplicationContext(), lChat, R.id.chat_text);
            cContent.setAdapter(adapter);
        } else {
            adapter.addView(new_msg);
        }
        cContent.setSelection(cContent.getCount() - 1);
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {
        for (int i = 0; i < list.size(); i++) {
            EMMessage cmdMessage = list.get(i);
            EMCmdMessageBody body = (EMCmdMessageBody) cmdMessage.getBody();
            Log.d("onCmdMessageReceived", body.action());
        }
    }

    @Override
    public void onMessageReadAckReceived(List<EMMessage> list) {

    }

    @Override
    public void onMessageDeliveryAckReceived(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }

}
