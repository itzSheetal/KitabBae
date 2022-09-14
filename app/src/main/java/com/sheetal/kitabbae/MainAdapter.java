package com.sheetal.kitabbae;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;


import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;


public class MainAdapter extends FirebaseRecyclerAdapter<MainModel,MainAdapter.myViewHolder> {

    Context context;




    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MainAdapter(@NonNull FirebaseRecyclerOptions<MainModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull MainModel model) {

        holder.booktitle.setText(model.getTitle());
        holder.bookauthor.setText(model.getAuthor());
        holder.bookcategory.setText(model.getCategory());
        Glide.with(holder.bookcoverimage.getContext()).load(model.getBook_cover_image()).into(holder.bookcoverimage);


        holder.donordetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), UserProfile.class);
                intent.putExtra("phone",model.getPhoneno());
                v.getContext().startActivity(intent);

            }
        });


        holder.bookdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus= DialogPlus.newDialog(holder.bookcoverimage.getContext())
                        .setContentHolder(new ViewHolder(R.layout.activity_display_book_info))
                        .setExpanded(true,2000)
                        .create();



                View view= dialogPlus.getHolderView();
                TextView title= view.findViewById(R.id.infotitle) ;
                TextView author= view.findViewById(R.id.infoauthor) ;
                TextView publisher= view.findViewById(R.id.infocategory) ;
                TextView edition= view.findViewById(R.id.infopublishyear) ;
                ImageView coverphoto=view.findViewById(R.id.book_cover_photo);


                title.setText(model.getTitle());
                author.setText(model.getAuthor());
                publisher.setText(model.getPublisher());
                edition.setText(model.getPublish_year());


                Glide.with(coverphoto.getContext()).load(model.getBook_cover_image()).into(coverphoto);
                dialogPlus.show();
            }
        });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_displayed_layout,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        public ImageView bookcoverimage;
        public TextView booktitle,bookauthor,bookcategory,phonenumber;
        public Button bookdetails,donordetails;
        public ImageView coverphoto;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            bookcoverimage=(ImageView)itemView.findViewById(R.id.bookcoverimage);
            booktitle=(TextView) itemView.findViewById(R.id.bookname);
            bookauthor=(TextView) itemView.findViewById(R.id.authorname);
            bookdetails=(Button) itemView.findViewById(R.id.bookdetails);
            bookcategory=(TextView)itemView.findViewById(R.id.category);
            coverphoto=(ImageView)itemView.findViewById(R.id.book_cover_photo) ;
            donordetails=(Button) itemView.findViewById(R.id.donordetails);



        }
    }
}
