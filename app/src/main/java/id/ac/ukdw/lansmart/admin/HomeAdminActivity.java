package id.ac.ukdw.lansmart.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import id.ac.ukdw.lansmart.LoginActivity;
import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.ZoomImageActivity;
import id.ac.ukdw.lansmart.admin.buyermenu.BuyerMenuAdminActivity;
import id.ac.ukdw.lansmart.admin.productmenu.ProductMenuSellerAdminActivity;
import id.ac.ukdw.lansmart.admin.sellermenu.SellerMenuAdminActivity;
import id.ac.ukdw.lansmart.constructor.Product;
import id.ac.ukdw.lansmart.constructor.Seller;

public class HomeAdminActivity extends AppCompatActivity {
    public static final String EXTRA_ADMINCODE = "id.ac.udkw.rockpaperscissors.EXTRA_ADMINCODE";
    public static final String EXTRA_PRODUCTCODE = "id.ac.udkw.rockpaperscissors.EXTRA_PRODUCTCODE";
    private ImageButton imageButton_seller_menu, imageButton_product_menu, imageButton_buyer_menu, imageButton_expensive, imageButton_cheapest;
    private TextView textView_totalSeller, textView_totalActiveProduct, textView_totalPassiveProduct, button_logout;
    private TextView textView_nameCheap, textView_sellerCheap, textView_priceCheap, textView_itemAvailableCheap;
    private TextView textView_nameExpensive, textView_sellerExpensive, textView_priceExpensive, textView_itemAvailableExpensive;

    private String openBy, productCodeExpensive, productCodeCheapest;
    private int totalSeller = 0, totalProduct = 0, productActive = 0, productPassive = 0;

    private DatabaseReference databaseReferenceSeller, databaseReferenceProduct;

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        button_logout = (TextView) findViewById(R.id.textView_button_logout_admin);
        textView_totalSeller = (TextView) findViewById(R.id.textView_total_seller_admin);
        textView_totalActiveProduct = (TextView) findViewById(R.id.textView_total_active_admin);
        textView_totalPassiveProduct = (TextView) findViewById(R.id.textView_total_passive_admin);
        textView_nameExpensive = (TextView) findViewById(R.id.textView_product_name_expensive_admin);
        textView_sellerExpensive = (TextView) findViewById(R.id.textView_product_seller_name_expensive_admin);
        textView_priceExpensive = (TextView) findViewById(R.id.textView_product_price_expensive_admin);
        textView_itemAvailableExpensive = (TextView) findViewById(R.id.textView_product_available_expensive_admin);
        textView_nameCheap = (TextView) findViewById(R.id.textView_product_name_cheapest_admin);
        textView_sellerCheap = (TextView) findViewById(R.id.textView_product_seller_name_cheapest_admin);
        textView_priceCheap = (TextView) findViewById(R.id.textView_product_price_cheapest_admin);
        textView_itemAvailableCheap = (TextView) findViewById(R.id.textView_product_available_cheapest_admin);
        imageButton_seller_menu = (ImageButton) findViewById(R.id.imageButton_seller_menu_admin);
        imageButton_product_menu = (ImageButton) findViewById(R.id.imageButton_product_menu_admin);
        imageButton_buyer_menu = (ImageButton) findViewById(R.id.imageButton_buyer_menu_admin);
        imageButton_expensive = (ImageButton) findViewById(R.id.imageButton_expensive_admin);
        imageButton_cheapest = (ImageButton) findViewById(R.id.imageButton_cheapest_admin);

        Intent intent = getIntent();
        openBy = intent.getStringExtra(LoginActivity.EXTRA_ADMINCODE);


        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReferenceSeller = db.getReference(Seller.class.getSimpleName());
        databaseReferenceProduct = db.getReference(Product.class.getSimpleName());

        databaseReferenceSeller.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setTextAdmin();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setTextAdmin();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        setTextAdmin();

        imageButton_seller_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellerMenu();
            }
        });

        imageButton_product_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productMenu();
            }
        });

        imageButton_buyer_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyerMenu();
            }
        });

        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutAdmin();
            }
        });

        imageButton_expensive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentZoom = new Intent(HomeAdminActivity.this, ZoomImageActivity.class);
                intentZoom.putExtra(EXTRA_PRODUCTCODE, productCodeExpensive);
                startActivity(intentZoom);
            }
        });

        imageButton_cheapest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentZoom = new Intent(HomeAdminActivity.this, ZoomImageActivity.class);
                intentZoom.putExtra(EXTRA_PRODUCTCODE, productCodeCheapest);
                startActivity(intentZoom);
            }
        });
    }

    private void logoutAdmin() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Apakah Anda Ingin Keluar?");
        alert.setCancelable(true);
        alert.setNegativeButton("Keluar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentlogout = new Intent(id.ac.ukdw.lansmart.admin.HomeAdminActivity.this, LoginActivity.class);
                startActivity(intentlogout);
            }
        });

        alert.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    public void setTextAdmin(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReferenceSeller = db.getReference(Seller.class.getSimpleName());
        databaseReferenceProduct = db.getReference(Product.class.getSimpleName());

        databaseReferenceSeller.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalSeller = 0;
                for(DataSnapshot item : snapshot.getChildren()){
                    totalSeller++;
                }
                textView_totalSeller.setText(totalSeller + " Penjual");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Error: " + error,Toast.LENGTH_SHORT).show();
            }
        });

        databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productActive = 0;
                productPassive = 0;
                totalProduct = 0;
                for (DataSnapshot item : snapshot.getChildren()){
                    Product product = item.getValue(Product.class);
                    if (product.getStatus().toLowerCase().equals("aktif")){
                        productActive++;
                    }
                    else {
                        productPassive++;
                    }
                    totalProduct++;
                }
                textView_totalActiveProduct.setText(productActive + " Produk");
                textView_totalPassiveProduct.setText(productPassive + " Produk");

                if(totalProduct == 0){
                    textView_nameCheap.setText("-");
                    textView_priceCheap.setText("-");
                    textView_sellerCheap.setText("-");
                    textView_itemAvailableCheap.setText("-");

                    textView_nameExpensive.setText("-");
                    textView_priceExpensive.setText("-");
                    textView_sellerExpensive.setText("-");
                    textView_itemAvailableExpensive.setText("-");
                }
                else{
                    Product expensive = null, cheapest = null;
                    ArrayList<Product> productList = new ArrayList<Product>();
                    for (DataSnapshot item : snapshot.getChildren()){
                        Product product = item.getValue(Product.class);
                        productList.add(product);
                    }

                    for (int i = 0; i<productList.size(); i++){
                        productList.get(i).getPrice();
                        if (i==0){
                            expensive = productList.get(i);
                            cheapest = productList.get(i);
                        }
                        else{
                            if ((expensive.getPrice() < productList.get(i).getPrice()) && productList.get(i).getStatus().equalsIgnoreCase("aktif")){
                                expensive = productList.get(i);
                            }
                            if ((cheapest.getPrice() > productList.get(i).getPrice()) && productList.get(i).getStatus().equalsIgnoreCase("aktif")){
                                cheapest = productList.get(i);
                            }
                        }
                    }

                    productCodeExpensive = expensive.getProductId();
                    productCodeCheapest = cheapest.getProductId();

                    textView_nameCheap.setText(cheapest.getNameProduct());
                    textView_priceCheap.setText(formatRupiah.format(cheapest.getPrice()));
                    textView_sellerCheap.setText("Penjual: " + cheapest.getSellerId());
                    textView_itemAvailableCheap.setText("Tersedia: " + cheapest.getAvailable());
                    Glide.with(getApplicationContext()).load(cheapest.getUrl()).into(imageButton_cheapest);

                    textView_nameExpensive.setText(expensive.getNameProduct());
                    textView_priceExpensive.setText(formatRupiah.format(expensive.getPrice()));
                    textView_sellerExpensive.setText("Penjual: " + expensive.getSellerId());
                    textView_itemAvailableExpensive.setText("Tersedia: " + expensive.getAvailable());
                    Glide.with(getApplicationContext()).load(expensive.getUrl()).into(imageButton_expensive);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Error: " + error,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sellerMenu(){
        Intent intentSellerMenu = new Intent(id.ac.ukdw.lansmart.admin.HomeAdminActivity.this, SellerMenuAdminActivity.class);
        intentSellerMenu.putExtra(EXTRA_ADMINCODE,openBy);
        startActivity(intentSellerMenu);
    }

    public void productMenu(){
        Intent intentProductMenu = new Intent(id.ac.ukdw.lansmart.admin.HomeAdminActivity.this, ProductMenuSellerAdminActivity.class);
        intentProductMenu.putExtra(EXTRA_ADMINCODE,openBy);
        startActivity(intentProductMenu);
    }

    public void buyerMenu(){
        Intent intentBuyerMenu = new Intent(id.ac.ukdw.lansmart.admin.HomeAdminActivity.this, BuyerMenuAdminActivity.class);
        intentBuyerMenu.putExtra(EXTRA_ADMINCODE, openBy);
        startActivity(intentBuyerMenu);
    }

    @Override
    public void onBackPressed() {
        logoutAdmin();
    }
}