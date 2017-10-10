package com.chat.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.R;
import com.chat.entity.Chat;
import com.chat.utils.ChatConst;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by m on 15.09.2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final String TAG = "log_chat";
    private List<Chat> chatList;
    private Handler handler;
    private String currentToken;
    private Context context;

    public ChatAdapter(Context context, List<Chat> list, Handler handler) {
        this.chatList = list;
        this.handler = handler;
        this.context = context;
        currentToken = FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public int getItemViewType(int position) {
        int i;
        Chat chat = chatList.get(position);
        if (currentToken.equals(chat.getCurrentToken())) {
            if (chat.getUrlFile() != null && chat.getUrlFile().size() > 0)
                i = 3;
            else
                i = 2;
        } else {
            if (chat.getUrlFile() != null && chat.getUrlFile().size() > 0)
                i = 1;
            else
                i = 0;
        }
        return i;
    }

    public void addAllToAdapter(List<Chat> list) {
        this.chatList.addAll(list);
        notifyDataSetChanged();
    }

    public Chat getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = 0;
        switch (viewType) {
            case 0:
                layout = R.layout.item_chat_current;
                break;
            case 1:
                layout = R.layout.item_chat_current_image;
                break;
            case 2:
                layout = R.layout.item_chat_com;
                break;
            case 3:
                layout = R.layout.item_chat_com_image;
                break;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Chat chat = chatList.get(position);
        holder.text3.setText(chat.getMessage());
        holder.text2.setText(ChatConst.sdf.format(new Date(chat.getLastUpdate())));
        if (chat.getUrlFile() != null && chat.getUrlFile().size() > 0) {
            for (String path : chat.getUrlFile()) {
                Callback callback = new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "ChatAdapter onSuccess ");
//                        progress.hideProgress();
                    }

                    @Override
                    public void onError() {
                        Log.i(TAG, "ChatAdapter onError");
//                        progress.hideProgress();
                    }
                };
                if (path.contains("https://")) {
//                    progress.showProgress();
                    Picasso.with(context)
                            .load(path)
                            .placeholder(R.drawable.placeholder)
                            .resize(250, 250)
                            .centerCrop()
                            .into(holder.imageView, callback);
                } else {
//                    progress.showProgress();
                    Picasso.with(context)
                            .load(new File(path))
                            .placeholder(R.drawable.placeholder)
                            .resize(250, 250)
                            .centerCrop()
                            .into(holder.imageView, callback);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text2)
        TextView text2;
        @BindView(R.id.text3)
        TextView text3;
        @BindView(R.id.imageChat)
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
