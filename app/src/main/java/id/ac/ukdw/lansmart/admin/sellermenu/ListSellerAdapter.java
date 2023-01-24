package id.ac.ukdw.lansmart.admin.sellermenu;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.constructor.Seller;

public class ListSellerAdapter extends RecyclerView.Adapter<ListSellerAdapter.ListViewHolder> {
    public static final String EXTRA_SELLERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_SELLERCODE";
    public static final String EXTRA_ADMINCODE = "id.ac.udkw.rockpaperscissors.EXTRA_ADMINCODE";

    private String openBy, sellerKey = "";
    private ArrayList<Seller> listSeller;

    public ListSellerAdapter(ArrayList<Seller> list, String openBy) {
        this.listSeller = list;
        this.openBy = openBy;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_seller_menu_admin, parent, false);
        return new ListViewHolder(view);
    }

    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Seller seller = listSeller.get(position);
        holder.textView_sellerName.setText("Nama: " + seller.getName());
        holder.textView_status.setText("Status: " + seller.getStatus());
        holder.textView_sellerCode.setText("Kode : " + seller.getUserName());

        if(seller.getStatus().toLowerCase().equals("pasif")){
            holder.textView_approveOrDelete.setText("Tambah");
            holder.imageButton_approveOrDelete.setImageResource(R.drawable.plus_green);
            holder.linearLayout_approveOrDelete_seller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    approveSeller(seller.getUserName(), openBy, v);
                }
            });
        }
        else if(seller.getStatus().toLowerCase().equals("aktif")){
            holder.textView_approveOrDelete.setText("Hapus");
            holder.imageButton_approveOrDelete.setImageResource(R.drawable.delete);
            holder.linearLayout_approveOrDelete_seller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteSeller(seller.getUserName(), openBy, v);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listSeller.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView textView_sellerName, textView_sellerCode, textView_status, textView_approveOrDelete;
        ImageButton imageButton_approveOrDelete;
        LinearLayout linearLayout_approveOrDelete_seller;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_sellerName = itemView.findViewById(R.id.sellerName_seller_admin);
            textView_sellerCode = itemView.findViewById(R.id.seller_usercode_admin);
            textView_status = itemView.findViewById(R.id.seller_status_admin);
            imageButton_approveOrDelete = itemView.findViewById(R.id.image_button_approveordelete_seller_admin);
            textView_approveOrDelete = itemView.findViewById(R.id.textView_approveordelete_seller_admin);
            linearLayout_approveOrDelete_seller = itemView.findViewById(R.id.linear_layout_approveordelete_seller_admin);
        }
    }

    public void deleteSeller(String usernameSeller, String openBy, View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        alert.setMessage("Apakah Anda Ingin Menghapus Penjual Ini?");
        alert.setCancelable(true);
        alert.setNegativeButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReferenceSeller;
                FirebaseDatabase db = FirebaseDatabase.getInstance();


                databaseReferenceSeller = db.getReference(Seller.class.getSimpleName());
                databaseReferenceSeller.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            Seller seller = item.getValue(Seller.class);
                            if (seller.getUserName().equals(usernameSeller)){
                                sellerKey = item.getKey();
                                seller.setStatus("Pasif");
                                seller.setEditBy(openBy);
                                Toast.makeText(v.getContext(),"Penjual Tidak Aktif!",Toast.LENGTH_SHORT).show();
                                databaseReferenceSeller.child(sellerKey).setValue(seller);
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

    public void approveSeller(String usernameSeller, String openBy, View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        alert.setMessage("Apakah Anda Ingin Mengaktifkan Penjual Ini?");
        alert.setCancelable(true);
        alert.setNegativeButton("Iya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReferenceSeller;
                FirebaseDatabase db = FirebaseDatabase.getInstance();

                databaseReferenceSeller = db.getReference(Seller.class.getSimpleName());
                databaseReferenceSeller.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            Seller seller = item.getValue(Seller.class);
                            if (seller.getUserName().equals(usernameSeller)){
                                sellerKey = item.getKey();
                                seller.setStatus("Aktif");
                                seller.setEditBy(openBy);
                                Toast.makeText(v.getContext(),"Penjual sekarang aktif.",Toast.LENGTH_SHORT).show();
                                databaseReferenceSeller.child(sellerKey).setValue(seller);
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

    public ArrayList<Seller> getListSeller() {
        return listSeller;
    }

    public void setListSeller(ArrayList<Seller> listSeller) {
        this.listSeller = listSeller;
    }
}
