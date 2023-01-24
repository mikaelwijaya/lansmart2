package id.ac.ukdw.lansmart.seller.reportseller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import id.ac.ukdw.lansmart.admin.sellermenu.SortByStatus;
import id.ac.ukdw.lansmart.seller.homeseller.HomeSellerActivity;
import id.ac.ukdw.lansmart.constructor.TransactionDetail;

public class ReportMenuSellerActivity extends AppCompatActivity {
    public static final String EXTRA_SELLERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_SELLERCODE";
    private Button button_nextPage_Buyer, button_prefPage_Buyer;
    private TextView textView_page_now, textView_total_page;
    private RecyclerView recyclerView_listReport;

    private String sellerCode;
    private int pages = 0, totalPage = 0;

    private ArrayList<TransactionDetail> listReport = new ArrayList<TransactionDetail>();
    private ArrayList<TransactionDetail> listThreeReport = new ArrayList<TransactionDetail>();

    private DatabaseReference databaseReferenceTransactionDetail,databaseReferenceSeller,databaseReferenceProduct;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_menu_seller);
        button_nextPage_Buyer = (Button) findViewById(R.id.button_nextPage_report_seller);
        button_prefPage_Buyer = (Button) findViewById(R.id.button_prefPage_report_seller);
        textView_page_now = (TextView) findViewById(R.id.textView_page_now_report_seller);
        textView_total_page = (TextView) findViewById(R.id.textView_total_page_report_seller);
        recyclerView_listReport = (RecyclerView) findViewById(R.id.recyclerView_report_menu_seller);

        Intent intent = getIntent();
        sellerCode = intent.getStringExtra(HomeSellerActivity.EXTRA_SELLERCODE);

        recyclerViewListReport(listReport, sellerCode);
    }

    private void recyclerViewListReport(ArrayList<TransactionDetail> listReport, String sellerCode) {
        databaseReferenceTransactionDetail = db.getReference(TransactionDetail.class.getSimpleName());
        databaseReferenceTransactionDetail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listReport.clear();
                listThreeReport.clear();
                for(DataSnapshot item : snapshot.getChildren()){
                    TransactionDetail transactionDetail = item.getValue(TransactionDetail.class);
                    if (transactionDetail.getSellerId().equals(sellerCode)){
                        listReport.add(transactionDetail);
                    }
                }
                Collections.sort(listReport, new SortByStatusReport());
                if (listReport.size()<=3){
                    totalPage = 1;
                    pages = 1;
                    listThreeReport = listReport;
                    showRecyclerList(listThreeReport, sellerCode);
                }
                else{
                    int mod = listReport.size()%3;

                    if (mod==0){
                        totalPage = listReport.size()/3;
                    }
                    else {
                        totalPage = (listReport.size()/3)+1;
                    }
                    pages = 1;
                    for (int i=0; i<3; i++){
                        listThreeReport.add(listReport.get(i));
                    }
                    showRecyclerList(listThreeReport, sellerCode);
                }
                textView_page_now.setText("" + pages);
                textView_total_page.setText("" + totalPage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button_nextPage_Buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages++;
//                halaman belum akhir
                if(pages < totalPage){
                    int start = (pages-1)*3;
                    int end = start + 2;
                    listThreeReport.clear();
                    for (int i=start; i <= end; i++){
                        listThreeReport.add(listReport.get(i));
                    }
                    showRecyclerList(listThreeReport, sellerCode);
                }
//                halaman akhir
                else if(pages == totalPage){
                    int start = (pages-1) * 3;
                    int end = listReport.size();
                    listThreeReport.clear();
                    for (int i=start; i<end; i++){
                        listThreeReport.add(listReport.get(i));
                    }
                    showRecyclerList(listThreeReport, sellerCode);
                }
//                tidak ada halaman
                else{
                    pages--;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Terakhir",Toast.LENGTH_SHORT).show();
                }
                textView_page_now.setText("" + pages);
            }
        });

        button_prefPage_Buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages--;
                int start = (pages-1)*3;
                int end = start + 2;
                listThreeReport.clear();
//                belum halaman awal
                if(pages >= 1){
                    for (int i=start; i <= end; i++){
                        listThreeReport.add(listReport.get(i));
                    }
                    showRecyclerList(listThreeReport, sellerCode);
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

    private void showRecyclerList(ArrayList<TransactionDetail> listReport, String sellerCode) {
        recyclerView_listReport.setLayoutManager(new LinearLayoutManager(this));
        ListReportSellerAdapter listReportSellerAdapter = new ListReportSellerAdapter(listReport, sellerCode);
        recyclerView_listReport.setAdapter(listReportSellerAdapter);
    }
}
