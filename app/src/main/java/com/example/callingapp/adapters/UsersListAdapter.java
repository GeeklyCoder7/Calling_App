package com.example.callingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.callingapp.R;
import com.example.callingapp.databinding.UsersListSampleLayoutBinding;
import com.example.callingapp.models.UserModel;

import java.util.ArrayList;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UsersListAdapterViewHolder> {
    Context context;
    ArrayList<UserModel> userModelArrayList;

    public UsersListAdapter(Context context, ArrayList<UserModel> userModelArrayList) {
        this.context = context;
        this.userModelArrayList = userModelArrayList;
    }

    @NonNull
    @Override
    public UsersListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UsersListAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.users_list_sample_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UsersListAdapterViewHolder holder, int position) {
        UserModel userModel = userModelArrayList.get(position);
        holder.binding.userNameTextView.setText(userModel.getUserName());
    }

    @Override
    public int getItemCount() {
        return userModelArrayList.size();
    }

    public static class UsersListAdapterViewHolder extends RecyclerView.ViewHolder {
        UsersListSampleLayoutBinding binding;
        public UsersListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = UsersListSampleLayoutBinding.bind(itemView);
        }
    }
}
