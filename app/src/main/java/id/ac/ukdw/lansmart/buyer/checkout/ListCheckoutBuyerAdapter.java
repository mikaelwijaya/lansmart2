package id.ac.ukdw.lansmart.buyer.checkout;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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

import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.ZoomImageActivity;
import id.ac.ukdw.lansmart.constructor.Cart;
import id.ac.ukdw.lansmart.constructor.Product;

public class ListCheckoutBuyerAdapter extends RecyclerView.Adapter<ListCheckoutBuyerAdapter.ListViewHolder>{
    public static final String EXTRA_BUYERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERCODE";
    public static final String EXTRA_PRODUCTCODE = "id.ac.udkw.rockpaperscissors.EXTRA_PRODUCTCODE";
    private int itemPerProduct = 0;
    private long pricePerProduct = 0, totalPricePerProduct;
    private ArrayList<Cart> listBuy;

    private Context context;

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    private DatabaseReference databaseReferenceCart, databaseReferenceProduct;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public ListCheckoutBuyerAdapter(ArrayList<Cart> list) {
        this.listBuy = list;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_row_checkout_buyer, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Cart cart = listBuy.get(position);
        holder.textView_productName.setText(cart.getProductName());
        holder.textView_pricePerProduct.setText(formatRupiah.format(cart.getPricePerProduct()));
        holder.textView_totalItemBuy.setText("Jumlah: " + cart.getItemPerProduct());
        holder.textView_totalPricePerProduct.setText("Total: " + formatRupiah.format(cart.getPricePerProduct() * cart.getItemPerProduct()));

        holder.imageButton_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productCode = cart.getProductId();
                Intent intentZoom = new Intent(v.getContext(), ZoomImageActivity.class);
                intentZoom.putExtra(EXTRA_PRODUCTCODE, productCode);
                v.getContext().startActivity(intentZoom);
            }
        });

        databaseReferenceProduct = db.getReference(Product.class.getSimpleName());
        databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Product product = item.getValue(Product.class);
                    if (product.getProductId().equals(cart.getProductId())){
                        try{
                            Glide.with(context).load(product.getUrl()).into(holder.imageButton_image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listBuy.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton_image;
        TextView textView_productName, textView_pricePerProduct, textView_totalItemBuy, textView_totalPricePerProduct;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton_image = itemView.findViewById(R.id.imageButton_checkout_buyer);
            textView_productName = itemView.findViewById(R.id.textView_product_name_checkout_buyer);
            textView_pricePerProduct = itemView.findViewById(R.id.textView_product_priceperitem_checkout_buyer);
            textView_totalItemBuy = itemView.findViewById(R.id.textView_itemperproduct_checkout_buyer);
            textView_totalPricePerProduct = itemView.findViewById(R.id.textView_totalperproduct_checkout_buyer);
        }
    }

    public int getItemPerProduct() {
        return itemPerProduct;
    }

    public void setItemPerProduct(int itemPerProduct) {
        this.itemPerProduct = itemPerProduct;
    }

    public long getPricePerProduct() {
        return pricePerProduct;
    }

    public void setPricePerProduct(long pricePerProduct) {
        this.pricePerProduct = pricePerProduct;
    }

    public long getTotalPricePerProduct() {
        return totalPricePerProduct;
    }

    public void setTotalPricePerProduct(long totalPricePerProduct) {
        this.totalPricePerProduct = totalPricePerProduct;
    }

    public ArrayList<Cart> getListBuy() {
        return listBuy;
    }

    public void setListBuy(ArrayList<Cart> listBuy) {
        this.listBuy = listBuy;
    }
}
