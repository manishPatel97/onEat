package com.example.dell.oneat.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.oneat.Interface.ItemClickListener;
import com.example.dell.oneat.R;

public class  FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView food_name,food_price;
    public ImageView food_img,share_img,cart_img;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);
        food_name = itemView.findViewById(R.id.food_name);
        food_img = itemView.findViewById(R.id.food_image);
        share_img = itemView.findViewById(R.id.btn_share);
        food_price= itemView.findViewById(R.id.food_price);
        cart_img=  itemView.findViewById(R.id.btn_quick_cart);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;

    }



    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}
