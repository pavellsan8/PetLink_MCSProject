package com.example.petlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.petlink.Adapter.AnimalAdapter;
import com.example.petlink.Model.Animal;
import com.example.petlink.databinding.ActivityCategoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    ActivityCategoryBinding binding;
    String animalType;
    private List<Animal> animalList;
    private AnimalAdapter animalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        animalType = (String) getIntent().getStringExtra("category");
        binding.backBtn.setOnClickListener(view -> finish());
        animalList = new ArrayList<>();
        animalAdapter = new AnimalAdapter(this, animalList);
        binding.categoryRV.setHasFixedSize(true);
        binding.categoryRV.setLayoutManager(new LinearLayoutManager(this));
        binding.categoryRV.setAdapter(animalAdapter);

        if (animalType.equals("cat")){

            binding.catCategory.setVisibility(View.VISIBLE);
            binding.dogCategory.setVisibility(View.GONE);
            search("Felis Catus");

        }else if (animalType.equals("dog")){
            binding.dogCategory.setVisibility(View.VISIBLE);
            binding.catCategory.setVisibility(View.GONE);
            search("Canis Lupus Familiaris");
        }
    }

    private void search(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Animals").orderByChild("type")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                animalList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Animal animal = dataSnapshot.getValue(Animal.class);
                    animalList.add(animal);

                }

                animalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}