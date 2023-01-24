package id.ac.ukdw.lansmart.buyer.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.constructor.Buyer;

public class ProfileBuyerActivity extends AppCompatActivity {
    public static final String EXTRA_BUYERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERCODE";
    private TextView textView_buyerId, textView_buyerName, textView_invoiceLimit, textView_address;
    private Button button_editProfile, button_changePass, button_editAddress;

    private String buyerCode, name, address;

    private DatabaseReference databaseReferenceBuyer;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_buyer);

        textView_buyerId = (TextView) findViewById(R.id.textView_id_profile_buyer);
        textView_buyerName = (TextView) findViewById(R.id.textView_name_profile_buyer);
        textView_address = (TextView) findViewById(R.id.textView_address_profile_buyer);
        button_editProfile = (Button) findViewById(R.id.button_edit_name_buyer);
        button_changePass = (Button) findViewById(R.id.button_change_password_buyer);
        button_editAddress = (Button) findViewById(R.id.button_edit_address_buyer);

        Intent intent = getIntent();
        buyerCode = intent.getStringExtra(id.ac.ukdw.lansmart.buyer.HomeBuyerActivity.EXTRA_BUYERCODE);

        databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
        databaseReferenceBuyer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Buyer buyer = item.getValue(Buyer.class);
                    if (buyer.getUserName().equals(buyerCode)){
                        name = buyer.getName();
                        address = (buyer.getAddress() + " " +buyer.getCodePos()).toString();
                    }
                }
                textView_buyerId.setText(buyerCode);
                textView_buyerName.setText(name);
                textView_address.setText(address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });

        button_changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPassword();
            }
        });

        button_editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAddress();
            }
        });
    }

    public void editProfile(){
        Intent intentEditProfile = new Intent(ProfileBuyerActivity.this, ChangeNameBuyerActivity.class);
        intentEditProfile.putExtra(EXTRA_BUYERCODE,buyerCode);
        startActivity(intentEditProfile);
    }

    public void editPassword(){
        Intent intentEditPass = new Intent(ProfileBuyerActivity.this, ChangePasswordBuyerActivity.class);
        intentEditPass.putExtra(EXTRA_BUYERCODE,buyerCode);
        startActivity(intentEditPass);
    }

    public void editAddress(){
        Intent intentEditAddress = new Intent(ProfileBuyerActivity.this, ChangeAddressBuyerActivity.class);
        intentEditAddress.putExtra(EXTRA_BUYERCODE,buyerCode);
        startActivity(intentEditAddress);
    }
}
