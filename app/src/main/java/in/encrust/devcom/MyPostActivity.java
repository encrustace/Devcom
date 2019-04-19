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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.encrust.devcom.AdapterClasses.MyPostAdapter;
import in.encrust.devcom.ModelClasses.PostModel;

public class MyPostActivity extends AppCompatActivity {

    List<PostModel> postList;
    TextView noData;
    ContentLoadingProgressBar progressBar;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    String receiverUid;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        receiverUid = mAuth.getCurrentUser().getUid();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My posts");

        progressBar = findViewById(R.id.activity_main_progressbar);
        noData = findViewById(R.id.activity_main_nodata);
        recyclerView = findViewById(R.id.activity_main_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        loadMyPosts();
    }


    public void loadMyPosts() {
        postList = new ArrayList<>();
        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    postList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PostModel post = snapshot.getValue(PostModel.class);

                        if (post.getReceiveruid().equals(receiverUid)) {
                            noData.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            progressBar.hide();
                            postList.add(post);
                        } else {
                            progressBar.hide();
                            noData.setVisibility(View.VISIBLE);
                            noData.setText("You did not have any Post");
                        }
                    }

                    MyPostAdapter myPostAdapter = new MyPostAdapter(MyPostActivity.this, postList);

                    recyclerView.setLayoutManager(new LinearLayoutManager(MyPostActivity.this));
                    recyclerView.setAdapter(myPostAdapter);

                } else {
                    progressBar.hide();
                    recyclerView.setVisibility(View.INVISIBLE);
                    noData.setVisibility(View.VISIBLE);
                    noData.setText("You did not have any Post");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyPostActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
