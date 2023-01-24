package id.ac.ukdw.lansmart.seller.reportseller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.ZoomImageActivity;
import id.ac.ukdw.lansmart.admin.HomeAdminActivity;
import id.ac.ukdw.lansmart.buyer.ListProductBuyerAdapter;
import id.ac.ukdw.lansmart.buyer.cart.ListCartBuyerAdapter;
import id.ac.ukdw.lansmart.buyer.checkout.ListCheckoutBuyerAdapter;
import id.ac.ukdw.lansmart.constructor.Buyer;
import id.ac.ukdw.lansmart.constructor.Product;
import id.ac.ukdw.lansmart.constructor.TransactionDetail;
import id.ac.ukdw.lansmart.seller.homeseller.HomeSellerActivity;
import id.ac.ukdw.lansmart.seller.homeseller.ListCheapProductHomeSellerAdapter;
import id.ac.ukdw.lansmart.seller.homeseller.ListExpensiveProductHomeSellerAdapter;

public class DetailTransactionActivity extends AppCompatActivity {
    public static final String EXTRA_PRODUCTCODE = "id.ac.udkw.rockpaperscissors.EXTRA_PRODUCTCODE";
    private TextView textView_detailId, textView_buyerName, textView_productName, textView_itemPerProduct, textView_productPrice, textView_totalAmont, textView_address, textView_status;
    private ImageButton imageButton_image;
    private Button button_back, button_approve;

    private String detailId, productId, buyerId, buyerName, url, status = "";

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    private DatabaseReference databaseReferenceProduct, databaseReferenceDetailTransaction, databaseReferenceBuyer;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);

        textView_detailId = (TextView) findViewById(R.id.textView_detailid_detail_transaction);
        textView_buyerName = (TextView) findViewById(R.id.textView_buyer_name_detail_transaction);
        textView_productName = (TextView) findViewById(R.id.textView_product_name_detail_transaction);
        textView_itemPerProduct = (TextView) findViewById(R.id.textView_item_perproduct_detail_transaction);
        textView_productPrice = (TextView) findViewById(R.id.textView_price_detail_transaction);
        textView_totalAmont = (TextView) findViewById(R.id.textView_total_detail_transaction);
        textView_address = (TextView) findViewById(R.id.textView_address_detail_transaction);
        textView_status = (TextView) findViewById(R.id.textView_status_detail_transaction);
        imageButton_image = (ImageButton) findViewById(R.id.imageButton_image_detail_transaction);
        button_back = (Button) findViewById(R.id.button_back_from_detail_transaction);
        button_approve = (Button) findViewById(R.id.button_approve_detail_transaction);

        Intent intent = getIntent();
        detailId = intent.getStringExtra(ListReportSellerAdapter.EXTRA_DETAILCODE);
        productId = intent.getStringExtra(ListReportSellerAdapter.EXTRA_PRODUCTCODE);

        databaseReferenceDetailTransaction = db.getReference(TransactionDetail.class.getSimpleName());
        databaseReferenceDetailTransaction.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    TransactionDetail transactionDetail = item.getValue(TransactionDetail.class);
                    if (transactionDetail.getDetailId().equals(detailId) && transactionDetail.getProductId().equals(productId)){
                        status = transactionDetail.getStatus();
                        buyerId = transactionDetail.getBuyerId();

                        textView_detailId.setText(transactionDetail.getDetailId());
                        textView_productName.setText(transactionDetail.getProductName());
                        textView_itemPerProduct.setText("" + transactionDetail.getItemPerProduct());
                        textView_productPrice.setText(formatRupiah.format(transactionDetail.getPricePerProduct()) + "");
                        textView_totalAmont.setText(formatRupiah.format(transactionDetail.getPriceTotalPerProduct()) + "");
                        textView_address.setText(transactionDetail.getAddress());
                        textView_status.setText(transactionDetail.getStatus());

                        if (status.equalsIgnoreCase("belum diterima")){
                            button_approve.setVisibility(View.VISIBLE);
                            button_approve.setClickable(true);
                            button_approve.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    approveReport(detailId, productId, v);
                                }
                            });
                        }
                        else{
                            button_approve.setVisibility(View.INVISIBLE);
                            button_approve.setClickable(false);
                        }

                        databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
                        databaseReferenceBuyer.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot item : snapshot.getChildren()){
                                    Buyer buyer = item.getValue(Buyer.class);
                                    if (buyer.getUserName().equals(buyerId)){
                                        buyerName = buyer.getName();
                                        textView_buyerName.setText(buyerName);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceProduct = db.getReference(Product.class.getSimpleName());
        databaseReferenceProduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Product product = item.getValue(Product.class);
                    if (product.getProductId().equals(productId)){
                        url = product.getUrl();
                    }
                }
                Glide.with(getApplicationContext()).load(url).into(imageButton_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageButton_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentZoom = new Intent(DetailTransactionActivity.this, ZoomImageActivity.class);
                intentZoom.putExtra(EXTRA_PRODUCTCODE, productId);
                startActivity(intentZoom);
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }

    public void approveReport(String detailId, String productId, View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        alert.setMessage("Apakah Anda Sudah Menerima?");
        alert.setCancelable(true);
        alert.setNegativeButton("Sudah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReferenceProductDetail;
                FirebaseDatabase db = FirebaseDatabase.getInstance();

                databaseReferenceProductDetail = db.getReference(TransactionDetail.class.getSimpleName());
                databaseReferenceProductDetail.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()){
                            TransactionDetail transactionDetail = item.getValue(TransactionDetail.class);
                            if (transactionDetail.getDetailId().equals(detailId) && transactionDetail.getProductId().equals(productId)){
                                transactionDetail.setStatus("Sudah Diterima");
                                databaseReferenceProductDetail.child(item.getKey()).setValue(transactionDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(v.getContext(),transactionDetail.getDetailId() + "Status Sudah Diterima",Toast.LENGTH_LONG);
                                        onBackPressed();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(),"Gagal Mengubah Status!",Toast.LENGTH_LONG);
                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
}
