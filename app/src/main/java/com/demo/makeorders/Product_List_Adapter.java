package com.demo.makeorders;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Product_List_Adapter extends RecyclerView.Adapter<Product_View_Holder> {
    ArrayList<Product> products;
    Product_View_Holder product_view_holder;
    Context context;
    ViewGroup parent;


    public Product_List_Adapter(ArrayList<Product> products, Product_View_Holder product_view_holder) {
        this.products = products;
        this.product_view_holder = product_view_holder;
    }

    public Product_List_Adapter(Product_View_Holder product_view_holder) {
        this.product_view_holder = product_view_holder;
    }

    public Product_List_Adapter(ArrayList<Product> products,Context context) {
        this.products = products;
        this.context=context;
        //Log.d("add: ",students.get(1).getFname());
    }

    @NonNull
    @Override
    public Product_View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent=parent;
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_view,parent,false);
       // view.setOnClickListener(onClickListener);
        return new Product_View_Holder(view,parent.getContext());

    }

    @Override
    public void onBindViewHolder(@NonNull Product_View_Holder holder, int position) {
        final Product product=products.get(position);
        holder.BindProduct(product);
        product_view_holder=holder;
    }

    public Product_List_Adapter() {
    }

    public void updateList(ArrayList<Product> productArrayList){
        products=productArrayList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return products.size();
    }
}