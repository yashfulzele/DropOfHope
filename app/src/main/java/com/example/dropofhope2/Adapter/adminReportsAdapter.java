package com.example.dropofhope2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
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

public class adminReportsAdapter extends RecyclerView.Adapter<adminReportsAdapter.adminReportsViewHolder> {
    private OnItemClickListener mListener;
    private Context mContext;
    private List<Map<String, String>> mReports;

    public adminReportsAdapter(Context context, List<Map<String, String>> reports) {
        mContext = context;
        mReports = reports;
    }

    @NonNull
    @Override
    public adminReportsAdapter.adminReportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.request_items, parent, false);
        return new adminReportsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull adminReportsAdapter.adminReportsViewHolder holder, int position) {
        Map<String, String> uploadCurrent = mReports.get(position);
        holder.textHead.setText(uploadCurrent.get("Name"));
        holder.subTextHead.setText(uploadCurrent.get("Message"));
        Picasso.get()
                .load(uploadCurrent.get("Image uri"))
                .placeholder(R.drawable.default_user)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mReports.size();
    }

    public class adminReportsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textHead, subTextHead;
        public ImageView imageView;

        public adminReportsViewHolder(@NonNull View itemView) {
            super(itemView);
            textHead = itemView.findViewById(R.id.card_text_head);
            subTextHead = itemView.findViewById(R.id.card_message);
            imageView = itemView.findViewById(R.id.card_image);
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
        mReports = filteredList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
