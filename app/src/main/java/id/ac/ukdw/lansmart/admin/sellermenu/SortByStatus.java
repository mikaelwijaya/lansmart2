package id.ac.ukdw.lansmart.admin.sellermenu;

import java.util.Comparator;

import id.ac.ukdw.lansmart.constructor.Seller;

public class SortByStatus implements Comparator<Seller> {
    @Override
    public int compare(Seller a, Seller b) {
        return b.getStatus().compareTo(a.getStatus());
    }
}
