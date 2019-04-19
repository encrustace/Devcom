package in.encrust.devcom;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.encrust.devcom.AdapterClasses.AppliedPostsAdapter;
import in.encrust.devcom.ModelClasses.PostModel;

public class AppliedPosts extends AppCompatActivity {
    List<PostModel> postList;
    ContentLoadingProgressBar progressBar;
    TextView noData;
    RecyclerView recyclerView;
    DrawerLayout drawerLayout;
    FirebaseUser firebaseUser;
    String senderuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        senderuid = firebaseUser.getUid();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Applied");

        progressBar = findViewById(R.id.activity_main_progressbar);
        noData = findViewById(R.id.activity_main_nodata);
        recyclerView = findViewById(R.id.activity_main_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        loadAppliedPosts();
    }


    public void loadAppliedPosts() {
        postList = new ArrayList<>();
        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference("Applied");
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    postList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PostModel post = snapshot.getValue(PostModel.class);

                        if (post.getSenderuid().equals(senderuid)) {
                            progressBar.hide();
                            noData.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            postList.add(post);
                        } else {
                            progressBar.hide();
                            noData.setVisibility(View.VISIBLE);
                            noData.setText("You did not applied");
                        }
                    }

                    AppliedPostsAdapter appliedPostsAdapter = new AppliedPostsAdapter(AppliedPosts.this, postList);

                    recyclerView.setLayoutManager(new LinearLayoutManager(AppliedPosts.this));
                    recyclerView.setAdapter(appliedPostsAdapter);
                } else {
                    progressBar.hide();
                    recyclerView.setVisibility(View.INVISIBLE);
                    noData.setVisibility(View.VISIBLE);
                    noData.setText("You did not applied");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AppliedPosts.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
