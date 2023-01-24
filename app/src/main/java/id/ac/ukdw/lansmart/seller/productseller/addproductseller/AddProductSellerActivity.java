package id.ac.ukdw.lansmart.seller.productseller.addproductseller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Scanner;

import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.seller.productseller.ProductMenuSellerActivity;

public class AddProductSellerActivity extends AppCompatActivity {
    public static final String EXTRA_SELLERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_SELLERCODE";
    public static final String EXTRA_IDPRODUCT = "id.ac.udkw.rockpaperscissors.EXTRA_IDPRODUCT";
    public static final String EXTRA_NAMEPRODUCT = "id.ac.udkw.rockpaperscissors.EXTRA_NAMEPRODUCT";
    public static final String EXTRA_PRICE = "id.ac.udkw.rockpaperscissors.EXTRA_PRICE";
    public static final String EXTRA_STOCK = "id.ac.udkw.rockpaperscissors.EXTRA_STOCK";
    public static final String EXTRA_STATUS = "id.ac.udkw.rockpaperscissors.EXTRA_STATUS";
    public static final String EXTRA_CATEGORY = "id.ac.udkw.rockpaperscissors.EXTRA_CATEGORY";
    private EditText editText_id, editText_name, editText_price, editText_stock;
    private Button button_add;
    private Spinner spinner_category, spinner_status;

    private String codeSeller, idProduct, nameProduct, price, stock, statusProduct, category;
    private String [] statusTemplate = {"Aktif", "Pasif"};
    private String [] categoryTemplate = {"Bahan Pokok", "Pakaian Dan Asesoris", "Peralatan Rumah Tangga", "Kesehatan", "Hobi", "Lainnya"};
    private DatabaseReference databaseReferenceSeller;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_seller);

        editText_id = (EditText) findViewById(R.id.editText_idproduct_addproduct_seller);
        editText_name = (EditText) findViewById(R.id.editText_name_product_addproduct_seller);
        editText_price = (EditText) findViewById(R.id.editText_price_product_addproduct_seller);
        editText_stock = (EditText) findViewById(R.id.editText_available_product_addproduct_seller);
        spinner_status = (Spinner) findViewById(R.id.spinner_status_product_addproduct_seller);
        spinner_category = (Spinner) findViewById(R.id.spinner_category_addproduct_seller);
        button_add = (Button) findViewById(R.id.button_submit_add_product_seller);

        Intent intent = getIntent();
        codeSeller = intent.getStringExtra(ProductMenuSellerActivity.EXTRA_SELLERCODE);

        ArrayAdapter<String> adapterSpinerStatus = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statusTemplate);
        adapterSpinerStatus.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_status.setAdapter(adapterSpinerStatus);

        ArrayAdapter<String> adapterSpinerCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryTemplate);
        adapterSpinerCategory.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_category.setAdapter(adapterSpinerCategory);

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct(codeSeller);
            }
        });

    }

    private void addProduct(String codeSeller) {
        if (editText_id.getText().toString().isEmpty() || editText_name.getText().toString().isEmpty() || editText_price.getText().toString().isEmpty() || editText_stock.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Isi Semua Terlebih Dahulu!", Toast.LENGTH_SHORT).show();
        }
        else {
            boolean check_stock = false, check_price = false;
            int check_stock2;
            Double check_price2;

            idProduct = editText_id.getText().toString();
            nameProduct = editText_name.getText().toString();
            statusProduct = spinner_status.getSelectedItem().toString();
            price = editText_price.getText().toString();
            stock = editText_stock.getText().toString();
            category = spinner_category.getSelectedItem().toString();

            try {
                check_price2 = Double.parseDouble(price);
                check_price = true;
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Isi Harga Dengan Benar!", Toast.LENGTH_SHORT).show();
            }

            try {
                check_stock2 = Integer.parseInt(stock);
                check_stock = true;
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Isi Jumlah Stok Dengan Benar!", Toast.LENGTH_SHORT).show();
            }

            if (check_stock == true && check_price == true){
                Intent intentImage = new Intent(AddProductSellerActivity.this, AddProductImageSellerActivity.class);
                intentImage.putExtra(EXTRA_SELLERCODE, codeSeller);
                intentImage.putExtra(EXTRA_IDPRODUCT, idProduct);
                intentImage.putExtra(EXTRA_NAMEPRODUCT, nameProduct);
                intentImage.putExtra(EXTRA_PRICE, price);
                intentImage.putExtra(EXTRA_STOCK, stock);
                intentImage.putExtra(EXTRA_STATUS, statusProduct);
                intentImage.putExtra(EXTRA_CATEGORY, category);
                startActivity(intentImage);
            }
        }
    }
}
