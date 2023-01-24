package id.ac.ukdw.lansmart.seller.productseller.addproductseller;

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

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.constructor.Product;
import id.ac.ukdw.lansmart.seller.homeseller.HomeSellerActivity;

public class AddProductImageSellerActivity extends AppCompatActivity {
    public static final String EXTRA_SELLERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_SELLERCODE";
    private ImageView imageView_product;
    private Button button_addImage, button_create;

    private String codeSeller, idProduct, nameProduct, statusProduct, category;
    private double price;
    private int stock;

    private DatabaseReference databaseReferenceProduct;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_image_seller);

        Intent intent = getIntent();
        codeSeller = intent.getStringExtra(AddProductSellerActivity.EXTRA_SELLERCODE);
        idProduct = intent.getStringExtra(AddProductSellerActivity.EXTRA_IDPRODUCT);
        nameProduct = intent.getStringExtra(AddProductSellerActivity.EXTRA_NAMEPRODUCT);
        price = Double.parseDouble(intent.getStringExtra(AddProductSellerActivity.EXTRA_PRICE));
        stock = Integer.parseInt(intent.getStringExtra(AddProductSellerActivity.EXTRA_STOCK));
        statusProduct = intent.getStringExtra(AddProductSellerActivity.EXTRA_STATUS);
        category = intent.getStringExtra(AddProductSellerActivity.EXTRA_CATEGORY);

        imageView_product = (ImageView) findViewById(R.id.imageView_add_product_image_seller);
        button_addImage = (Button) findViewById(R.id.button_add_product_image_seller);
        button_create = (Button) findViewById(R.id.button_add_finish_product_seller);

//        button_addImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addPicture(idProduct);
//            }
//        });

        button_addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(AddProductImageSellerActivity.this)
                        .cropSquare()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null){
                    Toast.makeText(getApplicationContext(), "Masukkan Gambar!", Toast.LENGTH_SHORT).show();
                }
                else{
                    createProduct(codeSeller, idProduct, nameProduct, price, stock, statusProduct, category);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        imageUri = data.getData();
        imageView_product.setImageURI(imageUri);
    }

//    public void addPicture(String idProduct){
//        Intent galleryIntent = new Intent();
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        startActivityForResult(galleryIntent, 2);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 2 && resultCode == RESULT_OK && data != null){
//            imageUri = data.getData();
//            imageView_product.setImageURI(imageUri);
//        }
//    }

    public void createProduct(String codeSeller, String idProduct, String nameProduct, Double price, int stock, String statusProduct, String category) {


        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Product product = new Product(codeSeller, idProduct, nameProduct, price, stock, statusProduct, category, imageUri.toString());
                databaseReferenceProduct = db.getReference(Product.class.getSimpleName());
                databaseReferenceProduct.push().setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intentHomeSeller = new Intent(AddProductImageSellerActivity.this, HomeSellerActivity.class);
                        intentHomeSeller.putExtra(EXTRA_SELLERCODE, codeSeller);
                        Toast.makeText(getApplicationContext(), "Produk Berhasil Ditambahkan.", Toast.LENGTH_SHORT).show();
                        startActivity(intentHomeSeller);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Produk Gagal Dibuat!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}
