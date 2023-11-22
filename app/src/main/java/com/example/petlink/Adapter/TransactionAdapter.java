package com.example.petlink.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petlink.AnimalDetailActivity;
import com.example.petlink.ChechoutActivity;
import com.example.petlink.Model.Animal;
import com.example.petlink.Model.Transaction;
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

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private Context context;
    private List<Transaction> transactionList;
    FirebaseUser firebaseUser;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_item, parent, false);
        return new TransactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Transaction transaction = transactionList.get(position);

        holder.remove.setOnClickListener(view -> FirebaseDatabase.getInstance().getReference("Cart").child(transaction.getId()).removeValue());

        holder.main.setOnClickListener(view -> context.startActivity(new Intent(context, AnimalDetailActivity.class).putExtra("animalID", transaction.getAnimalId())));

        holder.checkout.setOnClickListener(view -> context.startActivity(new Intent(context, ChechoutActivity.class).putExtra("animalID", transaction.getAnimalId())));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Animals").child(transaction.getAnimalId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Animal animal = snapshot.getValue(Animal.class);
                holder.name.setText(animal.getName());
                holder.price.setText(animal.getPrice());
                holder.location.setText(animal.getLocation());
                try{
                    Glide.with(context).load(animal.getImageUrl()).into(holder.imageView);
                }catch (NullPointerException ignore){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button remove, checkout;
        ImageView imageView;
        TextView name, price, location;
        MaterialCardView main;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            remove = itemView.findViewById(R.id.removeBtn);
            imageView = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            location = itemView.findViewById(R.id.location);
            main = itemView.findViewById(R.id.mainHandler);
            checkout = itemView.findViewById(R.id.checkoutBtn);
        }
    }
}
