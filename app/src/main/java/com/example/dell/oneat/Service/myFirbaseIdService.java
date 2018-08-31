package com.example.dell.oneat.Service;

import com.example.dell.oneat.Common.currentUser;
import com.example.dell.oneat.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class myFirbaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if(currentUser.currentuser!=null){
        updateTokenToFirebase(refreshedToken);}
    }

    private void updateTokenToFirebase(String refreshedToken) {
        FirebaseDatabase db= FirebaseDatabase.getInstance();
        DatabaseReference Tokens =db.getReference("Tokens");
        Token token = new Token(refreshedToken,false);
        Tokens.child(currentUser.currentuser.getPhone()).setValue(token);//make userphone number as a key



    }
}
