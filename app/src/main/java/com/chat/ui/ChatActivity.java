package com.chat.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.chat.R;
import com.chat.adapter.AdapterChat;
import com.chat.api.Manager;
import com.chat.entity.Chat;
import com.chat.entity.Request;
import com.chat.entity.User;
import com.chat.fcm.MyFirebaseMessagingService;
import com.chat.utils.ChatConst;
import com.chat.utils.ChatUtil;

import java.util.ArrayList;
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
    @BindView(R.id.addresses_confirm_root_view)
    RelativeLayout rootView;
    private static final String TAG = "log_tag";
    private AdapterChat adapter;
    private Manager managerApi;
    private User currentUser;
    private User companionUser;
    private String objectId;
    private int heightDiff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        managerApi = new Manager(handler);
        if (getIntent().getExtras() != null) {
            String token = getIntent().getExtras().getString("token", "");
            managerApi.getChatDao().pagination(token);
            managerApi.getUserDao().findUserAll(token);
        }
        keyboardSensor();
    }

    private void keyboardSensor() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff2 = rootView.getRootView().getHeight() - rootView.getHeight();
                if (heightDiff != heightDiff2) {
                    if (adapter != null)
                        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
                }
                heightDiff = heightDiff2;
            }
        });
    }

    @OnClick({R.id.sendButton})
    public void submit(View view) {
        switch (view.getId()) {
            case R.id.sendButton:
                String msg = textMsg.getText().toString();
                if (!msg.isEmpty()) {
                    managerApi.send(prepareSend(msg));
                }
                textMsg.setText(null);
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ChatConst.HANDLER_CHAT_LIST:
                    List<Chat> list = (List<Chat>) msg.obj;
                    if (list.size() > 0) objectId = list.get(list.size() - 1).getObjectId();
                    addChatToAdapter(list);
                    break;
                case ChatConst.HANDLER_RESULT_COMPAMION_USER:
                    companionUser = (User) msg.obj;
                    break;
                case ChatConst.HANDLER_RESULT_CURRENT_USER:
                    currentUser = (User) msg.obj;
                    break;
                case ChatConst.HANDLER_RECEIVE_MSG:
                    Chat chat = (Chat) msg.obj;
                    if (companionUser.getToken().equals(chat.getCompanionToken()))
                        addChatToAdapter(chat);
                    break;
            }
        }
    };

    private void addChatToAdapter(final Chat chat) {
        addChatToAdapter(new ArrayList<Chat>() {{
            add(chat);
        }});
    }

    private void addChatToAdapter(List<Chat> list) {
        if (adapter != null) {
            adapter.addAllToAdapter(list);
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

    private Request prepareSend(String msg) {
        String objectId = managerApi.getChatDao().createObjectId();
        Chat chat = new Chat();
        chat.setObjectId(objectId);
        chat.setMessage(msg);
        chat.setCurrentToken(companionUser.getToken());
        chat.setCompanionToken(currentUser.getToken());
        chat.setLastUpdate(new Date().getTime());

        managerApi.getChatDao().saveOrUpdate(chat);
        Request request = new Request();
        request.setTo(companionUser.getToken());
        request.getData().setMessage(ChatUtil.toJson(chat));

        addChatToAdapter(chat);

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
