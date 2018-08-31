package com.example.dell.oneat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.dell.oneat.Common.currentUser;
import com.example.dell.oneat.Database.Database;
import com.example.dell.oneat.Model.Food;
import com.example.dell.oneat.Model.Order;
import com.example.dell.oneat.Model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.lang.reflect.Array;
import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {//RatingDialogListener for rating bar

    TextView food_Name,food_price,food_desc;
    ImageView food_img;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btn_rating;
    CounterFab btn_cart;
    ElegantNumberButton numberButton;
    String foodid = "";
    FirebaseDatabase database;
    DatabaseReference food;
    DatabaseReference ratingTbl;
    Food cur_food;
    RatingBar ratingBar;


    protected void attachBaseContext(Context newBase) {
        //apply calligraphy to layout
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/amatic.ttf").setFontAttrId(R.attr.fontPath).build()
        );
        setContentView(R.layout.activity_food_detail);
      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        database = FirebaseDatabase.getInstance();
        food = database.getReference("Food");
        ratingTbl =database.getReference("Rating");
        numberButton = findViewById(R.id.number_button);
        btn_cart = findViewById(R.id.btncart);
        btn_rating =findViewById(R.id.ratingBtn);
        ratingBar =findViewById(R.id.ratingBar);
        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodid,cur_food.getName(),cur_food.getPrice(),numberButton.getNumber(),cur_food.getDiscount()
                ));
                Toast.makeText(FoodDetail.this,"Added to cart",Toast.LENGTH_SHORT).show();
            }
        });

        btn_cart.setCount(new Database(this).getCountCart());
        btn_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingAlertDialogBox();

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
            getRatingDetail(foodid);
        }




    }

    private void getRatingDetail(String foodid) {
        Query foodrating = ratingTbl.orderByChild("foodid").equalTo(foodid);
        foodrating.addValueEventListener(new ValueEventListener() {
            int count =0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postsnapshot:dataSnapshot.getChildren()){
                    Rating item = postsnapshot.getValue(Rating.class);
                    sum +=Integer.parseInt(item.getFoodRating());
                    count++;
                }
                if(count!=0) {
                    float avg = sum / count;
                    ratingBar.setRating(avg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ratingAlertDialogBox() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Ok","Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this Food")
                .setDescription("Please Rate it and give feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please Write your comment here.")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(R.color.Black)
                .setCommentBackgroundColor(R.color.White)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this).show();

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

    @Override
    public void onPositiveButtonClicked(int value, String comment) {
        //get Rating
        final Rating ratingObj = new Rating(currentUser.currentuser.getPhone(),foodid,String.valueOf(value),comment);
        //Upload to firebase
        ratingTbl.child(currentUser.currentuser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(currentUser.currentuser.getPhone()).exists()){
                    //remove old rating
                    ratingTbl.child(currentUser.currentuser.getPhone()).removeValue();
                    //update new rating
                    ratingTbl.child(currentUser.currentuser.getPhone()).setValue(ratingObj);
                }
                else{
                    ratingTbl.child(currentUser.currentuser.getPhone()).setValue(ratingObj);
                }
                Toast.makeText(FoodDetail.this,"Thank you for your Feedback !!!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }
}
