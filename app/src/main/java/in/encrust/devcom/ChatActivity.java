package in.encrust.devcom;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import in.encrust.devcom.AdapterClasses.ChatAdapter;
import in.encrust.devcom.ModelClasses.ChatModel;

public class ChatActivity extends AppCompatActivity {
    private ImageView profilePictureUrl;
    FirebaseAuth mAuth;
    private EditText messageContent;

    ChatAdapter chatAdapter;
    List<ChatModel> chatList;
    RecyclerView recyclerView;
    String currentUid;
    private TextView senderName;
    DatabaseReference userDatabaseReference;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.activity_chat_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Recyclerview casting
        recyclerView = findViewById(R.id.activity_chat_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profilePictureUrl = findViewById(R.id.activity_chat_profilepicture);
        senderName = findViewById(R.id.activity_chat_username);
        messageContent = findViewById(R.id.activity_chat_messagecontent);
        ImageButton sendButton = findViewById(R.id.activity_chat_sendmessage);

        intent = getIntent();
        final String userId = intent.getStringExtra("userId");
        mAuth = FirebaseAuth.getInstance();
        currentUid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageContent.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(currentUid, userId, msg);
                } else {
                    Toast.makeText(ChatActivity.this, "Can not send empty message", Toast.LENGTH_SHORT).show();
                }
                messageContent.setText("");
            }
        });


        //For profile picture and fullname start
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final String username = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                    final String profilepicture = Objects.requireNonNull(dataSnapshot.child("profilepicture").getValue()).toString();

                    senderName.setText(username);
                    Glide.with(ChatActivity.this).load(profilepicture).into(profilePictureUrl);

                    readMessages(currentUid, userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void sendMessage(String senderuid, String receiveruid, String message) {

        //For time
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currenTime = new SimpleDateFormat("dd-MM-yyyy" + "  " + "hh:mm:ss");
        String messageTime = currenTime.format(callForTime.getTime());

        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> chatmap = new HashMap<>();
        chatmap.put("time", messageTime);
        chatmap.put("senderuid", senderuid);
        chatmap.put("receiveruid", receiveruid);
        chatmap.put("message", message);

        userDatabaseReference.child("Chats").push().setValue(chatmap);
    }

    private void readMessages(final String myId, final String userId) {

        chatList = new ArrayList<>();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chat = snapshot.getValue(ChatModel.class);
                    if (chat.getReceiveruid().equals(myId) && chat.getSenderuid().equals(userId) ||
                            chat.getReceiveruid().equals(userId) && chat.getSenderuid().equals(myId)) {
                        chatList.add(chat);
                    }
                    chatAdapter = new ChatAdapter(ChatActivity.this, chatList);
                    recyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
