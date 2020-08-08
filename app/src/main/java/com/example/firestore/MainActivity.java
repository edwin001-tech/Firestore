package com.example.firestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private EditText enterTitle;
    private EditText enterThought;
    private Button saveButton;
    private TextView recTitle;
    private TextView recThought;
    private Button showButton;
    private Button updateButton;
    private Button deleteAll;


    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//  private DocumentReference journalRef = db.document("Journal/First Thoughts");   short hand

    private DocumentReference journalRef = db.collection("Journal")
            .document("First Thoughts");

    //Keys
    public static final String KEY_TITLE = "title";
    public static final String KEY_THOUGHT = "thought";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enterTitle = findViewById(R.id.edit_text_title);
        enterThought = findViewById(R.id.edit_text_thoughts);
        saveButton = findViewById(R.id.save_button);
        showButton = findViewById(R.id.show_data);
        recTitle = findViewById(R.id.rec_title);
        recThought = findViewById(R.id.rec_thoughts);
        updateButton = findViewById(R.id.update_data);
        deleteAll = findViewById(R.id.delete_all);

        updateButton.setOnClickListener(this);
        deleteAll.setOnClickListener(this);


        //Retrieve data from our collection
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                journalRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                               if (documentSnapshot.exists()){
//                                   String title = documentSnapshot.getString(KEY_TITLE);
//                                   String thought = documentSnapshot.getString(KEY_THOUGHT);

                                   Journal journal = documentSnapshot.toObject(Journal.class);

                                   recTitle.setText(journal.getTitle());
                                   recThought.setText(journal.getThought());


                               }
                                else {
                                    Toast.makeText(MainActivity.this,
                                             "no data exists",
                                            Toast.LENGTH_LONG)
                                            .show();

                               }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.toString());

                            }
                        });

            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = enterTitle.getText().toString().trim();
                String thought = enterThought.getText().toString().trim();

                Journal journal = new Journal();
                journal.setTitle(title);
                journal.setThought(thought);

//                Map<String, Object> data = new HashMap<>();
//                data.put(KEY_TITLE, title);
//                data.put(KEY_THOUGHT, thought);


                journalRef .set(journal)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG)
                                        .show();

                            }



                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.toString());
                            }
                        });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        journalRef.addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(MainActivity.this, "something went wrong",
                            Toast.LENGTH_LONG)
                            .show();

                }

                if (value != null && value.exists()){
                    String title = value.getString(KEY_TITLE);
                    String thought = value.getString(KEY_THOUGHT);

                    recTitle.setText(title);
                    recThought.setText(thought);

                    Journal journal = value.toObject(Journal.class);

                    recTitle.setText(journal.getTitle());
                    recThought.setText(journal.getThought());

                }else {
                    recTitle.setText("");
                    recThought.setText("");
                }

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.update_data:
                //call update
                updateMyData();
                break;
            case R.id.delete_all:
                //call delete
                deleteAll();
                break;
        }
    }

    private void deleteAll(){
        journalRef.delete();
    }

    private void deleteMyThought() {
        journalRef.update(KEY_THOUGHT, FieldValue.delete());

    }

    private void updateMyData() {
        String title = enterTitle.getText().toString().trim();
        String thought = enterThought.getText().toString().trim();

        Map<String, Object> data = new HashMap<>();
        data.put(KEY_TITLE, title);
        data.put(KEY_THOUGHT, thought);

        journalRef.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Updated!",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
