package in.encrust.devcom.AdapterClasses;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import in.encrust.devcom.ModelClasses.ChatModel;
import in.encrust.devcom.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    final static int MSG_TYPE_LEFT = 0;
    final static int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<ChatModel> chatList;
    FirebaseUser fUser;

    public ChatAdapter(Context context, List<ChatModel> chatList){
        this.chatList = chatList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT ) {
            View view = LayoutInflater.from(context).inflate(R.layout.chatcontent_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chatcontent_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatModel chat = chatList.get(position);

        holder.sentTime.setText(chat.getTime());
        holder.messageContent.setText(chat.getMessage());

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sentTime, messageContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sentTime = itemView.findViewById(R.id.chat_content_messagetime);
            messageContent = itemView.findViewById(R.id.chat_content_messagecontent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSenderuid().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
