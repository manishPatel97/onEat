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
import android.content.Intent;
import android.widget.Toast;

import com.example.dell.oneat.Common.currentUser;
import com.example.dell.oneat.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signin extends AppCompatActivity {

 TextView signup;
 Button signin;
 EditText username,password;
 TextView forgotpassword;
 ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        signup = findViewById(R.id.signup_text);
        signin = findViewById(R.id.signin);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        forgotpassword = findViewById(R.id.forgot_password);
        back = findViewById(R.id.back);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference user_table = database.getReference("User");

        signin.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (currentUser.isConnectedToInternet(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(Signin.this);
                    mDialog.setMessage("Please Wait..");
                    mDialog.show();
                    user_table.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //user information

                            if (dataSnapshot.child(username.getText().toString()).exists()) {
                                mDialog.dismiss();
                                User user = dataSnapshot.child(username.getText().toString()).getValue(User.class);
                                if (user.getPassword().equals(password.getText().toString())) {
                                    Intent home = new Intent(Signin.this, Home.class);
                                    currentUser.currentuser = user;
                                    currentUser.number = 4;
                                    System.out.println(currentUser.currentuser.getName());
                                    startActivity(home);
                                    finish();


                                } else {
                                    Toast.makeText(Signin.this, "sign in failed", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(Signin.this, "user not exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    Toast.makeText(Signin.this, "You are not Connected to Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
            }


        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signin.this,MainActivity.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signin.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
