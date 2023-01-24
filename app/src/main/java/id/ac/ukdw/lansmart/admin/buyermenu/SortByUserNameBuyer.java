package id.ac.ukdw.lansmart.admin.buyermenu;

import java.util.Comparator;

import id.ac.ukdw.lansmart.constructor.Buyer;

public class SortByUserNameBuyer implements Comparator<Buyer> {

    @Override
    public int compare(Buyer a, Buyer b) {
        return a.getUserName().compareTo(b.getUserName());
    }
}
