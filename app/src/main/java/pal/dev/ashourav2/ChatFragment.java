package pal.dev.ashourav2;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import static android.content.ContentValues.TAG;


public class ChatFragment extends Fragment {

    RecyclerView messages_RV;
    TextView chatterNameTV;
    TextView chatterStatusTV;
    EditText et_messageChat;
    ImageButton btn_send;
    ArrayList<MessageModel> messages;
    FirebaseAuth mAuth;




    private static final String ARG_conversationID = "conversationID";
    private static final String ARG_chatter = "chatter";
    private static final String ARG_chatterID = "chatterID";
    private String mConversationID;
    private String mChatter;
    private String mChatterID;
    private String myUserName;
    public ChatFragment() {
        // Required empty public constructor
    }
    public static ChatFragment newInstance(String conversationID, String chatter, String chatterID) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_conversationID, conversationID);
        args.putString(ARG_chatterID, chatterID);
        args.putString(ARG_chatter, chatter);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mConversationID = getArguments().getString(ARG_conversationID);
            mChatterID = getArguments().getString(ARG_chatterID);
            mChatter = getArguments().getString(ARG_chatter);

            Log.e("cnv ID", mConversationID);
            Log.e("chatter ID", mChatterID);
            Log.e("chatter Name", mChatter);
        }
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        Log.e("mConversationID",mConversationID);
        Log.e("mChatter",mChatter);
        Log.e("mChatterID",mChatterID);

        ((MainActivity) getActivity()).expandableBottomBar.setVisibility(View.GONE);
        chatterNameTV = view.findViewById(R.id.chatterNameTV);
        chatterStatusTV = view.findViewById(R.id.chatterStatusTV);
        chatterNameTV.setText(mChatter);
        messages_RV = view.findViewById(R.id.messages_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        messages_RV.setLayoutManager(linearLayoutManager);
        et_messageChat = view.findViewById(R.id.et_messageChat);
        btn_send = view.findViewById(R.id.btn_send);

        messages = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //get chatter status
        DatabaseReference getStatusRef = database.getReference("Users").child(mChatterID).child("last_seen");
        getStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("chatter Status", ""+ snapshot.getValue());
                String status = "Offline";
                try {
                    Long last_seen = Long.parseLong(""+snapshot.getValue());
                    Long diffrence = System.currentTimeMillis() - last_seen;
                    if (diffrence < (2 * 60000)) {
                        status = "Active now";
                        chatterStatusTV.setTextColor(Color.parseColor("#FFA500"));
                    } else {
                        status = "Offline";
                        chatterStatusTV.setTextColor(Color.WHITE);
                    }
                    chatterStatusTV.setText(status);
                } catch (Exception e) {e.printStackTrace();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //get my name
        DatabaseReference getNameRef = database.getReference("Users").child(mAuth.getUid());
        getNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myUserName = ""+snapshot.child("Name").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference myRef = database.getReference("conversations").child(mConversationID);
        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    System.out.println("postSnapShot key: " + postSnapshot.getKey());
                    try {
                        MessageModel message = new MessageModel(""+postSnapshot.child("sender").getValue(),
                                Long.valueOf(""+postSnapshot.child("timestamp").getValue()),
                                ""+postSnapshot.child("value").getValue());
                        if (Boolean.parseBoolean(""+postSnapshot.child("seen").getValue())) {
                            message.setSeen(true);
                            message.setSeenTimeStamp(Long.valueOf(""+postSnapshot.child("seenTimeStamp").getValue()));
                        }
                        messages.add(message);
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }


                messages.sort(Comparator.comparing(MessageModel::getTimestamp));
                //Collections.reverse(messages);

                for (int i = 0; i < messages.size(); i++) {
                    Log.e("Message [" + i + "]: ",""+ messages.get(i).value);
                }
                messages_RV.setAdapter(new MessagesRVAdapter(getContext(),messages));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentTime = "" + System.currentTimeMillis();
                //TODO if first message initialize cnv id

                if (!et_messageChat.getText().toString().isEmpty()){
                    String messageValue = et_messageChat.getText().toString();
                    String messageID = myRef.push().getKey();
                    assert messageID != null;
                    myRef.child(messageID).child("sender").setValue(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    myRef.child(messageID).child("timestamp").setValue(System.currentTimeMillis());
                    myRef.child(messageID).child("value").setValue(messageValue);
                    myRef.child(messageID).child("seen").setValue(false);
                    myRef.child(messageID).child("seenTimeStamp").setValue(null);



                    DatabaseReference last_msgCnv = database.getReference("Users");

                    //for user
                    last_msgCnv.child(mAuth.getUid())
                            .child("conversations")
                            .child(mChatterID)
                            .child("last_message")
                            .setValue(messageValue);
                    last_msgCnv.child(mAuth.getUid())
                            .child("conversations")
                            .child(mChatterID)
                            .child("seen")
                            .setValue("true");
                    last_msgCnv.child(mAuth.getUid())
                            .child("conversations")
                            .child(mChatterID)
                            .child("timestamp")
                            .setValue(currentTime);

                    //for chatter
                    last_msgCnv.child(mChatterID)
                            .child("conversations")
                            .child(mAuth.getUid())
                            .child("last_message")
                            .setValue(messageValue);
                    last_msgCnv.child(mChatterID)
                            .child("conversations")
                            .child(mAuth.getUid())
                            .child("seen")
                            .setValue("false");
                    last_msgCnv.child(mChatterID)
                            .child("conversations")
                            .child(mAuth.getUid())
                            .child("timestamp")
                            .setValue(currentTime);
                    last_msgCnv.child(mChatterID)
                            .child("conversations")
                            .child(mAuth.getUid())
                            .child("conversationID")
                            .setValue(mConversationID);
                    last_msgCnv.child(mChatterID)
                            .child("conversations")
                            .child(mAuth.getUid())
                            .child("Name")
                            .setValue(myUserName);
                    et_messageChat.setText("");
                }

            }
        });


        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).expandableBottomBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).expandableBottomBar.setVisibility(View.GONE);
    }
}