package com.rtellakula.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    private final int REQUEST_CODE = 20;
    private TodoItemDatabaseHelper todoItemDatabaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        todoItemDatabaseHelper = TodoItemDatabaseHelper.getInstance(this);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<>();
        readItems();
        itemsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
        setupListViewOnClickListener();
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void setupListViewListener(){
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        items.remove(position);
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();
                        return true;
                    }
                }
        );
    }

    private void setupListViewOnClickListener(){
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchEditView(items.get(position), position);
            }
        });
    }

    public void launchEditView(String item, int position){
        Intent editIntent = new Intent(MainActivity.this,EditItemActivity.class);
        editIntent.putExtra("editItem",item);
        editIntent.putExtra("position",position);
        startActivityForResult(editIntent, REQUEST_CODE);
    }

    public void onAddItem(View v){
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);

        etNewItem.setText("");
        writeItems();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        System.out.println("Called on Activity result" + resultCode + " - Request " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            String item = (String)data.getStringExtra("editItem");
            int position = (Integer)data.getSerializableExtra("position");
            items.set(position,item);
            System.out.println("New Item Description is : " + item + " - " + position) ;
            System.out.println("Items" + items);
            itemsAdapter.notifyDataSetChanged();
            writeItems();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void readItems(){
//        File filesDir = getFilesDir();
//        File todoFile = new File(filesDir,"todo.txt");
        try{
            items = new ArrayList<>();
            List<TodoItem> todoItems = todoItemDatabaseHelper.getAllTodoItems();
            for(TodoItem todoItem : todoItems ){
                System.out.println(todoItem.text);
                items.add(todoItem.text);
            }
        }catch (Exception e){
            items = new ArrayList<>();
            e.printStackTrace();
        }
    }

    private void writeItems(){
//        File filesDir = getFilesDir();
//        File todoFile = new File(filesDir,"todo.txt");
        try{
            todoItemDatabaseHelper.deleteAllTodo();
            for(String item : items){
                TodoItem todoItem = new TodoItem();
                todoItem.text = item;
                todoItemDatabaseHelper.addTodoItem(todoItem);
            }
            //FileUtils.writeLines(todoFile,items);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
