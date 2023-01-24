package id.ac.ukdw.lansmart.buyer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import id.ac.ukdw.lansmart.LoginActivity;
import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.buyer.cart.CartBuyerActivity;
import id.ac.ukdw.lansmart.buyer.checkout.CheckoutBuyerActivity;
import id.ac.ukdw.lansmart.buyer.profile.ChangeNameBuyerActivity;
import id.ac.ukdw.lansmart.buyer.profile.ChangePasswordBuyerActivity;
import id.ac.ukdw.lansmart.buyer.profile.ProfileBuyerActivity;
import id.ac.ukdw.lansmart.constructor.Buyer;

public class HomeBuyerActivity extends AppCompatActivity {
    public static final String EXTRA_BUYERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERCODE";
    public static final String EXTRA_CATEGORY = "id.ac.udkw.rockpaperscissors.EXTRA_CATEGORY";
    private ImageButton imageButton_profile, imageButton_cart, imageButton_bahanPokok, imageButton_pakaian, imageButton_peralatanRumahTangga,
            imagebutton_kesehatan, imageButton_hobi, imageButton_lainnya;
    private TextView textView_buyerName, textView_address_buyer, textView_button_logout;

    private String category, userCodeBuyer, name, address;

    private DatabaseReference databaseReferenceBuyer;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_buyer);

        textView_buyerName = (TextView) findViewById(R.id.textView_buyer_name);
        textView_address_buyer = (TextView) findViewById(R.id.textView_alamat_buyer);
        imageButton_profile = (ImageButton) findViewById(R.id.imageButton_profile_buyer);
        imageButton_cart = (ImageButton) findViewById(R.id.imageButton_cart_buyer);
        textView_button_logout = (TextView) findViewById(R.id.textView_button_logout_buyer);
        imageButton_bahanPokok = (ImageButton) findViewById(R.id.imageButton_bahanPokok);
        imageButton_pakaian = (ImageButton) findViewById(R.id.imageButton_pakaian);
        imageButton_peralatanRumahTangga = (ImageButton) findViewById(R.id.imageButton_peralatanRumahTangga);
        imagebutton_kesehatan = (ImageButton) findViewById(R.id.imageButton_kesehatan);
        imageButton_lainnya = (ImageButton) findViewById(R.id.imageButton_lainnya);
        imageButton_hobi = (ImageButton) findViewById(R.id.imageButton_hobi);

        Intent intent = getIntent();
        userCodeBuyer = intent.getStringExtra(LoginActivity.EXTRA_BUYERCODE);
        if (userCodeBuyer.isEmpty()){
            userCodeBuyer = intent.getStringExtra(ChangeNameBuyerActivity.EXTRA_BUYERCODE);
        }
        if (userCodeBuyer.isEmpty()){
            userCodeBuyer = intent.getStringExtra(ChangePasswordBuyerActivity.EXTRA_BUYERCODE);
        }

        if (userCodeBuyer.isEmpty()){
            userCodeBuyer = intent.getStringExtra(CheckoutBuyerActivity.EXTRA_BUYERCODE);
        }

        databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
        databaseReferenceBuyer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Buyer buyer = item.getValue(Buyer.class);
                    if (buyer.getUserName().equals(userCodeBuyer)){
                        name = buyer.getName();
                        address = buyer.getAddress();
                    }
                }

                textView_buyerName.setText(name);
                textView_address_buyer.setText(address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error + "", Toast.LENGTH_SHORT).show();
            }
        });

        imageButton_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileBuyer();
            }
        });

        imageButton_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartBuyer();
            }
        });

        textView_button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutBuyer();
            }
        });

        imageButton_bahanPokok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "bahan pokok";
                enterCategory(category);
            }
        });

        imageButton_pakaian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "pakaian dan asesoris";
                enterCategory(category);
            }
        });

        imageButton_peralatanRumahTangga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "peralatan rumah tangga";
                enterCategory(category);
            }
        });

        imagebutton_kesehatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "kesehatan";
                enterCategory(category);
            }
        });

        imageButton_hobi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "Hobi";
                enterCategory(category);
            }
        });

        imageButton_lainnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "lainnya";
                enterCategory(category);
            }
        });
    }

    private void enterCategory(String category) {
        if (category.equalsIgnoreCase("bahan pokok")){
            Intent intentCaregoryBahanPokok = new Intent(id.ac.ukdw.lansmart.buyer.HomeBuyerActivity.this, HomeBuyerCategoryActivity.class);
            intentCaregoryBahanPokok.putExtra(EXTRA_CATEGORY, category);
            intentCaregoryBahanPokok.putExtra(EXTRA_BUYERCODE, userCodeBuyer);
            startActivity(intentCaregoryBahanPokok);
        }
        else if (category.equalsIgnoreCase("Pakaian")){
            Intent intentCaregoryPakaian = new Intent(id.ac.ukdw.lansmart.buyer.HomeBuyerActivity.this, HomeBuyerCategoryActivity.class);
            intentCaregoryPakaian.putExtra(EXTRA_CATEGORY, category);
            intentCaregoryPakaian.putExtra(EXTRA_BUYERCODE, userCodeBuyer);
            startActivity(intentCaregoryPakaian);
        }
        else if (category.equalsIgnoreCase("peralatan rumah tangga")){
            Intent intentCaregoryPeralatanRumahTangga = new Intent(id.ac.ukdw.lansmart.buyer.HomeBuyerActivity.this, HomeBuyerCategoryActivity.class);
            intentCaregoryPeralatanRumahTangga.putExtra(EXTRA_CATEGORY, category);
            intentCaregoryPeralatanRumahTangga.putExtra(EXTRA_BUYERCODE, userCodeBuyer);
            startActivity(intentCaregoryPeralatanRumahTangga);
        }
        else if (category.equalsIgnoreCase("kesehatan")){
            Intent intentCaregoryKesehatan = new Intent(id.ac.ukdw.lansmart.buyer.HomeBuyerActivity.this, HomeBuyerCategoryActivity.class);
            intentCaregoryKesehatan.putExtra(EXTRA_CATEGORY, category);
            intentCaregoryKesehatan.putExtra(EXTRA_BUYERCODE, userCodeBuyer);
            startActivity(intentCaregoryKesehatan);
        }
        else if (category.equalsIgnoreCase("-")){
            Intent intentCaregory = new Intent(id.ac.ukdw.lansmart.buyer.HomeBuyerActivity.this, HomeBuyerCategoryActivity.class);
            intentCaregory.putExtra(EXTRA_CATEGORY, category);
            intentCaregory.putExtra(EXTRA_BUYERCODE, userCodeBuyer);
            startActivity(intentCaregory);
        }
        else {
            Intent intentCaregoryLainnya = new Intent(id.ac.ukdw.lansmart.buyer.HomeBuyerActivity.this, HomeBuyerCategoryActivity.class);
            intentCaregoryLainnya.putExtra(EXTRA_CATEGORY, category);
            intentCaregoryLainnya.putExtra(EXTRA_BUYERCODE, userCodeBuyer);
            startActivity(intentCaregoryLainnya);
        }
    }

    private void logoutBuyer() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Apakah Anda Ingin Keluar?");
        alert.setCancelable(true);
        alert.setNegativeButton("Keluar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentlogout = new Intent(id.ac.ukdw.lansmart.buyer.HomeBuyerActivity.this, LoginActivity.class);
                startActivity(intentlogout);
            }
        });

        alert.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    public void profileBuyer(){
        Intent intentProfileBuyer = new Intent(id.ac.ukdw.lansmart.buyer.HomeBuyerActivity.this, ProfileBuyerActivity.class);
        intentProfileBuyer.putExtra(EXTRA_BUYERCODE, userCodeBuyer);
        startActivity(intentProfileBuyer);
    }

    public void cartBuyer(){
        Intent intentCartBuyer = new Intent(id.ac.ukdw.lansmart.buyer.HomeBuyerActivity.this, CartBuyerActivity.class);
        intentCartBuyer.putExtra(EXTRA_BUYERCODE, userCodeBuyer);
        startActivity(intentCartBuyer);
    }

    @Override
    public void onBackPressed() {
        logoutBuyer();
    }
}
