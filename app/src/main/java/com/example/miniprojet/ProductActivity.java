package com.example.miniprojet;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class ProductActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true); // Enable disk persistence (optional)


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.activity_menu);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(productAdapter);


// Fetch product data from Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/products");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productList.add(product);
                }

                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

// ...

// Fetch image URLs from Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("product_images");
        for (Product product : productList) {
            storageReference.child(product.getImageUrl()).getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        product.setImageUrl(uri.toString());
                        productAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                    });
        }



    }
    public void addproduct (View x){
        Intent intent= new Intent(ProductActivity.this, AddProductActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }


    private void updateRatingInFirebase(String productId, float newRating) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(productId);

        productRef.child("rating").setValue(newRating);
        calculateAverageRating(productId);
    }

    private void calculateAverageRating(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(productId).child("ratings");

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalRatings = 0;
                float sumRatings = 0;

                for (DataSnapshot ratingSnapshot : snapshot.getChildren()) {
                    float ratingValue = ratingSnapshot.getValue(Float.class);
                    sumRatings += ratingValue;
                    totalRatings++;
                }

                if (totalRatings > 0) {
                    float averageRating = sumRatings / totalRatings;
                    updateAverageRating(productId, averageRating);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
            }
        });
    }

    private void updateAverageRating(String productId, float averageRating) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(productId);
        productRef.child("averageRating").setValue(averageRating);
    }

    public void onRatingBarClick(String productId, float newRating) {
        // Update rating in Firebase or perform other relevant operations
        updateRatingInFirebase(productId, newRating);
    }

}