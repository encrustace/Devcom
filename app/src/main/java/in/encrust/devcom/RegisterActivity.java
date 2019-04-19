package in.encrust.devcom;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    String registeremail;
    String registerpass;
    private TextView loginTab, registerTab;
    private TextInputLayout registerEmail;
    private TextInputLayout registerPass;
    private MaterialButton registerButton;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginTab = findViewById(R.id.activity_register_logintab);
        registerTab = findViewById(R.id.activity_register_registertab);
        registerEmail = findViewById(R.id.activity_register_email);
        registerPass = findViewById(R.id.activity_register_pass);
        registerButton = findViewById(R.id.activity_register_button);

        loginTab.setTextColor(Color.GRAY);


        loginTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registeremail = registerEmail.getEditText().getText().toString().trim();
                registerpass = registerPass.getEditText().getText().toString().trim();

                if (registeremail.isEmpty()) {
                    registerEmail.setError("Enter email");
                }
                if (registerpass.isEmpty()) {
                    registerPass.setError("Enter password");
                } else {
                    registerNew();
                }
            }
        });

    }


    private void registerNew() {
        firebaseAuth.createUserWithEmailAndPassword(registeremail, registerpass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(RegisterActivity.this, EditProfile.class));
                        } else {
                            registerEmail.setError(task.getException().getMessage());

                        }

                    }

                });
    }
}