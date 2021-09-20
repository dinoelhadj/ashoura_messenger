package pal.dev.ashourav2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SearchUserRVAdapter extends RecyclerView.Adapter<SearchUserRVAdapter.ViewHolder> implements View.OnClickListener {

    private static ArrayList<SearchUserModel> searchusers;
    private LayoutInflater mInflater;
    Context context;


    public SearchUserRVAdapter(Context context, ArrayList<SearchUserModel> searchusers ){
        this.mInflater = LayoutInflater.from(context);
        this.searchusers = searchusers;
        this.context = context;
        Log.e("Constructor, searchusers count:", "" + searchusers.size());
    }

    @Override
    public void onClick(View view) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView searchuser_name;
        TextView searchuser_email;
        ConstraintLayout searchuser_layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            searchuser_email = itemView.findViewById(R.id.searchuser_email);
            searchuser_name = itemView.findViewById(R.id.searchuser_name);
            searchuser_layout = itemView.findViewById(R.id.searchuser_layout);

        }
    }

    @NonNull
    @Override
    public SearchUserRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.searchuser, parent, false);
        DatabaseReference getName = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
        getName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String myName = snapshot.child("name").getValue().toString();
                try {
                    SharedPreferences sharedPref = context.getSharedPreferences("myName", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("myName", myName);
                    editor.apply();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return new SearchUserRVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUserRVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.searchuser_email.setText(searchusers.get(position).getEmail());
        holder.searchuser_name.setText(searchusers.get(position).getName());

        holder.searchuser_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("my uid",FirebaseAuth.getInstance().getUid());
                Log.e("usersearch uid",searchusers.get(position).getUid());
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .child("conversations")
                        .child(searchusers.get(position).getUid());
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean bool = false;
                        try {
                            if(snapshot.child("Name").getValue().toString().length()>0) {
                                bool = true;
                            }
                        } catch (Exception e) {
                            //ignore
                        }
                        if (bool){
                            try {
                                String conversationID = snapshot.child("conversationID").getValue().toString();
                                String senderID = searchusers.get(position).getUid();
                                String name = snapshot.child("Name").getValue().toString();
                                String lastMessage = snapshot.child("last_message").getValue().toString();
                                Boolean isSeen = Boolean.parseBoolean(snapshot.child("seen").getValue().toString());
                                Long timestamp = Long.parseLong(snapshot.child("timestamp").getValue().toString());

                                ConversationModel conversation = new ConversationModel(conversationID,senderID,name,lastMessage,isSeen,timestamp);
                                if (!lastMessage.equals("1stNullMsg")) {
                                    //load conversation
                                    if (!conversation.isSeen()) {
                                        // Write a message to the database
                                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = database
                                                .getReference("Users");

                                        myRef.child(mAuth.getCurrentUser().getUid())
                                                .child("conversations")
                                                .child(conversation.senderID)
                                                .child("seen")
                                                .setValue("true");
                                        myRef.child(mAuth.getCurrentUser().getUid())
                                                .child("conversations")
                                                .child(conversation.senderID)
                                                .child("seenTimeStamp")
                                                .setValue(System.currentTimeMillis());
                                    }
                                }

                                Bundle bundle = new Bundle();
                                bundle.putString("conversationID", conversation.conversationID);
                                bundle.putString("chatter", conversation.Name);
                                bundle.putString("chatterID", conversation.senderID);

                                ChatFragment chatFragment = new ChatFragment();
                                chatFragment.setArguments(bundle);

                                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, chatFragment).addToBackStack(null).commit();

                            } catch (Exception e){
                                e.printStackTrace();
                            }


                        } else {
                            //Creating an empty conversation
                            DatabaseReference cnvRef = FirebaseDatabase.getInstance().getReference("conversations").push();
                            Log.e("push",cnvRef.getKey());
                            cnvRef.child("ConversationInitializer").setValue("0");
                            userRef.child("conversationID").setValue(cnvRef.getKey());
                            userRef.child("Name").setValue(searchusers.get(position).getName());
                            userRef.child("last_message").setValue("1stNullMsg");
                            userRef.child("seen").setValue(false);
                            userRef.child("timestamp").setValue("1000000000000");



                            //adding conversation to other user conversations
                            DatabaseReference otherusrref = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(searchusers.get(position).getUid())
                                    .child("conversations")
                                    .child(FirebaseAuth.getInstance().getUid());
                            otherusrref.child("conversationID").setValue(cnvRef.getKey());
                            SharedPreferences sharedPref = context.getSharedPreferences("myName", Context.MODE_PRIVATE);
                            String myName =  sharedPref.getString("myName", "myName");
                            otherusrref.child("Name").setValue(myName);
                            otherusrref.child("last_message").setValue("1stNullMsg");
                            otherusrref.child("seen").setValue(false);
                            otherusrref.child("timestamp").setValue("1000000000000");


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return searchusers.size();
    }
}
