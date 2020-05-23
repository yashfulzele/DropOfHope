package com.example.dropofhope2.Adapter;

import android.content.Context;
import android.net.Uri;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class showRequestAdapter extends RecyclerView.Adapter<showRequestAdapter.showRequestViewHolder> {
    private Context mContext;
    private List<Map<String, String>> mUploads;
    private OnItemClickListener mListener;

    public showRequestAdapter(Context context, List<Map<String, String>> uploadsRequests) {
        mContext = context;
        mUploads = uploadsRequests;
    }

    @NonNull
    @Override
    public showRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.request_items, parent, false);
        return new showRequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull showRequestViewHolder holder, int position) {
        Map<String, String> uploadCurrent = mUploads.get(position);
        String string = uploadCurrent.get("Image uri");
        holder.textView.setText(uploadCurrent.get("Message"));
        holder.textHead.setText(uploadCurrent.get("Name"));
        Picasso.get()
                .load(string)
                .placeholder(R.drawable.default_user)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public void filterList(List<Map<String, String>> filteredList) {
        mUploads = filteredList;
        notifyDataSetChanged();
    }

    public class showRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView textView, textHead;
        public ImageView imageView;

        public showRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.card_message);
            textHead = itemView.findViewById(R.id.card_text_head);
            imageView = itemView.findViewById(R.id.card_image);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
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

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete");
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (item.getItemId() == 1) {
                        mListener.OnDeleteClick(position);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
        void OnDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
