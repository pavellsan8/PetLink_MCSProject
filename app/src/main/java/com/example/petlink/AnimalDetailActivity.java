package com.example.petlink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.petlink.Model.Animal;
import com.example.petlink.databinding.ActivityAnimalDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AnimalDetailActivity extends AppCompatActivity {

    ActivityAnimalDetailBinding binding;
    String animalID;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimalDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        animalID = (String) getIntent().getStringExtra("animalID");
        isLiked(animalID, binding.likeBtn);
        readAnimal();

        binding.likeBtn.setOnClickListener(view -> {
            if (binding.likeBtn.getTag().equals("like")) {
                FirebaseDatabase.getInstance().getReference().child("Likes").child(animalID)
                        .child(firebaseUser.getUid()).setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Likes").child(animalID)
                        .child(firebaseUser.getUid()).removeValue();
            }
        });

        binding.adoptBtn.setOnClickListener(view -> {
            adopt(animalID);
        });
        binding.backBtn.setOnClickListener(view -> finish());
        binding.openARBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, ARActivity.class);
            startActivity(intent);
        });
    }

    private void readAnimal() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Animals").child(animalID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Animal animal = snapshot.getValue(Animal.class);
                try {
                    Glide.with(getApplicationContext()).load(animal.getImageUrl()).into(binding.image);
                } catch (NullPointerException ignore) {

                }

                binding.name.setText(animal.getName());
                binding.price.setText(animal.getPrice());
                binding.location.setText(animal.getLocation());
                binding.type.setText(animal.getType());
                binding.age.setText(animal.getAge());
                binding.description.setText(animal.getDescription());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void adopt(String animalID) {
        String date = String.valueOf(System.currentTimeMillis());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart");
        String id = reference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("animalId", animalID);
        hashMap.put("user", firebaseUser.getUid());
        hashMap.put("date", date);

        reference.child(id).setValue(hashMap);
        Toast.makeText(this, "Transaksi berhasil ditambahkan", Toast.LENGTH_SHORT).show();

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