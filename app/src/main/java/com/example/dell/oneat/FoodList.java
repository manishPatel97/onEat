package com.example.dell.oneat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.oneat.Database.Database;
import com.example.dell.oneat.Interface.ItemClickListener;
import com.example.dell.oneat.Model.Category;
import com.example.dell.oneat.Model.Food;
import com.example.dell.oneat.Model.Order;
import com.example.dell.oneat.ViewHolder.FoodViewHolder;
import com.example.dell.oneat.ViewHolder.MenuViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodList extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference foodList;
    int flag=0;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String categoryID = "";
    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;
    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchadapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //facebook share
    CallbackManager callbackManager;
    ShareDialog shareDialog;



    //create Target from Picasso
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //share photo  to fb
            SharePhoto photo= new SharePhoto.Builder()
                    .setBitmap(bitmap).build();
            if(ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


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
        setContentView(R.layout.activity_food_list);
        //init Facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);



        //firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Food");
        recyclerView = findViewById(R.id.food_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(getIntent()!=null){
            categoryID = getIntent().getStringExtra("CategoryID");
            System.out.println("Category1 "+categoryID);
        }
        if(!categoryID.isEmpty()&&categoryID!=null){
            System.out.println("Category2 "+categoryID);
            loadListFood(categoryID);
        }
        materialSearchBar = findViewById(R.id.searchbar);
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for(String search :suggestList){
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())){
                        suggest.add(search);
                    }
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled){
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                setSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void setSearch(CharSequence text) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Food").orderByChild("name").equalTo(text.toString());
        //System.out.println("Category4 "+categoryID);
        //System.out.println("Query "+query);
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>().setQuery(query,Food.class).build();
        searchadapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {



            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                //System.out.println("Inside Sub menu");
                holder.food_name.setText(model.getName());
               // System.out.println(""+model.getName());
               // System.out.println(model.getImage());
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.food_img);
                final Food clickitem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //System.out.println("inside menu");
                        Intent intent = new Intent(FoodList.this,FoodDetail.class);
                        intent.putExtra("Category ID",searchadapter.getRef(position).getKey());
                        startActivity(intent);
                        //Toast.makeText(FoodList.this,""+clickitem.getName(),Toast.LENGTH_SHORT).show();
                    }
                });
                flag=1;
                recyclerView.setAdapter(searchadapter);

            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(view);
            }
        };
    }

    private void loadSuggest() {
        foodList.orderByChild("menuid").equalTo(categoryID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Food item = snapshot.getValue(Food.class);
                    suggestList.add(item.getName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.startListening();
    }
    private void loadListFood(String categoryID) {
        System.out.println("Category3 "+categoryID);
        Query query = FirebaseDatabase.getInstance().getReference().child("Food").orderByChild("menuid").equalTo(categoryID);
        System.out.println("Category4 "+categoryID);
        System.out.println("Query "+query);
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>().setQuery(query,Food.class).build();
        System.out.println("Category5 "+categoryID);
        System.out.println("options "+options);
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {

           // System.out.println("Category5 "+categoryID);
          //  @Override
           /* public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(view);
            }
*/
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, final int position, @NonNull final Food model) {
                System.out.println("Inside Sub menu");
                holder.food_name.setText(model.getName());
                holder.food_price.setText(String.format("$ %s",model.getPrice().toString()));
                System.out.println(""+model.getName());
                System.out.println(model.getImage());
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.food_img);
                final Food clickitem = model;

                //Quick Cart
                holder.cart_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Database(getBaseContext()).addToCart(new Order(
                                adapter.getRef(position).getKey(),
                                model.getName(),
                                model.getPrice(),
                                "1",
                                model.getDiscount()
                        ));
                        Toast.makeText(FoodList.this,"Added to cart",Toast.LENGTH_SHORT).show();
                    }
                });

                //click to share photo
                holder.share_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Hello");
                        Log.d("Share","In share btn");
                        Picasso.with(getApplicationContext()).load(model.getImage()).into(target);
                    }
                });

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        System.out.println("inside menu");
                        Intent intent = new Intent(FoodList.this,FoodDetail.class);
                        intent.putExtra("Category ID",adapter.getRef(position).getKey());
                        startActivity(intent);
                        Toast.makeText(FoodList.this,""+clickitem.getName(),Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(view);
            }
        };
        Log.d("Tag",""+adapter.getItemCount());
        flag=0;
            recyclerView.setAdapter(adapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        if(flag==1){
            searchadapter.startListening();
        }else{
        adapter.startListening();}
    }

    @Override
    public void onStop() {
        super.onStop();
        if(flag==1){
            searchadapter.startListening();
        }
        else{
        adapter.stopListening();}
    }
}
