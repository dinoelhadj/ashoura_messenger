package pal.dev.ashourav2;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ConversationsRVAdapter extends RecyclerView.Adapter<ConversationsRVAdapter.ViewHolder> implements View.OnClickListener {
    private static ArrayList<ConversationModel> conversations;
    private LayoutInflater mInflater;

    public ConversationsRVAdapter(Context context, ArrayList<ConversationModel> conversations ){
        this.mInflater = LayoutInflater.from(context);
        this.conversations = conversations;
        Log.e("Constructor, cnv count:", "" + conversations.size());
    }

    @Override
    public void onClick(View view) {

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView SenderTV;
        TextView lastMsgTv;
        TextView timeCnvTV;
        ConstraintLayout conversationLayout;

        public ViewHolder(View view) {
            super(view);
            SenderTV = view.findViewById(R.id.senderTV);
            lastMsgTv = view.findViewById(R.id.lastMsgTV);
            timeCnvTV = view.findViewById(R.id.timeOfCnvTV);
            conversationLayout = view.findViewById(R.id.conversationLayout);
        }
    }



    @Override
    public ConversationsRVAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.conversation, viewGroup, false);

        return new ConversationsRVAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ConversationsRVAdapter.ViewHolder viewHolder, final int position) {

        viewHolder.SenderTV.setText(conversations.get(position).getName());
        viewHolder.lastMsgTv.setText(conversations.get(position).getLastMessage());

        int hour = (int) ((conversations.get(position).timestamp / 1000) % 86400) / 3600;
        int minute = (int) (((conversations.get(position).timestamp / 1000) % 86400) % 3600) / 60;
        String htime = "" + hour;
        String mtime = "" + minute;
        if (hour < 10) {
            htime = "0" + hour;
        }
        if (minute < 10) {
            mtime = "0" + minute;
        }
        Long timeDiffrence = System.currentTimeMillis() - conversations.get(position).timestamp;
        if (timeDiffrence < 60000){
            viewHolder.timeCnvTV.setText("Now");
        }else if ( timeDiffrence < 3600000 && timeDiffrence > 60000) {
            viewHolder.timeCnvTV.setText((timeDiffrence / 60000) + " min");
        } else if (timeDiffrence > 3600000 && timeDiffrence < (3600000 * 1.3)){
            viewHolder.timeCnvTV.setText("1h");
        } else if (timeDiffrence > (3600000 * 1.3) && timeDiffrence < (3600000 * 1.7)){
            viewHolder.timeCnvTV.setText("1h and half");
        } else {
            viewHolder.timeCnvTV.setText(htime + ":" + mtime);
        }
        if (!conversations.get(position).isSeen()){
            viewHolder.SenderTV.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.lastMsgTv.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.timeCnvTV.setTypeface(Typeface.DEFAULT_BOLD);
        }
        viewHolder.conversationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("item click",conversations.get(position).getName());
                if (!conversations.get(position).isSeen()){
                    // Write a message to the database
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database
                            .getReference("Users");
                    // Read from the database
                    /*myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            String value = dataSnapshot.child(mAuth.getCurrentUser().getUid())
                                    .child(conversations.get(position).senderID)
                                    .child("seen").getValue(String.class);
                            Log.e("ds key",dataSnapshot.getKey());
                            Log.e("ds current user id",mAuth.getCurrentUser().getUid());
                            Log.e
                            Log.e("isSeen",value);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                    */

                    myRef.child(mAuth.getCurrentUser().getUid())
                            .child("conversations")
                            .child(conversations.get(position).senderID)
                            .child("seen")
                            .setValue("true");
                    myRef.child(mAuth.getCurrentUser().getUid())
                            .child("conversations")
                            .child(conversations.get(position).senderID)
                            .child("seenTimeStamp")
                            .setValue(System.currentTimeMillis());
                }

                Bundle bundle = new Bundle();
                bundle.putString("conversationID", conversations.get(position).conversationID);
                bundle.putString("chatter", conversations.get(position).Name);
                bundle.putString("chatterID", conversations.get(position).senderID);

                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, chatFragment).addToBackStack(null).commit();

            }
        });
    }


    @Override
    public int getItemCount() {
        return conversations.size();
    }
}
