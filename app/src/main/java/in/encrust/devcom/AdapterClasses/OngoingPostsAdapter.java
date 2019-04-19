package in.encrust.devcom.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.button.MaterialButton;
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
import java.util.Objects;

import in.encrust.devcom.ChatActivity;
import in.encrust.devcom.ModelClasses.PostModel;
import in.encrust.devcom.R;
import in.encrust.devcom.SubmitWork;

public class OngoingPostsAdapter extends RecyclerView.Adapter<OngoingPostsAdapter.ViewHolder> {

    private Context context;
    private List<PostModel> postList;
    private String userid;

    //Default constructor
    public OngoingPostsAdapter() {

    }

    public OngoingPostsAdapter(Context context, List<PostModel> postList) {
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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        userid = firebaseUser.getUid();


        if (post.getSenderuid().equals(userid)) {
            databaseReference.child("Users").child(post.getReceiveruid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final String receivername = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                        final String receiverprofilepicture = dataSnapshot.child("profilepicture").getValue().toString();

                        viewHolder.userName.setText(receivername);
                        Glide.with(context).load(receiverprofilepicture).into(viewHolder.profilePicture);

                        viewHolder.buttonThree.setText("Submit");
                        viewHolder.buttonThree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, SubmitWork.class);
                                intent.putExtra("postkey", post.getPostkey());
                                context.startActivity(intent);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {

            databaseReference.child("Users").child(post.getSenderuid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final String sendername = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                        final String senderprofilepicture = dataSnapshot.child("profilepicture").getValue().toString();

                        viewHolder.userName.setText(sendername);
                        Glide.with(context).load(senderprofilepicture).into(viewHolder.profilePicture);

                        viewHolder.buttonThree.setText("Check Status");
                        viewHolder.buttonThree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        viewHolder.postName.setText(post.getPostname());
        viewHolder.postTime.setText(post.getTime());
        viewHolder.buttonOne.setVisibility(View.INVISIBLE);

        viewHolder.buttonTwo.setText("Chat");
        viewHolder.buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                if (post.getReceiveruid().equals(userid)) {
                    intent.putExtra("userId", post.getSenderuid());
                } else {
                    intent.putExtra("userId", post.getReceiveruid());
                }
                context.startActivity(intent);
            }
        });

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
