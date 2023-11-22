package com.example.petlink.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petlink.AnimalDetailActivity;
import com.example.petlink.Model.Animal;
import com.example.petlink.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder> {

    private Context context;
    private List<Animal> animalList;

    public AnimalAdapter(Context context, List<Animal> animalList) {
        this.context = context;
        this.animalList = animalList;
    }

    private FirebaseUser firebaseUser;

    @NonNull
    @Override
    public AnimalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.animal_item, parent, false);
        return new AnimalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalAdapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Animal animal = animalList.get(position);

        holder.name.setText(animal.getName());
        holder.location.setText(animal.getLocation());
        holder.price.setText(animal.getPrice());

        try{

            Glide.with(context).load(animal.getImageUrl()).into(holder.image);
        }catch (NullPointerException ignore){

        }

        holder.main.setOnClickListener(view -> {

            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View,String>(holder.image, "imageTrans");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
            Intent intent = new Intent(context, AnimalDetailActivity.class).putExtra("animalID", animal.getId());
            context.startActivity(intent, options.toBundle());
        });
        isLiked(animal.getId(), holder.likeBtn);
        holder.likeBtn.setOnClickListener(view -> {
            if (holder.likeBtn.getTag().equals("like")) {
                FirebaseDatabase.getInstance().getReference().child("Likes").child(animal.getId())
                        .child(firebaseUser.getUid()).setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Likes").child(animal.getId())
                        .child(firebaseUser.getUid()).removeValue();
            }
        });
    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView main;
        ImageButton likeBtn;
        TextView name, location, price;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.mainHandler);
            name = itemView.findViewById(R.id.name);
            location = itemView.findViewById(R.id.location);
            price = itemView.findViewById(R.id.price);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            image = itemView.findViewById(R.id.image);
        }
    }


    private void isLiked(String postid, final ImageView imageView) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {

                    imageView.setImageResource(R.drawable.baseline_favorite_24);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.baseline_favorite_border_24);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
