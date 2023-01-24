package id.ac.ukdw.lansmart.buyer.cart;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import id.ac.ukdw.lansmart.ZoomImageActivity;
import id.ac.ukdw.lansmart.constructor.Cart;
import id.ac.ukdw.lansmart.constructor.Product;

public class ListCartBuyerAdapter extends RecyclerView.Adapter<ListCartBuyerAdapter.ListViewHolder>{
    public static final String EXTRA_BUYERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERCODE";
    public static final String EXTRA_PRODUCTCODE = "id.ac.udkw.rockpaperscissors.EXTRA_PRODUCTCODE";
    private int stockPerProduct;
    private boolean checkPlus = false;
    private ArrayList<Cart> listCart;

    private Context context;

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    private DatabaseReference databaseReferenceCart, databaseReferenceProduct;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public ListCartBuyerAdapter(ArrayList<Cart> list) {
        this.listCart = list;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_row_cart_buyer, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Cart cart = listCart.get(position);
        holder.textView_productName.setText(cart.getProductName());
        holder.textView_price.setText(formatRupiah.format(cart.getPricePerProduct()));

        databaseReferenceCart = db.getReference(Cart.class.getSimpleName());
        databaseReferenceProduct = db.getReference(Product.class.getSimpleName());
        databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Product product = item.getValue(Product.class);
                    if (product.getProductId().equals(cart.getProductId())){
                        holder.textView_available.setText("Tersedia: " + product.getAvailable());

                        try{
                            Glide.with(context).load(product.getUrl()).into(holder.imageButton_image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        holder.imageButton_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intentZoom = new Intent(v.getContext(), ZoomImageActivity.class);
                                intentZoom.putExtra(EXTRA_PRODUCTCODE, product.getProductId());
                                v.getContext().startActivity(intentZoom);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Cart temp = item.getValue(Cart.class);
                    if (temp.getBuyerId().equals(cart.getBuyerId()) && temp.getProductId().equals(cart.getProductId())){
                        holder.editText_itemPerProduct.setText(temp.getItemPerProduct() + "");
                        if (temp.getStatus().toLowerCase().equals("checked")){
                            holder.checkBox_itemPerProduct.setChecked(true);
                        }
                        else {
                            holder.checkBox_itemPerProduct.setChecked(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.checkBox_itemPerProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox_itemPerProduct.isChecked()){
                    databaseReferenceCart.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot item : snapshot.getChildren()){
                                Cart temp = item.getValue(Cart.class);
                                if (temp.getBuyerId().equals(cart.getBuyerId()) && temp.getProductId().equals(cart.getProductId())){
                                    temp.setStatus("Checked");
                                    databaseReferenceCart.child(item.getKey()).setValue(temp);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if (holder.checkBox_itemPerProduct.isChecked() == false){
                    databaseReferenceCart.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot item : snapshot.getChildren()){
                                Cart temp = item.getValue(Cart.class);
                                if (temp.getBuyerId().equals(cart.getBuyerId()) && temp.getProductId().equals(cart.getProductId())){
                                    temp.setStatus("Unchecked");
                                    temp.setCheckOut(false);
                                    databaseReferenceCart.child(item.getKey()).setValue(temp);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        holder.imageButton_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.getStatus().toLowerCase().equals("checked")){
                    cart.setStatus("Checked");
                }
                else{
                    cart.setStatus("Unchecked");
                }

                databaseReferenceProduct.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item2 : snapshot.getChildren()){
                            Product product2 = item2.getValue(Product.class);
                            if (product2.getProductId().equals(cart.getProductId())){
                                if (product2.getAvailable() >= cart.getItemPerProduct()+1){
                                    cart.setItemPerProduct(cart.getItemPerProduct()+1);
                                    databaseReferenceCart.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot item : snapshot.getChildren()){
                                                Cart temp = item.getValue(Cart.class);
                                                if (temp.getBuyerId().equals(cart.getBuyerId()) && temp.getProductId().equals(cart.getProductId())){
                                                    databaseReferenceCart.child(item.getKey()).setValue(cart);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(v.getContext(),"Pembelian Tidak Bisa Melebihi Produk Yang Tersedia",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        holder.imageButton_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart.setItemPerProduct(cart.getItemPerProduct()-1);
                if (cart.getStatus().toLowerCase().equals("checked")){
                    cart.setStatus("Checked");
                }
                else{
                    cart.setStatus("Unchecked");
                }

                databaseReferenceCart.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()){
                            Cart temp = item.getValue(Cart.class);
                            if (temp.getBuyerId().equals(cart.getBuyerId()) && temp.getProductId().equals(cart.getProductId())){
                                if (cart.getItemPerProduct()>0){
                                    databaseReferenceCart.child(item.getKey()).setValue(cart);
                                }
                                else {
                                    databaseReferenceCart.child(item.getKey()).removeValue();
                                }
                            }
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
        return listCart.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox_itemPerProduct;
        TextView textView_productName, textView_available, textView_price, editText_itemPerProduct;
        ImageButton imageButton_image, imageButton_plus, imageButton_minus;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton_image = itemView.findViewById(R.id.imageButton_image_product_cart);
            checkBox_itemPerProduct = itemView.findViewById(R.id.checkBox_itemPerProduct);
            textView_productName = itemView.findViewById(R.id.textView_product_name_buyer_cart);
            textView_price = itemView.findViewById(R.id.textView_product_price_buyer_cart);
            textView_available = itemView.findViewById(R.id.textView_availablePerProduct_cart);
            imageButton_plus = itemView.findViewById(R.id.button_plus_cart_buyer);
            imageButton_minus = itemView.findViewById(R.id.button_minus_cart_buyer);
            editText_itemPerProduct = itemView.findViewById(R.id.editText_counter_item_cart_buyer);
        }
    }

    public int getStockPerProduct() {
        return stockPerProduct;
    }

    public void setStockPerProduct(int stockPerProduct) {
        this.stockPerProduct = stockPerProduct;
    }

    public ArrayList<Cart> getListCart() {
        return listCart;
    }

    public void setListCart(ArrayList<Cart> listCart) {
        this.listCart = listCart;
    }
}
