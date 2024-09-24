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

import com.example.projectprm392.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signupTextView;

    private static final String ERROR_EMAIL_EMPTY = "Email is empty";
    private static final String ERROR_PASSWORD_EMPTY = "Password is empty";
    private static final String ERROR_LOGIN_FAIL = "Login failed";
    private static final String SUCCESS_LOGIN = "Login successful";

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.mail);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_btn);
        signupTextView = findViewById(R.id.sign_un);


        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }
    private void loginUser(){

        String pass = passwordEditText.getText().toString();
        String mail = emailEditText.getText().toString();

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
        auth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    showToast(SUCCESS_LOGIN);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    showToast(ERROR_LOGIN_FAIL);
                }
            }
        });

    }
    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}