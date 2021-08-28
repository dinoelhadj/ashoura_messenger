package pal.dev.ashourav2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class MessagesRVAdapter extends RecyclerView.Adapter<MessagesRVAdapter.ViewHolder>{

    private static ArrayList<MessageModel> messagesd;
    private LayoutInflater mInflater;
    private FirebaseAuth mAuth;

    ViewGroup GviewGroup;
    int GviewType;
    Context context;

    public MessagesRVAdapter(Context context, ArrayList<MessageModel> messages ){
        try{
            this.mInflater = LayoutInflater.from(context);
            this.context = context;
            messagesd = messages;
            Log.e("Constructor, count:", ""+messages.size());
        } catch (Exception e){e.printStackTrace();}
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageTV;
        LinearLayout msgLayout;
        LinearLayoutCompat msgLayoutIn;
        ImageView msgSenderIV;
        Space somespace;

        public ViewHolder(View view) {
            super(view);

            messageTV = view.findViewById(R.id.messageTV);
            msgLayout = view.findViewById(R.id.msgLayout);
            msgLayoutIn = view.findViewById(R.id.msgLayoutIn);
            msgSenderIV = view.findViewById(R.id.msgSenderImageView);
            somespace = view.findViewById(R.id.somespace);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        GviewGroup = viewGroup;
        GviewType = viewType;
        View view = mInflater.inflate(R.layout.message, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        mAuth = FirebaseAuth.getInstance();
        if (position < (messagesd.size()-1) && position > 0){
            if (!messagesd.get(position).senderID.matches(messagesd.get(position + 1).senderID)){
                viewHolder.somespace.setVisibility(View.VISIBLE);
            } else {
                viewHolder.somespace.setVisibility(View.GONE);
            }
            if (messagesd.get(position).getSenderID().matches(messagesd.get(position - 1).getSenderID())){
                Log.e("imageView","should be gone, " + messagesd.get(position).getValue() );
                //viewHolder.msgSenderIV.setBackgroundResource(R.drawable.ic_baseline_transparent);
                viewHolder.msgSenderIV.setImageDrawable(null);
            } else {
                Log.e("imageView","should not be gone, " + messagesd.get(position).getValue() );
                //viewHolder.msgSenderIV.setBackgroundResource(R.drawable.ic_baseline_person_30);
            }
        }
        if (messagesd.get(position).senderID.matches(mAuth.getCurrentUser().getUid())){
            viewHolder.msgLayout.setGravity(Gravity.RIGHT);
            viewHolder.msgLayoutIn.setBackground(ContextCompat.getDrawable(context, R.drawable.own_message_bg));
            viewHolder.msgSenderIV.setVisibility(View.GONE);
            viewHolder.messageTV.setTextColor(Color.WHITE);
        }
        else {
            viewHolder.msgLayout.setGravity(Gravity.LEFT);
            viewHolder.msgLayoutIn.setBackground(ContextCompat.getDrawable(context, R.drawable.message_bg));
            viewHolder.msgSenderIV.setVisibility(View.VISIBLE);
            viewHolder.messageTV.setTextColor(Color.parseColor("#B9000000"));
        }

        //set backgrounds
        if (messagesd.size() >= 3){
            if (messagesd.get(position).senderID.matches(mAuth.getCurrentUser().getUid())){
                if (position == 0){
                    if (messagesd.get(position).senderID.matches(messagesd.get(position + 1).getSenderID())){
                        viewHolder.msgLayoutIn.setBackground(ContextCompat.getDrawable(context, R.drawable.right_first_message_bg));
                    }
                } else if ( position == messagesd.size()-1){
                    if (messagesd.get(position).senderID.matches(messagesd.get(position - 1).getSenderID())){
                        viewHolder.msgLayoutIn.setBackground(ContextCompat.getDrawable(context, R.drawable.right_last_message_bg));
                    }
                } else {
                    if (messagesd.get(position).senderID.matches(messagesd.get(position - 1).getSenderID())
                            && messagesd.get(position).senderID.matches(messagesd.get(position + 1).getSenderID())){
                        viewHolder.msgLayoutIn.setBackground(ContextCompat.getDrawable(context, R.drawable.right_middle_message_bg));
                    } else if (messagesd.get(position).senderID.matches(messagesd.get(position + 1).getSenderID())){
                        viewHolder.msgLayoutIn.setBackground(ContextCompat.getDrawable(context, R.drawable.right_first_message_bg));
                    } else if (messagesd.get(position).senderID.matches(messagesd.get(position - 1).getSenderID())){
                        viewHolder.msgLayoutIn.setBackground(ContextCompat.getDrawable(context, R.drawable.right_last_message_bg));
                    }
                }
            } else {
                if (position == 0){
                    if (messagesd.get(position).senderID.matches(messagesd.get(position + 1).getSenderID())){
                        viewHolder.msgLayoutIn.setBackground(ContextCompat.getDrawable(context, R.drawable.left_first_message_bg));
                    }
                } else if ( position == messagesd.size()-1){
                    if (messagesd.get(position).senderID.matches(messagesd.get(position - 1).getSenderID())){
                        viewHolder.msgLayoutIn.setBackground(ContextCompat.getDrawable(context, R.drawable.left_last_message_bg));
                    }
                } else {
                    if (messagesd.get(position).senderID.matches(messagesd.get(position - 1).getSenderID())
                            && messagesd.get(position).senderID.matches(messagesd.get(position + 1).getSenderID())){
                        viewHolder.msgLayoutIn.setBackground(ContextCompat.getDrawable(context, R.drawable.left_middle_message_bg));
                    } else if (messagesd.get(position).senderID.matches(messagesd.get(position + 1).getSenderID())){
                        viewHolder.msgLayoutIn.setBackground(ContextCompat.getDrawable(context, R.drawable.left_first_message_bg));
                    } else if (messagesd.get(position).senderID.matches(messagesd.get(position - 1).getSenderID())){
                        viewHolder.msgLayoutIn.setBackground(ContextCompat.getDrawable(context, R.drawable.left_last_message_bg));
                    }
                }
            }
        }

        //set profile images

        viewHolder.messageTV.setText(messagesd.get(position).getValue());

    }

    @Override
    public int getItemCount() {
        return messagesd.size();
    }
}
