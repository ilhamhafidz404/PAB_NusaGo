package com.example.nusago.Adapters;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nusago.Models.User;
import com.example.nusago.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnUserActionListener {
        void onDeleteUser(User user);
    }

    private Context context;
    private List<User> userList;
    private OnUserActionListener listener;

    public UserAdapter(Context context, List<User> userList, OnUserActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        String userStatus = "Aktif";
        if (user.getDeletedAt() != null && !user.getDeletedAt().isEmpty() && !user.getDeletedAt().equals("null")) {
            userStatus = "Nonaktif - " + user.getFormattedDeletedAt();
        }

        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
        holder.role.setText("Role: " + user.getRole());
        holder.status.setText("Status: " + userStatus);

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteUser(user);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, role, status;
        Button btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_user_name);
            email = itemView.findViewById(R.id.tv_user_email);
            role = itemView.findViewById(R.id.tv_user_role);
            status = itemView.findViewById(R.id.tv_user_status);
            btnDelete = itemView.findViewById(R.id.btn_delete_user);
        }
    }
}
