package id.ac.ukdw.lansmart.buyer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import id.ac.ukdw.lansmart.ZoomImageActivity;
import id.ac.ukdw.lansmart.admin.HomeAdminActivity;
import id.ac.ukdw.lansmart.constructor.Cart;
import id.ac.ukdw.lansmart.constructor.Product;

public class ListProductBuyerAdapter extends RecyclerView.Adapter<id.ac.ukdw.lansmart.buyer.ListProductBuyerAdapter.ListViewHolder>{
    public static final String EXTRA_BUYERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERCODE";
    public static final String EXTRA_PRODUCTCODE = "id.ac.udkw.rockpaperscissors.EXTRA_PRODUCTCODE";
    private String openBy, buyerCode, productCode;
    private ArrayList<Product> listProduct;

    private Context context;

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    private DatabaseReference databaseReferenceCart, databaseReferenceBuyer;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public ListProductBuyerAdapter(ArrayList<Product> listProduct, String buyerCode) {
        this.listProduct = listProduct;
        this.buyerCode = buyerCode;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_row_home_buyer, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Product product = listProduct.get(position);
        holder.textView_productName.setText(product.getNameProduct());
        holder.textView_available.setText("Stok: " + product.getAvailable());
        holder.textView_price.setText(formatRupiah.format(product.getPrice()) + "");

        try{
            Glide.with(context).load(product.getUrl()).into(holder.imageButton_zoom);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.imageButton_zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productCode = listProduct.get(position).getProductId();
                Intent intentZoom = new Intent(v.getContext(), ZoomImageActivity.class);
                intentZoom.putExtra(EXTRA_PRODUCTCODE, productCode);
                v.getContext().startActivity(intentZoom);
            }
        });

        holder.button_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart cart = new Cart(buyerCode, product.getProductId(), product.getNameProduct(), 1, product.getPrice());

                databaseReferenceCart = db.getReference(Cart.class.getSimpleName());
                databaseReferenceCart.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean check = true;
                        for (DataSnapshot item : snapshot.getChildren()){
                            Cart temp = item.getValue(Cart.class);
                            if (temp.getProductId().equals(cart.getProductId()) && temp.getBuyerId().equals(cart.getBuyerId())){
                                check = false;
                                break;
                            }
                            else{
                                check = true;
                            }
                        }

                        if (check == true){
                            databaseReferenceCart.push().setValue(cart).addOnSuccessListener(suc ->{
                                Toast.makeText(v.getContext(),"Produk Dimasukkan Ke Keranjang", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(er->{
                                Toast.makeText(v.getContext(), ""+er.getMessage(),Toast.LENGTH_SHORT).show();
                            });
                        }
                        else {
                            Toast.makeText(v.getContext(),"Produk Sudah Ada Di Keranjang", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public String getBuyerCode() {
        return buyerCode;
    }

    public void setBuyerCode(String buyerCode) {
        this.buyerCode = buyerCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView textView_productName, textView_available, textView_price;
        Button button_buy;
        ImageButton imageButton_zoom;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_productName = itemView.findViewById(R.id.textView_product_name_buyer);
            textView_available = itemView.findViewById(R.id.textView_product_available_buyer);
            textView_price = itemView.findViewById(R.id.textView_product_price_buyer);
            button_buy = itemView.findViewById(R.id.button_buy_buyer);
            imageButton_zoom = itemView.findViewById(R.id.imageButton_product_buyer);
        }
    }

    public String getOpenBy() {
        return openBy;
    }

    public void setOpenBy(String openBy) {
        this.openBy = openBy;
    }

    public ArrayList<Product> getListProduct() {
        return listProduct;
    }

    public void setListProduct(ArrayList<Product> listProduct) {
        this.listProduct = listProduct;
    }
}
