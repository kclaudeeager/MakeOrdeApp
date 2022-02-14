package com.demo.makeorders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Product_View_Holder extends RecyclerView.ViewHolder {
    TextView productName, productPrice;
    Product product;
    Context context;

    public Product_View_Holder(@NonNull View itemView, Context context) {

        super(itemView);
        this.context = context;
        productName = (TextView) itemView.findViewById(R.id.product_name);
        productPrice = (TextView) itemView.findViewById(R.id.pPrice);

    }

    public void BindProduct(final Product product) {
        productName.setText(product.getpName());
        productPrice.setText(""+product.getPrice());
    }
}