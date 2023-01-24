package id.ac.ukdw.lansmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import id.ac.ukdw.lansmart.admin.HomeAdminActivity;
import id.ac.ukdw.lansmart.buyer.HomeBuyerActivity;
import id.ac.ukdw.lansmart.seller.homeseller.HomeSellerActivity;
import id.ac.ukdw.lansmart.constructor.Buyer;
import id.ac.ukdw.lansmart.constructor.Seller;
import id.ac.ukdw.lansmart.constructor.User;

public class LoginActivity extends AppCompatActivity {
    public static final String EXTRA_ADMINCODE = "id.ac.udkw.rockpaperscissors.EXTRA_ADMINCODE";
    public static final String EXTRA_SELLERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_SELLERCODE";
    public static final String EXTRA_BUYERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERCODE";
    private EditText editText_username, editText_password;
    private TextView textView_create_account;
    private Button button_submit;

    private String username, password;
    private boolean check = false;
    private int backButtonCount = 0;

    private DatabaseReference databaseReferenceUser, databaseReferenceSeller, databaseReferenceBuyer;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_username = (EditText) findViewById(R.id.editText_username_login);
        editText_password = (EditText) findViewById(R.id.editText_password_login);
        textView_create_account = (TextView) findViewById(R.id.textView_create_account);
        button_submit = (Button) findViewById(R.id.button_submit_login);

        textView_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editText_username.getText().toString();
                password = editText_password.getText().toString();
                checklogin(username,password);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Tekan Sekali Lagi Untuk Menutup Aplikasi.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    public void checklogin(String username, String password){
        databaseReferenceUser = db.getReference(User.class.getSimpleName());
        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()){
                    User user = item.getValue(User.class);
                    if(user.getUserName().equals(username) && user.getPassword().equals(password)){
                        check = true;
                        if(user.getRole() == 1){
                            loginAdmin(username);
                            break;
                        }
                        else if(user.getRole() == 2){
                            loginSeller(username);
                            break;
                        }
                        else if(user.getRole() == 3){
                            loginBuyer(username);
                            break;
                        }
                    }
                    else{
                        check = false;
                    }
                }

                if (check == false){
                    Toast.makeText(getApplicationContext(),"Nama Akun Atau Sandi Anda Salah, Silahkan Coba Kembali.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"" + error,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loginAdmin(String username){
        Intent intentAdmin = new Intent(LoginActivity.this, HomeAdminActivity.class);
        Toast.makeText(getApplicationContext(),"Berhasil Masuk",Toast.LENGTH_SHORT).show();
        intentAdmin.putExtra(EXTRA_ADMINCODE, username);
        startActivity(intentAdmin);
    }

    public void loginSeller(String username){
        databaseReferenceSeller = db.getReference(Seller.class.getSimpleName());
        databaseReferenceSeller.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()){
                    Seller seller = item.getValue(Seller.class);
                    if (seller.getUserName().equals(username)){
                        if (seller.getStatus().toLowerCase().equals("aktif")){
                            Intent intentSeller = new Intent(LoginActivity.this, HomeSellerActivity.class);
                            intentSeller.putExtra(EXTRA_SELLERCODE, username);
                            Toast.makeText(getApplicationContext(),"Berhasil Masuk",Toast.LENGTH_SHORT).show();
                            startActivity(intentSeller);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Akun Penjual Tidak Aktif!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error + "",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loginBuyer(String username){
        databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
        databaseReferenceBuyer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Buyer buyer = item.getValue(Buyer.class);
                    if (buyer.getUserName().equals(username)){
                        if (buyer.getStatus().toLowerCase().equals("aktif")){
                            Intent intentBuyer = new Intent(LoginActivity.this, HomeBuyerActivity.class);
                            intentBuyer.putExtra(EXTRA_BUYERCODE, username);
                            Toast.makeText(getApplicationContext(),"Berhasil Masuk",Toast.LENGTH_SHORT).show();
                            startActivity(intentBuyer);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Akun Pembeli Tidak Aktif!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error + "",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createNewAccount(){
        Intent intentNewAccount = new Intent(LoginActivity.this, CreateAccountActivity.class);
        startActivity(intentNewAccount);
    }
}