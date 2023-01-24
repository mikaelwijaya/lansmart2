package id.ac.ukdw.lansmart.seller.productseller.editproductseller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.constructor.Product;
import id.ac.ukdw.lansmart.constructor.Seller;
import id.ac.ukdw.lansmart.seller.productseller.ListProductSellerAdapter;
import id.ac.ukdw.lansmart.seller.productseller.addproductseller.AddProductImageSellerActivity;
import id.ac.ukdw.lansmart.seller.productseller.addproductseller.AddProductSellerActivity;

public class EditProductSellerAcivity extends AppCompatActivity {
    public static final String EXTRA_IDPRODUCT = "id.ac.udkw.rockpaperscissors.EXTRA_IDPRODUCT";
    private EditText editText_productId, editText_productName, editText_available, editText_price;
    private Button button_edit, button_edit_picture;
    private Spinner spinner_status, spinner_category;

    private String productId, productName, status, editBy, productKey, category, sellerId, available, price;
    private String [] statusTemplate = {"Aktif", "Pasif"};
    private String [] categoryTemplate = {"Bahan Pokok", "Pakaian Dan Asesoris", "Peralatan Rumah Tangga", "Kesehatan", "Hobi", "Lainnya"};

    private DatabaseReference databaseReferenceProduct, databaseReferenceSeller;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_seller);

        editText_productId = (EditText) findViewById(R.id.editText_edit_productId_product_seller);
        editText_productName = (EditText) findViewById(R.id.editText_edit_nameProduct_product_seller);
        editText_available = (EditText) findViewById(R.id.editText_edit_available_product_seller);
        editText_price = (EditText) findViewById(R.id.editText_edit_price_product_seller);
        spinner_status = (Spinner) findViewById(R.id.spinner_edit_status_product_seller);
        spinner_category = (Spinner) findViewById(R.id.spinner_edit_category_product_seller);
        button_edit = (Button) findViewById(R.id.button_submit_edit_product_seller);
        button_edit_picture = (Button) findViewById(R.id.button_edit_image_product_seller);

        Intent intent = getIntent();
        editBy = intent.getStringExtra(ListProductSellerAdapter.EXTRA_SELLERCODE);
        productId = intent.getStringExtra(ListProductSellerAdapter.EXTRA_PRODUCTCODE);

        ArrayAdapter<String> adapterSpinerStatus = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statusTemplate);
        adapterSpinerStatus.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_status.setAdapter(adapterSpinerStatus);

        ArrayAdapter<String> adapterSpinerCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryTemplate);
        adapterSpinerCategory.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_category.setAdapter(adapterSpinerCategory);

        databaseReferenceProduct = db.getReference(Product.class.getSimpleName());
        databaseReferenceProduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Product product = item.getValue(Product.class);
                    if (product.getProductId().equals(productId)){
                        productKey = item.getKey();
                        sellerId = product.getSellerId();
                        productName = product.getNameProduct();
                        available = Integer.toString(product.getAvailable());
                        price = Double.toString(product.getPrice());
                        status = product.getStatus();
                        category = product.getCategory();

                        button_edit_picture.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intentImage = new Intent(EditProductSellerAcivity.this, EditProductImageSellerActivity.class);
                                intentImage.putExtra(EXTRA_IDPRODUCT, productId);
                                startActivity(intentImage);
                            }
                        });
                    }
                }
                editText_productId.setText(productId);
                editText_productName.setText(productName);
                editText_available.setText(available + "");
                editText_price.setText(price + "");
                spinner_status.setSelection(getIndexStatus(spinner_status, status));
                spinner_category.setSelection(getIndexCategori(spinner_category, category));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProductSeller(productKey, editBy);
            }
        });
    }

    private int getIndexStatus(Spinner spinner, String status) {
        for (int i=0; i<spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(status)){
                return i;
            }
        }
        return 1;
    }

    private int getIndexCategori(Spinner spinner, String category) {
        for (int i=0; i<spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(category)){
                return i;
            }
        }
        return 1;
    }

    public void editProductSeller(String productKey, String editBy){
        productId = editText_productId.getText().toString();
        productName = editText_productName.getText().toString();
        available = editText_available.getText().toString();
        price = editText_price.getText().toString();
        status = spinner_status.getSelectedItem().toString();
        category = spinner_category.getSelectedItem().toString();

        databaseReferenceProduct = db.getReference(Product.class.getSimpleName());
        databaseReferenceProduct.child(productKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean check_available = false, check_price = false;
                Product product = snapshot.getValue(Product.class);
                product.setProductId(productId);
                product.setNameProduct(productName);
                product.setStatus(status);
                product.setCategory(category);
                product.setEditBy(editBy);

                try {
                    product.setAvailable(Integer.parseInt(available));
                    check_available = true;
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Isi Jumlah Stok Dengan Benar!",Toast.LENGTH_SHORT).show();
                }

                try {
                    product.setPrice(Double.parseDouble(price));
                    check_price = true;
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Isi Harga Dengan Benar!",Toast.LENGTH_SHORT).show();
                }

                if (check_available == true && check_price == true){
                    databaseReferenceProduct.child(productKey).setValue(product);
                    Toast.makeText(getApplicationContext(),"Produk Berhasil Diubah",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Produk Gagal Diubah!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
