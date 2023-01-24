package id.ac.ukdw.lansmart.admin.productmenu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class ListProductSellerAdminAdapter extends RecyclerView.Adapter<ListProductSellerAdminAdapter.ListViewHolder>{
    public static final String EXTRA_ADMINCODE = "id.ac.udkw.rockpaperscissors.EXTRA_ADMINCODE";
    public static final String EXTRA_SELLERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_SELLERCODE";
    public static final String EXTRA_PRODUCTCODE = "id.ac.udkw.rockpaperscissors.EXTRA_PRODUCTCODE";
    private String openBy, productCode;
    private ArrayList<Product> listProduct;

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    private Context context;

    public ListProductSellerAdminAdapter(ArrayList<Product> list, String openBy) {
        this.listProduct = list;
        this.openBy = openBy;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_product_menu_admin, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Product product = listProduct.get(position);
        holder.textView_productName.setText("Nama: " +product.getNameProduct());
        holder.textView_available.setText("Stok: " + product.getAvailable());
        holder.textView_status.setText("" + product.getStatus());
        holder.textView_price.setText(formatRupiah.format(product.getPrice()) + "");

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
                Intent intentEdit = new Intent(v.getContext(), EditProductSellerAdminAcivity.class);
                intentEdit.putExtra(EXTRA_SELLERCODE, listProduct.get(position).getSellerId());
                intentEdit.putExtra(EXTRA_PRODUCTCODE, listProduct.get(position).getProductId());
                intentEdit.putExtra(EXTRA_ADMINCODE, openBy);
                v.getContext().startActivity(intentEdit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView textView_productName, textView_available, textView_status, textView_price;
        ImageButton imageButton_image, imageButton_edit;
        LinearLayout linearLayout_edit_product;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton_image = itemView.findViewById(R.id.image_button_image_product_admin);
            textView_productName = itemView.findViewById(R.id.textView_name_product_menu_admin);
            textView_available = itemView.findViewById(R.id.textView_productstock_product_menu_admin);
            textView_status = itemView.findViewById(R.id.textView_status_product_seller_admin);
            textView_price = itemView.findViewById(R.id.textView_price_product_seller_menu_admin);
            imageButton_edit = itemView.findViewById(R.id.button_edit_product_seller_admin);
            linearLayout_edit_product = itemView.findViewById(R.id.linear_layout_edit_product_admin);
        }
    }

    private void editProductSeller(String sellerId, String nameProduct) {

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
