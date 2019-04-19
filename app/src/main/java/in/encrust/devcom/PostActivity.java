package in.encrust.devcom;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    ImageView profilePicture;
    ConstraintLayout openProfile;
    TextView userName, postTime, postName, postCatagory, budgetTag, postBudget, postDuration, postDescription;
    ImageButton editPost;
    MaterialButton applyButton;
    String postkey;
    Intent intent;
    DatabaseReference databaseReference;
    FirebaseUser firebaseAuth;

    String senderuid, receiveruid, postname, posttime, postdescription, postcatagory, postduration, budgettag, postbudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        profilePicture = findViewById(R.id.activity_post_profilepicture);
        userName = findViewById(R.id.activity_post_username);
        editPost = findViewById(R.id.activity_post_editpost);
        openProfile = findViewById(R.id.activity_post_const1);
        postTime = findViewById(R.id.activity_post_posttime);
        postName = findViewById(R.id.activity_post_postname);
        postCatagory = findViewById(R.id.activity_post_postcatagory);
        budgetTag = findViewById(R.id.activity_post_budgettag);
        postBudget = findViewById(R.id.activity_post_postbudget);
        postDuration = findViewById(R.id.activity_post_postduration);
        postDescription = findViewById(R.id.activity_post_postdescription);
        applyButton = findViewById(R.id.activity_post_submit);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
        senderuid = firebaseAuth.getUid();

        intent = getIntent();
        postkey = intent.getStringExtra("postkey");

        //Toolbar
        Toolbar toolbar = findViewById(R.id.activity_post_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Description");


        databaseReference.child("Posts").child(postkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    receiveruid = dataSnapshot.child("receiveruid").getValue().toString();
                    postname = dataSnapshot.child("postname").getValue().toString();
                    posttime = dataSnapshot.child("time").getValue().toString();
                    budgettag = dataSnapshot.child("budgettag").getValue().toString();
                    postbudget = dataSnapshot.child("postbudget").getValue().toString();
                    postcatagory = dataSnapshot.child("postcatagory").getValue().toString();
                    postduration = dataSnapshot.child("postduration").getValue().toString();
                    postdescription = dataSnapshot.child("postdescription").getValue().toString();

                    if (receiveruid.equals(senderuid)) {
                        editPost.setVisibility(View.VISIBLE);
                        applyButton.setVisibility(View.INVISIBLE);
                    } else {
                        editPost.setVisibility(View.INVISIBLE);
                        applyButton.setVisibility(View.VISIBLE);
                    }


                    databaseReference.child("Users").child(receiveruid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final String receivername = dataSnapshot.child("username").getValue().toString();
                                final String receiverprofilepicture = dataSnapshot.child("profilepicture").getValue().toString();

                                userName.setText(receivername);
                                Glide.with(PostActivity.this).load(receiverprofilepicture).into(profilePicture);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    postName.setText(postname);
                    postTime.setText(posttime);
                    budgetTag.setText(budgettag);
                    postBudget.setText(postbudget);
                    postCatagory.setText(postcatagory);
                    postDuration.setText(postduration);
                    postDescription.setText(postdescription);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        databaseReference.child("Applied").child(postkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("senderuid").getValue().toString().equals(senderuid)) {
                        applyButton.setText("Already Applied");
                        applyButton.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        openProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, Profile.class);
                intent.putExtra("receiveruid", receiveruid);
                PostActivity.this.startActivity(intent);
            }
        });

        editPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, EditPost.class);
                intent.putExtra("postkey", postkey);
                PostActivity.this.startActivity(intent);
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyForWork();
            }
        });

    }

    private void applyForWork() {

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currenTime = new SimpleDateFormat("dd-MM-yyyy" + "   " + "hh:mm:ss");
        final String applyingtime = currenTime.format(callForTime.getTime());
        HashMap postmap = new HashMap();

        postmap.put("senderuid", senderuid);
        postmap.put("receiveruid", receiveruid);
        postmap.put("time", applyingtime);
        postmap.put("postkey", postkey);
        postmap.put("postname", postname);
        postmap.put("postdescription", postdescription);
        postmap.put("postcatagory", postcatagory);
        postmap.put("postduration", postduration);
        postmap.put("postbudget", postbudget);
        postmap.put("budgettag", budgettag);

        databaseReference.child("Applied").child(postkey).updateChildren(postmap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PostActivity.this, "You applied successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
