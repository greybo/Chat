package com.chat.dao;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.chat.entity.User;
import com.chat.utils.ChatСonstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by m on 22.09.2017.
 */

public class UserDao extends ObjectDao {

    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private String key;

    public UserDao(Handler handler) {
        super(handler);
        if (userRef == null) {
            database = FirebaseDatabase.getInstance();
            userRef = database.getReference(ChatСonstants.USER_DATABASE_PATH);
        }
    }

    public void save(User user) {
        if (user == null) {
            error(ChatСonstants.HANDLER_RESULT_ERR);
            return;
        }
        final String key = userRef.push().getKey();
        user.setObjectId(key);

        userRef.child(key).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    success(ChatСonstants.HANDLER_RESULT_OK, key);
                } else {
                    error(ChatСonstants.HANDLER_RESULT_ERR);
                }
            }
        });
    }

    public void readAll() {
        userRef.orderByChild(ChatСonstants.USER_DATABASE_PATH)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<User> list = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null)
                                list.add(user);
                        }
                        success(ChatСonstants.HANDLER_USERS_LIST, list);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        error(ChatСonstants.HANDLER_RESULT_ERR);
                    }
                });

    }

    public void findHomeByKey(final String objectId) {
        Query query = userRef.orderByChild("objectId").equalTo(objectId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    User user = s.getValue(User.class);
                    success(ChatСonstants.HANDLER_RESULT_USER, user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                error(ChatСonstants.HANDLER_RESULT_ERR);
            }
        });
    }

}
