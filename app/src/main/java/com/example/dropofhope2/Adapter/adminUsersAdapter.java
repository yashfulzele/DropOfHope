package com.example.dropofhope2.Adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dropofhope2.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class adminUsersAdapter extends RecyclerView.Adapter<adminUsersAdapter.adminUsersViewHolder> {
    private OnItemClickListener mListener;
    private Context mContext;
    private List<Map<String, String>> mUsers;

    public adminUsersAdapter(Context context, List<Map<String, String>> users) {
        mContext = context;
        mUsers = users;
    }

    @NonNull
    @Override
    public adminUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.users_card, parent, false);
        return new adminUsersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull adminUsersAdapter.adminUsersViewHolder holder, int position) {
        Map<String, String> uploadCurrent = mUsers.get(position);
        holder.textHead.setText(uploadCurrent.get("Name"));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class adminUsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textHead;

        public adminUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            textHead = itemView.findViewById(R.id.card_text_head);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.OnItemClick(position);
                }
            }
        }
    }

    public void filterList(List<Map<String, String>> filteredList) {
        mUsers = filteredList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
