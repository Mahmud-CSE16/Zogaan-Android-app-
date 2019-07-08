package com.example.mahmud.zogaan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class UpdateProfile extends AppCompatActivity {

    private EditText newUserName, newUserEmail, newUserAge,newUserPhone,newUserAddress;
    private Spinner spinnerUpdate;
    private Button save;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private ImageView updateProfilePic;
    private static int PICK_IMAGE = 123;
    Uri imagePath;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    String userUid;
    String email, name, age,phone,address,specialistAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newUserName = findViewById(R.id.etNameUpdate);
        newUserEmail = findViewById(R.id.etEmailUpdate);
        newUserAge = findViewById(R.id.etAgeUpdate);
        newUserPhone = findViewById(R.id.etPhoneUpdate);
        newUserAddress=findViewById(R.id.etAddressUpdate);
        spinnerUpdate = findViewById(R.id.spinnerUpdate);
        save = findViewById(R.id.btnSave);
        updateProfilePic = findViewById(R.id.ivProfileUpdate);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        final DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                newUserName.setText(userProfile.getUserName());
                newUserAge.setText(userProfile.getUserAge());
                newUserEmail.setText(userProfile.getUserEmail());
                newUserPhone.setText(userProfile.getUserPhone());
                newUserAddress.setText(userProfile.getUserAddress());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UpdateProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        final StorageReference storageReference = firebaseStorage.getReference("profilepics");
        storageReference.child(firebaseAuth.getUid()) .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(updateProfilePic);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validate()) {

                    final StorageReference imageReference = storageReference.child(firebaseAuth.getUid());  //User id/Images/Profile Pic.jpg
                    userUid = firebaseAuth.getUid();
                    UploadTask uploadTask = imageReference.putFile(imagePath);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateProfile.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Toast.makeText(UpdateProfile.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    UserProfile userProfile = new UserProfile(name, email,age, phone, address, specialistAt, userUid);

                    databaseReference.setValue(userProfile);

                    finish();
                }
            }
        });

        updateProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null){
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                updateProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Boolean validate(){
        Boolean result = false;

         name = newUserName.getText().toString();
         age = newUserAge.getText().toString();
         email = newUserEmail.getText().toString();
         phone = newUserPhone.getText().toString();
         address=newUserAddress.getText().toString();;
         specialistAt = spinnerUpdate.getSelectedItem().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            newUserEmail.setError("Please enter a valid email");
            newUserEmail.requestFocus();
            return false;
        }

        if(name.isEmpty() || email.isEmpty() || age.isEmpty() || phone.isEmpty()|| address.isEmpty() || imagePath==null){
            if(name.isEmpty() ){
                newUserName.setError("Field can't be Empty");
            }else if(email.isEmpty()){
                newUserEmail.setError("Field can't be Empty");
            }else if(age.isEmpty()){
                newUserAge.setError("Field can't be Empty");
            }else if(phone.isEmpty()){
                newUserPhone.setError("Field can't be Empty");
            }else if(age.isEmpty()){
                newUserAddress.setError("Field can't be Empty");
            }else if(imagePath==null){
                Toast.makeText(getApplicationContext(),"Please select new profile pic",Toast.LENGTH_SHORT).show();
            }
        }else{
            result = true;
        }

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}