package com.example.dell.oneat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dell.oneat.Common.currentUser;
import com.example.dell.oneat.Interface.ItemClickListener;
import com.example.dell.oneat.Model.Category;
import com.example.dell.oneat.Model.Request;
import com.example.dell.oneat.ViewHolder.FoodViewHolder;
import com.example.dell.oneat.ViewHolder.MenuViewHolder;
import com.example.dell.oneat.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.example.dell.oneat.Common.currentUser.convertCodeToStatus;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;


    FirebaseDatabase database;
    DatabaseReference request;
    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        database = FirebaseDatabase.getInstance();
        request = database.getReference("Requests");
        recyclerView =findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if(getIntent()==null){
            loadOrder(currentUser.currentuser.getPhone());
        }else{
            loadOrder(getIntent().getStringExtra("userphone"));
        }

    }

    private void loadOrder(String phone) {
        System.out.println("Phone "+phone);
        Query query =  FirebaseDatabase.getInstance().getReference().child("Requests").orderByChild("phone").equalTo(phone);//request.orderByChild("phone").equalTo(phone);
        System.out.println("order qyeey "+ query);
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>().setQuery(query,Request.class).build();
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout,parent,false);
                return new OrderViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Request model) {
                        holder.txtOrder_id.setText(adapter.getRef(position).getKey());
                        System.out.println("order id: "+ adapter.getRef(position).getKey());
                        holder.txtOrder_status.setText(convertCodeToStatus(model.getStatus()));
                        System.out.println("order status: "+ convertCodeToStatus(model.getStatus()) );
                        holder.txtOrder_phone.setText(model.getPhone());
                System.out.println("order phone: "+ model.getPhone() );
                        holder.txtOredr_address.setText(model.getAddress());
                System.out.println("order address: "+ model.getAddress() );
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {


                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
