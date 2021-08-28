package pal.dev.ashourav2;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.ContentValues.TAG;

public class ConversationFragment extends Fragment {

    FirebaseAuth mAuth;
    RecyclerView conversations_recyclerView;
    ArrayList<ConversationModel> conversations;
    EditText et_searchCnv;


    public ConversationFragment() {
        // Required empty public constructor
    }

    public static ConversationFragment newInstance(String param1, String param2) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_conversation, container, false);

        et_searchCnv = v.findViewById(R.id.et_searchCnv);
        conversations_recyclerView = v.findViewById(R.id.conversations_recyclerView);
        conversations_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        conversations = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        DatabaseReference ConversationsREF = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).child("conversations");
        ConversationsREF.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("conversations Count: " ,""+dataSnapshot.getChildrenCount());
                Log.e("datasnapshot key: ","" + dataSnapshot.getKey());
                conversations.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    System.out.println("postSnapShot key: " + postSnapshot.getKey());
                    ConversationModel conversation = new ConversationModel(""+postSnapshot.child("conversationID").getValue(),
                            ""+postSnapshot.getKey(),
                            ""+postSnapshot.child("Name").getValue(),
                            ""+postSnapshot.child("last_message").getValue(),
                            Boolean.parseBoolean(""+postSnapshot.child("seen").getValue()),
                            Long.valueOf(""+postSnapshot.child("timestamp").getValue()));

                    Log.e("cnv id", conversation.getConversationID());
                    Log.e("cnv sender id","" + conversation.getSenderID());
                    Log.e("cnv sender", conversation.getName());
                    Log.e("cnv last_msg","" + conversation.getLastMessage());
                    Log.e("cnv seen", ""+conversation.isSeen());
                    Log.e("cnv time","" + conversation.getTimestamp());
                    conversations.add(conversation);

                }

                Log.e("cnvs count: ", ""+conversations.size());
                for (int i = 0; i < conversations.size(); i++) {
                    Log.e("cnv [" + i + "]: ",""+ conversations.get(i).getLastMessage());
                }
                conversations.sort(Comparator.comparing(ConversationModel::getTimestamp));
                Collections.reverse(conversations);
                conversations_recyclerView.setAdapter(new ConversationsRVAdapter(getContext(),conversations));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        et_searchCnv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editable.toString();
                DatabaseReference SearchRef = FirebaseDatabase.getInstance().getReference("Users");
                SearchRef.orderByChild("name").startAt(editable.toString()).limitToFirst(5).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.e("Clear","************************");
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            Log.e("child",""+postSnapshot.child("name").getValue());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        return v;
    }
}