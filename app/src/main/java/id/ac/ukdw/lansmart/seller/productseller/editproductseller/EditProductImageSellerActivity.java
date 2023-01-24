package id.ac.ukdw.lansmart.seller.productseller.editproductseller;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.constructor.Product;
import id.ac.ukdw.lansmart.seller.homeseller.HomeSellerActivity;
import id.ac.ukdw.lansmart.seller.productseller.addproductseller.AddProductSellerActivity;

public class EditProductImageSellerActivity extends AppCompatActivity {
    public static final String EXTRA_SELLERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_SELLERCODE";
    private ImageView imageView_product;
    private Button button_addImage, button_done;

    private String idProduct, productKey, url;
    private Uri imageUri;
    private Product editProduct;

    private DatabaseReference databaseReferenceProduct;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_image_seller);

        Intent intent = getIntent();
        idProduct = intent.getStringExtra(EditProductSellerAcivity.EXTRA_IDPRODUCT);

        imageView_product = (ImageView) findViewById(R.id.imageView_add_product_image_seller);
        button_addImage = (Button) findViewById(R.id.button_add_product_image_seller);
        button_done = (Button) findViewById(R.id.button_add_finish_product_seller);

        button_addImage.setText("GANTI GAMBAR");

        databaseReferenceProduct = db.getReference(Product.class.getSimpleName());
        databaseReferenceProduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Product product = item.getValue(Product.class);
                    if (product.getProductId().equals(idProduct)){
                        editProduct = item.getValue(Product.class);
                        productKey = item.getKey();
                        url = product.getUrl();
                        Glide.with(getApplicationContext()).load(url).into(imageView_product);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button_addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPicture(idProduct);
            }
        });

        button_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null){
                    onBackPressed();
                }
                else{
                    editProduct(productKey);
                }
            }
        });
    }

    public void editPicture(String idProduct){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imageView_product.setImageURI(imageUri);
        }
    }

    public void editProduct(String productKey) {
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                editProduct.setUrl(imageUri.toString());
                databaseReferenceProduct = db.getReference(Product.class.getSimpleName());
                databaseReferenceProduct.child(productKey).setValue(editProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(url);
                        storageReference.delete();
                        Toast.makeText(getApplicationContext(), "Berhasil Mengganti Gambar Produk", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Gagal Mengganti Gambar Produk!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Gagal Mengunggah Gambar Produk!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}
