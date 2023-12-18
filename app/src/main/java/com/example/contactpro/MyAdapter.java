package com.example.contactpro;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.contactpro.Contact;
import com.example.contactpro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.LinkedList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private  Context context;
    private LinkedList<Contact> contacts;
    public static Contact currentcontact;


    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(LinkedList<Contact> contacts, Context context) {
        this. contacts = new LinkedList<Contact>() ;
        this. contacts.addAll( contacts );
        this.context=context;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
// create a new view
        View itemLayoutView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_layout,
                        parent, false);
        MyViewHolder vh = new MyViewHolder(itemLayoutView);
        return vh;
    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.contact=contacts.get(position);
        // - get element from your dataset at this position
// - replace the contents of the view with that element
        holder.identification.setText(contacts.get(position).getPrenom()+" "+contacts.get(position).getNom());
// Reference to an image file in Cloud Storage
        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference(contacts.get(position).getUrl());
// Download directly from StorageReference using Glide
// (See MyAppGlideModule for Loader registration)
        Glide.with(context /* context */)
                .load(storageReference)
                .into(holder.photo);

  }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return contacts.size();
    }
    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        public TextView identification;
        public ImageView photo;
        Contact contact;
        // Context is a reference to the activity that contain the the recycler view
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            identification =itemLayoutView.findViewById(R.id.identification);
            photo= itemLayoutView.findViewById(R.id.contact_photo);
            itemLayoutView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            Intent myintent= new Intent(v.getContext(), Detail.class);
            myintent.putExtra("contact", contact);
            v.getContext().startActivity(myintent);
        }
    }
}