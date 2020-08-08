package com.example.firestore;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText enterTitle;
    private EditText enterThought;
    private Button saveButton;
    private TextView recTitle;
    private TextView recThought;
    private Button showButton;


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


        //Retrieve data from our collection
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                journalRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                               if (documentSnapshot.exists()){
                                   String title = documentSnapshot.getString(KEY_TITLE);
                                   String thought = documentSnapshot.getString(KEY_THOUGHT);

                                   recTitle.setText(title);
                                   recThought.setText(thought);


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

                Map<String, Object> data = new HashMap<>();
                data.put(KEY_TITLE, title);
                data.put(KEY_THOUGHT, thought);


                journalRef .set(data)
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
}