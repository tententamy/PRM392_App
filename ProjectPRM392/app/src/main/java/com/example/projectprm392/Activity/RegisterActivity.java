package com.example.projectprm392.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projectprm392.Domain.UserDomain;
import com.example.projectprm392.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Button signupButton;
    private TextView signinTextView;
    private FirebaseAuth auth;
    FirebaseDatabase db;

    private static final String ERROR_NAME_EMPTY = "Name is empty";
    private static final String ERROR_EMAIL_EMPTY = "Email is empty";
    private static final String ERROR_PASSWORD_EMPTY = "Password is empty";
    private static final String ERROR_PASSWORD_LENGTH = "Password must be more than 6 characters";
    private static final String ERROR_REGISTER_FAIL = "Registration failed";
    private static final String SUCCESS_REGISTER = "Registration successful";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        setupInsets();

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.mail);
        passwordEditText = findViewById(R.id.password);
        signupButton = findViewById(R.id.signup_btn);
        signinTextView = findViewById(R.id.sign_in);

        signinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void createUser() {
        String userName = nameEditText.getText().toString();
        String pass = passwordEditText.getText().toString();
        String mail = emailEditText.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            showToast(ERROR_NAME_EMPTY);
            return;
        }
        if (TextUtils.isEmpty(mail)) {
            showToast(ERROR_EMAIL_EMPTY);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            showToast("Invalid email format");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            showToast(ERROR_PASSWORD_EMPTY);
            return;
        }
        if (pass.length() < 6) {
            showToast(ERROR_PASSWORD_LENGTH);
            return;
        }

        auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    UserDomain user = new UserDomain(userName, mail, pass);
                    String id = task.getResult().getUser().getUid();
                    db.getReference().child("User ").child(id).setValue(user);

                    showToast(SUCCESS_REGISTER);


                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    showToast(ERROR_REGISTER_FAIL + ": " + task.getException().getMessage());
                }
            }
        });
    }


    private void showToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}