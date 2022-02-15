package com.demo.makeorders;

import static com.demo.makeorders.RetailerHome.onClickListener;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SelectedAdapter  extends RecyclerView.Adapter<SelectedViewHolder> {
    ArrayList<Product> products;
    SelectedViewHolder product_view_holder;
    Context context;
    ViewGroup parent;


    public SelectedAdapter(ArrayList<Product> products,SelectedViewHolder product_view_holder) {
        this.products = products;
        this.product_view_holder = product_view_holder;
    }

    public SelectedAdapter(SelectedViewHolder product_view_holder) {
        this.product_view_holder = product_view_holder;
    }

    public SelectedAdapter(ArrayList<Product> products, Context context) {
        this.products = products;
        this.context = context;
        //Log.d("add: ",students.get(1).getFname());
    }

    @NonNull
    @Override
    public SelectedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_view, parent, false);
        //view.setOnClickListener(onClickListener);
        return new SelectedViewHolder(view, parent.getContext());

    }

    @Override
    public void onBindViewHolder(@NonNull SelectedViewHolder holder, int position) {
        final Product product = products.get(position);
        holder.BindProduct(product);
        product_view_holder = holder;
    }

    public SelectedAdapter() {
    }

    public void updateList(ArrayList<Product> productArrayList) {
        products = productArrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
