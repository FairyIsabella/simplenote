package com.example.notes.Manager;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.example.notes.Dialog.EditDialog;
import com.example.notes.Dialog.InfoDialog;
import com.example.notes.Dialog.NoteDialog;
import com.example.notes.Dialog.MyOnClickListener;
import com.example.notes.View.MsgToast;
import com.example.notes.Activity.ContentActivity;
import com.example.notes.Model.Date;

import com.example.notes.Model.Note;
import com.example.notes.Util.StringUtil;
import com.example.ui.R;

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

        Bundle bundle = new Bundle();
        bundle.putSerializable("note", select_item);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }


    public void editClick(int position){
/**
        final InfoDialog dialog = new InfoDialog(mContext);
        dialog.show();

        final Note select_item = list.get(position);
        dialog.setEnableEdit(true);
        dialog.setTitle("修改标题");
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
 **/
        final EditDialog dialog = new EditDialog(mContext);
        dialog.show();

        final Note select_item = list.get(position);

        dialog.setTitle("编辑");
        dialog.setInfo(select_item.getName());

        dialog.setYesListener(new MyOnClickListener() {
            @Override
            public void onClick() {
                String newName = dialog.getInfo();

                if(StringUtil.isEmpty(newName.trim())){
                    MsgToast.showToast(mContext,"名字不能为空");
                }else{
                    update(select_item,newName,dialog.getLevel());
                }
                dialog.dismiss();
            }
        });
    }


    public void deleteClick(int position){


        Note select_item = list.get(position);
        delete(select_item);
        MsgToast.showToast(mContext,"已移至回收站");

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

        dbManager.delete(currentFolderName,note);
    }


    public void deleteNote(Note note) {

        final Note note1 = note;
       /** final InfoDialog warnDialog = new InfoDialog(mContext);

        warnDialog.show();
        warnDialog.setTitle("提示");
        warnDialog.setEnableEdit(false);
        warnDialog.setInfo("是否确认删除!");
        warnDialog.setYesListener(new MyOnClickListener() {
            @Override
            public void onClick() {
                **/
                dbManager.delete(currentFolderName,note1);
               // warnDialog.dismiss();
          //  }
        //});

    }

    public void update(Note preNote,Note newNote){
        dbManager.upDate(currentFolderName,preNote,newNote);
    }


    public void update(Note note,String newName,int newLevel){

        Note newNote = note.getClone();
        newNote.setName(newName);
        newNote.setLevel(newLevel);


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
