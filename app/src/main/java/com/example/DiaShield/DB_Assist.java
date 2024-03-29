package com.example.DiaShield;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB_Assist extends SQLiteOpenHelper {

    public static final String HEART_RATE_COL = "HEART_RATE_COL";
    public static final String RESPIRATORY_RATE_COL = "RESPIRATORY_RATE";
    public static final String TIRED_COL = "TIREDNESS";
    public static final String BREATH_COL = "SHORTNESS_OF_BREATH";
    public static final String COUGH_COL = "COUGH";
    public static final String SMELL_TASTE_COL = "LOSS_SMELL_TASTE";
    public static final String MUSCLE_PAIN_COL = "MUSCLE_ACHE";
    public static final String FEVER_COL = "FEVER";
    public static final String THROAT_COL = "SORE_THROAT";
    public static final String DIARRHEA_COL = "DIARRHEA";
    public static final String HEADACHE_COL = "HEADACHE";
    public static final String ID = "ID";
    public static final String SYMPTOMS_TABLE = "SYMPTOMS_TABLE";
    private static final String NAUSEA_COL = "NAUSEA_COL";

    public DB_Assist(@Nullable Context context) {
        super(context, "symptoms.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String databaseCreationQuery = "CREATE TABLE " + SYMPTOMS_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + HEART_RATE_COL + " INTEGER, " + RESPIRATORY_RATE_COL + " INTEGER, " + NAUSEA_COL + " INTEGER, " + HEADACHE_COL + " INTEGER, " + DIARRHEA_COL + " INTEGER, " + THROAT_COL + " INTEGER, " + FEVER_COL + " INTEGER, " + MUSCLE_PAIN_COL + " INTEGER, " + SMELL_TASTE_COL + " INTEGER, " + COUGH_COL + " INTEGER, " + BREATH_COL + " INTEGER, " + TIRED_COL + " INTEGER)";
        db.execSQL(databaseCreationQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
    public boolean onInsert(DB_Structure DB_Structure){
        SQLiteDatabase database =this.getWritableDatabase();
        ContentValues ratingData = new ContentValues();
        ratingData.put(HEART_RATE_COL, DB_Structure.HEART_RATE);
        ratingData.put(RESPIRATORY_RATE_COL, DB_Structure.RESPIRATORY_RATE);
        ratingData.put(NAUSEA_COL, DB_Structure.RATE_NAUSEA);
        ratingData.put(HEADACHE_COL, DB_Structure.RATE_HEADACHE);
        ratingData.put(DIARRHEA_COL, DB_Structure.RATE_DIARRHEA);
        ratingData.put(THROAT_COL, DB_Structure.RATE_SORE_THROAT);
        ratingData.put(FEVER_COL, DB_Structure.RATE_FEVER);
        ratingData.put(MUSCLE_PAIN_COL, DB_Structure.RATE_MUSCLE_PAIN);
        ratingData.put(SMELL_TASTE_COL, DB_Structure.RATE_SMELL_TASTE);
        ratingData.put(COUGH_COL, DB_Structure.RATE_COUGH);
        ratingData.put(BREATH_COL, DB_Structure.RATE_SHORTNESS_BREATH);
        ratingData.put(TIRED_COL, DB_Structure.RATE_TIRED);
        long insertResult = database.insert(SYMPTOMS_TABLE, null, ratingData);
        return insertResult == -1;
    }
}
