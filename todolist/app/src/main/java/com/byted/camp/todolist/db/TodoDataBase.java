package com.byted.camp.todolist.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.byted.camp.todolist.db.dao.TodoDao;
import com.byted.camp.todolist.db.entity.TodoItem;

@Database(entities = {TodoItem.class}, version = 2)
public abstract class TodoDataBase extends RoomDatabase {

    private static final String DATABASE_NAME = "todo_list.db";

    private static volatile TodoDataBase instance;

    public abstract TodoDao todoDao();

    public static TodoDataBase getInstance(final Context context){
        if (instance == null){
            synchronized (TodoDataBase.class){
                if (instance == null){
                    instance = buildDataBase(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private static TodoDataBase buildDataBase(Context todoContext){
        return Room.databaseBuilder(todoContext, TodoDataBase.class, DATABASE_NAME)
        //        .allowMainThreadQueries()
        //        .addMigrations(MIGRATION_1_2)
                .build();
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //
        }
    };
}
