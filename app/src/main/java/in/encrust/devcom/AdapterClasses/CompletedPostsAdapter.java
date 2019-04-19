package in.encrust.devcom.AdapterClasses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.button.MaterialButton;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import in.encrust.devcom.ModelClasses.PostModel;
import in.encrust.devcom.Payment;
import in.encrust.devcom.R;

public class CompletedPostsAdapter extends RecyclerView.Adapter<CompletedPostsAdapter.ViewHolder> {

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference, postReferance, senderReference, receiverReference;
    private String userid;
    private Context context;
    private List<PostModel> postList;

    public CompletedPostsAdapter() {

    }

    public CompletedPostsAdapter(Context context, List<PostModel> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.completedposts_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final PostModel post = postList.get(position);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        senderReference = FirebaseDatabase.getInstance().getReference().child("Users").child(post.getSenderuid());
        receiverReference = FirebaseDatabase.getInstance().getReference().child("Users").child(post.getSenderuid());

        postReferance = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userid = firebaseUser.getUid();

        if (post.getSenderuid().equals(userid)) {
            viewHolder.downloadButton.setText("Work Submitted");
            viewHolder.payButton.setVisibility(View.INVISIBLE);
            receiverReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        final String receivername = dataSnapshot.child("username").getValue().toString();
                        final String profilepicture = dataSnapshot.child("profilepicture").getValue().toString();
                        viewHolder.userName.setText(receivername);
                        Glide.with(context).load(profilepicture).into(viewHolder.profilePicture);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            viewHolder.userRating.setIsIndicator(false);
            postReferance.child("Completed").child(post.getPostkey()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {

                        if (dataSnapshot.child("secondtransaction").getValue().toString().equals("")){
                            viewHolder.downloadButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(context, "Pay remaining amount", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else {
                            viewHolder.payButton.setVisibility(View.INVISIBLE);
                            viewHolder.downloadButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    databaseReference.child("Completed").child(post.getPostkey()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                String workfile = dataSnapshot.child("workfile").getValue().toString();

                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(workfile));

                                                intent.putExtra("workfile", workfile);
                                                context.startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                        }

                        viewHolder.payButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                ImageView imageView = new ImageView(context);
                                imageView.setBackground(ContextCompat.getDrawable(context,  R.drawable.payment));

                                builder.setTitle("To download project you have to make payment of rest half amount");
                                builder.setView(imageView);
                                builder.setCancelable(true);

                                builder.setPositiveButton(
                                        "Confirm",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(context, Payment.class);
                                                Bundle bundle = new Bundle();

                                                bundle.putString("amount", post.postbudget);
                                                bundle.putString("postkey", post.getPostkey());
                                                intent.putExtras(bundle);
                                                context.startActivity(intent);
                                            }
                                        });

                                builder.setNegativeButton(
                                        "Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            senderReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final String sendername = dataSnapshot.child("username").getValue().toString();
                        final String profilepicture = dataSnapshot.child("profilepicture").getValue().toString();
                        viewHolder.userName.setText(sendername);
                        Glide.with(context).load(profilepicture).into(viewHolder.profilePicture);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        viewHolder.postName.setText(post.getPostname());
        viewHolder.postTime.setText(post.getTime());
        viewHolder.postBudget.setText("Budget: " + post.getPostbudget() + "$");
        viewHolder.postDescription.setText(post.getPostdescription());
        senderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    viewHolder.userRating.setRating(Float.parseFloat(dataSnapshot.child("rating").getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        viewHolder.constOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.constOne.setMaxHeight(1000);
            }
        });

        viewHolder.userRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                databaseReference.child("Users").child(post.getSenderuid()).child("rating").setValue(rating);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constOne;
        ImageView profilePicture;
        MaterialButton downloadButton, payButton;
        RatingBar userRating;
        TextView userName, postName, postTime, postBudget, postDescription;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.completedposts_username);
            postName = itemView.findViewById(R.id.completedposts_postname);
            postTime = itemView.findViewById(R.id.completedposts_posttime);
            postBudget = itemView.findViewById(R.id.completedposts_postbudget);
            postDescription = itemView.findViewById(R.id.completedposts_postdescription);
            profilePicture = itemView.findViewById(R.id.completedposts_profilepicture);
            constOne = itemView.findViewById(R.id.completedposts_const1);
            downloadButton = itemView.findViewById(R.id.completedposts_download);
            userRating = itemView.findViewById(R.id.completedposts_ratingbar);
            payButton = itemView.findViewById(R.id.completedposts_pay);


        }
    }
}
