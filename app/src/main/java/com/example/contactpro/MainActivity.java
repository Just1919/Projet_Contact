package com.example.contactpro;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnup;
    Button btnsignup;
    EditText nom;
    EditText prenom;
    EditText tel;
    EditText email;
    EditText password;
    EditText service;
    LinearLayout login;
    LinearLayout sign;
    LinearLayout success;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    FirebaseUser currentUser;
    private EditText username, password_log;
    private Button btnlog;
    private Button btnsuccess;
    private TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Reccuperation des bouttons
        btnup=(Button)findViewById(R.id.btnup);
        btnsignup=(Button)findViewById(R.id.btnsignup);
        btnlog=(Button)findViewById(R.id.btnlog);
        btnsuccess=(Button)findViewById(R.id.btnsuccess);
        //Reccuperation des champs de pour l'inscription
        nom=(EditText)findViewById(R.id.nom);
        prenom=(EditText)findViewById(R.id.prenom);
        tel=(EditText)findViewById(R.id.tel) ;
        email=(EditText)findViewById(R.id.email) ;
        password=(EditText)findViewById(R.id.password) ;
        service=(EditText)findViewById(R.id.service) ;
        //Reccuperation des layouts
        login=findViewById(R.id.login_layout);
        sign=findViewById(R.id.sign_layout);
        success=findViewById(R.id.success_layout);
        //instances firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        //mettre les boutons en ecoute
        btnup.setOnClickListener(this);
        btnsignup.setOnClickListener(this);
        btnlog.setOnClickListener(this);
        btnsuccess.setOnClickListener(this);
        ///
        username = findViewById(R.id.username);
        password_log = findViewById(R.id.password_log);
        signupRedirectText = findViewById(R.id.signupRedirectText);
    }
    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser!=null){
            sign.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
            success.setVisibility(View.VISIBLE);
        }
        else{
            sign.setVisibility(View.GONE);
            success.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
        }
    }
    private  void createAccount() {
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        if (!mail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            if (!pass.isEmpty()) {
                mAuth.createUserWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    //creation du  compte dans firestore
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("nom", nom.getText().toString());
                                    user.put("prenom)", prenom.getText().toString());
                                    user.put("tel", tel.getText().toString());
                                    user.put("email", email.getText().toString());
                                    user.put("password", password.getText().toString());
                                    user.put("service", service.getText().toString());
                                    DocumentReference docRef = db.collection("users").document(email.getText().toString());
                                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        Toast.makeText(MainActivity.this, "Sorry,this user exists", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        String myId = docRef.getId();
                                                        db.collection("users").document(email.getText().toString())
                                                                .set(user)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(MainActivity.this, "Account create with Success", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(MainActivity.this, "Account create Failed", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MainActivity.this, "Failed document", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    // fin de la creation du compte dans firestore
                                    Log.d(TAG, "createUserWithEmail:success");
                                    currentUser = mAuth.getCurrentUser();
                                    updateUI(currentUser);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });
            } else {
                password.setError("Empty fields are not allowed");
            }
        } else if (mail.isEmpty()) {
            email.setError("Empty fields are not allowed");
        } else {
            email.setError("Please enter correct email");
        }
    }
    private void login() {
        String mail = username.getText().toString();
        String pass = password_log.getText().toString();
        if (!mail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            if (!pass.isEmpty()) {
                mAuth.signInWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    currentUser = mAuth.getCurrentUser();
                                    updateUI(currentUser);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });
            } else {
                password_log.setError("Empty fields are not allowed");
            }
        } else if (mail.isEmpty()) {
            username.setError("Empty fields are not allowed");
        } else {
            username.setError("Please enter correct email");
        }
    }
    public void onClick(View view){
        switch(view.getId()){
            case R.id.btnsignup:
                createAccount();
                break;
            case R.id.btnup:
                login.setVisibility(View.GONE);
                success.setVisibility(View.GONE);
                sign.setVisibility(View.VISIBLE);
                break;
            case R.id.btnlog:
                login();
                break;
            case R.id.btnsuccess:
                Intent home = new Intent(MainActivity.this, Home.class);
                startActivity(home);
                break;
        }
    }
}