package id.ac.ukdw.lansmart.seller.productseller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.seller.homeseller.HomeSellerActivity;
import id.ac.ukdw.lansmart.constructor.Product;
import id.ac.ukdw.lansmart.seller.productseller.addproductseller.AddProductSellerActivity;

public class ProductMenuSellerActivity extends AppCompatActivity {
    public static final String EXTRA_SELLERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_SELLERCODE";
    private EditText editText_search;
    private TextView textView_page_now, textView_total_page;
    private ImageButton imageButton_search;
    private Button button_addProduct, button_nextPage_Buyer, button_prefPage_Buyer;
    private RecyclerView recyclerView_listProduct;
    private Spinner spinner_group_by;

    private int pages = 0, totalPage = 0;
    private String sellerCode, search, groupBy;
    private String [] groupByTemplate = {"Semua", "Bahan Pokok", "Pakaian Dan Asesoris", "Peralatan Rumah Tangga", "Kesehatan", "Hobi", "Lainnya"};


    private ArrayList<Product> listProduct = new ArrayList<Product>();
    private ArrayList<Product> listFourProduct = new ArrayList<Product>();

    private DatabaseReference databaseReferenceProduct;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_menu_seller);

        editText_search = (EditText) findViewById(R.id.editText_search_product_menu_seller);
        imageButton_search = (ImageButton) findViewById(R.id.imageButton_search_product_menu_seller);
        textView_page_now = (TextView) findViewById(R.id.textView_page_now_product_seller);
        textView_total_page = (TextView) findViewById(R.id.textView_total_page_product_seller);
        button_addProduct = (Button) findViewById(R.id.button_add_product_seller);
        button_nextPage_Buyer = (Button) findViewById(R.id.button_nextPage_product_seller);
        button_prefPage_Buyer = (Button) findViewById(R.id.button_prefPage_product_seller);
        spinner_group_by = (Spinner) findViewById(R.id.spinner_groupby_product_menu_seller);
        recyclerView_listProduct = (RecyclerView) findViewById(R.id.recyclerView_product_menu_seller);

        Intent intent = getIntent();
        sellerCode = intent.getStringExtra(HomeSellerActivity.EXTRA_SELLERCODE);

        ArrayAdapter<String> adapterSpinerStatus = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupByTemplate);
        adapterSpinerStatus.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_group_by.setAdapter(adapterSpinerStatus);

        spinner_group_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listProduct.clear();
                listFourProduct.clear();
                editText_search.setText("");
                groupBy = spinner_group_by.getSelectedItem().toString();
                recyclerViewListProduct(groupBy, sellerCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                listProduct.clear();
                listFourProduct.clear();
                editText_search.setText("");
                groupBy = "semua";
                recyclerViewListProduct(groupBy, sellerCode);
            }
        });

        button_addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_addProduct(sellerCode);
            }
        });

        imageButton_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchProduct(sellerCode);
            }
        });

        button_nextPage_Buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages++;
//                halaman belum akhir
                if(pages < totalPage){
                    int start = (pages-1)*4;
                    int end = start + 3;
                    listFourProduct.clear();
                    for (int i=start; i <= end; i++){
                        listFourProduct.add(listProduct.get(i));
                    }
                    showRecyclerView(listFourProduct, sellerCode);
                }
//                halaman akhir
                else if(pages == totalPage){
                    int start = (pages-1) * 4;
                    int end = listProduct.size();
                    listFourProduct.clear();

                    for (int i=start; i<end; i++){
                        listFourProduct.add(listProduct.get(i));
                    }
                    showRecyclerView(listFourProduct, sellerCode);
                }
//                tidak ada halaman
                else{
                    pages--;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Terakhir",Toast.LENGTH_SHORT).show();
                }
                textView_page_now.setText("" + pages);
                textView_total_page.setText("" + totalPage);
            }
        });

        button_prefPage_Buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages--;
                int start = (pages-1)*4;
                int end = start + 3;
                listFourProduct.clear();
//                belum halaman awal
                if(pages >= 1){
                    for (int i=start; i <= end; i++){
                        listFourProduct.add(listProduct.get(i));
                    }
                    showRecyclerView(listFourProduct, sellerCode);
                }
//                sudah halaman awal
                else{
                    pages++;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Pertama",Toast.LENGTH_SHORT).show();
                }
                textView_page_now.setText("" + pages);
                textView_total_page.setText("" + totalPage);
            }
        });
    }

    private void recyclerViewListProduct(String groupBy, String sellerCode) {
        databaseReferenceProduct = db.getReference(Product.class.getSimpleName());
        databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProduct.clear();
                listFourProduct.clear();
                for (DataSnapshot item : snapshot.getChildren()){
                    Product product = item.getValue(Product.class);
                    if (product.getSellerId().equals(sellerCode)){
                        if (groupBy.equalsIgnoreCase("semua")){
                            listProduct.add(product);
                        }
                        else if (groupBy.equalsIgnoreCase("bahan pokok")){
                            if (product.getCategory().equalsIgnoreCase("bahan pokok")){
                                listProduct.add(product);
                            }
                        }
                        else if (groupBy.equalsIgnoreCase("pakaian dan asesoris")){
                            if (product.getCategory().equalsIgnoreCase("pakaian dan asesoris")){
                                listProduct.add(product);
                            }
                        }
                        else if (groupBy.equalsIgnoreCase("peralatan rumah tangga")){
                            if (product.getCategory().equalsIgnoreCase("peralatan rumah tangga")){
                                listProduct.add(product);
                            }
                        }
                        else if (groupBy.equalsIgnoreCase("kesehatan")){
                            if (product.getCategory().equalsIgnoreCase("kesehatan")){
                                listProduct.add(product);
                            }
                        }
                        else if (groupBy.equalsIgnoreCase("hobi")){
                            if (product.getCategory().equalsIgnoreCase("hobi")){
                                listProduct.add(product);
                            }
                        }
                        else if (groupBy.equalsIgnoreCase("lainnya")){
                            if (product.getCategory().equalsIgnoreCase("lainnya")){
                                listProduct.add(product);
                            }
                        }
                    }
                }
                Collections.sort(listProduct, new SortByStatusProductSeller());
                if (listProduct.size()<=4){
                    totalPage = 1;
                    pages = 1;
                    listFourProduct = listProduct;
                    showRecyclerView(listFourProduct, sellerCode);
                }
                else{
                    int mod = listProduct.size()%4;
                    if (mod==0){
                        totalPage = listProduct.size()/4;
                    }
                    else {
                        totalPage = (listProduct.size()/4)+1;
                    }
                    pages = 1;
                    for (int i=0; i<4; i++){
                        listFourProduct.add(listProduct.get(i));
                    }
                    showRecyclerView(listFourProduct, sellerCode);
                }
                textView_page_now.setText("" + pages);
                textView_total_page.setText("" + totalPage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchProduct(String sellerCode) {
        search = editText_search.getText().toString();
        listProduct.clear();
        listFourProduct.clear();

        if (search.length()>0){
            groupBy = spinner_group_by.getSelectedItem().toString();
            databaseReferenceProduct = db.getReference(Product.class.getSimpleName());
            databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listProduct.clear();
                    listFourProduct.clear();
                    for (DataSnapshot item : snapshot.getChildren()){
                        Product product = item.getValue(Product.class);
                        if (groupBy.equalsIgnoreCase("semua")){
                            if (product.getNameProduct().equalsIgnoreCase(search)
                                    || product.getProductId().equalsIgnoreCase(search) || product.getStatus().equalsIgnoreCase(search)
                                    || product.getSellerId().equalsIgnoreCase(search)){
                                listProduct.add(product);
                            }
                        }
                        else{
                            if (product.getCategory().equalsIgnoreCase(groupBy) && (product.getNameProduct().equalsIgnoreCase(search)
                                    || product.getProductId().equalsIgnoreCase(search) || product.getStatus().equalsIgnoreCase(search)
                                    || product.getSellerId().equalsIgnoreCase(search))){
                                listProduct.add(product);
                            }
                        }
                    }
                    if (listProduct.size()<=4){
                        totalPage = 1;
                        pages = 1;
                        listFourProduct = listProduct;
                        showRecyclerView(listFourProduct, sellerCode);
                    }
                    else{
                        int mod = listProduct.size()%4;
                        if (mod==0){
                            totalPage = listProduct.size()/4;
                        }
                        else {
                            totalPage = (listProduct.size()/4)+1;
                        }
                        pages = 1;
                        for (int i=0; i<4; i++){
                            listFourProduct.add(listProduct.get(i));
                        }
                        showRecyclerView(listFourProduct, sellerCode);
                    }

                    textView_page_now.setText("" + pages);
                    textView_total_page.setText("" + totalPage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
    }

    private void showRecyclerView(ArrayList<Product> listProduct, String sellerCode) {
        recyclerView_listProduct.setLayoutManager(new LinearLayoutManager(this));
        ListProductSellerAdapter listProductSellerAdapter = new ListProductSellerAdapter(listProduct,sellerCode);
        recyclerView_listProduct.setAdapter(listProductSellerAdapter);
    }

    private void button_addProduct(String sellerCode) {
        Intent intent = new Intent(ProductMenuSellerActivity.this, AddProductSellerActivity.class);
        intent.putExtra(EXTRA_SELLERCODE, sellerCode);
        startActivity(intent);
    }
}
