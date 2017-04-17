package com.example.notes.Manager;

import android.content.Context;
import android.widget.BaseAdapter;

import com.example.notes.Dialog.InfoDialog;
import com.example.notes.Interface.MyOnClickListener;
import com.example.notes.util.MsgToast;
import com.example.notes.ui.RecycleActivity;
import com.example.notes.util.Note;

import java.util.List;


public class RecycleManager {
    private Context mContext;

    private List<Note> mData;
    private String currentFolderName;
    private BaseAdapter adapter;

    private DBManager dbManager;

    public RecycleManager( Context mContext, List<Note> list,BaseAdapter adapter) {
        this.mContext = mContext;
        this.dbManager = new DBManager(mContext);
        this.mData = list;
        this.currentFolderName = "recycle";
        this.adapter = adapter;
    }


    public void delete(int position){

        dbManager.delete(currentFolderName,mData.get(position));
        update_bottom(position);

    }

    public  void recovery(int position){


        Note note = mData.get(position);


        dbManager.recovery(note);
        update_bottom(position);
    }

    public void clearAll(int dataSize){


        if(dataSize == 0 ){
            MsgToast.showToast(mContext,"空空如也");
            return;
        }


        final InfoDialog warnDialog = new InfoDialog(mContext);
        warnDialog.show();
        warnDialog.setTitle("警告");
        warnDialog.setEnableEdit(false);
        warnDialog.setInfo("删除不可恢复!");
        warnDialog.setYesListener(new MyOnClickListener() {
            @Override
            public void onClick() {
                int number  = dbManager.clearAllFolder(currentFolderName);
                update_bottom(-1);
                MsgToast.showToast(mContext,"删除了 "+number+" 条数据");
                ((RecycleActivity)mContext).finish();
            }
        });



    }

    private  void update_bottom(int position){

        if(position!=-1) {
            mData.remove(position);
        }
        adapter.notifyDataSetChanged();
        ((RecycleActivity)mContext).update_bottom();
        if(mData.size()==0){
            ((RecycleActivity)mContext).finish();
        }
    }
}
