package com.example.petlink;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.petlink.Adapter.AnimalAdapter;
import com.example.petlink.Adapter.TransactionAdapter;
import com.example.petlink.Model.Transaction;
import com.example.petlink.databinding.FragmentTransactionBinding;
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

public class TransactionFragment extends Fragment {

    FragmentTransactionBinding binding;
    private FirebaseUser firebaseUser;

    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTransactionBinding.inflate(inflater, container, false);
        binding.toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(getContext(), transactionList);


        binding.transactionRV.setHasFixedSize(true);
        binding.transactionRV.setLayoutManager(new LinearLayoutManager(getContext()));
        reachTransaction();
        binding.transactionRV.setAdapter(transactionAdapter);

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

    private void reachTransaction(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    transactionList.clear();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Transaction transaction = dataSnapshot.getValue(Transaction.class);
//                        transactionList.add(transaction);
                        if (transaction.getUser().equals(firebaseUser.getUid())) {
                            transactionList.add(transaction);
                        }
                    }

                    transactionAdapter.notifyDataSetChanged();
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}