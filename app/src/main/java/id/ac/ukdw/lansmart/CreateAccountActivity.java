package id.ac.ukdw.lansmart;

import android.os.Bundle;
import android.view.View;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import id.ac.ukdw.lansmart.constructor.Buyer;
import id.ac.ukdw.lansmart.constructor.Seller;
import id.ac.ukdw.lansmart.constructor.User;

public class CreateAccountActivity extends AppCompatActivity {
    public static final String EXTRA_BUYERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERCODE";
    public static final String EXTRA_BUYERNAME = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERNAME";
    public static final String EXTRA_BUYERPASSWORD = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERPASSWORD";

    private EditText editText_userName, editText_name, editText_password, editText_repassword;
    private Button button_create;
    private RadioButton radioButton_buyer, radioButton_seller;

    private String userName, name, password, repassword;
    private int role = 0;

    private DatabaseReference databaseReferenceSeller, databaseReferenceBuyer, databaseReferenceUser;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public CreateAccountActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        editText_userName = (EditText) findViewById(R.id.editText_create_accountname);
        editText_name = (EditText) findViewById(R.id.editText_create_name);
        editText_password = (EditText) findViewById(R.id.editText_create_password);
        editText_repassword = (EditText) findViewById(R.id.editText_create_repassword);
        button_create = (Button) findViewById(R.id.button_submit_create_account);
        radioButton_buyer = (RadioButton) findViewById(R.id.radiobutton_buyer_create_account);
        radioButton_seller = (RadioButton) findViewById(R.id.radiobutton_seller_create_account);

        radioButton_buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                role = 1;
            }
        });

        radioButton_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                role = 2;
            }
        });

        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = editText_userName.getText().toString();
                name = editText_name.getText().toString();
                password = editText_password.getText().toString();
                repassword = editText_repassword.getText().toString();

                addData(role,userName,name,password,repassword);
            }
        });
    }

    public void addData(int role, String userName, String name, String password, String repassword){
        databaseReferenceSeller = db.getReference(Seller.class.getSimpleName());
        databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
        databaseReferenceUser = db.getReference(User.class.getSimpleName());

        boolean cekPass = false;

        if(userName.isEmpty()||name.isEmpty()||password.isEmpty()||repassword.isEmpty()){
            Toast.makeText(CreateAccountActivity.this, "Semua isian harus diisi!", Toast.LENGTH_SHORT).show();
        }
        else if(role == 0){
            Toast.makeText(CreateAccountActivity.this, "Mohon pilih jenis akun terlebih dahulu!", Toast.LENGTH_SHORT).show();
        }
        else{
            if (password.equals(repassword)){
                cekPass = true;
            }
            else{
                Toast.makeText(CreateAccountActivity.this, "Sandi dan Ulang Sandi tidak sama!", Toast.LENGTH_SHORT).show();
            }

            if (cekPass==true){
                if(role == 1){
                    Intent intent = new Intent(id.ac.ukdw.lansmart.CreateAccountActivity.this, CreateAccountBuyerActivity.class);
                    intent.putExtra(EXTRA_BUYERCODE, userName);
                    intent.putExtra(EXTRA_BUYERNAME, name);
                    intent.putExtra(EXTRA_BUYERPASSWORD, password);
                    startActivity(intent);
                }
                else if(role == 2){
                    User seller = new Seller(userName, name, password, userName, userName);
                    User user = new User(userName, name, password, userName, userName, 2);

                    databaseReferenceUser.push().setValue(user).addOnFailureListener(er->{
                        Toast.makeText(CreateAccountActivity.this, ""+er.getMessage(),Toast.LENGTH_SHORT).show();
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReferenceSeller.push().setValue(seller).addOnSuccessListener(suc->{
                                Toast.makeText(CreateAccountActivity.this,"Akun Penjual Terbuat!", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(er->{
                                Toast.makeText(CreateAccountActivity.this, ""+er.getMessage(),Toast.LENGTH_SHORT).show();
                            });

                            Intent intentLogin = new Intent(CreateAccountActivity.this, LoginActivity.class);
                            startActivity(intentLogin);
                        }
                    });
                }
            }
        }

    }
}