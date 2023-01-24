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
import id.ac.ukdw.lansmart.constructor.Buyer;
import id.ac.ukdw.lansmart.constructor.User;

public class ChangeNameBuyerActivity extends AppCompatActivity {
    public static final String EXTRA_BUYERCODE = "id.ac.udkw.rockpaperscissors.EXTRA_BUYERCODE";
    private EditText editText_name;
    private Button button_edit;

    private String buyerCode, name;

    private DatabaseReference databaseReferenceBuyer, databaseReferenceUser;
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name_buyer);

        editText_name = (EditText) findViewById(R.id.editText_edit_name_profile_buyer);
        button_edit = (Button) findViewById(R.id.button_edit_name_profile_buyer);

        Intent intent = getIntent();
        buyerCode = intent.getStringExtra(ProfileBuyerActivity.EXTRA_BUYERCODE);

        databaseReferenceBuyer = db.getReference(Buyer.class.getSimpleName());
        databaseReferenceBuyer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Buyer buyer = item.getValue(Buyer.class);
                    if (buyer.getUserName().equals(buyerCode)){
                        editText_name.setText(buyer.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error + "", Toast.LENGTH_SHORT).show();
            }
        });

        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editText_name.getText().toString();

                databaseReferenceBuyer.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()){
                            Buyer buyer = item.getValue(Buyer.class);
                            if (buyer.getUserName().equals(buyerCode)){
                                buyer.setName(name);
                                databaseReferenceBuyer.child(item.getKey()).setValue(buyer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        databaseReferenceUser = db.getReference(User.class.getSimpleName());
                                        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot item : snapshot.getChildren()){
                                                    User user = item.getValue(User.class);
                                                    if (user.getUserName().equals(buyerCode)){
                                                        user.setName(name);
                                                        databaseReferenceUser.child(item.getKey()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(getApplicationContext(),"Nama Sudah Diganti", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(ChangeNameBuyerActivity.this, id.ac.ukdw.lansmart.buyer.HomeBuyerActivity.class);
                                                                intent.putExtra(EXTRA_BUYERCODE, buyerCode);
                                                                startActivity(intent);
                                                            }
                                                        });
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
                                        Toast.makeText(getApplicationContext(),e + ", Nama Tidak Bisa Diubah!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(),error + "", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
