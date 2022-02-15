package com.demo.makeorders;
import static com.demo.makeorders.RetailerHome.isSelectedList;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectedViewHolder extends RecyclerView.ViewHolder {
    TextView productName, productPrice;
    Product product;
    Context context;

    public SelectedViewHolder(@NonNull View itemView, Context context) {

        super(itemView);
        this.context = context;
        productName = (TextView) itemView.findViewById(R.id.product_name);
        productPrice = (TextView) itemView.findViewById(R.id.pPrice);


    }

    public void BindProduct(final Product product) {
        productName.setText(product.getpName());
        if(product.getNumReq()!=0){
            productPrice.setText(""+product.getPrice()+" X "+product.getNumReq());
        }

    }
}

