package id.ac.ukdw.lansmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import id.ac.ukdw.lansmart.admin.HomeAdminActivity;
import id.ac.ukdw.lansmart.admin.productmenu.ListProductSellerAdminAdapter;
import id.ac.ukdw.lansmart.admin.productmenu.ProductMenuSellerAdminActivity;
import id.ac.ukdw.lansmart.buyer.HomeBuyerActivity;
import id.ac.ukdw.lansmart.buyer.ListProductBuyerAdapter;
import id.ac.ukdw.lansmart.buyer.cart.ListCartBuyerAdapter;
import id.ac.ukdw.lansmart.buyer.checkout.ListCheckoutBuyerAdapter;
import id.ac.ukdw.lansmart.constructor.Buyer;
import id.ac.ukdw.lansmart.constructor.Product;
import id.ac.ukdw.lansmart.seller.homeseller.ListCheapProductHomeSellerAdapter;
import id.ac.ukdw.lansmart.seller.homeseller.ListExpensiveProductHomeSellerAdapter;
import id.ac.ukdw.lansmart.seller.productseller.ListProductSellerAdapter;
import id.ac.ukdw.lansmart.seller.reportseller.DetailTransactionActivity;

public class ZoomImageActivity extends AppCompatActivity {
    private TextView textView_productName;
    private ImageView imageView_productImage;
    private Button button_back;
    private ProgressBar progressBar_download;

    private String produkId = null, name, url;

    private DatabaseReference databaseReferenceProduct;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);

        textView_productName = (TextView) findViewById(R.id.textView_name_product_zoom_image);
        imageView_productImage = (ImageView) findViewById(R.id.imageView_image_zoom_image);
        button_back = (Button) findViewById(R.id.button_back_from_zoom_image);
        progressBar_download = (ProgressBar) findViewById(R.id.progressBar_download_image_zoom_image);

        Intent intent = getIntent();

        if (produkId == null){
            produkId = intent.getStringExtra(ListProductBuyerAdapter.EXTRA_PRODUCTCODE);
        }
        if (produkId == null){
            produkId = intent.getStringExtra(HomeAdminActivity.EXTRA_PRODUCTCODE);
        }
        if (produkId == null){
            produkId = intent.getStringExtra(ListExpensiveProductHomeSellerAdapter.EXTRA_PRODUCTCODE);
        }
        if (produkId == null){
            produkId = intent.getStringExtra(ListCheapProductHomeSellerAdapter.EXTRA_PRODUCTCODE);
        }
        if (produkId == null){
            produkId = intent.getStringExtra(ListCartBuyerAdapter.EXTRA_PRODUCTCODE);
        }
        if (produkId == null){
            produkId = intent.getStringExtra(ListCheckoutBuyerAdapter.EXTRA_PRODUCTCODE);
        }
        if (produkId == null){
            produkId = intent.getStringExtra(DetailTransactionActivity.EXTRA_PRODUCTCODE);
        }
        if (produkId == null){
            produkId = intent.getStringExtra(ListProductSellerAdminAdapter.EXTRA_PRODUCTCODE);
        }
        if (produkId == null){
            produkId = intent.getStringExtra(ListProductSellerAdapter.EXTRA_PRODUCTCODE);
        }

        progressBar_download.setVisibility(View.VISIBLE);

        databaseReferenceProduct = db.getReference(Product.class.getSimpleName());
        databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Product product = item.getValue(Product.class);
                    if (product.getProductId().equals(produkId)) {
                        name = product.getNameProduct();
                        url = product.getUrl();
                        if (url.isEmpty() || url == null){
                            progressBar_download.setVisibility(View.VISIBLE);
                        }
                        else{
                            progressBar_download.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                textView_productName.setText(name);
                Glide.with(getApplicationContext()).load(url).into(imageView_productImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
