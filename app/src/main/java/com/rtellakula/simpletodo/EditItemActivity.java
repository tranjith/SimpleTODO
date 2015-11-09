package com.rtellakula.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    private String editItem;
    private int position;
    private EditText etItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent editIntent = getIntent();
        editItem = (String)editIntent.getSerializableExtra("editItem");
        position = (Integer)editIntent.getSerializableExtra("position");
        etItem = (EditText)findViewById(R.id.etItem);
        if(null!=editItem)
            etItem.setText(editItem);
    }

    public void onSaveItem(View v){
        Intent i = new Intent();
        i.putExtra("editItem",etItem.getText().toString());
        i.putExtra("position",position);
        setResult(RESULT_OK,i);
        finish();
    }

}
