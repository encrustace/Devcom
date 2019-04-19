package in.encrust.devcom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EditProfile extends AppCompatActivity {

    final static int GALLERY_PICK = 1;
    String profilepicture;
    private ImageView profilePicture;
    private TextInputLayout userName, userMobile, userBio, userSkills, userLanguages, userCity, userState, userCountry;
    private TextView userEmail;
    private Spinner userGender;
    private Button buttonSave;
    private ProgressDialog loadingBar;
    private String userId, downloadUrl = "default", currentprofilepicture;

    //firebase
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);


        profilePicture = findViewById(R.id.edit_profile_profilepicture);
        userEmail = findViewById(R.id.edit_profile_useremail);
        userName = findViewById(R.id.edit_profile_fullname);
        userMobile = findViewById(R.id.edit_profile_mobilenumber);
        userGender = findViewById(R.id.edit_profile_usergender);
        userBio = findViewById(R.id.edit_profile_bio);
        userSkills = findViewById(R.id.edit_profile_skills);
        userLanguages = findViewById(R.id.edit_profile_languages);
        userCity = findViewById(R.id.edit_profile_cityname);
        userState = findViewById(R.id.edit_profile_statename);
        userCountry = findViewById(R.id.edit_profile_countryname);
        buttonSave = findViewById(R.id.edit_profile_savebutton);
        loadingBar = new ProgressDialog(this);


        List<String> genderlist = new ArrayList<String>();
        genderlist.add("Male");
        genderlist.add("Female");
        genderlist.add("Other");
        final ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderlist);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userGender.setAdapter(genderAdapter);


        //Firebase
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();


        //Retrieving user data
        databaseReference.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentprofilepicture = dataSnapshot.child("profilepicture").getValue().toString();
                    String useremail = dataSnapshot.child("useremail").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String usermobile = dataSnapshot.child("usermobile").getValue().toString();
                    String usergender = dataSnapshot.child("usergender").getValue().toString();
                    String userbio = dataSnapshot.child("userbio").getValue().toString();
                    String userskills = dataSnapshot.child("userskills").getValue().toString();
                    String userlanguages = dataSnapshot.child("userlanguages").getValue().toString();
                    String usercity = dataSnapshot.child("usercity").getValue().toString();
                    String userstate = dataSnapshot.child("userstate").getValue().toString();
                    String usercountry = dataSnapshot.child("usercountry").getValue().toString();

                    userName.getEditText().setText(username);
                    userEmail.setText(useremail);
                    userMobile.getEditText().setText(usermobile);
                    userBio.getEditText().setText(userbio);
                    userSkills.getEditText().setText(userskills);
                    userLanguages.getEditText().setText(userlanguages);
                    userCity.getEditText().setText(usercity);
                    userState.getEditText().setText(userstate);
                    userCountry.getEditText().setText(usercountry);

                    int genderposition = genderAdapter.getPosition(usergender);
                    userGender.setSelection(genderposition);

                    Glide.with(getApplicationContext()).load(currentprofilepicture).into(profilePicture);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Image selector
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent().
                        setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), GALLERY_PICK);


            }
        });

        //Save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

    }


    //Get image data from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            final Uri imageUri = data.getData();
            Uri gPick = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Crop.of(imageUri, gPick).asSquare().start(EditProfile.this);

        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            profilePicture.setImageURI(Crop.getOutput(data));
            uploadImage();
        }
    }

    //Upload image method
    private void uploadImage() {
        loadingBar.setMessage("Uploading profile picture");
        loadingBar.show();
        profilePicture.setDrawingCacheEnabled(true);
        profilePicture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask = storageReference.child("Profile pictures").child(userId).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Something wrong", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                while (!downloadUri.isSuccessful()) ;
                downloadUrl = downloadUri.getResult().toString();
                loadingBar.dismiss();
            }
        });
    }

    //Update profile
    private void updateProfile() {

        if (downloadUrl == null) {
            profilepicture = currentprofilepicture;
        } else {
            profilepicture = downloadUrl;
        }
        final String username = userName.getEditText().getText().toString();
        final String useremail = mAuth.getCurrentUser().getEmail();
        final String usermobile = userMobile.getEditText().getText().toString().trim();
        final String usergender = userGender.getSelectedItem().toString();
        final String userbio = userBio.getEditText().getText().toString().trim();
        final String userskills = userSkills.getEditText().getText().toString().trim();
        final String userlanguages = userLanguages.getEditText().getText().toString().trim();
        final String usercity = userCity.getEditText().getText().toString().trim();
        final String userstate = userState.getEditText().getText().toString().trim();
        final String usercountry = userCountry.getEditText().getText().toString().trim();


        checkProfilepicture(profilepicture);
        checkUsername(username);
        checkUsermobile(usermobile);
        checkUserbio(userbio);
        checkUserskills(userskills);
        checkUserlanguages(userlanguages);
        checkUsercity(usercity);
        checkUserstate(userstate);
        checkUsercountry(usercountry);

        if (checkProfilepicture(profilepicture) == true && checkUsername(username) == true && checkUsermobile(usermobile) == true
                && checkUserbio(userbio) == true && checkUserskills(userskills) == true && checkUserlanguages(userlanguages) == true
                && checkUsercity(usercity) == true && checkUserstate(userstate) == true && checkUsercountry(usercountry) == true) {
            HashMap usermap = new HashMap();

            usermap.put("profilepicture", profilepicture);
            usermap.put("username", username);
            usermap.put("useremail", useremail);
            usermap.put("usermobile", usermobile);
            usermap.put("usergender", usergender);
            usermap.put("userbio", userbio);
            usermap.put("userskills", userskills);
            usermap.put("userlanguages", userlanguages);
            usermap.put("usercity", usercity);
            usermap.put("userstate", userstate);
            usermap.put("usercountry", usercountry);
            usermap.put("rating", "0");

            databaseReference.child("Users").child(userId).updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        loadingBar.dismiss();
                        Toast.makeText(EditProfile.this, "Profile updated", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(EditProfile.this, MainActivity.class));
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(EditProfile.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    }

                }
            });
        }


    }

    private boolean checkProfilepicture(String profilepicture) {

        if (profilepicture.equals("default")) {
            Toast.makeText(this, "Select profile picture", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkUsername(String username) {

        if (username.isEmpty()) {
            userName.setError("Required");
            return false;
        } else {
            userName.setError(null);
            return true;
        }
    }

    private boolean checkUsermobile(String usermobile) {

        if (usermobile.isEmpty()) {
            userMobile.setError("Required");
            return false;
        } else {
            userMobile.setError(null);
            return true;
        }
    }


    private boolean checkUserbio(String userbio) {
        if (userbio.isEmpty()) {
            userBio.setError("Required");
            return false;
        } else {
            userBio.setError(null);
            return true;
        }
    }

    private boolean checkUserskills(String userskills) {


        if (userskills.isEmpty()) {
            userSkills.setError("Required");
            return false;
        } else {
            userSkills.setError(null);
            return true;
        }
    }

    private boolean checkUserlanguages(String userlanguages) {
        if (userlanguages.isEmpty()) {
            userLanguages.setError("Required");
            return false;
        } else {
            userLanguages.setError(null);
            return true;
        }
    }

    private boolean checkUsercity(String usercity) {
        if (usercity.isEmpty()) {
            userCity.setError("Required");
            return false;
        } else {
            userCity.setError(null);
            return true;
        }
    }

    private boolean checkUserstate(String userstate) {
        if (userstate.isEmpty()) {
            userState.setError("Required");
            return false;
        } else {
            userState.setError(null);
            return true;
        }
    }

    private boolean checkUsercountry(String usercountry) {
        if (usercountry.isEmpty()) {
            userCountry.setError("Required");
            return false;
        } else {
            userCountry.setError(null);
            return true;
        }
    }
}
