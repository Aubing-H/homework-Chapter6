package com.byted.camp.todolist;

import android.arch.persistence.room.Query;
import android.view.View;

import com.byted.camp.todolist.beans.Note;

import java.util.List;

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public interface NoteOperator {

//    @Query("select * from todo_list")
//    List<Note> getAll();
//
//    @Query("select * from todo_list where id in IN(:todo_ids)")
//    List<Note> loadAllByIds(int[] todo_ids);

    void deleteNote(Note note);

    void updateNote(Note note);
}
