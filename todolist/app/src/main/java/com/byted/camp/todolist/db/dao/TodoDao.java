package com.byted.camp.todolist.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.entity.TodoItem;

import java.util.List;

@Dao
public interface TodoDao {
    @Query("SELECT * FROM todo_list")
    List<TodoItem> getAll();

    @Query("SELECT * FROM todo_list WHERE id = :item_id")
    TodoItem findItemById(long item_id);

    @Insert
    long insert(TodoItem todoItem);

    @Update
    void update(TodoItem todoItem);

    @Delete
    void delete(TodoItem todoItem);

}
