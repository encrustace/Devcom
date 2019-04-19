package in.encrust.devcom;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.encrust.devcom.AdapterClasses.PostsAdapter;
import in.encrust.devcom.ModelClasses.PostModel;

public class MainActivity extends AppCompatActivity {
    private List<PostModel> postList;
    ImageView navHeaderProfile;
    TextView navHeaderUsername, noData;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String userId;


    ArrayList<ContactsContract.Profile> list;
    PostsAdapter adapter;
    ContentLoadingProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noData = findViewById(R.id.activity_main_nodata);
        recyclerView = findViewById(R.id.activity_main_recyclerview);
        postList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Firebase casting
        firebaseAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.activity_main_drawer);
        progressBar = findViewById(R.id.activity_main_progressbar);
        navigationView = findViewById(R.id.activity_main_navigationview);
        View navView = navigationView.inflateHeaderView(R.layout.nav_header_main);

        navHeaderProfile = navView.findViewById(R.id.nav_header_profile);
        navHeaderUsername = navView.findViewById(R.id.nav_header_username);

        //Navigation bar and toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.new_post:
                        startActivity(new Intent(MainActivity.this, NewPostActivity.class));
                        break;
                    case R.id.applied_post:
                        startActivity(new Intent(MainActivity.this, AppliedPosts.class));
                        break;
                    case R.id.ongong_task:
                        startActivity(new Intent(MainActivity.this, OngoingPostActivity.class));
                        break;
                    case R.id.post_responses:
                        startActivity(new Intent(MainActivity.this, ResponsesActivity.class));
                        break;
                    case R.id.my_posts:
                        startActivity(new Intent(MainActivity.this, MyPostActivity.class));
                        break;
                    case R.id.completed_task:
                        startActivity(new Intent(MainActivity.this, CompletedPosts.class));
                        break;
                    case R.id.user_profile:
                        Intent intent = new Intent(MainActivity.this, Profile.class);
                        intent.putExtra("receiveruid", userId);
                        MainActivity.this.startActivity(intent);
                        break;
                    case R.id.log_out:
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        break;

                    case R.id.terms:
                        startActivity(new Intent(MainActivity.this, Terms.class));
                        break;

                    case R.id.help:
                        helpFun();
                        break;

                }
                return false;
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            userId = firebaseAuth.getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //Call loadPosts method
                        loadPosts();
                        loadNavHeaderData();
                    } else {
                        startActivity(new Intent(MainActivity.this, EditProfile.class));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.activity_main_drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void loadPosts() {
        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    postList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PostModel post = snapshot.getValue(PostModel.class);
                        progressBar.hide();
                        noData.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        postList.add(post);
                    }

                    PostsAdapter postsAdapter = new PostsAdapter(MainActivity.this, postList);

                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    recyclerView.setAdapter(postsAdapter);

                } else {
                    progressBar.hide();
                    recyclerView.setVisibility(View.INVISIBLE);
                    noData.setVisibility(View.VISIBLE);
                    noData.setText("No Post Available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadNavHeaderData() {
        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        userDatabaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String profilepicture = Objects.requireNonNull(dataSnapshot.child("profilepicture").getValue()).toString();
                    String username = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();

                    navHeaderUsername.setText(username);
                    Glide.with(getApplicationContext()).load(profilepicture).into(navHeaderProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void helpFun(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        EditText editText = new EditText(this);
        editText.setMinHeight(100);
        builder.setTitle("Write your problem");
        builder.setView(editText);
        builder.setCancelable(true);

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       sendMail();
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

    private void sendMail() {

    }
}
