package id.ac.ukdw.lansmart.seller.reportseller;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.constructor.TransactionDetail;

public class ListReportSellerAdapter extends RecyclerView.Adapter<ListReportSellerAdapter.ListViewHolder> {
    public static final String EXTRA_DETAILCODE = "id.ac.udkw.rockpaperscissors.EXTRA_DETAILCODE";
    public static final String EXTRA_PRODUCTCODE = "id.ac.udkw.rockpaperscissors.EXTRA_PRODUCTCODE";

    private String sellerCode, detailCode, productCode;
    private ArrayList<TransactionDetail> listReport;

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    public ListReportSellerAdapter(){

    }

    public ListReportSellerAdapter(ArrayList<TransactionDetail> list, String sellerCode) {
        this.listReport = list;
        this.sellerCode = sellerCode;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_report_menu_seller, parent, false);
        return new ListViewHolder(view);
    }

    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        TransactionDetail transactionDetail = listReport.get(position);
        holder.textView_productName.setText(transactionDetail.getProductName());
        holder.textView_buyPerItem.setText("Membeli: " + transactionDetail.getItemPerProduct());
        holder.textView_totalPrice.setText("Total: " + formatRupiah.format(transactionDetail.getItemPerProduct() * transactionDetail.getPricePerProduct()));
        holder.textView_status.setText("Status: " + transactionDetail.getStatus());
        holder.textView_address.setText("Alamat: " + transactionDetail.getAddress());

        if(transactionDetail.getStatus().toLowerCase().equalsIgnoreCase("belum diterima")){
            holder.linearLayout_acc_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    approveReport(transactionDetail.getDetailId(), transactionDetail.getProductId(), v);
                }
            });
        }
        else {
            holder.linearLayout_acc_report.setVisibility(View.INVISIBLE);
//            holder.imageButton_approve.setVisibility(View.INVISIBLE);
            holder.imageButton_approve.setClickable(false);
        }

        holder.linearLayout_detail_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDetail = new Intent(v.getContext(), DetailTransactionActivity.class);
                intentDetail.putExtra(EXTRA_DETAILCODE, transactionDetail.getDetailId());
                intentDetail.putExtra(EXTRA_PRODUCTCODE, transactionDetail.getProductId());
                v.getContext().startActivity(intentDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listReport.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView textView_productName, textView_buyPerItem, textView_totalPrice, textView_address, textView_status;
        ImageButton imageButton_approve, imageButton_detail;
        LinearLayout linearLayout_acc_report, linearLayout_detail_report;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_productName = itemView.findViewById(R.id.textView_productcode_report_menu_seller);
            textView_buyPerItem = itemView.findViewById(R.id.textView_buyperitem_report_menu_seller);
            textView_totalPrice = itemView.findViewById(R.id.textView_totalprice_report_seller);
            textView_address = itemView.findViewById(R.id.textView_addressbuyer_report_seller);
            textView_status = itemView.findViewById(R.id.textView_status_report_seller);
            imageButton_approve = itemView.findViewById(R.id.button_approve_report_seller);
            imageButton_detail = itemView.findViewById(R.id.button_detail_report_seller);
            linearLayout_acc_report = itemView.findViewById(R.id.linear_layout_accept_report_seller);
            linearLayout_detail_report = itemView.findViewById(R.id.linear_layout_detail_report_seller);
        }
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
