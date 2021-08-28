package pal.dev.ashourav2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.TimeZone;

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;

public class MainActivity extends AppCompatActivity {
    public ExpandableBottomBar expandableBottomBar;
    FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("TimeZone", TimeZone.getDefault().getDisplayName(false,TimeZone.SHORT));
        fragmentContainer = findViewById(R.id.fragmentContainer);
        expandableBottomBar = findViewById(R.id.expandable_bottom_bar);
        expandableBottomBar.setVisibility(View.GONE);

        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragmentContainer, new LoginFragment(), "LoginFragmentTag").
                commit();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mAuth.getCurrentUser() != null){
                    databaseReference.child("Users").child(mAuth.getUid()).child("last_seen").setValue(System.currentTimeMillis());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}