package com.example.petlink;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.petlink.Adapter.AnimalAdapter;
import com.example.petlink.Model.Animal;
import com.example.petlink.databinding.FragmentHomeBinding;
import com.example.petlink.databinding.FragmentWishlistBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {

    FragmentWishlistBinding binding;
    private AnimalAdapter animalAdapter;
    private List<Animal> animalList;
    private List<String> likes;

    private FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWishlistBinding.inflate(inflater, container, false);
        binding.toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        animalList = new ArrayList<>();
        animalAdapter = new AnimalAdapter(getContext(), animalList);
        binding.wishlistRV.setAdapter(animalAdapter);
        checkLiked(firebaseUser.getUid());

        return binding.getRoot();
    }
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void checkLiked(String profileId) {
        likes = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes")
                .child(profileId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    likes.add(dataSnapshot.getKey());
                }

                readLikes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readLikes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Animals");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                animalList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Animal animal = dataSnapshot.getValue(Animal.class);
                    for (String id : likes) {
                        if (animal.getId().equals(id)) {
                            animalList.add(animal);
                        }
                    }
                }

                animalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}