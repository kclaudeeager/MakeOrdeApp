package com.demo.makeorders;

import static com.demo.makeorders.AdminHomePage.onClickListener;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class  UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    ArrayList<User> users;
    UserViewHolder userViewHolder;
    Context context;
    ViewGroup parent;


    public UserAdapter(ArrayList<User> users, UserViewHolder userViewHolder) {
        this.users = users;
        this.userViewHolder = userViewHolder;
    }

    public UserAdapter(UserViewHolder userViewHolder) {
        this.userViewHolder = userViewHolder;
    }

    public UserAdapter(ArrayList<User> users,Context context) {
        this.users = users;
        this.context=context;
        //Log.d("add: ",students.get(1).getFname());
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent=parent;
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_vew,parent,false);
        view.setOnClickListener(onClickListener);
        return new UserViewHolder(view,parent.getContext());

    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final User user=users.get(position);
        holder.BindUser(user);
        userViewHolder=holder;
    }

    public UserAdapter() {
    }

    public void updateList(ArrayList<User> userArrayList){
        users=userArrayList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return users.size();
    }
}