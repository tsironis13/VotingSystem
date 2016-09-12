package com.votingsystem.tsiro.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Handler;
import android.util.Log;

import com.votingsystem.tsiro.POJO.JnctFirmSurveysFields;
import com.votingsystem.tsiro.POJO.SurveysFields;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.votingsystem.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by giannis on 27/8/2016.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String debugTag = MySQLiteHelper.class.getSimpleName();
    private static MySQLiteHelper sInstance;
    private Context context;
    private static String DB_PATH;
    private SQLiteDatabase db;

    //Table names
    private static final String TABLE_SURVEYS               = "Surveys";
    private static final String TABLE_JNCT_FIRM_SURVEYS     = "Jnct_firm_surveys";
    private static final String TABLE_RECENT_SEARCHES       = "Recent_searches";

    //Common column names
    private static final String KEY_ID = "id";

    //Survey table - column names
    private static final String KEY_TITLE           = "title";
    private static final String KEY_RESPONSES       = "responses";
    private static final String KEY_LAST_MODIFIED   = "last_modified";

    //Jnct_firm_surveys table - column names
    private static final String KEY_FIRM_ID     = "firm_id";
    private static final String KEY_SURVEY_ID   = "survey_id";

    //Recent_searches table - column names
    private static final String KEY_SEARCH      = "search";
    private static final String KEY_SEARCHED_AT = "searched_at";

    //Table create statements
    private static final String CREATE_TABLE_SURVEY = "CREATE TABLE "
            + TABLE_SURVEYS + "(" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_TITLE + " TEXT, "
            + KEY_RESPONSES + " INTEGER, " + KEY_LAST_MODIFIED + " INTEGER" + ")";

    private static final String CREATE_TABLE_JNCT_FIRM_SURVEYS = "CREATE TABLE "
            + TABLE_JNCT_FIRM_SURVEYS + "(" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_FIRM_ID + " INTEGER, "
            + KEY_SURVEY_ID + " INTEGER" + ")";

    private static final String CREATE_TABLE_RECENT_SEARCHES = "CREATE TABLE "
            + TABLE_RECENT_SEARCHES + "(" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_SEARCH + " TEXT, "
            + KEY_SEARCHED_AT + " INTEGER" + ")";

    public static MySQLiteHelper getInstance(Context context) {
        return sInstance == null ? sInstance = new MySQLiteHelper(context) : sInstance;
    }

    private MySQLiteHelper(Context context) {
        super(context, context.getResources().getString(R.string.database), null, AppConfig.VERSION);
        this.context = context;
        DB_PATH = context.getDatabasePath(context.getResources().getString(R.string.database)).getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SURVEY);
        db.execSQL(CREATE_TABLE_JNCT_FIRM_SURVEYS);
        db.execSQL(CREATE_TABLE_RECENT_SEARCHES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SURVEYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JNCT_FIRM_SURVEYS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_SEARCHES);

        onCreate(db);
    }

    public void createDatabase() {
        boolean dbExists = checkDatabase();

        if (!dbExists) this.getReadableDatabase();
    }

    public boolean checkDatabase() {
        boolean checkDB = false;

        try {
            File dbFile = context.getDatabasePath(context.getResources().getString(R.string.database));
            checkDB = dbFile.exists();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return checkDB;
    }

    public void openDatabase() throws SQLiteException {
        try {
            db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
            Log.e(debugTag, db+"");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void closeDB() {
        if (db != null && db.isOpen()) db.close();
    }

    @SuppressWarnings("unchecked")
    public <T> void insertToDatabase(String table, List<T> list, String text) {
        ContentValues contentValues = new ContentValues();
        if (text == null) {
            if (list != null) {
                if (list.get(0) instanceof JnctFirmSurveysFields) {
                    List<JnctFirmSurveysFields> jnctFirmSurveysFieldsList = (List<JnctFirmSurveysFields>) list;
                    for (JnctFirmSurveysFields jnctList : jnctFirmSurveysFieldsList) {
                        contentValues.put(KEY_FIRM_ID, jnctList.getFirmIdFk());
                        contentValues.put(KEY_SURVEY_ID, jnctList.getSurveyIdFk());
                        db.insert(table, null, contentValues);
                    }
                } else {
                    List<SurveysFields> surveysFieldsList = (List<SurveysFields>) list;
                    for (SurveysFields surveysFields : surveysFieldsList) {
                        contentValues.put(KEY_ID, surveysFields.getSurveyId());
                        contentValues.put(KEY_TITLE, surveysFields.getTitle());
                        contentValues.put(KEY_RESPONSES, surveysFields.getResponses());
                        contentValues.put(KEY_LAST_MODIFIED, surveysFields.getLastModified());
                        db.insert(table, null, contentValues);
                    }
                }
            }
        }
    }

    public boolean isDatabaseEmpty() {
        Cursor cursor = db.rawQuery("SELECT * FROM Surveys", null);
        int count = cursor.getCount();
        while (cursor.moveToNext()) {
//            Log.e(debugTag, cursor.getColumnName(1)+ ": "+cursor.getInt(1)+" "+ cursor.getColumnName(3)+": "+cursor.getInt(3));
        }
        Log.e(debugTag, count+"");
        cursor.close();
        return count != 0;
    }

    public List<SurveysFields> getFirmSurveys(int firm_id, String search_text) {
        List<SurveysFields> matches = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Surveys " +
                                        "LEFT JOIN Jnct_firm_surveys ON Surveys.id = Jnct_Firm_Surveys.survey_id\n" +
                                        "WHERE Jnct_firm_surveys.firm_id = " + firm_id +" AND Surveys.title LIKE '%" + search_text + "%'", null);
        int count = cursor.getCount();
        Log.e(debugTag, count+"");
        while (cursor.moveToNext()) {
//            Log.e(debugTag, cursor.getColumnName(1)+ ": "+cursor.getString(1));
            matches.add(new SurveysFields(cursor.getInt(0), cursor.getInt(2), cursor.getInt(3), cursor.getString(1)));
        }
        cursor.close();
        return matches;
    }
}
