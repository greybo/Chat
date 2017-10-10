package com.chat.dao.net;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chat.dao.ObjectDao;
import com.chat.entity.Chat;
import com.chat.utils.ChatConst;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by m on 23.09.2017.
 */

public class ChatDao extends ObjectDao {

    private DatabaseReference chatRef;
    private String objectId = null;

    public ChatDao(Handler handler) {
        super(handler);
        if (chatRef == null) {
            chatRef = FirebaseDatabase.getInstance().getReference(ChatConst.CHAT_DATABASE_PATH);
            chatRef.keepSynced(true);
        }
    }

    public void save(Chat chat) {
        if (chat == null) {
            error(ChatConst.HANDLER_RESULT_ERR);
            return;
        }
        objectId = chatRef.push().getKey();
        chat.setObjectId(objectId);
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

    public void readAllByObjectId(final String objectId) {
        Query query;
        if (objectId != null) {
            query = chatRef.orderByKey().startAt(objectId);
        } else {
            query = chatRef.orderByChild(ChatConst.CHAT_DATABASE_PATH);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Chat> chats = new ArrayList<>();
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    Chat chat = s.getValue(Chat.class);
                    if (chat.equalsTokens() && (objectId == null || !chat.getObjectId().equals(objectId))) {
                        chats.add(chat);
                        Log.i(ChatConst.TAG + "1", "chatDao filter chat: " + chat.toString());
                    }
                }
                success(ChatConst.HANDLER_CHAT_LIST, chats);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(ChatConst.TAG, "onCancelled: ");
                error(ChatConst.HANDLER_RESULT_ERR);
            }
        });
    }

    public void readAllByToken(final String companionToken) {
        final List<Chat>list=new ArrayList<>();
        Query query = chatRef.orderByChild(ChatConst.CHAT_DATABASE_PATH);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    Chat chat = s.getValue(Chat.class);
                    if (chat.equalsTokens(companionToken)){
                        list.add(chat);
//                        Log.i(ChatConst.TAG,"readAllByToken: "+chat.toString());
                    }

                }
                success(ChatConst.HANDLER_CHAT_LIST, list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                error(ChatConst.HANDLER_RESULT_ERR);
            }
        });
    }

    public void updateByMap(String objectId, Map<String, Object> update) {
        chatRef.child(objectId).updateChildren(update);
    }
    public void updateByObject(final Chat chat) {
        if (chat.getObjectId() == null) return;

        chat.setLastUpdate(new Date().getTime());
        chatRef.child(chat.getObjectId())
                .getRef().setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    success(ChatConst.HANDLER_RESULT_OK, chat);
                }
            }
        });
    }
}
