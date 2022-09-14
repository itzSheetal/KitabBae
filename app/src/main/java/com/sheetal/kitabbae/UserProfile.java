package com.sheetal.kitabbae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    CircleImageView profilephoto;
    TextView userfullname,useraddress,usercourse,userphone,useremail,usercity;
    Button contactdonor;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    private FirebaseAuth mAuth;
    String phoneno;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        userfullname=findViewById(R.id.user_profile_name);
        useraddress=findViewById(R.id.profile_address);
        useremail=findViewById(R.id.profile_email);
        usercourse=findViewById(R.id.profile_branch);
        userphone=findViewById(R.id.profile_phoneno);
        profilephoto=findViewById(R.id.user_profile_photo);
        contactdonor=findViewById(R.id.contactdonor);
        usercity=findViewById(R.id.profile_city);
        Intent intent=getIntent();
        phoneno=intent.getStringExtra("phone");

        contactdonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(Intent.ACTION_VIEW);
               intent.setData(Uri.parse("https://wa.me/+91"+phoneno+"?text=Hi,Is anyone Available?"));
               startActivity(intent);
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance("https://kitabbae-b9d9a-default-rtdb.asia-southeast1.firebasedatabase.app/");
        ref=firebaseDatabase.getReference().child("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){

                    if (ds.child("phonenumber").getValue().equals(phoneno)) {
                        userfullname.setText(ds.child("name").getValue(String.class));
                        useremail.setText(ds.child("email").getValue(String.class));
                        userphone.setText(ds.child("phonenumber").getValue(String.class));
                        usercourse.setText(ds.child("class").getValue(String.class));
                        useraddress.setText(ds.child("locality").getValue(String.class));
                        usercity.setText(ds.child("city").getValue(String.class));
                        String url = ds.child("profilepictureurl").getValue().toString();
                        Glide.with(getApplicationContext()).load(url).into(profilephoto);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}


