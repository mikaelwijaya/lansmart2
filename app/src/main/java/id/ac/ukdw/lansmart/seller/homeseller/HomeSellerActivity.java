package id.ac.ukdw.lansmart.seller.homeseller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import id.ac.ukdw.lansmart.seller.productseller.addproductseller.AddProductImageSellerActivity;
import id.ac.ukdw.lansmart.seller.productseller.ProductMenuSellerActivity;
import id.ac.ukdw.lansmart.seller.reportseller.ReportMenuSellerActivity;
import id.ac.ukdw.lansmart.constructor.Product;

public class HomeSellerActivity extends AppCompatActivity {
    public static final String EXTRA_SELLERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_SELLERCODE";
    private ImageButton imageButton_product, imageButton_report;
    private RecyclerView recyclerView_cheap, recyclerView_expensive;
    private TextView textView_button_logout;

    private String sellerCode;
    private ArrayList<Product> listProduct = new ArrayList<Product>();
    private ArrayList<Product> listCheap = new ArrayList<Product>();
    private ArrayList<Product> listExpensive = new ArrayList<Product>();

    private DatabaseReference databaseReferenceSeller, databaseReferenceProduct;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_seller);

        recyclerView_cheap = (RecyclerView) findViewById(R.id.recyclerView_cheapest_seller);
        recyclerView_expensive = (RecyclerView) findViewById(R.id.recyclerView_expensive_seller);
        imageButton_product = (ImageButton) findViewById(R.id.imageButton_product_menu_seller);
        imageButton_report = (ImageButton) findViewById(R.id.imageButton_report_menu_seller);
        textView_button_logout = (TextView) findViewById(R.id.textView_button_logout_seller);

        Intent intent = getIntent();
        sellerCode = intent.getStringExtra(LoginActivity.EXTRA_SELLERCODE);

        if (sellerCode.isEmpty()){
            sellerCode = intent.getStringExtra(AddProductImageSellerActivity.EXTRA_SELLERCODE);
        }

        databaseReferenceProduct = db.getReference(Product.class.getSimpleName());
        databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProduct.clear();
                listCheap.clear();
                listExpensive.clear();
                for (DataSnapshot item : snapshot.getChildren()){
                    Product product = item.getValue(Product.class);
                    if (product.getSellerId().equals(sellerCode) && product.getStatus().equalsIgnoreCase("aktif")){
                        listProduct.add(product);
                    }
                }

                for (int i = 0; i < listProduct.size(); i++) {
                    for (int j = 0; j < listProduct.size()-i-1; j++) {
                        double price1, price2;
                        price1 = listProduct.get(j).getPrice();
                        price2 = listProduct.get(j+1).getPrice();

                        if (price1 < price2) {
                            Product tempProduct = listProduct.get(j);
                            listProduct.set(j, listProduct.get(j+1));
                            listProduct.set(j+1, tempProduct);
                        }

                    }
                }

                if (listProduct.size()>=3){
                    listExpensive.clear();
                    listCheap.clear();
                    for (int i=listProduct.size(); i>listProduct.size()-3; i--){
                        listCheap.add(listProduct.get(i-1));
                    }

                    for (int i=0; i<3; i++){
                        listExpensive.add(listProduct.get(i));
                    }

                    showRecyclerListCheap(listCheap);
                    showRecyclerListExpensive(listExpensive);
                }
                else {
                    listExpensive.clear();
                    listCheap.clear();

                    listExpensive = listProduct;

                    for (int i=listProduct.size(); i>0; i--){
                        listCheap.add(listProduct.get(i-1));
                    }

                    showRecyclerListCheap(listCheap);
                    showRecyclerListExpensive(listExpensive);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageButton_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productMenu();
            }
        });

        imageButton_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportMenu();
            }
        });

        textView_button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutSeller();
            }
        });


    }

    @Override
    public void onBackPressed() {
        logoutSeller();
    }

    private void logoutSeller() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Apakah Anda Ingin Keluar?");
        alert.setCancelable(true);
        alert.setNegativeButton("Keluar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentlogout = new Intent(HomeSellerActivity.this, LoginActivity.class);
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

    public void showRecyclerListCheap(ArrayList<Product> listCheap){
        recyclerView_cheap.setLayoutManager(new LinearLayoutManager(this));
        ListCheapProductHomeSellerAdapter listCheapProductHomeSellerAdapterCheap = new ListCheapProductHomeSellerAdapter(listCheap);
        recyclerView_cheap.setAdapter(listCheapProductHomeSellerAdapterCheap);
    }

    public void showRecyclerListExpensive(ArrayList<Product> listExpensive){
        recyclerView_expensive.setLayoutManager(new LinearLayoutManager(this));
        ListExpensiveProductHomeSellerAdapter listExpensiveProductHomeSellerAdapterCheap = new ListExpensiveProductHomeSellerAdapter(listExpensive);
        recyclerView_expensive.setAdapter(listExpensiveProductHomeSellerAdapterCheap);
    }

    public void productMenu(){
        Intent intentProductMenu = new Intent(HomeSellerActivity.this, ProductMenuSellerActivity.class);
        intentProductMenu.putExtra(EXTRA_SELLERCODE, sellerCode);
        startActivity(intentProductMenu);
    }

    public void reportMenu(){
        Intent intentReportMenu = new Intent(HomeSellerActivity.this, ReportMenuSellerActivity.class);
        intentReportMenu.putExtra(EXTRA_SELLERCODE, sellerCode);
        startActivity(intentReportMenu);
    }
}