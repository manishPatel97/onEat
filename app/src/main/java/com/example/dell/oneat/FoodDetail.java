package com.example.dell.oneat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.dell.oneat.Database.Database;
import com.example.dell.oneat.Model.Food;
import com.example.dell.oneat.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetail extends AppCompatActivity {

    TextView food_Name,food_price,food_desc;
    ImageView food_img;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btn_cart;
    ElegantNumberButton numberButton;
    String foodid = "";
    FirebaseDatabase database;
    DatabaseReference food;
    Food cur_food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        database = FirebaseDatabase.getInstance();
        food = database.getReference("Food");
        numberButton = findViewById(R.id.number_button);
        btn_cart = findViewById(R.id.btncart);
        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodid,cur_food.getName(),cur_food.getPrice(),numberButton.getNumber(),cur_food.getDiscount()
                ));
                Toast.makeText(FoodDetail.this,"Added to cart",Toast.LENGTH_SHORT).show();
            }
        });
        collapsingToolbarLayout = findViewById(R.id.collapising);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar);
        food_img = findViewById(R.id.food_img);
        food_Name = findViewById(R.id.food_Name);
        food_desc = findViewById(R.id.food_desc);
        food_price = findViewById(R.id.food_price);
        if(getIntent()!=null){
            foodid = getIntent().getStringExtra("Category ID");
        }
        if(!foodid.isEmpty()){
            getFoodDetail(foodid);
        }




    }

    private void getFoodDetail(String foodid) {
        food.child(foodid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               cur_food = dataSnapshot.getValue(Food.class);
                Picasso.with(getBaseContext()).load(cur_food.getImage()).into(food_img);
                collapsingToolbarLayout.setTitle(cur_food.getName());
                food_price.setText(cur_food.getPrice());
                food_Name.setText(cur_food.getName());
                food_desc.setText(cur_food.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
