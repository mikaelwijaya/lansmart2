package id.ac.ukdw.lansmart.constructor;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Transaction {
    private String transactionId, buyerId, address;
    private long amount;
    private int item, quantity;

    @SuppressLint("SimpleDateFormat")
    private String dateTime = new SimpleDateFormat("YYYY-MM-dd-HH-mm").format(Calendar.getInstance().getTime());

    public Transaction(){

    }

    public Transaction(String buyerId, long amount, int item, int quantity, String address){
        this.transactionId = "TRANSACTION-" + buyerId + "-" + dateTime;
        this.buyerId = buyerId;
        this.amount = amount;
        this.item = item;
        this.quantity = quantity;
        this.address = address;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
