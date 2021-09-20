package pal.dev.ashourav2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

public class RegisterFragment extends Fragment {


    EditText etEmailAddressRegister;
    EditText etNameRegister;
    EditText etPasswordRegister;
    Button registerBtnRegister;
    Button loginBtnRegister;




    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    public RegisterFragment() {
        // Required empty public constructor
    }
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        etEmailAddressRegister = view.findViewById(R.id.etEmailAddressRegister);
        etNameRegister = view.findViewById(R.id.etNameRegister);
        etPasswordRegister = view.findViewById(R.id.etPasswordRegister);
        registerBtnRegister = view.findViewById(R.id.registerBtnRegister);
        loginBtnRegister = view.findViewById(R.id.loginBtnRegister);

        registerBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                String email = etEmailAddressRegister.getText().toString().trim();
                String name = etNameRegister.getText().toString().trim();
                String password = etPasswordRegister.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            Log.d(TAG, "onComplete: UID: " + mAuth.getUid());
                            Log.d(TAG, "onComplete: CurrentUser: " + mAuth.getCurrentUser().getUid());

                            // Write a message to the database
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Users").child(mAuth.getUid());

                            myRef.child("name").setValue(name);
                            myRef.child("email").setValue(email);

                            Toast.makeText(getContext(), "Successfully Registred!", Toast.LENGTH_SHORT).show();

                            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "signInWithCustomToken:success");
                                        Toast.makeText(getActivity(), "Signed in " + email, Toast.LENGTH_SHORT).show();
                                        getActivity().getSupportFragmentManager().beginTransaction().
                                                replace(R.id.fragmentContainer, new ConversationFragment(), "ConversationFragmentTag").
                                                commit();

                                    } else {
                                        Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                                        Toast.makeText(getActivity(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Log.d(TAG, "onComplete: Task Unsuccessful");
                            Toast.makeText(getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        loginBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragmentContainer, new LoginFragment(), "LoginFragmentTag").
                        commit();
            }
        });


        return view;
    }
}