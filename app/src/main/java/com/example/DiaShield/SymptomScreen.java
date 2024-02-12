package com.example.DiaShield;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

public class SymptomScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinnerSymptom;
    RatingBar ratingBar;
    Button btnUpload, btnSubmit;
    String spinnerSelect = "";
    float selectRat = 0;
    String usr_nme = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DB_Structure DBStructureGlobal = (DB_Structure) getIntent().getParcelableExtra("myDBObject");
        System.out.println(DBStructureGlobal + "received");
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            usr_nme = "";
        } else {
            usr_nme = extras.getString("name");
        }
        setContentView(R.layout.activity_symptoms_screen);
        spinnerSymptom = findViewById(R.id.sympSpinner);
        ratingBar = findViewById(R.id.sympRating);
        ArrayAdapter<String> spinnerAdpter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.symptoms_list));
        spinnerAdpter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinnerSymptom.setAdapter(spinnerAdpter);
        spinnerSymptom.setOnItemSelectedListener(this);
        submitSymptoms(DBStructureGlobal);
        uploadToDB(DBStructureGlobal);
    }

    public void submitSymptoms(DB_Structure DBStructureGlobal) {

        btnSubmit = findViewById(R.id.submitBtn);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRat = ratingBar.getRating();

                if (spinnerSelect.equals("Nausea")) {
                    DBStructureGlobal.setRATE_HEADACHE((int) selectRat);
                    System.out.println(DBStructureGlobal.getRATE_NAUSEA());
                }

                if (spinnerSelect.equals("Headache")) {
                    DBStructureGlobal.setRATE_HEADACHE((int) selectRat);
                    System.out.println(DBStructureGlobal.getRATE_HEADACHE());
                }

                if (spinnerSelect.equals("Diarrhea")) {
                    DBStructureGlobal.setRATE_DIARRHEA((int) selectRat);
                }

                if (spinnerSelect.equals("Sore Throat")) {
                    DBStructureGlobal.setRATE_SORE_THROAT((int) selectRat);
                }

                if (spinnerSelect.equals("Fever")) {
                    DBStructureGlobal.setRATE_FEVER((int) selectRat);
                }

                if (spinnerSelect.equals("Muscle Pain")) {
                    DBStructureGlobal.setRATE_MUSCLE_PAIN((int) selectRat);
                }

                if (spinnerSelect.equals("Loss of Smell/Taste")) {
                    DBStructureGlobal.setRATE_SMELL_TASTE((int) selectRat);
                }

                if (spinnerSelect.equals("Cough")) {
                    DBStructureGlobal.setRATE_COUGH((int) selectRat);
                }

                if (spinnerSelect.equals("Shortness of Breath")) {
                    DBStructureGlobal.setRATE_SHORTNESS_BREATH((int) selectRat);
                }

                if (spinnerSelect.equals("Tiredness")) {
                    DBStructureGlobal.setRATE_TIRED((int) selectRat);
                }
                Toast.makeText(getApplicationContext(), spinnerSelect +": "+ selectRat, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadToDB(DB_Structure DBStructureGlobal) {
        btnUpload = findViewById(R.id.uploadBtn);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Uploading to DB...", Toast.LENGTH_LONG).show();
                DB_Assist databaseAction = new DB_Assist(getApplicationContext());
                DB_Structure reading = null;
                reading = new DB_Structure(DBStructureGlobal.getHEART_RATE(), DBStructureGlobal.getRESPIRATORY_RATE(), DBStructureGlobal.getRATE_NAUSEA(), DBStructureGlobal.getRATE_HEADACHE(), DBStructureGlobal.getRATE_DIARRHEA(), DBStructureGlobal.getRATE_SORE_THROAT(), DBStructureGlobal.getRATE_FEVER(), DBStructureGlobal.getRATE_MUSCLE_PAIN(), DBStructureGlobal.getRATE_SMELL_TASTE(), DBStructureGlobal.getRATE_COUGH(), DBStructureGlobal.getRATE_SHORTNESS_BREATH(), DBStructureGlobal.getRATE_TIRED(), usr_nme);

                if (databaseAction.onInsert(reading) == true) {
                    Toast.makeText(getApplicationContext(), "Upload to DB Successful!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void onChangeSymptom() {
        spinnerSymptom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ratingBar.setRating(0);
                spinnerSelect = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String symptom = parent.getItemAtPosition(position).toString();
        spinnerSelect = symptom;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}