package pal.dev.ashourav2;

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
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    EditText etEmailAddress;
    EditText etPassword;
    Button loginBtn;
    Button registerBtn;
    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        etEmailAddress = v.findViewById(R.id.etEmailAddress);
        etPassword = v.findViewById(R.id.etPassword);
        loginBtn = v.findViewById(R.id.loginBtn);
        registerBtn = v.findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmailAddress.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCustomToken:success");
                            Toast.makeText(getActivity(), "Signed in " + email, Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().beginTransaction().
                                    replace(R.id.fragmentContainer, new ConversationFragment(), "ChatFragmentTag").
                                    commit();

                        } else {
                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragmentContainer, new RegisterFragment(), "RegisterFragmentTag").
                        commit();
            }
        });
        return v;
    }
}