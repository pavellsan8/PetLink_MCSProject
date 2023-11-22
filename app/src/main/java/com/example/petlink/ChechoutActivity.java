package com.example.petlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.petlink.Model.Animal;
import com.example.petlink.databinding.ActivityChechoutBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChechoutActivity extends AppCompatActivity {

    ActivityChechoutBinding binding;
    String animalID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChechoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        animalID = (String) getIntent().getStringExtra("animalID");
        readAnimal();
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}