package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TodoEntry.TABLE_NAME + " (" +
                    TodoEntry.TODO_ID + " INTEGER PRIMARY KEY, " +
                    TodoEntry.TODO_CONTENT + " TEXT, " +
                    TodoEntry.TODO_DATE + "TEXT, " +
                    TodoEntry.TODO_STATE + "INTEGER)";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TodoEntry.TABLE_NAME;
//    public static final String SQL_CREATE_ENTRIES =
//            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
//                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
//                    FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
//                    FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT)";
//
//    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    private TodoContract() {
    }

    public static class TodoEntry implements BaseColumns{
        public static final String TABLE_NAME = "todo_list";
        public static final String TODO_ID = "id";
        public static final String TODO_DATE = "date";
        public static final String TODO_STATE = "state";
        public static final String TODO_CONTENT = "content";
    }
}
