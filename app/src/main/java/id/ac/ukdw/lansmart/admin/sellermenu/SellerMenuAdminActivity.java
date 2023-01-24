package id.ac.ukdw.lansmart.admin.sellermenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
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
import id.ac.ukdw.lansmart.admin.HomeAdminActivity;
import id.ac.ukdw.lansmart.constructor.Seller;

public class SellerMenuAdminActivity extends AppCompatActivity {
    private EditText editText_search;
    private ImageButton imageButton_search;
    private RadioButton radioButton_status, radioButton_userCode;
    private TextView textView_page_now, textView_total_page;
    private RecyclerView recyclerView_seller;
    private Button button_nextPage_seller_admin, button_prefPage_seller_admin;

    private String search, openBy;
    private int orderBy = 0, pages = 0, totalPage = 0;

    private ArrayList<Seller> listSeller = new ArrayList<Seller>();
    private ArrayList<Seller> listFourSeller = new ArrayList<Seller>();

    private DatabaseReference databaseReferenceSeller;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_menu_admin);

        Intent intent = getIntent();
        openBy = intent.getStringExtra(HomeAdminActivity.EXTRA_ADMINCODE);

        editText_search = (EditText) findViewById(R.id.editText_search_seller_menu_admin);
        imageButton_search = (ImageButton) findViewById(R.id.imageButton_search_seller_menu_admin);
        textView_page_now = (TextView) findViewById(R.id.textView_page_now_seller_admin);
        textView_total_page = (TextView) findViewById(R.id.textView_total_page_seller_admin);
        radioButton_status = (RadioButton) findViewById(R.id.radioButton_byStatus_seller_menu_admin);
        radioButton_userCode = (RadioButton) findViewById(R.id.radioButton_byUserCode_seller_menu_admin);
        button_nextPage_seller_admin = (Button) findViewById(R.id.button_nextPage_seller_admin);
        button_prefPage_seller_admin = (Button) findViewById(R.id.button_prefPage_seller_admin);

        radioButton_userCode.setChecked(true);

        radioButton_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBy = 1;
                orderByStatus();
            }
        });

        radioButton_userCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBy = 2;
                orderByUsercode();
            }
        });

        recyclerView_seller = findViewById(R.id.recyclerView_seller_menu_admin);
        recyclerListSeller();

        imageButton_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search = editText_search.getText().toString();
                serachSeller(search);

            }
        });

        button_nextPage_seller_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages++;
//                halaman belum akhir
                if(pages < totalPage){
                    int start = (pages-1)*4;
                    int end = start + 3;
                    listFourSeller.clear();
                    for (int i=start; i <= end; i++){
                        listFourSeller.add(listSeller.get(i));
                    }
                    textView_page_now.setText("" + pages);
                    showRecyclerList(listFourSeller, openBy);
                }
//                halaman akhir
                else if(pages == totalPage){
                    int start = (pages-1) * 4;
                    int end = listSeller.size();
                    listFourSeller.clear();

                    for (int i=start; i<end; i++){
                        listFourSeller.add(listSeller.get(i));
                    }
                    textView_page_now.setText("" + pages);
                    showRecyclerList(listFourSeller, openBy);
                }
//                tidak ada halaman
                else{
                    pages--;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Terakhir",Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_prefPage_seller_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages--;
                int start = (pages-1)*4;
                int end = start + 3;
                listFourSeller.clear();
//                belum halaman awal
                if(pages >= 1){
                    for (int i=start; i <= end; i++){
                        listFourSeller.add(listSeller.get(i));
                    }
                    textView_page_now.setText("" + pages);
                    showRecyclerList(listFourSeller, openBy);
                }
//                sudah halaman awal
                else{
                    pages++;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Pertama",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void recyclerListSeller(){
        databaseReferenceSeller = db.getReference(Seller.class.getSimpleName());
        databaseReferenceSeller.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listSeller.clear();
                for(DataSnapshot item : snapshot.getChildren()){
                    Seller seller = item.getValue(Seller.class);
                    listSeller.add(seller);
                }

                if (listSeller.size()<=4){
                    totalPage = 1;
                    pages = 1;
                    listFourSeller = listSeller;
                    showRecyclerList(listFourSeller, openBy);
                }
                else{
                    int mod = listSeller.size()%4;

                    if (mod==0){
                        totalPage = listSeller.size()/4;
                    }
                    else {
                        totalPage = (listSeller.size()/4)+1;
                    }
                    pages = 1;
                    for (int i=0; i<4; i++){
                        listFourSeller.add(listSeller.get(i));
                    }
                    showRecyclerList(listFourSeller, openBy);
                }
                textView_page_now.setText("" + pages);
                textView_total_page.setText("" + totalPage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void showRecyclerList(ArrayList<Seller> listSeller, String openBy){
        recyclerView_seller.setLayoutManager(new LinearLayoutManager(this));
        ListSellerAdapter listSellerAdapter = new ListSellerAdapter(listSeller,openBy);
        recyclerView_seller.setAdapter(listSellerAdapter);
    }

    public void serachSeller(String search){
        if (search.length()==0){
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
        else {
            databaseReferenceSeller = db.getReference(Seller.class.getSimpleName());
            databaseReferenceSeller.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listSeller.clear();
                    for (DataSnapshot item : snapshot.getChildren()){
                        Seller seller = item.getValue(Seller.class);
                        if (seller.getName().equalsIgnoreCase(search) || seller.getUserName().equals(search)){
                            listSeller.add(seller);
                        }
                    }
                    if (listSeller.size()<=4){
                        totalPage = 1;
                        pages = 1;
                        listFourSeller = listSeller;
                        showRecyclerList(listFourSeller, openBy);
                    }
                    else{
                        int mod = listSeller.size()%4;

                        if (mod==0){
                            totalPage = listSeller.size()/4;
                        }
                        else {
                            totalPage = (listSeller.size()/4)+1;
                        }
                        pages = 1;
                        for (int i=0; i<4; i++){
                            listFourSeller.add(listSeller.get(i));
                        }
                        showRecyclerList(listFourSeller, openBy);
                    }
                    textView_page_now.setText("" + pages);
                    textView_total_page.setText("" + totalPage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void orderByStatus(){
        databaseReferenceSeller = db.getReference(Seller.class.getSimpleName());
        databaseReferenceSeller.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Seller> temp = new ArrayList<Seller>();
                for (DataSnapshot item : snapshot.getChildren()){
                    Seller seller = item.getValue(Seller.class);
                    temp.add(seller);
                }
                Collections.sort(temp, new SortByStatus());
                showRecyclerList(temp, openBy);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void orderByUsercode(){
        databaseReferenceSeller = db.getReference(Seller.class.getSimpleName());
        databaseReferenceSeller.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Seller> temp = new ArrayList<Seller>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Seller seller = item.getValue(Seller.class);
                    temp.add(seller);
                }

                Collections.sort(temp, new SortByUserNameSeller());
                showRecyclerList(temp, openBy);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
