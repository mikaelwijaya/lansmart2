package id.ac.ukdw.lansmart.buyer.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import id.ac.ukdw.lansmart.buyer.cart.CartBuyerActivity;
import id.ac.ukdw.lansmart.constructor.Buyer;
import id.ac.ukdw.lansmart.constructor.Cart;
import id.ac.ukdw.lansmart.constructor.Product;
import id.ac.ukdw.lansmart.constructor.Transaction;
import id.ac.ukdw.lansmart.constructor.TransactionDetail;

public class CheckoutBuyerActivity extends AppCompatActivity {
    public static final String EXTRA_BUYERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERCODE";
    private TextView textView_totalAmount, textView_totalItem, textView_totalQuantity, textView_page_now, textView_total_page;
    private Button button_buy, button_nextPage_buy, button_prefPage_buy;
    private RecyclerView recyclerView_buy;

    private String buyerCode, buyerName, address;
    int totalItem = 0, totalQuantity = 0, pages = 0, totalPage = 0;
    long totalAmount = 0;

    private ArrayList<Cart> listBuy = new ArrayList<Cart>();
    private ArrayList<Cart> listThreeBuy = new ArrayList<Cart>();

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    private DatabaseReference databaseReferenceCart, databaseReferenceTransaction,
            databaseReferenceDetailTransaction, databaseReferenceBuyer, databaseReferenceProduct;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_buyer);

        textView_totalAmount = (TextView) findViewById(R.id.textView_total_amount_checkout_buyer);
        textView_totalItem = (TextView) findViewById(R.id.textView_total_item_checkout_buyer);
        textView_totalQuantity = (TextView) findViewById(R.id.textView_total_iquantity_checkout_buyer);
        button_buy = (Button) findViewById(R.id.button_buy_checkout_buyer);
        button_nextPage_buy = (Button) findViewById(R.id.button_nextPage_checkout);
        button_prefPage_buy = (Button) findViewById(R.id.button_prefPage_checkout);
        textView_page_now = (TextView) findViewById(R.id.textView_page_now_checkout_buyer);
        textView_total_page = (TextView) findViewById(R.id.textView_total_page_checkout_buyer);
        recyclerView_buy = (RecyclerView) findViewById(R.id.recyclerView_buy_buyer);

        Intent intent = getIntent();
        buyerCode = intent.getStringExtra(CartBuyerActivity.EXTRA_BUYERCODE);

        databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
        databaseReferenceBuyer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Buyer buyer = item.getValue(Buyer.class);
                    if (buyer.getUserName().equalsIgnoreCase(buyerCode)){
                        buyerName = buyer.getName();
                        address = (buyer.getAddress() + " " + buyer.getCodePos()).toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceCart = db.getReference(Cart.class.getSimpleName());
        databaseReferenceCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBuy = new ArrayList<Cart>();
                totalItem = 0;
                totalQuantity = 0;
                totalAmount = 0;
                for (DataSnapshot item : snapshot.getChildren()){
                    Cart cart = item.getValue(Cart.class);
                    if (cart.getBuyerId().equals(buyerCode)){
                        if (cart.isCheckOut() == true){
                            listBuy.add(cart);
                            totalAmount = (long) (totalAmount + (cart.getItemPerProduct() * cart.getPricePerProduct()));
                            totalItem = totalItem + cart.getItemPerProduct();
                            totalQuantity = totalQuantity + 1;
                        }
                    }
                }
                if (listBuy.size()<=3){
                    totalPage = 1;
                    pages = 1;
                    listThreeBuy = listBuy;
                    showRecyclerListBuy(listThreeBuy, buyerCode);
                }
                else{
                    int mod = listBuy.size()%4;

                    if (mod==0){
                        totalPage = listBuy.size()/3;
                    }
                    else {
                        totalPage = (listBuy.size()/3)+1;
                    }
                    pages = 1;
                    for (int i=0; i<3; i++){
                        listThreeBuy.add(listBuy.get(i));
                    }
                    showRecyclerListBuy(listThreeBuy, buyerCode);
                }

                textView_page_now.setText("" + pages);
                textView_total_page.setText("" + totalPage);
                textView_totalAmount.setText(formatRupiah.format(totalAmount));
                textView_totalItem.setText(totalItem + " barang");
                textView_totalQuantity.setText(totalQuantity + " produk");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button_nextPage_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages++;
//                halaman belum akhir
                if(pages < totalPage){
                    textView_page_now.setText("" + pages);
                    int start = (pages-1)*3;
                    int end = start + 2;
                    listThreeBuy.clear();
                    for (int i=start; i <= end; i++){
                        listThreeBuy.add(listBuy.get(i));
                    }
                    showRecyclerListBuy(listThreeBuy, buyerCode);
                }
//                halaman akhir
                else if(pages == totalPage){
                    textView_page_now.setText("" + pages);
                    int start = (pages-1) * 3;
                    int end = listBuy.size();
                    listThreeBuy.clear();

                    for (int i=start; i<end; i++){
                        listThreeBuy.add(listBuy.get(i));
                    }
                    showRecyclerListBuy(listThreeBuy, buyerCode);
                }
//                tidak ada halaman
                else{
                    pages--;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Terakhir",Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_prefPage_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pages--;
                int start = (pages-1)*3;
                int end = start + 2;
                listThreeBuy.clear();
//                belum halaman awal
                if(pages >= 1){
                    textView_page_now.setText("" + pages);
                    for (int i=start; i <= end; i++){
                        listThreeBuy.add(listBuy.get(i));
                    }
                    showRecyclerListBuy(listThreeBuy, buyerCode);
                }
//                sudah halaman awal
                else{
                    pages++;
                    Toast.makeText(getApplicationContext(), "Sudah Mencapai Halaman Pertama",Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderProduct();
            }
        });
    }

    public void showRecyclerListBuy(ArrayList<Cart> listBuy, String buyerCode){
        recyclerView_buy.setLayoutManager(new LinearLayoutManager(this));
        ListCheckoutBuyerAdapter listCheckoutBuyerAdapter = new ListCheckoutBuyerAdapter(listBuy);
        recyclerView_buy.setAdapter(listCheckoutBuyerAdapter);
    }

    public void orderProduct(){
        databaseReferenceTransaction = db.getReference(Transaction.class.getSimpleName());
        databaseReferenceDetailTransaction = db.getReference(TransactionDetail.class.getSimpleName());
        databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
        databaseReferenceProduct = db.getReference(Product.class.getSimpleName());

//        pengecekan stok produk
//        buat transaksidetail per barang
//        mengurangi stok produk
//        menghapus cart yang checked
//        buat transaksi 1 kali

        try{
            databaseReferenceProduct.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot item : snapshot.getChildren()){
                        Product product = item.getValue(Product.class);
                        for (int i=0; i<listBuy.size(); i++){
                            if (product.getProductId().equalsIgnoreCase(listBuy.get(i).getProductId())){
                                if (product.getAvailable()>=listBuy.get(i).getItemPerProduct()){
                                    TransactionDetail transactionDetail = new TransactionDetail(buyerCode, product.getSellerId(), product.getProductId(), product.getNameProduct()
                                            , listBuy.get(i).getItemPerProduct(), listBuy.get(i).getPricePerProduct(), (long) (listBuy.get(i).getItemPerProduct()
                                            * listBuy.get(i).getPricePerProduct()), address, "Belum Diterima");
                                    databaseReferenceDetailTransaction.push().setValue(transactionDetail);

                                    databaseReferenceCart.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot item : snapshot.getChildren()){
                                                Cart cart = item.getValue(Cart.class);
                                                if (cart.getProductId().equals(product.getProductId())){
                                                    if (cart.isCheckOut() == true){
                                                        databaseReferenceCart.child(item.getKey()).removeValue();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    product.setAvailable(product.getAvailable()-listBuy.get(i).getItemPerProduct());
                                    databaseReferenceProduct.child(item.getKey()).setValue(product);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Toast.makeText(getApplicationContext(), "Terima Kasih, Barang Akan Segera Dikirim", Toast.LENGTH_LONG).show();
            Transaction transaction = new Transaction(buyerCode, totalAmount, totalItem, totalQuantity, address);
            databaseReferenceTransaction.push().setValue(transaction);
        }
        catch (Exception e){

        }

        Intent intentMainMenuBuyer = new Intent(CheckoutBuyerActivity.this, id.ac.ukdw.lansmart.buyer.HomeBuyerActivity.class);
        intentMainMenuBuyer.putExtra(EXTRA_BUYERCODE, buyerCode);
        startActivity(intentMainMenuBuyer);

    }
}
