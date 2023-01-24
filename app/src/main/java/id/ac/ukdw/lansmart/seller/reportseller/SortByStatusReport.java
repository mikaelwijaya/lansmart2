package id.ac.ukdw.lansmart.seller.reportseller;

import java.util.Comparator;

import id.ac.ukdw.lansmart.constructor.Product;
import id.ac.ukdw.lansmart.constructor.Seller;
import id.ac.ukdw.lansmart.constructor.Transaction;
import id.ac.ukdw.lansmart.constructor.TransactionDetail;

public class SortByStatusReport implements Comparator<TransactionDetail> {
    @Override
    public int compare(TransactionDetail a, TransactionDetail b) {
        return a.getStatus().compareTo(b.getStatus());
    }
}
