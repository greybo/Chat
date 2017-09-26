package com.chat.dao;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chat.entity.Chat;
import com.chat.utils.ChatConst;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m on 23.09.2017.
 */

public class ChatDao extends ObjectDao {
    private FirebaseDatabase database;
    private DatabaseReference chatRef;
    private String objectId = null;

    public ChatDao(Handler handler) {
        super(handler);
        if (chatRef == null) {
            database = FirebaseDatabase.getInstance();
            chatRef = database.getReference(ChatConst.CHAT_DATABASE_PATH);
        }
    }

    public void saveOrUpdate(Chat chat) {
        if (chat == null) {
            error(ChatConst.HANDLER_RESULT_ERR);
            return;
        }

        if (chat.getObjectId() == null) {
            objectId = createObjectId();
            chat.setObjectId(objectId);
        } else {
            objectId = chat.getObjectId();
        }

        chatRef.child(objectId).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    success(ChatConst.HANDLER_RESULT_OK, objectId);
                } else {
                    error(ChatConst.HANDLER_RESULT_ERR);
                }
            }
        });
    }

    public String createObjectId() {
        return chatRef.push().getKey();
    }

    public void findChatByToken(final String companionToken) {
        String token = FirebaseInstanceId.getInstance().getToken();
        Query query = chatRef.orderByChild("currentToken").equalTo(token);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    Chat chat = s.getValue(Chat.class);
                    if (chat.getCompanionToken().equals(companionToken))
                        success(ChatConst.HANDLER_CHAT_LIST, chat);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                error(ChatConst.HANDLER_RESULT_ERR);
            }
        });
    }

    public void pagination(String token) {
        pagination(token, objectId, false);
    }

    public void pagination(String token, final String objectId) {
        pagination(token, objectId, false);
    }

    private void pagination(String token, final String objectId, final boolean startAtCurrent) {
        final String[] tokens = {FirebaseInstanceId.getInstance().getToken(), token};
        Query query;
        final int limit = 20;
        if (objectId != null) {
            query = chatRef.orderByKey().startAt(objectId).limitToLast(limit);
        } else {
            query = chatRef.orderByChild(ChatConst.CHAT_DATABASE_PATH).limitToLast(limit);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Chat> chats = new ArrayList<>();
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    Chat chat = s.getValue(Chat.class);
                    if (objectId == null || (startAtCurrent || !chat.getObjectId().equals(objectId))) {
                        if (chat.equalsTokens(tokens)) {
                            Log.i(ChatConst.TAG, "key: " + chat.getObjectId() + " " + objectId);
                            chats.add(chat);
                        }
                    }
                }
                success(ChatConst.HANDLER_CHAT_LIST, chats);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                error(ChatConst.HANDLER_RESULT_ERR);
            }
        });
    }

    public void filterStartAtCurrent(final String token, final String objectId) {
        if (objectId == null) {
            return;
        }
        pagination(token, objectId, true);
    }
}
