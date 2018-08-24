package com.example.dell.oneat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import com.example.dell.oneat.Common.currentUser;
import com.example.dell.oneat.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button signIn,signUp ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signIn = findViewById(R.id.signin_button);
        signUp = findViewById(R.id.signup_button);
        //init Paper
        Paper.init(this);


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Signin.class);
                startActivity(intent);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,signup.class);
                startActivity(intent);
            }
        });
        String user = Paper.book().read(currentUser.USER_KEY);
        String pswd = Paper.book().read(currentUser.PWD_KEY);
        if(user!=null && pswd !=null){
            if(!user.isEmpty() && !pswd.isEmpty()){
                login(user,pswd);

            }
        }
    }

    private void login(final String phone, final String pswd) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference user_table = database.getReference("User");
        if (currentUser.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please Wait..");
            mDialog.show();
            user_table.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //user information

                    if (dataSnapshot.child(phone).exists()) {
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        if (user.getPassword().equals(pswd)) {
                            Intent home = new Intent(MainActivity.this, Home.class);
                            currentUser.currentuser = user;
                            currentUser.number = 4;
                            System.out.println(currentUser.currentuser.getName());
                            startActivity(home);
                            finish();


                        } else {
                            Toast.makeText(MainActivity.this, "sign in failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "user not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(MainActivity.this, "You are not Connected to Internet", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
