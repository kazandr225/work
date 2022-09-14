package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btnAdd, btnClear;
    EditText Name, Surname;

    ConnectionHelper connectionHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        Name = (EditText) findViewById(R.id.Name);
        Surname = (EditText) findViewById(R.id.Surname);

        Name = (EditText) findViewById(R.id.Name);
        Name.setOnFocusChangeListener(this::onFocusChange);
        Surname = (EditText) findViewById(R.id.Surname);
        Surname.setOnFocusChangeListener(this::onFocusChange);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        UpdateTable();
    }

    public void onFocusChange(View view, boolean b) {
        switch(view.getId()){
            case R.id.Name:
                if(b){
                    Name.setText("");
                }
                else if(Name.getText().toString().equals("")){
                    Name.setText("Имя");
                }
                break;
            case R.id.Surname:
                if(b){
                    Surname.setText("");
                }
                else if(Surname.getText().toString().equals("")){
                    Surname.setText("Фамилия");
                }
                break;
        }
    }

    public void UpdateTable(){
        Cursor cursor = database.query(DBHelper.TABLE_AUTOMOBILES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int markaIndex = cursor.getColumnIndex(DBHelper.KEY_MARKA);
            int modelIndex = cursor.getColumnIndex(DBHelper.KEY_MODEL);
            int gosNomIndex = cursor.getColumnIndex(DBHelper.KEY_GOSNOM);
            TableLayout dbOutput = findViewById(R.id.dbOutput);
            dbOutput.removeAllViews();
            do {
                TableRow dbOutputRow = new TableRow(this);
                dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                TextView outputID = new TextView(this);
                params.weight = 1.0f;
                outputID.setLayoutParams(params);
                outputID.setText(cursor.getString(idIndex));
                outputID.setTextSize(12);
                dbOutputRow.addView(outputID);

                TextView outputMarka = new TextView(this);
                params.weight = 3.0f;
                outputMarka.setLayoutParams(params);
                outputMarka.setText(cursor.getString(markaIndex));
                outputMarka.setTextSize(12);
                dbOutputRow.addView(outputMarka);

                TextView outputModel = new TextView(this);
                params.weight = 3.0f;
                outputModel.setLayoutParams(params);
                outputModel.setText(cursor.getString(modelIndex));
                outputModel.setTextSize(12);
                dbOutputRow.addView(outputModel);

                TextView outputGosNom = new TextView(this);
                params.weight = 3.0f;
                outputGosNom.setLayoutParams(params);
                outputGosNom.setText(cursor.getString(gosNomIndex));
                outputGosNom.setTextSize(12);
                dbOutputRow.addView(outputGosNom);

                Button deleteBtn = new Button(this);
                deleteBtn.setOnClickListener(this);
                params.weight = 1.0f;
                deleteBtn.setLayoutParams(params);
                deleteBtn.setText("-");
                deleteBtn.setTextSize(12);
                deleteBtn.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(deleteBtn);

                dbOutput.addView(dbOutputRow);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                String name = Name.getText().toString();
                String surname = Surname.getText().toString();
                contentValues = new ContentValues();

                contentValues.put(DBHelper.KEY_MARKA, name);
                contentValues.put(DBHelper.KEY_MODEL, surname);

                database.insert(DBHelper.TABLE_AUTOMOBILES, null, contentValues);
                Name.setText("Имя");
                Surname.setText("Фамилия");
                UpdateTable();
                break;
            case R.id.btnClear:
                database.delete(DBHelper.TABLE_AUTOMOBILES, null, null);
                TableLayout dbOutput = findViewById(R.id.dbOutput);
                dbOutput.removeAllViews();
                UpdateTable();
                break;
            default:
                View outputDBRow = (View) v.getParent();
                ViewGroup outputDB = (ViewGroup) outputDBRow.getParent();
                outputDB.removeView(outputDBRow);
                outputDB.invalidate();

                database.delete(DBHelper.TABLE_AUTOMOBILES, DBHelper.KEY_ID + " = ?", new String[]{String.valueOf(v.getId())});

                contentValues = new ContentValues();
                Cursor cursorUpdater = database.query(DBHelper.TABLE_AUTOMOBILES, null, null, null, null, null, null);
                if (cursorUpdater.moveToFirst()) {
                    int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                    int markaIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_MARKA);
                    int modelIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_MODEL);
                    int gosNomIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_GOSNOM);
                    int realID = 1;
                    do{
                        if(cursorUpdater.getInt(idIndex) > idIndex){
                            contentValues.put(DBHelper.KEY_ID, realID);
                            contentValues.put(DBHelper.KEY_MARKA, cursorUpdater.getString(markaIndex));
                            contentValues.put(DBHelper.KEY_MODEL, cursorUpdater.getString(modelIndex));
                            contentValues.put(DBHelper.KEY_GOSNOM, cursorUpdater.getString(gosNomIndex));
                            database.replace(DBHelper.TABLE_AUTOMOBILES, null, contentValues);
                        }
                        realID++;
                    } while(cursorUpdater.moveToNext());
                    if(cursorUpdater.moveToLast() && (cursorUpdater.getInt(idIndex) == realID)){
                        database.delete(DBHelper.TABLE_AUTOMOBILES, DBHelper.KEY_ID + " = ?", new String[]{cursorUpdater.getString(idIndex)});
                    }
                    UpdateTable();
                }
                break;
        }
    }

}