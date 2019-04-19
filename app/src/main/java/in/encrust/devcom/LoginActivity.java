package in.encrust.devcom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView registerTab, loginTab;
    private TextInputLayout loginEmail;
    private  TextInputLayout loginPass;
    private MaterialButton loginButton;
    private  TextView recoveryLink;
    String email;
    String pass;

    private ProgressDialog progressDialog;

    private FirebaseAuth devLoginAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.activity_login_email);
        loginPass = findViewById(R.id.activity_login_pass);
        loginButton = findViewById(R.id.activity_login_button);
        recoveryLink = findViewById(R.id.recovery_link);
        registerTab = findViewById(R.id.activity_login_registertab);
        loginTab = findViewById(R.id.activity_login_logintab);

        registerTab.setTextColor(Color.GRAY);

        loginButton.setOnClickListener(this);
        recoveryLink.setOnClickListener(this);
        registerTab.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        devLoginAuth = FirebaseAuth.getInstance();
        if (devLoginAuth.getCurrentUser() != null){
            // Home activity.
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

    }

    public  void authLogin(){
        // For firebase login auth.
        devLoginAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()){
                                    // Start home activity.
                                    finish();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "Enter correct email credentials", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );

    }

   //For loging task
  public  void userLogin(){

        email = loginEmail.getEditText().getText().toString().trim();
        pass = loginPass.getEditText().getText().toString().trim();
      if (TextUtils.isEmpty(email)){
          loginEmail.setError("Enter email");
      }
      if (TextUtils.isEmpty(pass)){
          loginPass.setError("Enter password");
      }
      else {
          progressDialog.setMessage("Please wait");
          progressDialog.show();
          authLogin();
      }
  }

    @Override
    public void onClick(View v) {
        if (v == registerTab) {
            finish();
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }

        if (v == loginButton) {
            userLogin();
        }
    }

}
