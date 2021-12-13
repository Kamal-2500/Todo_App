package com.example.todoapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    public List<ToDoModel> todoList;
    private MainActivity activity;
    private Database db;

    public ToDoAdapter(Database db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout,parent,false);
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        CheckBox task;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.tasksCheckBox);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        db = new Database(this.getContext());
        db.openDatabase();

        final ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTasks());
        holder.task.setChecked(toBoolean(item.getStatus()));
        if (toBoolean(item.getStatus())) {
            db.updateStatus(item.getId(), 1);
            holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.task.setTextColor(Color.GRAY);
        } else {
            db.updateStatus(item.getId(), 0);
            holder.task.setPaintFlags(holder.task.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.task.setTextColor(Color.BLACK);
        }
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus(item.getId(), 1);
                    holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.task.setTextColor(Color.GRAY);
                } else {
                    db.updateStatus(item.getId(), 0);
                    holder.task.setPaintFlags(holder.task.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.task.setTextColor(Color.BLACK);
                }
            }
        });
    }

    boolean toBoolean(int n)
    {
        return n!=0;
    }

    public void setTasks(List<ToDoModel> todoList)
    {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public Context getContext()
    {
        return activity;
    }

    public int getItemCount()
    {
        return todoList.size();
    }

    public void editItem(int position)
    {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTasks());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }
}
