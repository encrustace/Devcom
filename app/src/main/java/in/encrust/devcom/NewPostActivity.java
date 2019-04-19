package in.encrust.devcom;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class NewPostActivity extends AppCompatActivity {
    private Spinner postCatagory, postDuration, budgetTag;
    private EditText postName, postDescription, postBudget;
    private MaterialButton postButton;

    //firebase
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);

        //Casting
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        postName = findViewById(R.id.activity_newpost_postname);
        postDescription = findViewById(R.id.activity_newpost_description);
        postCatagory = findViewById(R.id.activity_newpost_catagory);
        postDuration = findViewById(R.id.activity_newpost_duration);
        budgetTag = findViewById(R.id.activity_newpost_budgettag);
        postBudget = findViewById(R.id.activity_newpost_budget);
        postButton = findViewById(R.id.activity_newpost_submitbutton);

        List<String> catagorylist = new ArrayList<String>();
        catagorylist.add("Graphics & design");
        catagorylist.add("Online Marketing");
        catagorylist.add("Writing & Translation");
        catagorylist.add("Video & Animation");
        catagorylist.add("Music & Audio");
        catagorylist.add("Programming & Tech");
        catagorylist.add("Business");
        catagorylist.add("Fun & Lifestyle");

        List<String> deliverytimelist = new ArrayList<String>();
        deliverytimelist.add("1 day");
        deliverytimelist.add("2 days");
        deliverytimelist.add("3 days");
        deliverytimelist.add("4 days");
        deliverytimelist.add("5 days");
        deliverytimelist.add("6 days");
        deliverytimelist.add("7 days");
        deliverytimelist.add("8 days");
        deliverytimelist.add("9 days");
        deliverytimelist.add("10 days");
        deliverytimelist.add("11 days");
        deliverytimelist.add("12 days");
        deliverytimelist.add("13 days");
        deliverytimelist.add("14 days");
        deliverytimelist.add("15 days");
        deliverytimelist.add("16 days");
        deliverytimelist.add("17 days");
        deliverytimelist.add("18 days");
        deliverytimelist.add("19 days");
        deliverytimelist.add("20 days");
        deliverytimelist.add("21 days");
        deliverytimelist.add("22 days");
        deliverytimelist.add("23 days");
        deliverytimelist.add("24 days");
        deliverytimelist.add("25 days");
        deliverytimelist.add("26 days");
        deliverytimelist.add("27 days");
        deliverytimelist.add("28 days");
        deliverytimelist.add("29 days");
        deliverytimelist.add("30 days");


        List<String> budgettaglist = new ArrayList<String>();
        budgettaglist.add("Fixed Price");
        budgettaglist.add("Not Fixed");

        ArrayAdapter<String> catagoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, catagorylist);
        catagoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        postCatagory.setAdapter(catagoryAdapter);

        ArrayAdapter<String> deliverytimeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, deliverytimelist);
        deliverytimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        postDuration.setAdapter(deliverytimeAdapter);

        ArrayAdapter<String> budgettagAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, budgettaglist);
        budgettagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        budgetTag.setAdapter(budgettagAdapter);


        //Navigation bar and toolbar
        Toolbar toolbar = findViewById(R.id.activity_newpost_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Submit new Post");

        //Post button click
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNewTask();
            }
        });

    }

    private void postNewTask() {
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currenTime = new SimpleDateFormat("dd-MM-yyyy" + "  " + "hh:mm:ss");
        final String postkey = databaseReference.child("Posts").push().getKey();
        final String receiveruid = firebaseUser.getUid();
        final String postname = postName.getText().toString().trim();
        final String posttime = currenTime.format(callForTime.getTime());
        final String postdescription = postDescription.getText().toString().trim();
        final String postcatagory = postCatagory.getSelectedItem().toString();
        final String postduration = postDuration.getSelectedItem().toString();
        final String budgettag = budgetTag.getSelectedItem().toString();
        final String postbudget = postBudget.getText().toString().trim();

        if (postname.isEmpty()) {
            postName.setError("Required");
        } else if (postdescription.isEmpty()) {
            postDescription.setError("Required");
        } else if (postbudget.isEmpty()) {
            postBudget.setError("Required");
        } else {
            HashMap postmap = new HashMap();
            postmap.put("receiveruid", receiveruid);
            postmap.put("time", posttime);
            postmap.put("postkey", postkey);
            postmap.put("postname", postname);
            postmap.put("postdescription", postdescription);
            postmap.put("postcatagory", postcatagory);
            postmap.put("postduration", postduration);
            postmap.put("postbudget", postbudget);
            postmap.put("budgettag", budgettag);

            databaseReference.child("Posts").child(postkey).updateChildren(postmap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(NewPostActivity.this, "Posted your task", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }
            });
        }

    }
}
