package id.ac.ukdw.lansmart.seller.homeseller;

import android.annotation.SuppressLint;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.ZoomImageActivity;
import id.ac.ukdw.lansmart.constructor.Product;

public class ListExpensiveProductHomeSellerAdapter extends RecyclerView.Adapter<ListExpensiveProductHomeSellerAdapter.ListViewHolder> {
    public static final String EXTRA_PRODUCTCODE = "id.ac.udkw.rockpaperscissors.EXTRA_PRODUCTCODE";
    private String productCode;
    private ArrayList<Product> listProduct;

    private Context context;

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    public ListExpensiveProductHomeSellerAdapter(ArrayList<Product> list) {
        this.listProduct = list;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_row_expensive_seller, parent, false);
        return new ListViewHolder(view);
    }

    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        int number = position+1;
        Product product = listProduct.get(position);
        holder.textView_number.setText(number + ". ");
        holder.textView_productName.setText(product.getNameProduct());
        holder.textView_productPrice.setText("" + formatRupiah.format(product.getPrice()));
        holder.textView_available.setText("Stok: " + product.getAvailable());
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
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton_image;
        TextView textView_number, textView_productName, textView_productPrice, textView_available, textView_status;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton_image = itemView.findViewById(R.id.imageButton_expensive_seller);
            textView_number = itemView.findViewById(R.id.textView_counter_item_expensive_seller);
            textView_productName = itemView.findViewById(R.id.textView_product_name_expensive_seller);
            textView_productPrice = itemView.findViewById(R.id.textView_product_price_expensive_seller);
            textView_available = itemView.findViewById(R.id.textView_available_expensive_seller);
            textView_status = itemView.findViewById(R.id.textView_status_product_expensive_seller);
        }
    }
}
