package com.chat.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.chat.R;
import com.chat.adapter.AdapterChat;
import com.chat.api.Manager;
import com.chat.entity.Chat;
import com.chat.entity.Request;
import com.chat.entity.User;
import com.chat.fcm.MyFirebaseMessagingService;
import com.chat.utils.ChatConst;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {
    @BindView(R.id.textMsg)
    EditText textMsg;
    @BindView(R.id.recycler_view_chat)
    RecyclerView recyclerView;

    private AdapterChat adapter;
    private Manager managerApi;
    private User currentUser;
    private User companionUser;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        managerApi = new Manager(handler);
        if (getIntent().getExtras() != null) {
            String token = getIntent().getExtras().getString("companionToken", "");
            managerApi.getUserDao().findUserByToken(token, false);
            managerApi.getUserDao().findUserByToken(FirebaseInstanceId.getInstance().getToken(), true);

        }
    }

    @OnClick(R.id.sendButton)
    public void submit(View view) {
        managerApi.send(prepareSend(textMsg.getText().toString()));
        textMsg.setText(null);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ChatConst.HANDLER_CHAT_LIST:
                    List<Chat> list = (List<Chat>) msg.obj;
                    if (list.size() > 0) key = list.get(list.size() - 1).getObjectId();
                    addChatToAdapter(list);
                    break;
                case ChatConst.HANDLER_RESULT_COMPAMION_USER:
                    companionUser = (User) msg.obj;
                    getChat();
                    break;
                case ChatConst.HANDLER_RESULT_CURRENT_USER:
                    currentUser = (User) msg.obj;
                    getChat();
                    break;
                case ChatConst.HANDLER_RECIVE_MSG:
                    String token = (String) msg.obj;
                    if (companionUser.getToken().equals(token))
                        managerApi.getChatDao().filterChat(token, key);
                    break;
            }
        }
    };

    private void getChat() {
        if (currentUser != null && companionUser != null)
            managerApi.getChatDao().filterChat(companionUser.getToken(), key);
    }

    private void addChatToAdapter(List<Chat> list) {
        if (adapter != null) {
            adapter.addList(list);
        } else {
            createAdapter(list);
        }
        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);

    }

    private void createAdapter(List<Chat> list) {
        adapter = new AdapterChat(list, handler);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }


    Chat chat = new Chat();

    private Request prepareSend(String msg) {
        Request request = new Request();
        request.setTo(companionUser.getToken());
        request.getData().setCurrentName(currentUser.getName());
        request.getData().setMessage(msg);
        request.getData().setCompanionToken(currentUser.getToken());

        chat.setMessage(msg);
        chat.setCurrentToken(currentUser.getToken());
        chat.setCompanionToken(companionUser.getToken());
        chat.setLastUpdate(new Date().getTime());
        addChatToAdapter(new ArrayList<Chat>() {{
            add(chat);
        }});
        //TODO
        managerApi.getChatDao().save(chat);

        return request;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyFirebaseMessagingService.setHandler(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyFirebaseMessagingService.setHandler(handler);
    }
}
