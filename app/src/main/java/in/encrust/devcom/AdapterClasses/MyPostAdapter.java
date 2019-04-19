package in.encrust.devcom.AdapterClasses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import in.encrust.devcom.PostActivity;
import in.encrust.devcom.R;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder> {

    private Context context;
    private List<PostModel> postList;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    //Default constructor
    public MyPostAdapter() {

    }

    public MyPostAdapter(Context context, List<PostModel> postList) {
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

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(post.getReceiveruid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final String receivername = dataSnapshot.child("username").getValue().toString();
                    final String receiverprofilepicture = dataSnapshot.child("profilepicture").getValue().toString();
                    viewHolder.userName.setText(receivername);
                    Glide.with(context).load(receiverprofilepicture).into(viewHolder.profilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = firebaseUser.getUid();

        if (userId.equals(post.getReceiveruid())) {

            viewHolder.buttonTwo.setVisibility(View.INVISIBLE);
            viewHolder.buttonThree.setVisibility(View.INVISIBLE);
        }


        viewHolder.buttonOne.setText("Delete Post");
        viewHolder.buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Delete Warning");
                builder.setMessage("Are you sure want to delete your Post?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String postKey = post.getPostkey();
                                deletePost(postKey);
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
        });
        viewHolder.openPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostActivity.class);

                intent.putExtra("postkey", post.getPostkey());
                context.startActivity(intent);

            }
        });
    }

    private void deletePost(String postKey) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(postKey);
        databaseReference.removeValue();
        Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePicture;
        ConstraintLayout openPost;
        TextView userName, postName, postTime;
        MaterialButton buttonOne, buttonTwo, buttonThree;

        public ViewHolder(@NonNull View itemView) {
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