package com.byted.camp.todolist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.persistence.room.Update;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.dao.TodoDao;
import com.byted.camp.todolist.db.entity.TodoItem;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.operation.db.dao.UserDao_Impl;
import com.byted.camp.todolist.ui.NoteListAdapter;
import com.byted.camp.todolist.db.TodoDataBase;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "hjinbing";

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    private TodoDataBase todoDataBase;

    private SimpleDateFormat simpleDateFormat;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        todoDataBase = TodoDataBase.getInstance(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        Log.d(TAG, "new NoteListAdapter setup");

        recyclerView.setAdapter(notesAdapter);
        Log.d(TAG, "recycle view set Adapter");

        notesAdapter.refresh(loadNotesFromDatabase());
        Log.d(TAG, "recycle refresh");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
            loadNotesFromDatabase();
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        List<Note> temp_list = new ArrayList<>();
        try{
            MyThread myThread = new MyThread(this);
            myThread.start();
            myThread.join();
            List<TodoItem> todoItems = myThread.todoItems;
            for (TodoItem item: todoItems) {
                try {
                    Note note = new Note(item.id);
                    note.setState(item.todoState.equals("TODO") ? State.TODO : State.DONE);
                    note.setDate(simpleDateFormat.parse(item.todoDate));
                    note.setContent(item.todoContent);
                    temp_list.add(note);
                } catch (ParseException e) {
                    Log.d(TAG, "ParseException" + e.toString());
                }
            }
            return temp_list;
        }
        catch (Exception e){
            Log.d(TAG, "Exception: " + e.toString());
        }
        return temp_list;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        MyInterface deleteItf = new MyInterface() {
            @Override
            public void operate(Note note) {
                TodoItem todoItem = todoDataBase.todoDao().findItemById(note.id);
                todoDataBase.todoDao().delete(todoItem);
            }
        };
        try{
            new OperateThread(note, deleteItf).start();
        }
        catch (Exception e){
            Log.d(TAG, "$ EXCEPTION: " + e.toString());
        }
    }

    private void updateNode(Note note) {
        // 更新数据
        MyInterface updateIf = new MyInterface() {
            @Override
            public void operate(Note note) {
                TodoItem todoItem = todoDataBase.todoDao().findItemById(note.id);
                todoItem.todoState = note.getState() == State.DONE ? "DONE": "TODO";
                todoItem.todoDate = simpleDateFormat.format(note.getDate());
                todoItem.todoContent = note.getContent();
                todoDataBase.todoDao().update(todoItem);
            }
        };
        try{
            OperateThread operateThread = new OperateThread(note, updateIf);
            operateThread.start();
        }
        catch (Exception e){
            Log.d(TAG, "$ EXCEPTION: " + e.toString());
        }
    }

    private static class MyThread extends Thread{
        public List<TodoItem> todoItems;
        private Context context;
        public MyThread(Context context){
            super();
            this.context = context;
        }
        public void run(){
            todoItems = TodoDataBase.getInstance(context).todoDao().getAll();
        }
    }

    private static class OperateThread extends Thread{
        private MyInterface itf;
        private Note note;
        public OperateThread(Note note, MyInterface itf){
            super();
            this.note = note;
            this.itf = itf;
        }
        public void run(){
            itf.operate(note);
        }
    }

    public interface MyInterface{
        void operate(Note note);
    }
}
