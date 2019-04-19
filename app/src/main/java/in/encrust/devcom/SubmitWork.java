package in.encrust.devcom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class SubmitWork extends AppCompatActivity {
    final static int GALLERY_PICK = 1;
    TextView selectFile, postInstructions;
    MaterialButton submitWork;
    ProgressDialog progressDialog;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Intent intent;
    String postkey, workfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_post);


        postInstructions = findViewById(R.id.submit_post_instructions);
        selectFile = findViewById(R.id.submit_post_selectfile);
        submitWork = findViewById(R.id.submit_post_submitbutton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading file");

        storageReference = FirebaseStorage.getInstance().getReference("Workfiles");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        intent = getIntent();

        postkey = intent.getStringExtra("postkey");



        //Navigation bar and toolbar
        Toolbar toolbar = findViewById(R.id.submit_post_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Submit Work");


        //Image selector
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent().
                        setAction(Intent.ACTION_GET_CONTENT)
                        .setType("application/zip"), GALLERY_PICK);


            }
        });
    }

    //Get image data from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            final Uri fileUri = data.getData();

            progressDialog.show();
            final UploadTask uploadTask = storageReference.child(postkey).putFile(fileUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SubmitWork.this, "Something wrong", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                    while (!downloadUri.isSuccessful()) ;
                    workfile = Objects.requireNonNull(downloadUri.getResult()).toString();

                    databaseReference.child("Ongoingposts").child(postkey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String budgettag = Objects.requireNonNull(dataSnapshot.child("budgettag").getValue()).toString();
                                String firstpaidamount = dataSnapshot.child("firstpaidamount").getValue().toString();
                                String firsttransaction = dataSnapshot.child("firsttransaction").getValue().toString();
                                String postbudget = dataSnapshot.child("postbudget").getValue().toString();
                                String postcatagory = dataSnapshot.child("postcatagory").getValue().toString();
                                String postdescription = dataSnapshot.child("postdescription").getValue().toString();
                                String postduration = dataSnapshot.child("postduration").getValue().toString();
                                String postname = dataSnapshot.child("postname").getValue().toString();
                                String receiveruid = dataSnapshot.child("receiveruid").getValue().toString();
                                String senderuid = dataSnapshot.child("senderuid").getValue().toString();
                                Calendar callForTime = Calendar.getInstance();
                                SimpleDateFormat currenTime = new SimpleDateFormat("dd-MM-yyyy" + "  " + "hh:mm:ss");
                                final String completiontime = currenTime.format(callForTime.getTime());


                                HashMap postmap = new HashMap();

                                postmap.put("budgettag", budgettag);
                                postmap.put("firstpaidamount", firstpaidamount);
                                postmap.put("firsttransaction", firsttransaction);
                                postmap.put("postbudget", postbudget);
                                postmap.put("postcatagory", postcatagory);
                                postmap.put("postdescription", postdescription);
                                postmap.put("postduration", postduration);
                                postmap.put("postname", postname);
                                postmap.put("receiveruid", receiveruid);
                                postmap.put("senderuid", senderuid);
                                postmap.put("time", completiontime);
                                postmap.put("postkey", postkey);
                                postmap.put("workfile", workfile);
                                postmap.put("secondpaidamount", "");
                                postmap.put("secondtransaction", "");
                                postmap.put("transferred", "");

                                databaseReference.child("Completed").child(postkey).updateChildren(postmap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        databaseReference.child("Ongoingposts").child(postkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(SubmitWork.this, "You completed work successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SubmitWork.this, CompletedPosts.class));
                                            }
                                        });
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });
        }
    }
}
