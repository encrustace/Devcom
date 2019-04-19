package in.encrust.devcom.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import in.encrust.devcom.ModelClasses.PostModel;
import in.encrust.devcom.PostActivity;
import in.encrust.devcom.R;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<PostModel> postList;
    DatabaseReference databaseReference;

    //Default constructor
    public PostsAdapter() {

    }

    public PostsAdapter(Context context, List<PostModel> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final PostModel post = postList.get(i);

        viewHolder.postName.setText(post.getPostname());
        viewHolder.postBudget.setText("Budget: " + post.getPostbudget() + "$");
        viewHolder.postTime.setText(post.getTime());
        viewHolder.postDescription.setText(post.getPostdescription());

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


        viewHolder.openPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostActivity.class);

                intent.putExtra("postkey", post.getPostkey());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName, postName, postBudget, postTime, postDescription;
        ImageView profilePicture;
        CardView openPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.post_content_username);
            postName = itemView.findViewById(R.id.post_content_postname);
            postBudget = itemView.findViewById(R.id.post_content_postbudget);
            postTime = itemView.findViewById(R.id.post_content_posttime);
            postDescription = itemView.findViewById(R.id.post_content_postdescription);
            profilePicture = itemView.findViewById(R.id.post_content_profilepicture);
            openPost = itemView.findViewById(R.id.post_content_cardview);
        }
    }
}