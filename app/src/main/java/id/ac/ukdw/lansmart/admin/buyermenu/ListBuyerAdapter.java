package id.ac.ukdw.lansmart.admin.buyermenu;

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
import id.ac.ukdw.lansmart.constructor.Buyer;

public class ListBuyerAdapter extends RecyclerView.Adapter<ListBuyerAdapter.ListViewHolder> {
    public static final String EXTRA_BUYERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERCODE";
    public static final String EXTRA_ADMINCODE = "id.ac.udkw.rockpaperscissors.EXTRA_ADMINCODE";

    private String openBy, buyerKey;
    private ArrayList<Buyer> listBuyer;

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    public ListBuyerAdapter(ArrayList<Buyer> listBuyer, String openBy) {
        this.listBuyer = listBuyer;
        this.openBy = openBy;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_buyer_menu_admin, parent, false);
        return new ListViewHolder(view);
    }

    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Buyer buyer = listBuyer.get(position);
        holder.textView_buyerId.setText("Kode: " + buyer.getUserName());
        holder.textView_buyerName.setText("Nama: " + buyer.getName());
        holder.textView_status.setText("Status: " + buyer.getStatus());

        if (buyer.getStatus().toLowerCase().equals("aktif")){
            holder.textView_approveOrDelete.setText("Hapus");
            holder.imageButton_delete.setImageResource(R.drawable.delete);
            holder.linearLayout_approveOrDelete_buyer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteBuyer(buyer.getUserName(), openBy, v);
                }
            });
        }
        else if (buyer.getStatus().toLowerCase().equals("pasif")){
            holder.textView_approveOrDelete.setText("Tambah");
            holder.imageButton_delete.setImageResource(R.drawable.plus_green);
            holder.linearLayout_approveOrDelete_buyer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    undeleteBuyer(buyer.getUserName(), openBy, v);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return listBuyer.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView textView_buyerId, textView_buyerName, textView_status, textView_approveOrDelete;
        ImageButton imageButton_delete;
        LinearLayout linearLayout_approveOrDelete_buyer;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_buyerId = itemView.findViewById(R.id.textView_buyerId_buyer_admin);
            textView_status = itemView.findViewById(R.id.textView_buyerStatus_buyer_admin);
            textView_buyerName = itemView.findViewById(R.id.textView_name_buyer);
            imageButton_delete = itemView.findViewById(R.id.image_button_delete_buyer_admin);
            textView_approveOrDelete = itemView.findViewById(R.id.textView_approveordelete_buyer_admin);
            linearLayout_approveOrDelete_buyer = itemView.findViewById(R.id.linear_layout_approveordelete_buyer_admin);
        }
    }

    public void deleteBuyer(String userName, String openBy, View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        alert.setMessage("Apakah Anda Ingin Menghapus Pembeli Ini?");
        alert.setCancelable(true);
        alert.setNegativeButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReferenceBuyer;
                FirebaseDatabase db = FirebaseDatabase.getInstance();

                databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
                databaseReferenceBuyer.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            Buyer buyer = item.getValue(Buyer.class);
                            if (buyer.getUserName().equals(userName)){
                                buyerKey = item.getKey();
                                buyer.setStatus("Pasif");
                                buyer.setEditBy(openBy);
                                databaseReferenceBuyer.child(buyerKey).setValue(buyer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(v.getContext(),"Pembeli Sudah Tidak Aktif ",Toast.LENGTH_SHORT).show();
                                    }
                                });;
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

    public void undeleteBuyer(String userName, String openBy, View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        alert.setMessage("Apakah Anda Ingin Mengaktifkan Pembeli Ini?");
        alert.setCancelable(true);
        alert.setNegativeButton("Aktifkan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReferenceBuyer;
                FirebaseDatabase db = FirebaseDatabase.getInstance();

                databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
                databaseReferenceBuyer.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            Buyer buyer = item.getValue(Buyer.class);
                            if (buyer.getUserName().equals(userName)){
                                buyerKey = item.getKey();
                                buyer.setStatus("Aktif");
                                buyer.setEditBy(openBy);
                                databaseReferenceBuyer.child(buyerKey).setValue(buyer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(v.getContext(),"Pembeli Sudah Aktif",Toast.LENGTH_SHORT).show();
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

    public String getOpenBy() {
        return openBy;
    }

    public void setOpenBy(String openBy) {
        this.openBy = openBy;
    }

    public ArrayList<Buyer> getListBuyer() {
        return listBuyer;
    }

    public void setListBuyer(ArrayList<Buyer> listBuyer) {
        this.listBuyer = listBuyer;
    }
}
