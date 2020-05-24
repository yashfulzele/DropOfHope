package com.example.dropofhope2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dropofhope2.R;

import java.util.List;
import java.util.Map;

public class adminFeedbackAdapter extends RecyclerView.Adapter<adminFeedbackAdapter.adminFeedbackViewHolder>{

    private Context mContext;
    private List<Map<String, String>> mFeedback;

    public adminFeedbackAdapter (Context context, List<Map<String, String>> feedback){
        mContext = context;
        mFeedback = feedback;
    }

    @NonNull
    @Override
    public adminFeedbackAdapter.adminFeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.feedback_card, parent, false);
        return new adminFeedbackViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull adminFeedbackAdapter.adminFeedbackViewHolder holder, int position) {
        Map<String, String> uploadCurrent = mFeedback.get(position);
        holder.textHead.setText(uploadCurrent.get("Name"));
        holder.subTextHead.setText(uploadCurrent.get("Feedback"));
        holder.subSubTextHead.setText(uploadCurrent.get("Email"));
    }

    @Override
    public int getItemCount() {
        return mFeedback.size();
    }

    public class adminFeedbackViewHolder extends RecyclerView.ViewHolder {
        public TextView textHead, subTextHead, subSubTextHead;
        public adminFeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            textHead = itemView.findViewById(R.id.card_text_head);
            subTextHead = itemView.findViewById(R.id.card_feedback);
            subSubTextHead = itemView.findViewById(R.id.card_email);
        }
    }

    public void filterList(List<Map<String, String>> filteredList) {
        mFeedback = filteredList;
        notifyDataSetChanged();
    }
}
