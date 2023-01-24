package id.ac.ukdw.lansmart;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import id.ac.ukdw.lansmart.constructor.Buyer;
import id.ac.ukdw.lansmart.constructor.Seller;
import id.ac.ukdw.lansmart.constructor.User;

public class CreateAccountBuyerActivity extends AppCompatActivity {
    private TextView textView_top;
    private EditText editText_street, editText_codePos;
    private Button button_create;

    private String userName, name, password, address, codePos;
    private int role = 3;

    private DatabaseReference databaseReferenceBuyer, databaseReferenceUser;
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
        userName = intent.getStringExtra(CreateAccountActivity.EXTRA_BUYERCODE);
        name = intent.getStringExtra(CreateAccountActivity.EXTRA_BUYERNAME);
        password = intent.getStringExtra(CreateAccountActivity.EXTRA_BUYERPASSWORD);



        textView_top.setText("Buat Akun Pembeli");
        textView_top.setTextColor(Color.parseColor("#F44336"));

        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = editText_street.getText().toString();
                codePos = editText_codePos.getText().toString();

                databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
                databaseReferenceUser = db.getReference(User.class.getSimpleName());
                User buyer = new Buyer(userName, name, password, userName, userName, address, codePos);
                User user = new User(userName, name, password, userName, userName, role);
                databaseReferenceUser.push().setValue(user).addOnFailureListener(er->{
                    Toast.makeText(CreateAccountBuyerActivity.this, ""+er.getMessage(),Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        databaseReferenceBuyer.push().setValue(buyer).addOnSuccessListener(suc->{
                            Toast.makeText(CreateAccountBuyerActivity.this,"Akun Pembeli Terbuat!", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(er->{
                            Toast.makeText(CreateAccountBuyerActivity.this, ""+er.getMessage(),Toast.LENGTH_SHORT).show();
                        });

                        Intent intentLogin = new Intent(CreateAccountBuyerActivity.this, LoginActivity.class);
                        startActivity(intentLogin);
                    }
                });
            }
        });
    }
}