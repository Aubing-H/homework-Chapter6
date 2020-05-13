package com.byted.camp.todolist;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byted.camp.todolist.db.TodoDataBase;
import com.byted.camp.todolist.db.entity.TodoItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = "hjinbing";

    private EditText editText;
    private Button addBtn;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                Log.d(TAG, "NoteActivity content: " + content);
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean saveNote2Database(String content) {
        // TODO 插入一条新数据，返回是否插入成功
        // content为数据内容，需添加时间和id号
        TodoItem todoItem = new TodoItem();
        todoItem.todoContent = content;
        todoItem.todoDate = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        todoItem.todoState = "TODO";
        String temp = UUID.randomUUID().toString().replace("-", "");
        todoItem.id = Long.parseLong(temp.substring(8, 23), 16);
        long temp_a = 0;
        try{
            MyNewThread thread = new MyNewThread(this.getApplicationContext(), todoItem);
            thread.start();
            thread.join();
            temp_a = thread.rt_val;
        }catch (Exception e){
            Log.d(TAG, "EXCEPTION: " + e.toString());
        }
        return temp_a != 0;
    }

    private static class MyNewThread extends Thread{
        public Long rt_val;
        private TodoItem todoItem;
        private Context context;
        public MyNewThread(Context context, TodoItem todoItem){
            this.context = context;
            this.todoItem = todoItem;
        }
        public void run(){
            rt_val = TodoDataBase.getInstance(context).todoDao().insert(todoItem);
        }
    }
}
