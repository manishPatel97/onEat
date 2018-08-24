package com.example.dell.oneat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.oneat.Common.currentUser;
import com.example.dell.oneat.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.Intent;
public class signup extends AppCompatActivity {
    private EditText Name,phone,email,password;
    private TextView msg,signin;
    private Button Signup;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Name = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        email =findViewById(R.id.Email);
        password= findViewById(R.id.password);
        Signup = findViewById(R.id.register);
        msg = findViewById(R.id.message);
        signin = findViewById(R.id.signin_text);
        back= findViewById(R.id.back);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference user_table = database.getReference("User");

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this,Signin.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this,MainActivity.class);
                startActivity(intent);
            }
        });
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser.isConnectedToInternet(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(signup.this);
                    mDialog.setMessage("Please Wait..");
                    mDialog.show();
                    user_table.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(phone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(signup.this, "User Already Exist", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();
                                User user = new User(Name.getText().toString(), password.getText().toString(), email.getText().toString(), phone.getText().toString());
                                user_table.child(phone.getText().toString()).setValue(user);
                                Toast.makeText(signup.this, "Register Successfully...", Toast.LENGTH_SHORT).show();
                                finish();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(signup.this, "You are not Connected to Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });


    }
}
