package com.example.contactpro;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Detail extends AppCompatActivity implements View.OnClickListener {
    TextView nom;
    TextView identification;
    TextView tel;
    TextView email;
    TextView service;
    ImageView photo;
     Contact contact;
     ImageButton favori;
    ImageButton share;
     ImageButton call;
    ImageButton msg;
    ImageButton mail;
    ImageButton videocall;
    ImageButton edit;
    ImageButton delete;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        identification= (TextView) findViewById(R.id.identification);
        tel= (TextView) findViewById(R.id.tel);
        email= (TextView) findViewById(R.id.email);
        service= (TextView) findViewById(R.id.service);
        photo=(ImageView) findViewById(R.id.photo);
        favori= (ImageButton) findViewById(R.id.favori);
        share= (ImageButton) findViewById(R.id.share);
        call= (ImageButton) findViewById(R.id.call);
        msg= (ImageButton) findViewById(R.id.msg);
        mail= (ImageButton) findViewById(R.id.mail);
        videocall= (ImageButton) findViewById(R.id.videocall);
        edit= (ImageButton) findViewById(R.id.edit);
        delete= (ImageButton) findViewById(R.id.delete);

        call.setOnClickListener(this);
        mail.setOnClickListener(this);
        msg.setOnClickListener(this);
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);
        videocall.setOnClickListener(this);
        favori.setOnClickListener(this);
        share.setOnClickListener(this);

        contact= (Contact) getIntent().getSerializableExtra("contact");
        identification.setText(contact.getPrenom()+" "+contact.getNom());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        favorisCheck();

    }
    protected void onResume() {
        super.onResume();
        favorisCheck();
    }
    protected void favorisCheck() {
        if(contact.isFavori()) {
            favori.setImageResource(R.drawable.baseline_favorite_24);
        }else{
            favori.setImageResource(R.drawable.baseline_favorite_border_24);
        }
    }
    public void favoris(String knck){
        DocumentReference docRef=db.collection("users").document(currentUser.getEmail())
                .collection("contacts").document(contact.getTel());
        docRef.set(contact).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid){
                        Toast.makeText(Detail.this, knck, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Detail.this, "Failed ", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void shareContact(){
        DocumentReference docRef=db.collection("Shared_Contacts").document(contact.getTel());
        docRef.set(contact).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid){
                        Toast.makeText(Detail.this, "contact shared", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Detail.this, "Failed ", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    public void deleteContact(){
        DocumentReference docRef=db.collection("users").document(currentUser.getEmail())
                .collection("contacts").document(contact.getTel());
        docRef
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Detail.this, "Contact successfully deleted!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Detail.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onClick(View view) {
        switch(view.getId()){
            case R.id.call:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + contact.getTel()));//change the number
                startActivity(callIntent);
                break;
            case R.id.mail:
                Intent mailIntent = new Intent(Intent.ACTION_SEND);
                mailIntent.setData(Uri.parse("mailto:"));
                mailIntent.putExtra(Intent.EXTRA_EMAIL,contact.getEmail());
                Intent choosermail=Intent.createChooser(mailIntent,"Send Email");
                startActivity(choosermail);
                break;
            case R.id.msg:
                Intent msgIntent = new Intent(Intent.ACTION_VIEW);
                msgIntent.setData(Uri.parse("sms:"+ contact.getTel()));
                Intent chooser=Intent.createChooser(msgIntent,"Send SMS");
                startActivity(chooser);
                break;
            case R.id.favori:
                if(contact.isFavori()){
                    contact.setFavori(false);
                    String knck="remove to favoris";
                    favoris(knck);
                }else {
                    contact.setFavori(true);
                    String knck="add to favoris";
                    favoris(knck);
                }
                favorisCheck();
                break;
            case R.id.edit:
                Intent editIntent = new Intent(Detail.this,Update.class);
                editIntent.putExtra("contact",contact);
                startActivity(editIntent);
                break;
            case R.id.share:
                shareContact();
                break;
            case R.id.delete:
                deleteContact();
                Intent home = new Intent(Detail.this, Home.class);
                startActivity(home);
                break;
        }

    }

}