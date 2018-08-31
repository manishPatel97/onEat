package com.example.dell.oneat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.oneat.Database.Database;
import com.example.dell.oneat.Common.*;
import com.example.dell.oneat.Model.Notification;
import com.example.dell.oneat.Model.Order;
import com.example.dell.oneat.Model.Request;
import com.example.dell.oneat.Model.Sender;
import com.example.dell.oneat.Model.Token;
import com.example.dell.oneat.Model.myResponses;
import com.example.dell.oneat.Remote.APIService;
import com.example.dell.oneat.ViewHolder.CartAdapter;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

public class Cart extends AppCompatActivity {
    private static final int PAYPAL_REQUEST_CODE = 9999;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;
    TextView txtTotalPrice;
    FButton   btnplace;
    List<Order> orders = new ArrayList<>();
    CartAdapter adapter;
    APIService mService;
    static PayPalConfiguration configur = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(config.PAYPAL_CLIENT_ID);
    String Address;

    @Override
    protected void attachBaseContext(Context newBase) {
        //apply calligraphy to layout
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //apply calligraphy or fonts to layout.
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/amatic.ttf").setFontAttrId(R.attr.fontPath).build()
        );
        System.out.println("cart 1");
        setContentView(R.layout.activity_cart);//error in this line
        System.out.println("cart 2");
        //init Paypal
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configur);
        startService(intent);



        //init service
        mService = currentUser.getFCMService();
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
                if(orders.size()>0){
                    showAlertDialog();
                }
                else{
                    Toast.makeText(Cart.this, "Your cart is Empty.", Toast.LENGTH_SHORT).show();
                }
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

                //show paypal to  payment
                Address = editAddress.getText().toString();
                String formatAccount = txtTotalPrice.getText().toString().replace("$","").replace(",","");
                PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAccount),"USD","On Eat App Order",PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configur);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
                startActivityForResult(intent,PAYPAL_REQUEST_CODE);
               /* Request request = new Request(
                        currentUser.currentuser.getName(),
                        currentUser.currentuser.getPhone(),editAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),orders

                );
                String order_number = String.valueOf(System.currentTimeMillis());
                requests.child(order_number).setValue(request);
                new Database(getBaseContext()).cleanCart();
                //Toast.makeText(Cart.this,"Thank you for placing order",Toast.LENGTH_SHORT).show();
                //finish();
                sendNotificationOrder(order_number);*/

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PAYPAL_REQUEST_CODE){
            if(resultCode ==RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation!=null){
                    try{
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);

                        Request request = new Request(
                                currentUser.currentuser.getName(),
                                currentUser.currentuser.getPhone(),Address,
                                txtTotalPrice.getText().toString(),orders,
                                jsonObject.getJSONObject("response").getString("state")

                        );
                        String order_number = String.valueOf(System.currentTimeMillis());
                        requests.child(order_number).setValue(request);
                        new Database(getBaseContext()).cleanCart();

                        sendNotificationOrder(order_number);
                        Toast.makeText(Cart.this,"Thank you for placing order",Toast.LENGTH_SHORT).show();
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else if (resultCode== Activity.RESULT_CANCELED){
                Toast.makeText(Cart.this,"Payment Cancel",Toast.LENGTH_SHORT).show();

            }
            else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
                Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendNotificationOrder(final String order_number) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query =tokens.orderByChild("serverToken").equalTo(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postsnapshot : dataSnapshot.getChildren()){
                    Token serverToken = postsnapshot.getValue(Token.class);

                    //create raw payload to send

                    Notification notification  = new Notification("OnEat","You have new Order #" +order_number);
                    //System.out.println("Order Title "+notification.getTitle());
                    Sender content =  new Sender(serverToken.getToken(),new Notification("OnEat","You have new Order #" +order_number));
                    System.out.println("Server Token"+serverToken.getToken());
                    System.out.println("Content to "+content.to+" notification title "+notification.getTitles()+"notification body "+notification.getBody());
                    mService.sendnotifiaction(content).enqueue(new Callback<myResponses>() {
                        @Override
                        public void onResponse(Call<myResponses> call, Response<myResponses> response) {
                            System.out.println("Response "+response.raw());
                            System.out.println("Response "+response.body().success);
                            if(response.code()==200) {
                                if (response.body().success == 1) {
                                    Toast.makeText(Cart.this, "Thank You, Order Place", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(Cart.this, "Failed !!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<myResponses> call, Throwable t) {
                            Log.e("ERROR",t.getMessage());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadlistfood() {
        orders = new Database(this).getCarts();
        adapter =new CartAdapter(orders,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        int total =0;
        for(Order order:orders){
            total += (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        }
        Locale locale =new Locale("en","US");
        NumberFormat fmt =NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(currentUser.DELETE)){
            DeleteCart(item.getOrder());
        }

        return true;

    }

    private void DeleteCart(int position) {
        orders.remove(position);
        //Delete all old data from SQLite
        new Database(this).cleanCart();
        //update new data from List<Order> to SQLite
        for (Order item :orders)
            new Database(this).addToCart(item);

        //Refresh after Delete Cart
        loadlistfood();
    }
}
