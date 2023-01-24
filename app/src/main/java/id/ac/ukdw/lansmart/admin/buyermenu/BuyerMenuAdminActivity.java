package id.ac.ukdw.lansmart.admin.buyermenu;

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
import id.ac.ukdw.lansmart.constructor.Buyer;

public class BuyerMenuAdminActivity extends AppCompatActivity {
    private EditText editText_search;
    private ImageButton imageButton_search;
    private TextView textView_page_now, textView_total_page;
    private RadioButton radioButton_userName, radioButton_address;
    private RecyclerView recyclerView_buyer;
    private Button button_nextPage_buyer_admin, button_prefPage_buyer_admin;

    private String search, openBy;
    private int orderBy = 0, pages = 0, totalPage = 0;

    private Bundle savedInstanceState;

    private ArrayList<Buyer> listBuyer = new ArrayList<Buyer>();
    private ArrayList<Buyer> listFourBuyer = new ArrayList<Buyer>();

    private DatabaseReference databaseReferenceBuyer;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_menu_admin);

        this.savedInstanceState = savedInstanceState;

        Intent intent = getIntent();
        openBy = intent.getStringExtra(HomeAdminActivity.EXTRA_ADMINCODE);

        editText_search = (EditText) findViewById(R.id.editText_search_buyer_menu_admin);
        imageButton_search = (ImageButton) findViewById(R.id.imageButton_search_buyer_menu_admin);
        textView_page_now = (TextView) findViewById(R.id.textView_page_now_buyer_admin);
        textView_total_page = (TextView) findViewById(R.id.textView_total_page_buyer_admin);
        radioButton_userName = (RadioButton) findViewById(R.id.radioButton_byName_buyer_menu_admin);
        radioButton_address = (RadioButton) findViewById(R.id.radioButton_byStatus_buyer_menu_admin);
        button_nextPage_buyer_admin = (Button) findViewById(R.id.button_nextPage_buyer_admin);
        button_prefPage_buyer_admin = (Button) findViewById(R.id.button_prefPage_buyer_admin);
        recyclerView_buyer = findViewById(R.id.recyclerView_buyer_menu_admin);

        radioButton_userName.setChecked(true);

        radioButton_userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBy = 1;
                orderByUserName();
            }
        });

        radioButton_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBy = 2;
                orderByAddress();
            }
        });

        listBuyer.clear();
        recyclerListBuyer();

        imageButton_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search = editText_search.getText().toString();
                serachBuyer(search);

            }
        });

        button_nextPage_buyer_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages++;
//                halaman belum akhir
                if(pages < totalPage){
                    int start = (pages-1)*4;
                    int end = start + 3;
                    listFourBuyer.clear();
                    for (int i=start; i <= end; i++){
                        listFourBuyer.add(listBuyer.get(i));
                    }
                    showRecyclerView(listFourBuyer, openBy);
                }
//                halaman akhir
                else if(pages == totalPage){
                    int start = (pages-1) * 4;
                    int end = listBuyer.size();
                    listFourBuyer.clear();
                    for (int i=start; i < end; i++){
                        listFourBuyer.add(listBuyer.get(i));
                    }
                    showRecyclerView(listFourBuyer, openBy);
                }
//                tidak ada halaman
                else{
                    pages--;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Terakhir",Toast.LENGTH_SHORT).show();
                }
                textView_page_now.setText("" + pages);
            }
        });

        button_prefPage_buyer_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages--;
                int start = (pages-1)*4;
                int end = start + 3;
                listFourBuyer.clear();
//                belum halaman awal
                if(pages >= 1){
                    for (int i=start; i <= end; i++){
                        listFourBuyer.add(listBuyer.get(i));
                    }
                    showRecyclerView(listFourBuyer, openBy);
                }
//                sudah halaman awal
                else{
                    pages++;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Pertama",Toast.LENGTH_SHORT).show();
                }
                textView_page_now.setText("" + pages);
            }
        });
    }

    public void serachBuyer(String search){
        listBuyer.clear();
        listFourBuyer.clear();
        if (search.length()==0){
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
        else {
            databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
            databaseReferenceBuyer.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listBuyer.clear();
                    listFourBuyer.clear();
                    for (DataSnapshot item : snapshot.getChildren()){
                        Buyer buyer = item.getValue(Buyer.class);
                        if (buyer.getName().equalsIgnoreCase(search) || buyer.getUserName().equals(search) || buyer.getStatus().equalsIgnoreCase(search)
                                || buyer.getCodePos().equalsIgnoreCase(search)){
                            listBuyer.add(buyer);
                        }
                    }
                    if (listBuyer.size()<=4){
                        totalPage = 1;
                        pages = 1;
                        listFourBuyer = listBuyer;
                        showRecyclerView(listFourBuyer, openBy);
                    }
                    else{
                        int mod = listBuyer.size()%4;

                        if (mod==0){
                            totalPage = listBuyer.size()/4;
                        }
                        else {
                            totalPage = (listBuyer.size()/4)+1;
                        }
                        pages = 1;
                        for (int i=0; i<4; i++){
                            listFourBuyer.add(listBuyer.get(i));
                        }
                        showRecyclerView(listFourBuyer, openBy);
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

    public void recyclerListBuyer(){
        listBuyer.clear();
        listFourBuyer.clear();
        databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
        databaseReferenceBuyer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBuyer.clear();
                listFourBuyer.clear();
                for(DataSnapshot item : snapshot.getChildren()){
                    Buyer buyer = item.getValue(Buyer.class);
                    listBuyer.add(buyer);
                }

                if (listBuyer.size()<=4){
                    totalPage = 1;
                    pages = 1;
                    listFourBuyer = listBuyer;
                    showRecyclerView(listFourBuyer, openBy);
                }
                else{
                    int mod = listBuyer.size()%4;

                    if (mod==0){
                        totalPage = listBuyer.size()/4;
                    }
                    else {
                        totalPage = (listBuyer.size()/4)+1;
                    }
                    pages = 1;
                    for (int i=0; i<4; i++){
                        listFourBuyer.add(listBuyer.get(i));
                    }
                    showRecyclerView(listFourBuyer, openBy);
                }
                textView_page_now.setText("" + pages);
                textView_total_page.setText("" + totalPage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showRecyclerView(ArrayList<Buyer> listBuyer, String openBy){
        recyclerView_buyer.setLayoutManager(new LinearLayoutManager(this));
        ListBuyerAdapter listBuyerAdapter = new ListBuyerAdapter(listBuyer,openBy);
        recyclerView_buyer.setAdapter(listBuyerAdapter);
    }

    public void orderByUserName(){
        databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
        databaseReferenceBuyer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Buyer> temp = new ArrayList<Buyer>();
                for (DataSnapshot item : snapshot.getChildren()){
                    Buyer buyer = item.getValue(Buyer.class);
                    temp.add(buyer);
                }

                Collections.sort(temp, new SortByUserNameBuyer());
                showRecyclerView(temp, openBy);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void orderByAddress(){
        databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
        databaseReferenceBuyer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Buyer> temp = new ArrayList<Buyer>();
                for (DataSnapshot item : snapshot.getChildren()){
                    Buyer buyer = item.getValue(Buyer.class);
                    temp.add(buyer);
                }

                Collections.sort(temp, new SortByStatus());
                showRecyclerView(temp, openBy);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}