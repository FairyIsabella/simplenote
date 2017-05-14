package com.example.notes.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.notes.Dialog.InfoDialog;
import com.example.notes.Dialog.ProDialog;
import com.example.notes.Dialog.MyOnClickListener;
import com.example.notes.Util.LocationUtil;
import com.example.notes.Manager.NoteManager;
import com.example.notes.Model.Date;
import com.example.notes.View.MsgToast;
import com.example.notes.Model.Note;
import com.example.notes.Util.StringUtil;
import com.example.ui.R;

import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.richeditor.RichEditor;

/**
 * 新建备忘录
 */

public class CreateActivity extends BaseActivity implements View.OnClickListener {

   // private TextView model_title;

    //题目
    private EditText title;
    //内容
    private RichEditor mEditor;
    //日期
    private Date date;
    //日期视图
    private TextView date_view;
    //位置
    private TextView location;
    //level
    private int level;
    //新建的文件夹
    private String currentFolderName;

    //模式
    private boolean model; // (false 新建模式   true 编辑模式)
    //编辑的Note
    private Note edit_Note;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);


        TextView  model_title = (TextView) findViewById(R.id.title_toolbar);

        Intent intent = this.getIntent();//新建模式
        currentFolderName = intent.getStringExtra("currentFolderName");
        model_title.setText("新备忘录");

        if(currentFolderName == null){//编辑模式
            model_title.setText("编辑备忘录");
            model = true;//更改状态
            edit_Note = (Note) intent.getSerializableExtra("note");//获取编辑的note
            currentFolderName = edit_Note.getFolderName();
        }

        init_NoteEditor();
        init_view();
        init_Toolbar();


        if(model){//编辑模式
            init_edit();
        }
    }


    /**
     * 编辑初始化
     */
    private void init_edit(){

        title.setText( edit_Note.getName() );
        mEditor.setHtml( edit_Note.getText() );
        date_view.setText( edit_Note.getDate().getDetailDate() );
        location.setText( edit_Note.getLocation());

    }

    /**
     * 视图初始化
     */
    private  void init_view(){

        title = (EditText) findViewById(R.id.title_create);
        location  = (TextView) findViewById(R.id.location_create);

        date_view = (TextView) findViewById(R.id.date_create);
        date = new Date( );
        date_view.setText(date.getDetailDate());



        Button btn_red = (Button) findViewById(R.id.btn_red);
        btn_red.setOnClickListener(this);
        Button btn_orange = (Button) findViewById(R.id.btn_orange);
        btn_orange.setOnClickListener(this);
        Button btn_green = (Button) findViewById(R.id.btn_green);
        btn_green.setOnClickListener(this);

        init_fab();
        init_bottom();
    }

    /**
     * 底部栏的初始化
     */
    private void init_bottom(){

       findViewById(R.id.open_bottom_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.bottom_create).setVisibility(View.GONE);
                findViewById(R.id.editor_bottom).setVisibility(View.VISIBLE);
            }
        });

        ImageView  reBack = (ImageView)findViewById(R.id.reback_bottom_create);

        if(!model) {//编辑模式
            reBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!model) {//编辑模式
                        if (isEdit()) {
                            MsgToast.showToast(CreateActivity.this, "还没开始编辑呢");
                        } else {

                            final InfoDialog warnDialog = new InfoDialog(CreateActivity.this);
                            warnDialog.show();
                            warnDialog.setTitle("提示");
                            warnDialog.setEnableEdit(false);
                            warnDialog.setInfo("将所编辑内容全部清空吗？");
                            warnDialog.setYesListener(new MyOnClickListener() {
                                @Override
                                public void onClick() {
                                    warnDialog.dismiss();
                                    reset();
                                }
                            });

                        }
                    }
                }
            });
        }else{
            reBack.setVisibility(View.GONE);
        }

        findViewById(R.id.hide_bottom_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findViewById(R.id.fab_create).setVisibility(View.VISIBLE);
                findViewById(R.id.editor_bottom).setVisibility(View.GONE);
                findViewById(R.id.bottom_create).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.location_bottom_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

    }

    /**
     * 是否进行了编辑
     * @return
     */
    private  boolean isEdit(){
        return StringUtil.isEmpty(title.getText().toString()) &&
                StringUtil.isEmpty(mEditor.getHtml());
    }

    /**
     * 重置
     */
    private void reset(){

        title.setText("");
        mEditor.setHtml("");
        location.setText("未定位");
        level=Note.GRE_LEVEL;
    }

    /**
     * fab的初始化
     */
    private void init_fab(){

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_create);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.editor_bottom).setVisibility(View.VISIBLE);
                findViewById(R.id.bottom_create).setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 获取定位
     */
    private void getLocation(){


            final ProDialog proDialog = new ProDialog(this,"正在定位...");
            proDialog.show();

            LocationUtil mLocationMag = new LocationUtil(getApplicationContext());

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    proDialog.dismiss();
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 1500);

            String address = mLocationMag.getLocation();

            location.setText(address);
    }

    /**
     * 初始化Editor
     */
    private  void init_NoteEditor() {



        mEditor = (RichEditor) findViewById(R.id.editor);

        mEditor.setFontSize(14);
        mEditor.setPlaceholder("在这里写下内容");


        findViewById(R.id.action_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.bottom_create).setVisibility(View.VISIBLE);
                findViewById(R.id.editor_bottom).setVisibility(View.GONE);
            }
        });


        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });


        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });
        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_deleteline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });


        findViewById(R.id.action_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertTodo();
            }
        });

        findViewById(R.id.action_menulist).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mEditor.setNumbers();
            }
        });


        findViewById(R.id.action_menubullte).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        findViewById(R.id.action_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });


       findViewById(R.id.action_center).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mEditor.setAlignCenter();
           }
       });
        findViewById(R.id.action_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });
    }


    /**
     * toolbar初始
     */

    private  void init_Toolbar(){

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.pic_deleteall);//设置取消图标
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });


        toolbar.inflateMenu(R.menu.menu_create);//设置右上角的填充菜单


        if(model) {//编辑模式
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                        NoteManager noteManager = new NoteManager(CreateActivity.this, currentFolderName);

                        Note newNote = new Note(title.getText().toString(), edit_Note.getDate(),
                                location.getText().toString(), mEditor.getHtml(), currentFolderName, level);

                        noteManager.update(edit_Note, newNote);
                        MsgToast.showToast(CreateActivity.this, "已保存");
                        finish();

                    return false;
                }
            });


        }else {//新建模式
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {


                        String titleName = title.getText().toString();
                        if(StringUtil.isEmpty(titleName)){
                            titleName="未命名";
                        }
                        Note create_note = new Note(titleName, date,
                                location.getText().toString(), mEditor.getHtml(),
                                currentFolderName, level);

                        NoteManager noteManager = new NoteManager(CreateActivity.this, currentFolderName);
                        noteManager.add(create_note);
                        hideOrOpenKeyBoard();
                        finish();

                    return false;
                }
            });

        }


    }

    /**
     * 监听事件
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

             case R.id.btn_red:
             case R.id.btn_green:
             case R.id.btn_orange:
             change_level(v);
             break;


          }
    }

    /**
     * 改变level
     * @param v
     */

    private void change_level(View v) {


        StringBuilder sb = new StringBuilder(4);
        switch (v.getId()) {
            case R.id.btn_red:
                level = Note.RED_LEVEL;
                sb.append("Red");
                break;
            case R.id.btn_orange:
                level = Note.ORA_LEVEL;
                sb.append("Orange");
                break;
            case R.id.btn_green:
                level = Note.GRE_LEVEL;
                sb.append("Green");
                break;
        }
        MsgToast.showToast(this, sb.toString());
    }

    /**
     * 键盘的显示和隐藏
     */
    private void hideOrOpenKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
