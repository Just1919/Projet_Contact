package com.example.contactpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Update extends AppCompatActivity implements View.OnClickListener{
    EditText prenom;
    EditText nom;
    EditText tel;
    Button update;
    EditText email;
    EditText service;
    String url="photo/image.jpeg";
    Contact contact;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        update= (Button) findViewById(R.id.update);
        nom=(EditText)findViewById(R.id.nom);
        prenom=(EditText)findViewById(R.id.prenom);
        tel=(EditText)findViewById(R.id.tel) ;
        email=(EditText)findViewById(R.id.email) ;
        service=(EditText)findViewById(R.id.service) ;
        update.setOnClickListener(this);
        contact= (Contact) getIntent().getSerializableExtra("contact");
        nom.setText(contact.getNom());
        prenom.setText(contact.getPrenom());
        tel.setText(contact.getTel());
        email.setText(contact.getEmail());
        service.setText(contact.getService());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

    }
    public void update(){
        contact.setNom(nom.getText().toString());
        contact.setPrenom(prenom.getText().toString());
        contact.setTel(tel.getText().toString());
        contact.setEmail(email.getText().toString());
        contact.setService(service.getText().toString());
        contact.setUrl(url);
        DocumentReference docRef=db.collection("users").document(currentUser.getEmail())
                .collection("contacts").document(tel.getText().toString());
        docRef.set(contact).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid){
                        Toast.makeText(Update.this, "Updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Update.this, "Failed ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onClick(View view){
        if(view.getId()==R.id.update){
            update();
            Intent detailIntent=new Intent(Update.this,Detail.class);
            detailIntent.putExtra("contact",contact);
            startActivity(detailIntent);
        }

    }
}