package id.ac.ukdw.lansmart.buyer.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import id.ac.ukdw.lansmart.R;
import id.ac.ukdw.lansmart.buyer.HomeBuyerActivity;
import id.ac.ukdw.lansmart.constructor.Buyer;
import id.ac.ukdw.lansmart.constructor.User;

public class ChangePasswordBuyerActivity extends AppCompatActivity {
    public static final String EXTRA_BUYERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERCODE";
    private EditText editText_passOld, editText_passNew, editText_repass;
    private Button button_change;

    private String buyerCode, passOld1, passOld2, passNew, repass, buyerKey;

    private DatabaseReference databaseReferenceBuyer, databaseReferenceUser;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_buyer);

        editText_passOld = (EditText) findViewById(R.id.editText_old_pass_buyer);
        editText_passNew = (EditText) findViewById(R.id.editText_new_pass_buyer);
        editText_repass = (EditText) findViewById(R.id.editText_new_pass_buyer2);
        button_change = (Button) findViewById(R.id.button_change_password_profile_buyer);

        Intent intent = getIntent();
        buyerCode = intent.getStringExtra(ProfileBuyerActivity.EXTRA_BUYERCODE);

        databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
        databaseReferenceBuyer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Buyer buyer = item.getValue(Buyer.class);
                    if (buyer.getUserName().equals(buyerCode)) {
                        buyerKey = item.getKey();
                        passOld1 = buyer.getPassword();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass(buyerCode, buyerKey);
            }
        });
    }

    public void changePass(String buyerCode, String buyerKey){
        passOld2 = editText_passOld.getText().toString();
        passNew = editText_passNew.getText().toString();
        repass = editText_repass.getText().toString();

        if (passOld2.isEmpty()){
            Toast.makeText(getApplicationContext(),"Old Password Must Filled", Toast.LENGTH_SHORT).show();
        }
        else if (passNew.isEmpty()){
            Toast.makeText(getApplicationContext(),"New Password Must Filled", Toast.LENGTH_SHORT).show();
        }
        else if (repass.isEmpty()){
            Toast.makeText(getApplicationContext(),"Rewrite New Password Must Filled", Toast.LENGTH_SHORT).show();
        }
        else{
            if (passNew.equals(repass)){
                if (passOld1.equals(passOld2)){
                    databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
                    databaseReferenceBuyer.child(buyerKey).child("password").setValue(passNew).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReferenceUser = db.getReference(User.class.getSimpleName());
                            databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot item : snapshot.getChildren()){
                                        User user = item.getValue(User.class);
                                        if (user.getUserName().equals(buyerCode)){
                                            user.setPassword(passNew);
                                            databaseReferenceUser.child(item.getKey()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(),"Password Has Been Change", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(ChangePasswordBuyerActivity.this, HomeBuyerActivity.class);
                                                    intent.putExtra(EXTRA_BUYERCODE, buyerCode);
                                                    startActivity(intent);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(),e + "Password Didn't Change", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),e + ", Password Didn't Change", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(),"Old Password Incorrect", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"New Password And Rewrite New Password Did't Match", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
