package id.ac.ukdw.lansmart.admin.buyermenu;

import java.util.Comparator;

import id.ac.ukdw.lansmart.constructor.Buyer;

public class SortByStatus implements Comparator<Buyer> {

    @Override
    public int compare(Buyer a, Buyer b) {
        return b.getStatus().compareTo(a.getStatus());
    }
}