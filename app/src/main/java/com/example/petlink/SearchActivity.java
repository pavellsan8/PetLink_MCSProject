package com.example.petlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.petlink.Adapter.AnimalAdapter;
import com.example.petlink.Model.Animal;
import com.example.petlink.databinding.ActivitySearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;

    private List<Animal> animalList;
    private AnimalAdapter animalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        animalList = new ArrayList<>();
        animalAdapter = new AnimalAdapter(this, animalList);
        binding.searchRV.setHasFixedSize(true);
        binding.searchRV.setLayoutManager(new LinearLayoutManager(this));
        binding.searchRV.setAdapter(animalAdapter);

        binding.backBtn.setOnClickListener(view -> finish());

        binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                search(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void search(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Animals").orderByChild("name")
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