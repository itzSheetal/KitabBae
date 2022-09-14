package com.sheetal.kitabbae;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DonateActivity extends AppCompatActivity {

    private TextInputEditText bookName,bookAuthor,bookPublisher,bookYear,phoneNo;
    private ImageView bookCover;
    private Button donateBook;
    private Spinner category;
    Uri resultUri;
    private ProgressDialog loader;
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    // creating a variable for
    // our object class
    BookInfo bookInfo;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1 && resultCode== RESULT_OK && data!=null){
            resultUri=data.getData();
            bookCover.setImageURI(resultUri);

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        bookCover=findViewById(R.id.bookcover);
        bookName=findViewById(R.id.book_name);
        bookAuthor=findViewById(R.id.author_name);
        bookPublisher=findViewById(R.id.publisher);
        bookYear=findViewById(R.id.publication_year);
        donateBook=findViewById(R.id.donate_book);
        category=findViewById(R.id.book_category);
        phoneNo=findViewById(R.id.phoneNo);
        loader= new ProgressDialog(this);

        firebaseDatabase = FirebaseDatabase.getInstance("https://kitabbae-b9d9a-default-rtdb.asia-southeast1.firebasedatabase.app/");


        // below line is used to get reference for our database.
        //change by Me
        // databaseReference = firebaseDatabase.getReference();

        bookInfo = new BookInfo();
        mAuth=FirebaseAuth.getInstance();





        bookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        donateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Title= bookName.getText().toString().trim();
                final String Author= bookAuthor.getText().toString().trim();
                final String Publisher= bookPublisher.getText().toString().trim();
                final String Year= bookYear.getText().toString().trim();
                final String Category= category.getSelectedItem().toString();
                final String PhoneNo= phoneNo.getText().toString().trim();

                if(TextUtils.isEmpty(Title)){
                    bookName.setError("Book Title is required");
                    return;
                }
                if(TextUtils.isEmpty(Author)){
                    bookAuthor.setError("Author Name is required");
                    return;
                }
                if(TextUtils.isEmpty(Publisher)){
                    bookPublisher.setError("Publisher Name is required");
                    return;
                }
                if(TextUtils.isEmpty(Year)){
                    bookYear.setError("Publish Year is required");
                    return;
                }
                if(TextUtils.isEmpty(PhoneNo) || PhoneNo.length()<10 ){
                    bookYear.setError("Invalid Phone Number");
                    return;
                }
                if(Category=="Mention the category"){
                    Toast.makeText(DonateActivity.this, "Select a Valid City", Toast.LENGTH_SHORT).show();
                    return;
                }

                else {
                    String currentUserId=mAuth.getCurrentUser().getUid();

                    loader.setMessage("Updating...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    String Bookid= UUID.randomUUID().toString();
                    databaseReference = firebaseDatabase.getReference().child("Allbooks").child(Bookid);
                    HashMap usermap =new HashMap();
                    usermap.put("currentuserId",currentUserId);
                    usermap.put("title",Title);
                    usermap.put("author",Author);
                    usermap.put("publisher",Publisher);
                    usermap.put("publish_year",Year);
                    usermap.put("category",Category);
                    usermap.put("phoneno",PhoneNo);
                    databaseReference.setValue(usermap);

                    databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // inside the method of on Data change we are setting
                                // our object class to our database reference.
                                // data base reference will sends data to firebase.
                                //Change By Me
                                // databaseReference.setValue(bookInfo);
                                Log.d("data", String.valueOf(bookInfo));

                                // after adding this data we are showing toast message.
                                Toast.makeText(DonateActivity.this, "Book added for donation...", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // if the data is not added or it is cancelled then
                                // we are displaying a failure toast message.
                                Toast.makeText(DonateActivity.this, "Fail to add data... " + error, Toast.LENGTH_SHORT).show();
                                Log.d("Failed", "Faild to Add data");
                            }

                        } );

                   // addDatatoFirebase(Title, Author, Publisher,Year,Category,currentUserId);


                    if(resultUri!=null){

                        final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Book cover Images").child(currentUserId);
                        Bitmap bitmap=null;

                        try{
                            bitmap= MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri) ;
                        }catch(IOException e){
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
                        byte[] data = byteArrayOutputStream.toByteArray();
                        UploadTask uploadTask= filepath.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DonateActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if(taskSnapshot.getMetadata()!=null && taskSnapshot.getMetadata().getReference()!=null){
                                    Task<Uri> result=taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl= uri.toString();
                                            Map newImageMap= new HashMap();
                                            newImageMap.put("book_cover_image",imageUrl);

                                            databaseReference.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if(task.isSuccessful()) {
                                                        Toast.makeText(DonateActivity.this, "Image Url added to the database successfully", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Toast.makeText(DonateActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                            finish();
                                        }
                                    });
                                }
                            }
                        });

                        Intent intent =new Intent(DonateActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        loader.dismiss();

                    }
                    loader.dismiss();

                }

            }
        });

    }


    }

