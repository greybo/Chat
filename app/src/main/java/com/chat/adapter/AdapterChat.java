package com.chat.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chat.R;
import com.chat.entity.Chat;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by m on 15.09.2017.
 */

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ViewHolder> {
    private static final String TAG = "log_chat";
    private List<Chat> list;
    private Handler handler;
    private String currentToken;

    public AdapterChat(List<Chat> list, Handler handler) {
        this.list = list;
        this.handler = handler;
        currentToken = FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public int getItemViewType(int position) {
        return (currentToken.equals(list.get(position).getCurrentToken())) ? 2 : 1;
    }

    public void addAllToAdapter(List<Chat> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public Chat getItem(int position) {
        return list.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout;
        if (viewType == 1)
            layout = R.layout.item_chat2;
        else
            layout = R.layout.item_chat;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = list.get(position);
        holder.text3.setText(chat.getMessage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text2)
        TextView text2;
        @BindView(R.id.text3)
        TextView text3;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
