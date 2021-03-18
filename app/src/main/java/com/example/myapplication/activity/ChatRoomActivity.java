package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishMessageInfo;
import com.backendless.messaging.PublishOptions;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.rt.messaging.Channel;
import com.backendless.rt.messaging.MessageInfoCallback;
import com.example.myapplication.R;
import com.example.myapplication.adapter.MessageListAdapter;
import com.example.myapplication.data.BaseMessage;
import com.example.myapplication.data.ChatHistory;
import com.example.myapplication.utility.TestApplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * this is the class that manages the functioning of the chat. All messages from a group will be
 * displayed and new messages can be sent.
 */
public class ChatRoomActivity extends AppCompatActivity {

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    public static final String TAG = "RTChat";
    private EditText message;
    private Channel channel;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        List messageList = new ArrayList <BaseMessage>();
        mMessageRecycler = findViewById(R.id.reyclerview_message_list);

        //I create and set the message adapter
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

        message = findViewById(R.id.edittext_chatbox);
        send= findViewById(R.id.button_chatbox_send);

        //I recover the messages already sent and add them to the list
        retrieveMessageHistory(messageList);
        mMessageAdapter.notifyDataSetChanged();

        // this will be the name of the chat channel
        final String channelName="chat "+ TestApplication.group.getName();

        //I subscribe to the communication channel with its API and set the message publishing options
        channel = Backendless.Messaging.subscribe(channelName);
        PublishOptions publishOptions = new PublishOptions();
        publishOptions.setPublisherId(TestApplication.user.getProperty("username").toString());
        publishOptions.putHeader( "groupId", TestApplication.group.getObjectId() );

        //I join the channel
        channel.addJoinListener(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                //if the connection to the chat was successful, I send everyone the message "I joined the chat!"
                Backendless.Messaging.publish(channelName, " I joined the chat!", publishOptions, new AsyncCallback<MessageStatus>() {
                    @Override
                    public void handleResponse(MessageStatus response) {
                        Log.d(TAG, "Sent joined " + response);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        ChatRoomActivity.this.handleFault(fault);
                    }
                });


            }

            @Override
            public void handleFault(BackendlessFault fault) {
                ChatRoomActivity.this.handleFault(fault);
            }
        });

        //when a message is sent on the channel and I receive it
        channel.addMessageListener(new MessageInfoCallback() {
            @Override
            public void handleResponse(PublishMessageInfo message) {
                Log.i( "MYAPP", "Published message - " + message.getMessage() );
                Log.i( "MYAPP", "Publisher ID - " + message.getPublisherId() );
                Log.i( "MYAPP", "Message headers - " + message.getHeaders().toString() );
                Log.i( "MYAPP", "Message subtopic " + message.getSubtopic() );
                //I insert the message in the adapter and display it
                BaseMessage mex= new BaseMessage();
                mex.setMessage(message.getMessage().toString());
                mex.setUser(message.getPublisherId());
                mex.setCreatedAt(new Date());
                messageList.add(mex);
                mMessageAdapter.notifyItemInserted(messageList.size() - 1);
                mMessageRecycler.scrollToPosition(messageList.size() - 1);

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                ChatRoomActivity.this.handleFault(fault);
            }
        });


        // when I press the "send" button I send the message on the chat communication channel I joined
        send.setOnClickListener(view -> {
            message.setEnabled(false);

            Backendless.Messaging.publish(channelName, message.getText().toString(),publishOptions, new AsyncCallback<MessageStatus>() {
                @Override
                public void handleResponse(MessageStatus response) {
                    Log.d(TAG, "Sent message " + response);
                    message.setText("", TextView.BufferType.EDITABLE);
                    message.setEnabled(true);
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    message.setEnabled(true);
                }
            });
        });

    }

    private void handleFault(BackendlessFault fault) {
        Log.e(TAG, fault.toString());
    }

    /**
     * function for recovering messages saved on the server
     */
    private void retrieveMessageHistory(List messageList) {
        DataQueryBuilder dataQuery = DataQueryBuilder.create();
        dataQuery.setOffset(0);
        dataQuery.setPageSize(100);
        dataQuery.setSortBy("created");
        dataQuery.setWhereClause("groupId='"+TestApplication.group.getObjectId()+"'");
        Backendless.Data.of(ChatHistory.class).find(dataQuery, new AsyncCallback<List<ChatHistory>>() {
            @Override
            public void handleResponse(List<ChatHistory> response) {
                for (ChatHistory chatHi : response){
                    BaseMessage mex = new BaseMessage();
                    mex.setMessage(chatHi.getMessageData());
                    mex.setUser(chatHi.getPublisher());
                    mex.setCreatedAt(chatHi.getCreated());
                    messageList.add(mex);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (channel != null)
            channel.leave();
    }

}
