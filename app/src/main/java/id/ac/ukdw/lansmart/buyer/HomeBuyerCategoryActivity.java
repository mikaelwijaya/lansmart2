package id.ac.ukdw.lansmart.buyer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import id.ac.ukdw.lansmart.buyer.cart.CartBuyerActivity;
import id.ac.ukdw.lansmart.buyer.checkout.CheckoutBuyerActivity;
import id.ac.ukdw.lansmart.buyer.profile.ChangeNameBuyerActivity;
import id.ac.ukdw.lansmart.buyer.profile.ChangePasswordBuyerActivity;
import id.ac.ukdw.lansmart.buyer.profile.ProfileBuyerActivity;
import id.ac.ukdw.lansmart.constructor.Buyer;
import id.ac.ukdw.lansmart.constructor.Product;

public class HomeBuyerCategoryActivity extends AppCompatActivity {
    public static final String EXTRA_BUYERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERCODE";
    private ImageButton imageButton_profile, imageButton_cart, imageButton_search;
    private Button button_nextPage_Buyer, button_prefPage_Buyer;
    private EditText editText_search;
    private TextView textView_buyerName, textView_address_buyer, textView_button_logout;
    private RecyclerView recyclerView_product;

    private String search, userCodeBuyer, name, address, category;
    private int pages = 0, totalPage = 0;

    ArrayList<Product> listProduct = new ArrayList<Product>();
    ArrayList<Product> listFiveProduct = new ArrayList<Product>();

    private DatabaseReference databaseReferenceProduct, databaseReferenceBuyer;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_buyer_category);

        textView_buyerName = (TextView) findViewById(R.id.textView_buyer_name_category);
        textView_address_buyer = (TextView) findViewById(R.id.textView_alamat_buyer_category);
        imageButton_profile = (ImageButton) findViewById(R.id.imageButton_profile_buyer_category);
        imageButton_cart = (ImageButton) findViewById(R.id.imageButton_cart_buyer_category);
        imageButton_search = (ImageButton) findViewById(R.id.imageButton_search_buyer_category);
        editText_search = (EditText) findViewById(R.id.editText_search_buyer_category);
        recyclerView_product = findViewById(R.id.recyclerView_product_buyer_category);
        textView_button_logout = (TextView) findViewById(R.id.textView_button_logout_buyer_category);
        button_nextPage_Buyer = (Button) findViewById(R.id.button_nextPage_Buyer_category);
        button_prefPage_Buyer = (Button) findViewById(R.id.button_prefPage_Buyer_category);

        Intent intent = getIntent();
        category = intent.getStringExtra(HomeBuyerActivity.EXTRA_CATEGORY);
        userCodeBuyer = intent.getStringExtra(HomeBuyerActivity.EXTRA_BUYERCODE);

        databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
        databaseReferenceBuyer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Buyer buyer = item.getValue(Buyer.class);
                    if (buyer.getUserName().equals(userCodeBuyer)) {
                        name = buyer.getName();
                        address = buyer.getAddress();
                    }
                }

                textView_buyerName.setText(name);
                textView_address_buyer.setText(address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error + "", Toast.LENGTH_SHORT).show();
            }
        });


        databaseReferenceProduct = db.getReference(Product.class.getSimpleName());
        databaseReferenceProduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Product product = item.getValue(Product.class);
                    if (product.getStatus().equalsIgnoreCase("aktif") && product.getCategory().equalsIgnoreCase(category)){
                        listProduct.add(product);
                    }
                }

                if (listProduct.size()<=5){
                    totalPage = 1;
                    pages = 1;
                    listFiveProduct = listProduct;
                    showRecyclerListProduct(listFiveProduct);
                }
                else{
                    int mod = listProduct.size()%4;

                    if (mod==0){
                        totalPage = listProduct.size()/5;
                    }
                    else {
                        totalPage = (listProduct.size()/5)+1;
                    }
                    pages = 1;
                    for (int i=0; i<5; i++){
                        listFiveProduct.add(listProduct.get(i));
                    }
                    showRecyclerListProduct(listFiveProduct);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageButton_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileBuyer();
            }
        });

        imageButton_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartBuyer();
            }
        });

        imageButton_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBuyer();
            }
        });

        textView_button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutBuyer(v);
            }
        });

        button_nextPage_Buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages++;
//                halaman belum akhir
                if(pages < totalPage){
                    int start = (pages-1)*5;
                    int end = start + 4;
                    listFiveProduct.clear();
                    for (int i=start; i <= end; i++){
                        listFiveProduct.add(listProduct.get(i));
                    }
                    showRecyclerListProduct(listFiveProduct);
                }
//                halaman akhir
                else if(pages == totalPage){
                    int start = (pages-1) * 5;
                    int end = listProduct.size();
                    listFiveProduct.clear();

                    for (int i=start; i<end; i++){
                        listFiveProduct.add(listProduct.get(i));
                    }
                    showRecyclerListProduct(listFiveProduct);
                }
//                tidak ada halaman
                else{
                    pages--;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Terakhir",Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_prefPage_Buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages--;
                int start = (pages-1)*5;
                int end = start + 4;
                listFiveProduct.clear();
//                belum halaman awal
                if(pages >= 1){
                    for (int i=start; i <= end; i++){
                        listFiveProduct.add(listProduct.get(i));
                    }
                    showRecyclerListProduct(listFiveProduct);
                }
//                sudah halaman awal
                else{
                    pages++;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Pertama",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void logoutBuyer(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        alert.setMessage("Apakah Anda Ingin Keluar?");
        alert.setCancelable(true);
        alert.setNegativeButton("Keluar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentlogout = new Intent(HomeBuyerCategoryActivity.this, LoginActivity.class);
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

    public void showRecyclerListProduct(ArrayList<Product> listProduct){
        recyclerView_product.setLayoutManager(new LinearLayoutManager(this));
        ListProductBuyerAdapter listProductBuyerAdapter = new ListProductBuyerAdapter(listProduct, userCodeBuyer);
        recyclerView_product.setAdapter(listProductBuyerAdapter);
    }

    public void profileBuyer(){
        Intent intentProfileBuyer = new Intent(HomeBuyerCategoryActivity.this, ProfileBuyerActivity.class);
        intentProfileBuyer.putExtra(EXTRA_BUYERCODE, userCodeBuyer);
        startActivity(intentProfileBuyer);
    }

    public void cartBuyer(){
        Intent intentCartBuyer = new Intent(HomeBuyerCategoryActivity.this, CartBuyerActivity.class);
        intentCartBuyer.putExtra(EXTRA_BUYERCODE, userCodeBuyer);
        startActivity(intentCartBuyer);
    }

    public void searchBuyer(){
        search = editText_search.getText().toString();

        if (search.isEmpty()){
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
        else{
            listProduct.clear();
            databaseReferenceProduct.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean check = false;
                    for (DataSnapshot item : snapshot.getChildren()){
                        Product product = item.getValue(Product.class);
                        if (product.getNameProduct().equalsIgnoreCase(search) || product.getProductId().equals(search)){
                            check = true;
                            listProduct.add(product);
                        }
                    }

                    if (check == true){
                        recyclerView_product = findViewById(R.id.recyclerView_product_buyer_category);
                        showRecyclerListProduct(listProduct);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Tidak ada produk " + search,Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
