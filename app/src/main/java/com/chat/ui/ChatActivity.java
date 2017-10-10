package com.chat.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.chat.R;
import com.chat.adapter.ChatAdapter;
import com.chat.api.Manager;

//import com.chat.dao.local.ChatRealm;
import com.chat.dao.net.FileUploadDao;
import com.chat.entity.Chat;
import com.chat.entity.Request;
import com.chat.entity.User;
import com.chat.fcm.MyFirebaseMessagingService;
import com.chat.utils.ChatConst;
import com.chat.utils.ChatUtil;
import com.chat.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatConst.TAG;
    @BindView(R.id.textMsg)
    EditText textMsg;
    @BindView(R.id.recycler_view_chat)
    RecyclerView recyclerView;
    @BindView(R.id.addresses_confirm_root_view)
    RelativeLayout rootView;

    private ChatAdapter adapter;
    private Manager managerApi;
    private User currentUser;
    private User companionUser;
    private static Uri imageUri;
    private String objectId;
    private int heightDiff;
    private List<String> filePaths;
    private int indexPermission;
    private String tokenCompanion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        managerApi = new Manager(handler);
        filePaths = new ArrayList<>();

        if (getIntent().getExtras() != null) {
            tokenCompanion = getIntent().getExtras().getString("token", "");
            managerApi.getUserDao().findUserAll(tokenCompanion);
//            managerApi.getChatDao().readAllByToken(tokenCompanion);
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

    @OnClick({R.id.sendButton, R.id.imageGallery, R.id.imageCamera})
    public void submit(View view) {
        switch (view.getId()) {
            case R.id.sendButton:
                String msg = textMsg.getText().toString();
                prepareSend(msg);
                break;
            case R.id.imageGallery:
                indexPermission = 0;
                if (PermissionUtil.checkPermission(this, PermissionUtil.PERMISSIONS[indexPermission]))
                    startAction();
                break;
            case R.id.imageCamera:
                indexPermission = 1;
                if (PermissionUtil.checkPermission(this, PermissionUtil.PERMISSIONS[indexPermission]))
                    startAction();
                break;
        }
    }

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, ChatConst.ACTION_SELECT_IMAGE);
    }

    private void openCamera() {
        ContentValues value = new ContentValues();
        value.put(MediaStore.Images.Media.TITLE, "IMG");
        value.put(MediaStore.Images.Media.DESCRIPTION, "Camera");
        imageUri = getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, ChatConst.ACTION_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == ChatConst.ACTION_SELECT_IMAGE) {
            imageUri = data.getData();
        }
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
        int columnIndex;
        if (cursor != null) {
            columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            cursor.moveToFirst();
            filePaths.add(cursor.getString(columnIndex));
            cursor.close();
        }
        if (filePaths.size() > 0)
            prepareSend("");
        Log.i(TAG, "file uri: " + filePaths);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ChatConst.HANDLER_CHAT_LIST:
                    List<Chat> list = (List<Chat>) msg.obj;
                    if (list.size() > 0)
                        objectId = list.get(list.size() - 1).getObjectId();
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
                    Log.i(TAG, "readAll: " + chat);
                    if (companionUser.getToken().equals(chat.getCompanionToken()))
                        addChatToAdapter(chat);
                    break;
                case ChatConst.HANDLER_IMAGE_SAVE_OK:
                    sendFCM((Chat) msg.obj);
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
        adapter = new ChatAdapter(this, list, handler);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }


    private void prepareSend(String msg) {
        Chat chat = new Chat();
        if ((msg != null && msg.length() > 0) || filePaths.size() > 0) {
            chat.setMessage(msg);
            chat.setCurrentToken(companionUser.getToken());
            chat.setCompanionToken(currentUser.getToken());
            chat.setCompanionName(currentUser.getName());
            chat.setUrlFile(filePaths);
            chat.setLastUpdate(new Date().getTime());
            managerApi.getChatDao().save(chat);

            if (filePaths.size() > 0) {
                new FileUploadDao(handler).saveFile(chat);
                filePaths = new ArrayList<>();
            } else {
                sendFCM(chat);
            }
            addChatToAdapter(chat);
        }
    }

    private void sendFCM(Chat chat) {
        Request request = new Request();
        request.setTo(companionUser.getToken());
        request.getData().setMessage(ChatUtil.toJson(chat));
        managerApi.send(request);
        textMsg.setText(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_AUDIO_ACTIVITY
                && PermissionUtil.isPermissionGranted(this, permissions[0])) {
            startAction();
        } else {
            if (PermissionUtil.checkShouldShowRequestPermission(this, permissions[0])) {
                Toast.makeText(this, "Permission deferred", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startAction() {

        switch (indexPermission) {
            case 0:
                openGallery();
                break;
            case 1:
                openCamera();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyFirebaseMessagingService.setHandler(null);
        adapter = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyFirebaseMessagingService.setHandler(handler);
        managerApi.getChatDao().readAllByToken(tokenCompanion);
    }
}
