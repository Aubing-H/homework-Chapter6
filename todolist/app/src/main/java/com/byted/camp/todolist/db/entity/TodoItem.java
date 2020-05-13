package com.byted.camp.todolist.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "todo_list")
public class TodoItem {
    @PrimaryKey
    public long id;

    @ColumnInfo(name = "state")
    public String todoState;

    @ColumnInfo(name = "content")
    public String todoContent;

    @ColumnInfo(name = "date")
    public String todoDate;
}
