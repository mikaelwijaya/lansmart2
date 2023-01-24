package id.ac.ukdw.lansmart.buyer.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.buyer.HomeBuyerActivity;
import id.ac.ukdw.lansmart.buyer.checkout.CheckoutBuyerActivity;
import id.ac.ukdw.lansmart.constructor.Cart;
import id.ac.ukdw.lansmart.constructor.Product;

public class CartBuyerActivity extends AppCompatActivity {
    public static final String EXTRA_BUYERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERCODE";
    private CheckBox checkBox_all;
    private TextView textView_total, textView_page_now, textView_total_page;
    private Button button_checkOut, button_nextPage_cart, button_prefPage_cart;
    private RecyclerView recyclerView_cart;

    private String buyerCode;
    private Boolean all = false;
    private int pages = 0, totalPage = 0;

    private ArrayList<Cart> listCart = new ArrayList<Cart>();
    private ArrayList<Cart> listFourProduct = new ArrayList<Cart>();

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    private DatabaseReference databaseReferenceCart;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_buyer);

        checkBox_all = (CheckBox) findViewById(R.id.checkBox_all_cart_buyer);
        textView_total = (TextView) findViewById(R.id.textView_total_cart_buyer);
        button_checkOut = (Button) findViewById(R.id.button_chechout_buyer);
        button_nextPage_cart = (Button) findViewById(R.id.button_nextPage_cart);
        button_prefPage_cart = (Button) findViewById(R.id.button_prefPage_cart);
        textView_page_now = (TextView) findViewById(R.id.textView_page_now_cart_buyer);
        textView_total_page = (TextView) findViewById(R.id.textView_total_page_cart_buyer);
        recyclerView_cart = (RecyclerView) findViewById(R.id.recyclerView_cart_buyer);

        Intent intent = getIntent();
        buyerCode = intent.getStringExtra(HomeBuyerActivity.EXTRA_BUYERCODE);

        databaseReferenceCart = db.getReference(Cart.class.getSimpleName());
        databaseReferenceCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long total = 0;
                for (DataSnapshot item : snapshot.getChildren()){
                    Cart cart = item.getValue(Cart.class);
                    if (cart.getBuyerId().equals(buyerCode)){
                        if (cart.getStatus().equalsIgnoreCase("checked")){
                            total = (long) (total + (cart.getItemPerProduct() * cart.getPricePerProduct()));
                        }
                    }
                }
                textView_total.setText(formatRupiah.format(total));
                recyclerListCart(listCart, buyerCode);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkBox_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReferenceCart = db.getReference(Cart.class.getSimpleName());
                if (checkBox_all.isChecked()){
                    databaseReferenceCart.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot item : snapshot.getChildren()){
                                Cart cart = item.getValue(Cart.class);
                                if (cart.getBuyerId().equals(buyerCode)){
                                    cart.setStatus("Checked");
                                    databaseReferenceCart.child(item.getKey()).setValue(cart);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if (checkBox_all.isChecked() == false){
                    databaseReferenceCart.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot item : snapshot.getChildren()){
                                Cart cart = item.getValue(Cart.class);
                                if (cart.getBuyerId().equals(buyerCode)){
                                    cart.setStatus("Unchecked");
                                    cart.setCheckOut(false);
                                    databaseReferenceCart.child(item.getKey()).setValue(cart);
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

        button_nextPage_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages++;
//                halaman belum akhir
                if(pages < totalPage){
                    textView_page_now.setText("" + pages);
                    int start = (pages-1)*4;
                    int end = start + 3;
                    listFourProduct.clear();
                    for (int i=start; i <= end; i++){
                        listFourProduct.add(listCart.get(i));
                    }
                    showRecyclerView(listFourProduct);
                }
//                halaman akhir
                else if(pages == totalPage){
                    textView_page_now.setText("" + pages);
                    int start = (pages-1) * 4;
                    int end = listCart.size();
                    listFourProduct.clear();

                    for (int i=start; i<end; i++){
                        listFourProduct.add(listCart.get(i));
                    }
                    showRecyclerView(listFourProduct);
                }
//                tidak ada halaman
                else{
                    pages--;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Terakhir",Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_prefPage_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages--;
                int start = (pages-1)*4;
                int end = start + 3;
                listFourProduct.clear();
//                belum halaman awal
                if(pages >= 1){
                    textView_page_now.setText("" + pages);
                    for (int i=start; i <= end; i++){
                        listFourProduct.add(listCart.get(i));
                    }
                    showRecyclerView(listFourProduct);
                }
//                sudah halaman awal
                else{
                    pages++;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Pertama",Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOutCart(buyerCode);
            }
        });

//        recyclerListCart(listCart, buyerCode);
    }

    public void recyclerListCart(ArrayList<Cart> listCart, String buyerCode){
        databaseReferenceCart = db.getReference(Cart.class.getSimpleName());
        databaseReferenceCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCart.clear();
                for (DataSnapshot item : snapshot.getChildren()){
                    Cart cart = item.getValue(Cart.class);
                    if (cart.getBuyerId().equals(buyerCode)){
                        listCart.add(cart);
                    }
                }
                if (listCart.size()<=4){
                    totalPage = 1;
                    pages = 1;
                    listFourProduct = listCart;
                    showRecyclerView(listFourProduct);
                }
                else{
                    int mod = listCart.size()%4;

                    if (mod==0){
                        totalPage = listCart.size()/4;
                    }
                    else {
                        totalPage = (listCart.size()/4)+1;
                    }
                    pages = 1;
                    for (int i=0; i<4; i++){
                        listFourProduct.add(listCart.get(i));
                    }
                    showRecyclerView(listFourProduct);
                }
                textView_page_now.setText("" + pages);
                textView_total_page.setText("" + totalPage);

                for (DataSnapshot item : snapshot.getChildren()){
                    Cart cart = item.getValue(Cart.class);
                    if (cart.getStatus().equalsIgnoreCase("checked")){
                        checkBox_all.setChecked(true);
                    }
                    else{
                        checkBox_all.setChecked(false);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void showRecyclerView(ArrayList<Cart> listCart){
        recyclerView_cart.setLayoutManager(new LinearLayoutManager(this));
        ListCartBuyerAdapter listCartBuyerAdapter = new ListCartBuyerAdapter(listCart);
        recyclerView_cart.setAdapter(listCartBuyerAdapter);
    }

    public void checkOutCart(String buyerCode){
        databaseReferenceCart = db.getReference(Cart.class.getSimpleName());
        databaseReferenceCart.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Cart cart = item.getValue(Cart.class);
                    if (cart.getCartId().equals(buyerCode) && cart.getStatus().toLowerCase().equals("checked")){
                        cart.setCheckOut(true);
                        databaseReferenceCart.child(item.getKey()).setValue(cart);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Intent intentCheckout = new Intent(CartBuyerActivity.this, CheckoutBuyerActivity.class);
        intentCheckout.putExtra(EXTRA_BUYERCODE, buyerCode);
        startActivity(intentCheckout);
    }

    public Boolean getAll() {
        return all;
    }

    public void setAll(Boolean all) {
        this.all = all;
    }

    public ArrayList<Cart> getListCart() {
        return listCart;
    }

    public void setListCart(ArrayList<Cart> listCart) {
        this.listCart = listCart;
    }
}
