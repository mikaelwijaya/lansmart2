package id.ac.ukdw.lansmart.seller.productseller;

import java.util.Comparator;

import id.ac.ukdw.lansmart.constructor.Product;
import id.ac.ukdw.lansmart.constructor.TransactionDetail;

public class SortByStatusProductSeller implements Comparator<Product> {
    @Override
    public int compare(Product a, Product b) {
        return b.getStatus().compareTo(a.getStatus());
    }
}
