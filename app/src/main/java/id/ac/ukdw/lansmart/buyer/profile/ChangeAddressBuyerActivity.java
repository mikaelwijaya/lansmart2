package id.ac.ukdw.lansmart.buyer.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import id.ac.ukdw.lansmart.CreateAccountActivity;
import id.ac.ukdw.lansmart.LoginActivity;
import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.constructor.Buyer;
import id.ac.ukdw.lansmart.constructor.User;

public class ChangeAddressBuyerActivity extends AppCompatActivity {
    private TextView textView_top;
    private EditText editText_street, editText_codePos;
    private Button button_create;

    private String buyerKey, userName, address, codePos;
    private int role = 3;

    private DatabaseReference databaseReferenceBuyer;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_buyer);

        textView_top = (TextView) findViewById(R.id.textView_top_address_buyer);
        editText_street = (EditText) findViewById(R.id.editText_create_buyer_street_address);
        editText_codePos = (EditText) findViewById(R.id.editText_create_buyer_codepos_address);
        button_create = (Button) findViewById(R.id.button_submit_create_account_buyer);

        Intent intent = getIntent();
        userName = intent.getStringExtra(ProfileBuyerActivity.EXTRA_BUYERCODE);

        textView_top.setText("UBAH ALAMAT");
        textView_top.setTextColor(Color.BLACK);

        databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
        databaseReferenceBuyer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Buyer buyer = item.getValue(Buyer.class);
                    if (buyer.getUserName().equals(userName)) {
                        address = buyer.getAddress();
                        codePos = buyer.getCodePos();

                    }
                }
                editText_street.setText(address);
                editText_codePos.setText(codePos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = editText_street.getText().toString();
                codePos = editText_codePos.getText().toString();

                if (codePos.length() == 6){
                    databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
                    databaseReferenceBuyer.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot item : snapshot.getChildren()) {
                                Buyer buyer = item.getValue(Buyer.class);
                                if (buyer.getUserName().equals(userName)) {
                                    buyerKey = item.getKey();
                                    buyer.setAddress(address);
                                    buyer.setCodePos(codePos);
                                }
                            }
                            databaseReferenceBuyer.child(buyerKey).child("address").setValue(address).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ChangeAddressBuyerActivity.this, "Alamat Sudah Terganti", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ChangeAddressBuyerActivity.this, "Alamat Tidak Terganti!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChangeAddressBuyerActivity.this, "Alamat Tidak Terganti!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(ChangeAddressBuyerActivity.this,"Masukkan kode pos dengan benar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}