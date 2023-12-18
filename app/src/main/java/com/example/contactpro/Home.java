package com.example.contactpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.LinkedList;

public class Home extends AppCompatActivity implements View.OnClickListener{
    TextView username;
    private FloatingActionButton fab_add;
    private ImageButton logout;
    private Button gout;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    LinkedList<Contact> contacts;
    LinkedList<Contact> allContacts; // Added attribute for storing all contacts
    RecyclerView contactsRecycler;
    EditText searchEditText; // Added EditText for search

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fab_add=(FloatingActionButton) findViewById(R.id.fab_add);
        logout= (ImageButton) findViewById(R.id.logout);
        fab_add.setOnClickListener(this);
        logout.setOnClickListener(this);
        Bundle extras=getIntent().getExtras();
        contactsRecycler=(RecyclerView)findViewById(R.id.list_contacts);
        db = FirebaseFirestore.getInstance();
        contacts= new LinkedList<Contact>();
        allContacts = new LinkedList<Contact>();
        searchEditText = findViewById(R.id.login); // Initialize the EditText for search

        // Set up the RecyclerView
        contactsRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Home.this);
        contactsRecycler.setLayoutManager(layoutManager);
        MyAdapter myAdapter = new MyAdapter(contacts, Home.this);
        contactsRecycler.setAdapter(myAdapter);

        // Load the contacts
        getContacts();

        // Set up the search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the contacts based on the new search query
                filterContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do here
            }
        });
    }

    protected void onResume() {
        super.onResume();
        // Refresh the contacts when the activity is resumed
        getContacts();
    }

    void getContacts() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference docRef = db.collection("users").document(currentUser.getEmail());
        docRef.collection("contacts").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            contacts.clear();
                            allContacts.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Contact c = new Contact(document.get("nom").toString(), document.get("prenom").toString(), document.get("tel").toString(), document.get("email").toString(), document.get("service").toString(), document.get("url").toString(), (Boolean)document.get("favori"));
                                contacts.add(c);
                                allContacts.add(c);
                            }
                            // Update the RecyclerView with the new contacts
                            MyAdapter myAdapter = new MyAdapter(contacts, Home.this);
                            contactsRecycler.setAdapter(myAdapter);

                        } else {
                            Log.d("not ok", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void filterContacts(String query) {
        contacts.clear();

        for (Contact contact : allContacts) {
            String fullName = contact.getPrenom() + " " + contact.getNom();
            if (fullName.toLowerCase().contains(query.toLowerCase())) {
                contacts.add(contact);
            }
        }

        MyAdapter myAdapter = new MyAdapter(contacts, Home.this);
        contactsRecycler.setAdapter(myAdapter);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.fab_add:
                Intent NewContact= new Intent(Home.this, New.class);
                startActivity(NewContact);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent out= new Intent(Home.this, MainActivity.class);
                startActivity(out);
                break;
        }
    }
}
