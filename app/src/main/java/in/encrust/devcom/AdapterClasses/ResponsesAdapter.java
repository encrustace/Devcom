package in.encrust.devcom.AdapterClasses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import in.encrust.devcom.ModelClasses.PostModel;
import in.encrust.devcom.PostActivity;
import in.encrust.devcom.Profile;
import in.encrust.devcom.R;

public class ResponsesAdapter extends RecyclerView.Adapter<ResponsesAdapter.ViewHolder> {

    private Context context;
    private List<PostModel> postList;

    //Default constructor
    public ResponsesAdapter() {

    }

    public ResponsesAdapter(Context context, List<PostModel> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.commonposts_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final PostModel post = postList.get(position);

        viewHolder.postName.setText(post.getPostname());
        viewHolder.postTime.setText(post.getTime());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(post.getSenderuid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final String sendername = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                    final String senderprofilepicture = Objects.requireNonNull(dataSnapshot.child("profilepicture").getValue()).toString();

                    viewHolder.userName.setText(sendername);
                    Glide.with(context).load(senderprofilepicture).into(viewHolder.profilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String userid = firebaseUser.getUid();
        if (userid.equals(post.getReceiveruid())) {
            viewHolder.buttonOne.setVisibility(View.INVISIBLE);
        }

        viewHolder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Profile.class);

                intent.putExtra("receiveruid", post.getSenderuid());
                context.startActivity(intent);

            }
        });

        viewHolder.openPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostActivity.class);

                intent.putExtra("postkey", post.getPostkey());
                context.startActivity(intent);

            }
        });

        viewHolder.buttonTwo.setText("Accept");
        viewHolder.buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /// acceptApplication(post.getPostkey(), post.getSenderuid());
                termsWarning(post.getPostkey(), post.getSenderuid());
            }
        });

        viewHolder.buttonThree.setText("Reject");


    }

    private void termsWarning(final String postkey, final String senderuid){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setMessage("Before confirming your order to the developer you have to make a payment of half your budget.\n Do you agree to make the payment?");
        builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       acceptApplication(postkey, senderuid);
                    }
                });

        builder.setNegativeButton("Not Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void acceptApplication(final String postkey, final String senderuid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        ConstraintLayout constraintLayout = new ConstraintLayout(context);
        ConstraintLayout.LayoutParams constParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        constraintLayout.setId(0);
        constraintLayout.setLayoutParams(constParams);

        ImageView imageView = new ImageView(context);
        imageView.setBackground(context.getDrawable(R.drawable.payment));

        final EditText editText = new EditText(context);
        ConstraintLayout.LayoutParams editTextParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        editText.setHint("Enter transaction ID after payment");
        editText.setLayoutParams(editTextParams);

        constraintLayout.addView(imageView);
        constraintLayout.addView(editText);

        builder.setView(constraintLayout);
        builder.setCancelable(true);
        builder.setTitle("Pay half amount before accepting");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Applied").child(postkey);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    final String senderuid = Objects.requireNonNull(dataSnapshot.child("senderuid").getValue()).toString();
                                    String receiveruid = Objects.requireNonNull(dataSnapshot.child("receiveruid").getValue()).toString();
                                    String postname = Objects.requireNonNull(dataSnapshot.child("postname").getValue()).toString();
                                    String postdescription = Objects.requireNonNull(dataSnapshot.child("postdescription").getValue()).toString();
                                    final String postkey = Objects.requireNonNull(dataSnapshot.child("postkey").getValue()).toString();
                                    String postcatagory = Objects.requireNonNull(dataSnapshot.child("postcatagory").getValue()).toString();
                                    String postduration = Objects.requireNonNull(dataSnapshot.child("postduration").getValue()).toString();
                                    String budgettag = Objects.requireNonNull(dataSnapshot.child("budgettag").getValue()).toString();
                                    String postbudget = Objects.requireNonNull(dataSnapshot.child("postbudget").getValue()).toString();
                                    String firstpaidamount = Integer.toString(Integer.parseInt(postbudget)/2);
                                    String firsttransaction = editText.getText().toString();

                                    Calendar callForTime = Calendar.getInstance();
                                    SimpleDateFormat currenTime = new SimpleDateFormat("dd-MM-yyyy" + "  " + "hh:mm:ss");
                                    final String acceptingtime = currenTime.format(callForTime.getTime());

                                    HashMap responsemap = new HashMap();
                                    responsemap.put("senderuid", senderuid);
                                    responsemap.put("receiveruid", receiveruid);
                                    responsemap.put("postname", postname);
                                    responsemap.put("time", acceptingtime);
                                    responsemap.put("postdescription", postdescription);
                                    responsemap.put("postkey", postkey);
                                    responsemap.put("postcatagory", postcatagory);
                                    responsemap.put("postduration", postduration);
                                    responsemap.put("budgettag", budgettag);
                                    responsemap.put("postbudget", postbudget);
                                    responsemap.put("firstpaidamount", firstpaidamount);
                                    responsemap.put("firsttransaction", firsttransaction);
                                    responsemap.put("secondpaidamount", "");
                                    responsemap.put("secondtransaction", "");
                                    responsemap.put("transferred", "");

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ongoingposts");
                                    databaseReference.child(postkey).updateChildren(responsemap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.child("Applied").child(postkey).removeValue();
                                            databaseReference.child("Posts").child(postkey).removeValue();

                                            Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show();
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
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePicture;
        ConstraintLayout openPost;
        TextView userName, postName, postTime;
        MaterialButton buttonOne, buttonTwo, buttonThree;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.commonposts_content_profilepicture);
            userName = itemView.findViewById(R.id.commonposts_content_username);
            postName = itemView.findViewById(R.id.commonposts_content_postname);
            postTime = itemView.findViewById(R.id.commonposts_content_postdate);
            buttonOne = itemView.findViewById(R.id.commonposts_content_button1);
            buttonTwo = itemView.findViewById(R.id.commonposts_content_button2);
            buttonThree = itemView.findViewById(R.id.commonposts_content_button3);
            openPost = itemView.findViewById(R.id.commonposts_content_const1);

        }
    }
}
