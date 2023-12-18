package com.example.contactpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class New extends AppCompatActivity implements View.OnClickListener{
 EditText prenom;
 EditText nom;
 EditText tel;
 Button add;
 EditText service;
 String url="photo/image.jpeg";
 EditText nemail;

 private FirebaseFirestore db;
 private FirebaseAuth mAuth;
 private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        add= (Button) findViewById(R.id.add);
        nom=(EditText)findViewById(R.id.nom);
        prenom=(EditText)findViewById(R.id.prenom);
        tel=(EditText)findViewById(R.id.tel) ;
        service=(EditText)findViewById(R.id.service) ;
        nemail=(EditText)findViewById(R.id.email) ;
        //url=(EditText)findViewById(R.id.url) ;
        add.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void createContact() {
        // Create a new contact
        currentUser = mAuth.getCurrentUser();
        Map<String, Object> contact = new HashMap<>();
        contact.put("nom",nom.getText().toString());
        contact.put("prenom",prenom.getText().toString());
        contact.put("tel",tel.getText().toString());
        contact.put("email",nemail.getText().toString());
        contact.put("service",service.getText().toString());
        contact.put("url",url);
        contact.put("favori",false);
        DocumentReference docRef=db.collection("users").document(currentUser.getEmail())
                        .collection("contacts").document(tel.getText().toString());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                        {
                            Toast.makeText(New.this, "Sorry,this contact exists", Toast.LENGTH_SHORT).show();
                        }else{
                            String myId = docRef.getId();
                            db.collection("users").document(currentUser.getEmail())
                                    .collection("contacts").document(tel.getText().toString())
                                    .set(contact)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(New.this, "Success", Toast.LENGTH_SHORT).show();
                                            Intent home = new Intent(New.this, Home.class);
                                            startActivity(home);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(New.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(New.this, "Failed document", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.add:
              createContact();
                break;
        }
    }
}