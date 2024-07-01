package com.example.miniprojet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddProductActivity extends AppCompatActivity {

    private EditText productNameEditText;
    private EditText productPriceEditText;
    private ImageView productImageView;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        productNameEditText = findViewById(R.id.editTextProductName);
        productPriceEditText = findViewById(R.id.editTextProductPrice);
        productImageView = findViewById(R.id.imageViewProduct);

        Button selectImageButton = findViewById(R.id.buttonSelectImage);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                getContent.launch(galleryIntent);
            }
        });

        Button addProductButton = findViewById(R.id.buttonAddProduct);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProductToFirebase();
            }
        });
    }

    private void uploadProductToFirebase() {
        String name = productNameEditText.getText().toString().trim();
        String price = productPriceEditText.getText().toString().trim();

        if (!name.isEmpty() && !price.isEmpty() && selectedImageUri != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("product_images/" + "product_image.jpg");

            UploadTask uploadTask = storageRef.putFile(selectedImageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    String productId = database.getReference("products").push().getKey();
                    Product product = new Product(productId, name, Double.parseDouble(price), imageUrl);

                    database.getReference("products").child(productId).setValue(product);

                    showToast("Product added successfully!");

                    finish();
                });
            }).addOnFailureListener(e -> {
                showToast("Failed to upload product. Please try again.");
            });
        } else {
            showToast("Please enter product name, price, and select an image.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private final ActivityResultLauncher<Intent> getContent =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri imageUri = data.getData();
                        selectedImageUri = imageUri;
                        Picasso.get().load(imageUri).into(productImageView);
                    }
                }
            });

    public void cancelBtn(View view) {
        showToast("Product addition canceled.");
        Intent intent = new Intent(AddProductActivity.this, ProductActivity.class);
        startActivity(intent);
    }
}
