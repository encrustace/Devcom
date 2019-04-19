package in.encrust.devcom;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    ImageView profilePicture;
    ImageButton profileEdit;
    RatingBar userRating;
    TextView userName, userEmail, userLocation, userBio, userLanguages, userSkills;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    Intent intent;
    String userid, receiveruid, profilepicture, username, useremail, usercity, userstate, usercountry, userbio, userlanguages, userskills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        profilePicture = findViewById(R.id.profile_profilepicture);
        userName = findViewById(R.id.profile_username);
        userEmail = findViewById(R.id.profile_useremail);
        userRating = findViewById(R.id.profile_ratingbar);
        userLocation = findViewById(R.id.profile_userlocation);
        profileEdit = findViewById(R.id.profile_editprofile);
        userBio = findViewById(R.id.profile_userbio);
        userLanguages = findViewById(R.id.profile_userlanguages);
        userSkills = findViewById(R.id.profile_userskills);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        userid = firebaseAuth.getCurrentUser().getUid();

        intent = getIntent();
        receiveruid = intent.getStringExtra("receiveruid");
        if (receiveruid.equals(userid)) {
            profileEdit.setVisibility(View.VISIBLE);
        } else {
            profileEdit.setVisibility(View.INVISIBLE);
        }

        databaseReference.child("Users").child(receiveruid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    profilepicture = dataSnapshot.child("profilepicture").getValue().toString();
                    username = dataSnapshot.child("username").getValue().toString();
                    useremail = dataSnapshot.child("useremail").getValue().toString();
                    usercity = dataSnapshot.child("usercity").getValue().toString();
                    userstate = dataSnapshot.child("userstate").getValue().toString();
                    usercountry = dataSnapshot.child("usercountry").getValue().toString();
                    userbio = dataSnapshot.child("userbio").getValue().toString();
                    userlanguages = dataSnapshot.child("userlanguages").getValue().toString();
                    userskills = dataSnapshot.child("userskills").getValue().toString();
                    userRating.setRating(Float.parseFloat(dataSnapshot.child("rating").getValue().toString()));

                    Glide.with(getApplicationContext()).load(profilepicture).into(profilePicture);
                    userName.setText(username);
                    userEmail.setText(useremail);
                    userLocation.setText(usercity + ", " + userstate + ", " + usercountry);
                    userBio.setText(userbio);
                    userLanguages.setText(userlanguages);
                    userSkills.setText(userskills);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("Completed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, EditProfile.class));
            }
        });


    }
}
