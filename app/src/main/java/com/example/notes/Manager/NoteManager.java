package com.example.notes.Manager;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.example.notes.Dialog.InfoDialog;
import com.example.notes.Dialog.NoteDialog;
import com.example.notes.Interface.MyOnClickListener;
import com.example.notes.util.MsgToast;
import com.example.notes.ui.ContentActivity;
import com.example.notes.util.Date;

import com.example.notes.util.Note;
import com.example.notes.util.StringUtil;

import java.util.List;


/**
 * Created by 阿买 on 2017/1/18.
 */

public class NoteManager{

    private Context mContext;
    private List<Note> list;

    private String currentFolderName;
    private BaseAdapter adapter;
    private  DBManager dbManager;



    public NoteManager(Context context,String currentFolderName){
        this.mContext=context;
        this.currentFolderName=currentFolderName;
        dbManager=new DBManager(mContext);
    }

    public NoteManager(Context context,String currentFolderName,
                           List<Note> list,BaseAdapter adapter){
        this(context,currentFolderName);
        this.list=list;
        this.adapter=adapter;
    }

    /**
     * listView的短点击事件
     * @param position
     */
    public void ItemClick(int position){
        final Note select_item = list.get(position);
        ItemClick(select_item);

    }

    /**
     * 打开note
     * @param select_item
     */
    private void ItemClick(Note select_item){

        Intent intent = new Intent(mContext,ContentActivity.class);
        intent.putExtra("currentFolderName",currentFolderName);
        Bundle bundle = new Bundle();
        bundle.putSerializable("note", select_item);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }


    public void editClick(int position){

        final InfoDialog dialog = new InfoDialog(mContext);
        dialog.show();

        final Note select_item = list.get(position);
        dialog.setEnableEdit(true);
        dialog.setTitle("重命名此便签");
        dialog.setInfo(select_item.getName());

        dialog.setYesListener(new MyOnClickListener() {
            @Override
            public void onClick() {
                String newName = dialog.getInfo();
                if(StringUtil.isEmpty(newName.trim())){
                    MsgToast.showToast(mContext,"名字不能为空");
                }else if(!newName.equals(select_item.getName())){
                    update(select_item,newName);
                }
                dialog.dismiss();
            }
        });
    }


    public void deleteClick(int position){


        Note select_item = list.get(position);
        delete(select_item);
        MsgToast.showToast(mContext,"已删除,但仍可以在回收站找到它");

    }

    public void add(Note note){
        dbManager.insert(currentFolderName,note);

    }

    public void add(){


        final NoteDialog dialog = new NoteDialog(mContext);
        dialog.show();
        dialog.setTitle("新的备忘录");
        dialog.setInfo("建立一个名字");
        dialog.setEnableEdit(true);

        dialog.setYesListener(new MyOnClickListener() {
            @Override
            public void onClick() {
                if(!StringUtil.isEmpty(dialog.getInfo())){

                    final Note note = new Note(dialog.getInfo(),
                            new Date(), null,"",
                            currentFolderName,dialog.getLevel());

                    list.add(note);
                    adapter.notifyDataSetChanged();
                    dbManager.insert(currentFolderName,note);


                    final InfoDialog confirm =  new InfoDialog(mContext);
                    confirm.show();
                    confirm.setInfo("是否打开 "+note.getName()+" ?");
                    confirm.setEnableEdit(false);
                    confirm.setYesListener(new MyOnClickListener() {
                        @Override
                        public void onClick() {
                            ItemClick(note);
                            confirm.dismiss();
                        }
                    });
                }else {
                    MsgToast.showToast(mContext,"不能为空哟");
                }
                dialog.dismiss();
            }
        });

    }


    public void delete(Note note) {
        list.remove(note);

        adapter.notifyDataSetChanged();

       // ((MainActivity)mContext).update_bottom();

        dbManager.delete(currentFolderName,note);
    }


    public void deleteNote(Note note) {


        final InfoDialog warnDialog = new InfoDialog(mContext);
        final Note note1 = note;
        warnDialog.show();
        warnDialog.setTitle("提示");
        warnDialog.setEnableEdit(false);
        warnDialog.setInfo("是否确认删除!");
        warnDialog.setYesListener(new MyOnClickListener() {
            @Override
            public void onClick() {
                dbManager.delete(currentFolderName,note1);
                warnDialog.dismiss();
                ((ContentActivity) mContext).finish();
            }
        });

    }

    public void update(Note preNote,Note newNote){
        dbManager.upDate(currentFolderName,preNote,newNote);
    }


    public void update(Note note,String newName){

        Note newNote = note.getClone();
        newNote.setName(newName);

        dbManager.upDate(currentFolderName,note,newNote);

        if(list!=null) {
            int index = list.indexOf(note);
            list.set(index, newNote);
            adapter.notifyDataSetChanged();
        }
    }


    public Note updateContent(Note note ,String content){

        Note newNote = note.getClone();
        newNote.setText(content);


        dbManager.upDate(currentFolderName,note,newNote);
        return newNote;
    }


    public Note updateLocation(Note note ,String location){



        Note newNote = note.getClone();

        newNote.setLocation(location);
        dbManager.upDate(currentFolderName,note,newNote);
        return newNote;
    }

     public Note updateLevel(Note note ,int level){

        Note newNote =note.getClone();
        newNote.setLevel(level);

        dbManager.upDate(currentFolderName,note,newNote);
         return  newNote;
    }
}
