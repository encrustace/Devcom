package in.encrust.devcom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class Payment extends AppCompatActivity {
    TextView amountView;
    EditText transactionView;
    String transactionid;
    Button confirmButton;
    String amount;
    String postkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        amountView = findViewById(R.id.payment_amount);
        transactionView = findViewById(R.id.payment_transaction_id);
        confirmButton = findViewById(R.id.payment_submit);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        int a = Integer.parseInt(Objects.requireNonNull(bundle.getString("amount")))/2;
        amount = Integer.toString(a);
        postkey = bundle.getString("postkey");
        amountView.setText(amount);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

    }

    private void updateData(){
        transactionid = transactionView.getText().toString();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Completed").child(postkey);

        HashMap postmap = new HashMap();

        postmap.put("secondpaidamount", amount);
        postmap.put("secondtransaction", transactionid);

        databaseReference.updateChildren(postmap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                startActivity(new Intent(Payment.this, CompletedPosts.class));
            }
        });
    }
}
