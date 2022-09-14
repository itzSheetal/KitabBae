package com.sheetal.kitabbae;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {

    private TextView backButton;
    private CircleImageView profileImage;
    private TextInputEditText registerName,  registerPhone, registerMail,registerPassword,locality,year_degree;
    private Button registerButton;
    private Spinner city;
    private Uri resultUri;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private  FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        backButton=findViewById(R.id.back_button);
        registerButton=findViewById(R.id.register_button);
        profileImage=findViewById(R.id.profile_image);
        registerName=findViewById(R.id.register_name);
        registerMail=findViewById(R.id.register_mail);
        registerPhone=findViewById(R.id.register_phone);
        registerPassword=findViewById(R.id.register_password);
        locality=findViewById(R.id.address);
        year_degree=findViewById(R.id.class_degree);
        city=findViewById(R.id.city);
        loader= new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance("https://kitabbae-b9d9a-default-rtdb.asia-southeast1.firebasedatabase.app/");
       // databaseReference= FirebaseDatabase.getInstance().getReference().child("users");

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email= registerMail.getText().toString().trim();
                final String password= registerPassword.getText().toString().trim();
                final String phone= registerPhone.getText().toString().trim();
                final String name = registerName.getText().toString().trim();
                final String City= city.getSelectedItem().toString();
                final String classDegree= year_degree.getText().toString().trim();
                final String address= locality.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    registerMail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    registerPassword.setError("Password is required");
                    return;
                }
                if(TextUtils.isEmpty(name)){
                    registerName.setError("FullName is required");
                    return;
                }
                if(TextUtils.isEmpty(phone)){
                    registerPhone.setError("Phone number is required");
                    return;
                }

                if(City=="City"){
                    Toast.makeText(SignupActivity.this, "Select a Valid City", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(address)){
                    locality.setError("Locality is required");
                    return;
                }
                if (TextUtils.isEmpty(classDegree)){
                    year_degree.setError("Class is required");
                    return;
                }
                else{
                    loader.setMessage("Registering you...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                String error = task.getException().toString();
                                Toast.makeText(SignupActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
                            }else{
                                String ClientID= UUID.randomUUID().toString();
                                databaseReference= firebaseDatabase.getReference().child("Users").child(ClientID);
                                HashMap userInfo= new HashMap();
                                userInfo.put("id",ClientID);
                                userInfo.put("name",name);
                                userInfo.put("email",email);
                                userInfo.put("phonenumber",phone);
                                userInfo.put("class",classDegree);
                                userInfo.put("city",City);
                                userInfo.put("locality",address);

                                databaseReference.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){

                                            Toast.makeText(SignupActivity.this, "Data Set Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(SignupActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                       // loader.dismiss();
                                    }
                                });
                                if (resultUri!=null){
                                    final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Profile images").child(ClientID);
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
                                            Toast.makeText(SignupActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
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
                                                        String imageUrl=uri.toString();
                                                        Map newImageMap = new HashMap();
                                                        newImageMap.put("profilepictureurl",imageUrl);
                                                        databaseReference.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if(task.isSuccessful()) {
                                                                    Toast.makeText(SignupActivity.this, "Image Url added to the database successfully", Toast.LENGTH_SHORT).show();
                                                                }else{
                                                                    Toast.makeText(SignupActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                        finish();
                                                    }
                                                });
                                            }
                                        }
                                    });

                                    Intent intent =new Intent(SignupActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    loader.dismiss();

                                }

                            }
                        }
                    });

                }


            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1 && resultCode== RESULT_OK && data!=null){
            resultUri=data.getData();
            profileImage.setImageURI(resultUri);

        }
    }
}