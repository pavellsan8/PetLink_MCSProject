package com.example.petlink;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.petlink.Adapter.AnimalAdapter;
import com.example.petlink.Model.Animal;
import com.example.petlink.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    private List<Animal> animalList;
    private AnimalAdapter animalAdapter;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getAnimals();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        animalList = new ArrayList<>();
        animalAdapter = new AnimalAdapter(getContext(), animalList);
        binding.animalsRV.setHasFixedSize(true);
        binding.animalsRV.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.animalsRV.setAdapter(animalAdapter);

        binding.searchBar.setOnClickListener(view -> startActivity(new Intent(getContext(), SearchActivity.class)));

        binding.catCategory.catCategory.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), CategoryActivity.class).putExtra("category", "cat");
            startActivity(intent);
        });

        binding.dogCategory.dogCategory.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), CategoryActivity.class).putExtra("category", "dog");
            startActivity(intent);
        });

        binding.userBtn.setOnClickListener(view -> openDialog());

        return binding.getRoot();
    }

    private void openDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.user_dialog);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.findViewById(R.id.logoutBtn).setOnClickListener(view -> {
            logOut();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.dismissBtn).setOnClickListener(view -> {
            dialog.dismiss();
        });
    }

    private void logOut() {


        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }
        };

        auth.addAuthStateListener(authStateListener);
        auth.signOut();
    }

    private void getAnimals(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Animals");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    animalList.clear();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Animal animal = dataSnapshot.getValue(Animal.class);
                        animalList.add(animal);
//                        Toast.makeText(getContext(), animal.getName(), Toast.LENGTH_SHORT).show();
                    }

                    animalAdapter.notifyDataSetChanged();
                } else {
                }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}