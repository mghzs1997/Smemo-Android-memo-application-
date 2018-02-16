package com.lfwl.smemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mgh on 2017/11/25.
 */

public class memoEdit extends Activity {
    public static int ENTER_STATE = 0;
    public static long ID = 0;
    private TextView edit_date;
    private EditText edit_text;
    private TextView edit_title;
    private TextView edit_finish;
    private TextView edit_back;
    private LinearLayout edit_delect;
    private LinearLayout edit_deletearea;
    private DBAdapter db;
    private Context thiscontext;
    private Memo memos[];
    private Memo memo;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.editlayout);
        edit_date = (TextView) findViewById(R.id.edit_date);
        edit_text = (EditText) findViewById(R.id.edit_text);
        edit_title = (TextView) findViewById(R.id.edit_title);
        edit_finish = (TextView) findViewById(R.id.edit_finish);
        edit_back = (TextView) findViewById(R.id.edit_back);
        edit_delect = (LinearLayout) findViewById(R.id.edit_delete);
        edit_deletearea = (LinearLayout) findViewById(R.id.edit_deletearea);
        thiscontext = this;
        db = new DBAdapter(thiscontext);
        if (ENTER_STATE == 0) {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String dateString = sdf.format(date);
            edit_title.setText(R.string.memo_new);
            edit_deletearea.setVisibility(View.INVISIBLE);
            edit_date.setText(dateString);
            memo = new Memo();
            memo.date = dateString;
            edit_text.requestFocus();
        } else if (ENTER_STATE == 1) {
            db.open();
            memos = db.getOneDateByID(ID);
            memo = memos[0];
            db.close();
            edit_text.setFocusable(false);
            edit_title.setText(R.string.memo_detail);
            edit_date.setText(memo.date);
            edit_text.setText(memo.text);
            edit_finish.setVisibility(View.INVISIBLE);
            edit_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edit_text.setFocusable(true);
                    edit_text.setFocusableInTouchMode(true);
                    edit_text.requestFocus();
                    edit_title.setText(R.string.memo_edit);
                    edit_finish.setVisibility(View.VISIBLE);
                    edit_deletearea.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edit_text,0);
                }
            });
        }
        edit_back.setOnClickListener(new backOnClickListener());
        edit_finish.setOnClickListener(new backOnClickListener());
        edit_delect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(thiscontext);
                builder.setTitle("删除该便签");
                builder.setMessage("确认删除该便签吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.open();
                        db.deleteOneData(ID);
                        db.close();
                        Intent data = new Intent();
                        setResult(1, data);
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create();
                builder.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new backOnClickListener().onClick(null);
        super.onBackPressed();
    }

    class backOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            memo.text = edit_text.getText().toString();
            if (ENTER_STATE == 0) {
                if (!memo.text.equals("")) {
                    db.open();
                    db.insert(memo);
                    db.close();
                } else {
                    Intent data = new Intent();
                    setResult(0, data);
                    finish();
                }
            } else {
                if (memo.text.equals("")) {
                    db.open();
                    db.deleteOneData(ID);
                    db.close();
                } else {
                    db.open();
                    db.updateOneData(ID, memo);
                    db.close();
                }
            }
            Intent data = new Intent();
            setResult(1, data);
            finish();
        }
    }
}
