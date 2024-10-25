package com.upn.contactsapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upn.contactsapp.entities.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FirebaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        Button btn = findViewById(R.id.btnCreateOnFirebase);
        EditText etName = findViewById(R.id.etName);



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference contactRef = database.getReference("N00264477").child("contacts");

        List<Contact> contacts = new ArrayList<>();
        contactRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    Contact c = child.getValue(Contact.class);
                    contacts.add(c);
                    Log.i("MAIN-APP", c.uuid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



//        btn.setOnClickListener(v -> {
//            // Write a message to the database
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference myRef = database.getReference("N00264477");
//            DatabaseReference table = myRef.child("contacts");
//
//
//            String name = etName.getText().toString();
//
//            Contact c1 = new Contact("Edward", "14556123124");
//            c1.uuid = UUID.randomUUID().toString();
//            table.child(c1.uuid).setValue(c1);
//            etName.setText("");
//
//            Toast.makeText(FirebaseActivity.this, "Se creó el contacto en Firebase", Toast.LENGTH_SHORT).show();
//
//       });

    }
}