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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mgh on 2017/11/25.
 */

public class memoDelect extends Activity {

    private Context thiscontext;
    private ListView listview;
    private List<Map<String, Object>> memolist;
    private HashMap<Integer, Boolean> selected;
    private DBAdapter db;
    private SimpleAdapter simpleadapter;
    private TextView choose_cancel;
    private TextView choose_title;
    private LinearLayout choose_delete;
    private CheckBox ctv_cb;
    private int selectnum;
    private Memo memos[];
    //private TextView choose_all;
    //private TextView choose_fan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.deletelayout);
        thiscontext = this;
        listview = (ListView) findViewById(R.id.choose_listview);
        memolist = new ArrayList<Map<String, Object>>();
        db = new DBAdapter(thiscontext);
        choose_cancel = (TextView) findViewById(R.id.choose_cancel);
        choose_title = (TextView) findViewById(R.id.choose_title);
        choose_delete = (LinearLayout) findViewById(R.id.choose_delete);
        //choose_all = (TextView) findViewById(R.id.choose_all);
        //choose_fan = (TextView) findViewById(R.id.choose_fan);
        selectnum = 0;
        RefreshMemoList();
        choose_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                setResult(0, data);
                finish();
            }
        });
        choose_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectnum > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(thiscontext);
                    builder.setTitle("删除选中便签");
                    builder.setMessage("确认删除选中便签吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.open();
                            for(int i = 0; i < selected.size(); i++) {
                                if(selected.get(i)) {
                                    db.deleteOneData(memos[i].id);
                                }
                            }
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
            }
        });
    }

    public void RefreshMemoList() {
        simpleadapter = new SimpleAdapter(thiscontext, getData(), R.layout.choose_listview, new String[] { "ctv_text", "ctv_date", "ctv_selected" },
                new int[] {R.id.ctv_text, R.id.ctv_date, R.id.ctv_cb }) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ctv_cb = (CheckBox)view.findViewById(R.id.ctv_cb);
                if(selected.containsKey(position) == false || selected.get(position) == false) {
                    ctv_cb.setChecked(false);
                } else {
                    ctv_cb.setChecked(true);
                }
                ctv_cb.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(View view) {
                        if(((CheckBox)view).isChecked()){
                            selected.remove(position);
                            selected.put(position, true);
                            selectnum++;
                        }else{
                            selected.remove(position);
                            selected.put(position, false);
                            selectnum--;
                        }
                        if(selectnum > 0) {
                            choose_title.setText(getString(R.string.selected1) + selectnum + getString(R.string.selected2));
                        } else {
                            choose_title.setText(R.string.choose);
                        }
                    }
                }

                );
                return view;
            }
        };
        listview.setAdapter(simpleadapter);
    }

    private List<Map<String, Object>> getData() {
        db.open();
        memos = db.getAllDate();
        selected = new HashMap<Integer, Boolean>(memos.length);
        db.close();
        if(memos != null) {
            for (int i = 0; i < memos.length; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                selected.put(i, false);
                map.put("ctv_id", memos[i].id);
                map.put("ctv_text", memos[i].text);
                map.put("ctv_date", memos[i].date.substring(memos[i].date.indexOf('0') + 1,memos[i].date.indexOf(' ')));
                memolist.add(map);
            }
        }
        return memolist;
    }
}