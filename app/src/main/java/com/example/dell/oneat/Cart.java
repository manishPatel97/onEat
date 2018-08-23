package com.example.dell.oneat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.oneat.Database.Database;
import com.example.dell.oneat.Common.*;
import com.example.dell.oneat.Model.Order;
import com.example.dell.oneat.Model.Request;
import com.example.dell.oneat.ViewHolder.CartAdapter;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Cart extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;
    TextView txtTotalPrice;
    FButton   btnplace;
    List<Order> orders = new ArrayList<>();
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("cart 1");
        setContentView(R.layout.activity_cart);//error in this line
        System.out.println("cart 2");
        //firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        //init
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        txtTotalPrice = findViewById(R.id.total);
        btnplace = findViewById(R.id.btn_placeOrder);
        btnplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showAlertDialog();
            }
        });
        loadlistfood();
    }

    private void showAlertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One More Step!");
        alertDialog.setMessage("Enter your Address: ");
        final EditText editAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editAddress.setLayoutParams(lp);
        alertDialog.setView(editAddress);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request = new Request(
                        currentUser.currentuser.getName(),
                        currentUser.currentuser.getPhone(),editAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),orders

                );

                requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this,"Thank you for placing order",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void loadlistfood() {
        orders = new Database(this).getCarts();
        adapter =new CartAdapter(orders,this);
        recyclerView.setAdapter(adapter);
        int total =0;
        for(Order order:orders){
            total += (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        }
        Locale locale =new Locale("en","US");
        NumberFormat fmt =NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));

    }
}
