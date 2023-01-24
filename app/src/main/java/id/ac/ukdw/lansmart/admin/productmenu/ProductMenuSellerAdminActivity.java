package id.ac.ukdw.lansmart.admin.productmenu;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import id.ac.ukdw.lansmart.admin.sellermenu.ListSellerAdapter;
import id.ac.ukdw.lansmart.constructor.Product;
import id.ac.ukdw.lansmart.seller.productseller.SortByStatusProductSeller;

public class ProductMenuSellerAdminActivity extends AppCompatActivity {
    private EditText editText_search;
    private ImageButton imageButton_search;
    private TextView textView_page_now, textView_total_page;
    private RecyclerView recyclerView_product;
    private Button button_nextPage_product_admin, button_prefPage_product_admin;
    private Spinner spinner_group_by;

    private String search, openBy, groupBy;
    private int pages = 0, totalPage = 0;
    private String [] groupByTemplate = {"Semua", "Bahan Pokok", "Pakaian Dan Asesoris", "Peralatan Rumah Tangga", "Kesehatan", "Hobi", "Lainnya"};

    private ArrayList<Product> listProduct = new ArrayList<Product>();
    private ArrayList<Product> listFourProduct = new ArrayList<Product>();

    private DatabaseReference databaseReferenceProduct;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_menu_admin);

        editText_search = (EditText) findViewById(R.id.editText_search_product_menu_admin);
        imageButton_search = (ImageButton) findViewById(R.id.imageButton_search_product_menu_admin);
        textView_page_now = (TextView) findViewById(R.id.textView_page_now_product_admin);
        textView_total_page = (TextView) findViewById(R.id.textView_total_page_product_admin);
        button_nextPage_product_admin = (Button) findViewById(R.id.button_nextPage_product_admin);
        button_prefPage_product_admin = (Button) findViewById(R.id.button_prefPage_product_admin);
        spinner_group_by = (Spinner) findViewById(R.id.spinner_groupby_product_menu_admin);
        recyclerView_product = findViewById(R.id.recyclerView_product_menu_seller_admin);

        Intent intent = getIntent();
        openBy = intent.getStringExtra(ListSellerAdapter.EXTRA_ADMINCODE);

        ArrayAdapter<String> adapterSpinerStatus = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupByTemplate);
        adapterSpinerStatus.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_group_by.setAdapter(adapterSpinerStatus);

        spinner_group_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editText_search.setText("");
                groupBy = spinner_group_by.getSelectedItem().toString();
                recyclerListProduct(listProduct, groupBy, openBy);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editText_search.setText("");
                groupBy = "semua";
                recyclerListProduct(listProduct, groupBy, openBy);
            }
        });


        button_nextPage_product_admin.setOnClickListener(new View.OnClickListener() {
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
                    showRecyclerView(listFourProduct, openBy);
                }
//                halaman akhir
                else if(pages == totalPage){
                    int start = (pages-1) * 4;
                    int end = listProduct.size();
                    listFourProduct.clear();

                    for (int i=start; i<end; i++){
                        listFourProduct.add(listProduct.get(i));
                    }
                    showRecyclerView(listFourProduct, openBy);
                }
//                tidak ada halaman
                else{
                    pages--;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Terakhir",Toast.LENGTH_SHORT).show();
                }
                textView_page_now.setText("" + pages);
            }
        });

        button_prefPage_product_admin.setOnClickListener(new View.OnClickListener() {
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
                    showRecyclerView(listFourProduct, openBy);
                }
//                sudah halaman awal
                else{
                    pages++;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Pertama",Toast.LENGTH_SHORT).show();
                }
                textView_page_now.setText("" + pages);
            }
        });

        imageButton_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search = editText_search.getText().toString();
                groupBy = spinner_group_by.getSelectedItem().toString();
                listProduct.clear();
                listFourProduct.clear();

                if (search.length()>0){
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
                                showRecyclerView(listFourProduct, openBy);
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
                                showRecyclerView(listFourProduct, openBy);
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
        });
    }

    public void recyclerListProduct(ArrayList<Product> listProduct, String groupBy, String  openBy){
        listProduct.clear();
        listFourProduct.clear();
        databaseReferenceProduct = db.getReference(Product.class.getSimpleName());
        databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProduct.clear();
                listFourProduct.clear();
                for(DataSnapshot item : snapshot.getChildren()){
                    Product product = item.getValue(Product.class);
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

                Collections.sort(listProduct, new SortByStatusProductSeller());
                if (listProduct.size()<=4){
                    totalPage = 1;
                    pages = 1;
                    listFourProduct = listProduct;
                    showRecyclerView(listFourProduct, openBy);
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
                    showRecyclerView(listFourProduct, openBy);
                }
                textView_page_now.setText("" + pages);
                textView_total_page.setText("" + totalPage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showRecyclerView(ArrayList<Product> listProduct, String openBy){
        recyclerView_product.setLayoutManager(new LinearLayoutManager(this));
        ListProductSellerAdminAdapter listProductSellerAdminAdapter = new ListProductSellerAdminAdapter(listProduct,openBy);
        recyclerView_product.setAdapter(listProductSellerAdminAdapter);
    }
}
