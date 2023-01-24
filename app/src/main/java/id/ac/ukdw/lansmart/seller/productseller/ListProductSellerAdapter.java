package id.ac.ukdw.lansmart.seller.productseller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.ZoomImageActivity;
import id.ac.ukdw.lansmart.constructor.Product;
import id.ac.ukdw.lansmart.seller.productseller.editproductseller.EditProductSellerAcivity;

public class ListProductSellerAdapter extends RecyclerView.Adapter<ListProductSellerAdapter.ListViewHolder> {
    public static final String EXTRA_SELLERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_SELLERCODE";
    public static final String EXTRA_PRODUCTCODE = "id.ac.udkw.rockpaperscissors.EXTRA_PRODUCTCODE";
    private String sellerCode, productCode;
    private ArrayList<Product> listProduct;

    private Context context;

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    private DatabaseReference databaseReferenceProduct;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public ListProductSellerAdapter(ArrayList<Product> list, String sellerCode) {
        this.listProduct = list;
        this.sellerCode = sellerCode;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_product_menu_seller, parent, false);
        return new ListViewHolder(view);
    }

    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Product product = listProduct.get(position);
        holder.textView_productName.setText(product.getNameProduct());
        holder.textView_productPrice.setText("" + formatRupiah.format(product.getPrice()));
        holder.textView_available.setText("Tersedia: " + product.getAvailable());
        holder.textView_status.setText(product.getStatus());

        try{
            Glide.with(context).load(product.getUrl()).into(holder.imageButton_image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.imageButton_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productCode = listProduct.get(position).getProductId();
                Intent intentZoom = new Intent(v.getContext(), ZoomImageActivity.class);
                intentZoom.putExtra(EXTRA_PRODUCTCODE, productCode);
                v.getContext().startActivity(intentZoom);
            }
        });

        holder.linearLayout_edit_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProduct(listProduct.get(position).getProductId(), listProduct.get(position).getSellerId(), v);
            }
        });
    }

    private void editProduct(String productId, String sellerCode, View v) {
        Intent intentEditProduct = new Intent(v.getContext(), EditProductSellerAcivity.class);
        intentEditProduct.putExtra(EXTRA_SELLERCODE, sellerCode);
        intentEditProduct.putExtra(EXTRA_PRODUCTCODE, productId);
        v.getContext().startActivity(intentEditProduct);
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView textView_productName, textView_productPrice, textView_available, textView_status;
        ImageButton imageButton_image, imageButton_edit;
        LinearLayout linearLayout_edit_product;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton_image = itemView.findViewById(R.id.image_button_image_product_seller);
            textView_productName = itemView.findViewById(R.id.productname_product_menu_seller);
            textView_productPrice = itemView.findViewById(R.id.textView_product_price_seller);
            textView_available = itemView.findViewById(R.id.productstock_product_menu_seller);
            textView_status = itemView.findViewById(R.id.productstatus_product_menu_seller);
            imageButton_edit = itemView.findViewById(R.id.imageButton_update_product_menu_seller);
            linearLayout_edit_product = itemView.findViewById(R.id.linear_layout_edit_product_seller);
        }
    }
}
