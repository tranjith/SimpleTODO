package com.rtellakula.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rtellakula on 11/19/15.
 */
public class TodoItemDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todoDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TODO_ITEMS = "todo";

    private static final String TODO_ID = "id";
    private static final String TODO_TEXT = "text";

    private static TodoItemDatabaseHelper sInstance;

    public static synchronized TodoItemDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TodoItemDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private TodoItemDatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_ITEMS + "(" +TODO_ID + " INTEGER PRIMARY KEY," +
                TODO_TEXT + " TEXT " + ")";
        db.execSQL(CREATE_TODO_TABLE);

    }

    /**
     * Create a new TODOItem
     * @param todoItem : todo_item to be saved in database
     */
    public void addTodoItem(TodoItem todoItem){
        SQLiteDatabase db =getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(TODO_TEXT,todoItem.text);
            db.insertOrThrow(TODO_ITEMS,null,values);
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d("Database Tag", "Error while trying to add post to database");
        }finally {
            db.endTransaction();
        }
    }

    /**
     * Get all todo_items from database
     * @return list of all todo_items from database
     */
    public List<TodoItem> getAllTodoItems() {
        List<TodoItem> todos = new ArrayList<>();
        String TODOITEMS_SELECT_QUERY = String.format("SELECT * FROM %s",TODO_ITEMS);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODOITEMS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    TodoItem temp = new TodoItem();
                    temp.id = cursor.getInt(cursor.getColumnIndex(TODO_ID));
                    temp.text = cursor.getString(cursor.getColumnIndex(TODO_TEXT));
                    todos.add(temp);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d("Read DB","Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return  todos;
    }

    /**
     * Update todo_items in database
     * @param todoItem TODO_Item updated
     * @return
     */
    public int updateTodoItem(TodoItem todoItem){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TODO_TEXT,todoItem.text);
        return  db.update(TODO_ITEMS,values,TODO_ID+" = ?", new String[] {String.valueOf(todoItem.id)});
    }

    /**
     * Delete all the todo_items in database
     */
    public void deleteAllTodo(){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TODO_ITEMS,null,null);
            db.setTransactionSuccessful();
        } catch (Exception e){
            Log.d("Delete All Todo", "Error while deleting all the todoItems");
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Deleting todo_item from database
     * @param todoItem
     */
    public void deleteTodoItem(TodoItem todoItem) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TODO_ITEMS,TODO_ID+" = ?", new String[] {String.valueOf(todoItem.id)});
            db.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion!=newVersion  ){
            db.execSQL("DROP TABLE IF EXISTS " + TODO_ITEMS);
            onCreate(db);
        }
    }
}
