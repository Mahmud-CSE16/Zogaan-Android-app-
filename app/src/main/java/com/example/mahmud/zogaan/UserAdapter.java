package com.example.mahmud.zogaan;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<UserProfile> userProfiles;
    private Context context;

    public UserAdapter(Context context,ArrayList<UserProfile> userProfiles) {
        this.userProfiles = userProfiles;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_list_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        UserProfile userProfile = userProfiles.get(i);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference("profilepics");

        storageReference.child(userProfile.getUserUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(viewHolder.imageView);
            }
        });
        try {
            viewHolder.ageTextView.setText("Age: "+userProfile.getUserAge());
            viewHolder.headTextView.setText(userProfile.getUserName());
            viewHolder.addressTextView.setText("Address: "+userProfile.getUserAddress());
        }catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return userProfiles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView headTextView,
                addressTextView,
                 ageTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewId);
            headTextView = itemView.findViewById(R.id.tvNameId);
            addressTextView = itemView.findViewById(R.id.tvAddressId);
            ageTextView = itemView.findViewById(R.id.tvAgeId);
        }
    }
}
