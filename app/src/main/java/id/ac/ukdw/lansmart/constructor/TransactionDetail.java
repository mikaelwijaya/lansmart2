package id.ac.ukdw.lansmart.constructor;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TransactionDetail {
    private String detailId, transactionId, productId, productName, status, sellerId, buyerId, address;
    private int itemPerProduct;
    private double pricePerProduct;
    private long priceTotalPerProduct;

    DatabaseReference databaseReferenceProduct;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @SuppressLint("SimpleDateFormat")
    private String dateTime = new SimpleDateFormat("YYYY-MM-dd-HH-mm").format(Calendar.getInstance().getTime());

    public TransactionDetail(){

    }

    public TransactionDetail(String buyerId, String sellerId, String productId, String productName, int itemPerProduct, double pricePerProduct, long priceTotalPerProduct, String address, String status){
        final String[] temp = {null};
        this.detailId = "DETAIL-" + dateTime;
        this.transactionId = "TRANSACTION-" + buyerId + "-" + dateTime;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.productName = productName;
        this.itemPerProduct = itemPerProduct;
        this.pricePerProduct = pricePerProduct;
        this.priceTotalPerProduct = priceTotalPerProduct;
        this.address = address;
        this.status = status;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getItemPerProduct() {
        return itemPerProduct;
    }

    public void setItemPerProduct(int itemPerProduct) {
        this.itemPerProduct = itemPerProduct;
    }

    public double getPricePerProduct() {
        return pricePerProduct;
    }

    public void setPricePerProduct(double pricePerProduct) {
        this.pricePerProduct = pricePerProduct;
    }

    public long getPriceTotalPerProduct() {
        return priceTotalPerProduct;
    }

    public void setPriceTotalPerProduct(long priceTotalPerProduct) {
        this.priceTotalPerProduct = priceTotalPerProduct;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }
}
