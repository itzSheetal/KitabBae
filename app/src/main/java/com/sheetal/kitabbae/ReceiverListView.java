package com.sheetal.kitabbae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReceiverListView extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView nav_view;
   private CircleImageView nav_profile_image;
    private TextView nav_fullname,nav_email,nav_phone;

    private DatabaseReference databaseref,ref;
    private ProgressBar progressBar;
   // Button chat ;
    private  FirebaseDatabase firebaseDatabase;
    RecyclerView recyclerView;
    MainAdapter mainAdapter;
    private FirebaseAuth mAuth;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_list_view);
        drawerLayout=findViewById(R.id.drawerLayout);
        toolbar=findViewById(R.id.toolbar);
        nav_view=findViewById(R.id.nav_view);
       // chat =findViewById(R.id.chat);

        mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail();


        progressBar=findViewById(R.id.progressbar);
        recyclerView=(RecyclerView)findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(FirebaseDatabase.getInstance("https://kitabbae-b9d9a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Allbooks"), MainModel.class)
                        .build();
        progressBar.setVisibility(View.GONE);
        mainAdapter=new MainAdapter(options);
        recyclerView.setAdapter(mainAdapter);



       nav_profile_image=nav_view.getHeaderView(0).findViewById(R.id.nav_profile_image);
        nav_fullname=nav_view.getHeaderView(0).findViewById(R.id.nav_username);
        nav_email=nav_view.getHeaderView(0).findViewById(R.id.nav_useremail);
        nav_phone=nav_view.getHeaderView(0).findViewById(R.id.nav_phoneno);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Kitab-bae");
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(ReceiverListView.this,drawerLayout,toolbar,R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(this);
        firebaseDatabase = FirebaseDatabase.getInstance("https://kitabbae-b9d9a-default-rtdb.asia-southeast1.firebasedatabase.app/");
        ref=firebaseDatabase.getReference().child("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    if (ds.child("email").getValue().equals(email)) {
                        nav_fullname.setText(ds.child("name").getValue(String.class));
                        nav_email.setText(ds.child("email").getValue(String.class));
                        nav_phone.setText(ds.child("phonenumber").getValue(String.class));
                       String url=ds.child("profilepictureurl").getValue().toString();
                        Glide.with(getApplicationContext()).load(url).into(nav_profile_image);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        mainAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu2,menu);
        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                processsearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                processsearch(s);

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void processsearch(String s){

        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(FirebaseDatabase.getInstance("https://kitabbae-b9d9a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Allbooks").orderByChild("title").startAt(s).endAt(s+"\uf8ff" ), MainModel.class)
                        .build();

        mainAdapter=new MainAdapter(options);
        mainAdapter.startListening();
        recyclerView.setAdapter(mainAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent1=new Intent(ReceiverListView.this,LoginActivity.class);
                startActivity(intent1);

            case R.id.About:
                Intent intent2=new Intent(ReceiverListView.this,AboutPage.class);
                startActivity(intent2);

            case R.id.Share:
                try{
                    Intent intent= new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT,"SHARE DEMO");
                    String shareMessage="https://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID+"\n\n";
                    intent.putExtra(Intent.EXTRA_TEXT,shareMessage);
                    startActivity(Intent.createChooser(intent,"share by"));
                } catch (Exception e) {
                    Toast.makeText(ReceiverListView.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}