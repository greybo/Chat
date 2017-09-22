package com.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chat.R;
import com.chat.adapter.AdapterUser;
import com.chat.api.Manager;
import com.chat.entity.User;
import com.chat.fcm.MyFirebaseMessagingService;
import com.chat.utils.ChatСonstants;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "log_tag";
    private Manager managerApi;
    private RecyclerView recyclerView;
    private AdapterUser adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        managerApi = new Manager(handler);
        managerApi.getDao().readAll();
        MyFirebaseMessagingService.setHandler(handler);

    }

    private void createAdapter(List<User> list) {
        adapter = new AdapterUser(list, handler);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ChatСonstants.HANDLER_USERS_LIST:
                    List<User> list = (List<User>) msg.obj;
                    createAdapter(list);
                    break;
                case ChatСonstants.HANDLER_RECIVE_MSG:
//                    adapter.notifyDataSetChanged();
                    break;
                case ChatСonstants.HANDLER_USER_OBJ:

                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
            }
        }
    };

}
