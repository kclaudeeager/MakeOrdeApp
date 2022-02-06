package com.demo.makeorders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserViewHolder extends RecyclerView.ViewHolder {
TextView company,role;
User user;
    public  UserViewHolder(@NonNull View itemView, Context context) {

        super(itemView);
        company=(TextView) itemView.findViewById(R.id.company_name);
        role=(TextView) itemView.findViewById(R.id.company_role);

    }
    public void BindUser(final User user){
        company.setText(user.getCompany());
        switch (user.getRole()){
            case 0:
                role.setText("retailer");
                break;
            case 1:
                role.setText("supplier");
                break;
            case 2:
                role.setText("distributer");
                break;
            case 3:
                role.setText("manufacture");
                break;
            case 4:
                role.setText("admin");
                break;
            default:
                role.setText("Unknown");
                break;

        }
    }






}