package com.example.synctest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText Name;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    ArrayList<Contact> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        Name = (EditText)findViewById(R.id.name);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(adapter);
        readFromLocalStorage();
    }

    public void submitName(View view){
        Toast.makeText(MainActivity.this, "Botão funcionando!", Toast.LENGTH_SHORT).show();
        String name = Name.getText().toString();
        saveToLocalStorage(name);
        Name.setText("");
    }

    private void readFromLocalStorage(){
        arrayList.clear();
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.readFromLocalDatabase(database);

        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(DbContract.NAME));
            int sync_status = cursor.getInt(cursor.getColumnIndex(DbContract.SYNC_STATUS));
            arrayList.add(new Contact(name,sync_status));
        }

        adapter.notifyDataSetChanged();
        cursor.close();
        dbHelper.close();
    }

    private void saveToLocalStorage(String name){
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //if(checkNetworkConnection()){

        //} else {
            dbHelper.saveToLocalDatabase(name, DbContract.SYNC_STATUS_FAILED,database);
       // }
        readFromLocalStorage();

        dbHelper.close();
    }

    public boolean checkNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!= null && networkInfo.isConnected());
    }
}
