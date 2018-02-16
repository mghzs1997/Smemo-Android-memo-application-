package com.lfwl.smemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private Context thiscontext;
    private TextView addmemo;
    private ListView listview;
    private TextView editmemo;
    private SimpleAdapter simpleadapter;
    private List<Map<String, Object>> memolist;
    private DBAdapter db;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        thiscontext = this;
        listview = (ListView) findViewById(R.id.listview);
        memolist = new ArrayList<Map<String, Object>>();
        addmemo = (TextView) findViewById(R.id.addmemo);
        editmemo = (TextView) findViewById(R.id.editmemo);

        editmemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(memolist.size() != 0) {
                    Intent intent = new Intent(thiscontext, memoDelect.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("info", "");
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 2);
                }
            }
        });

        addmemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoEdit.ENTER_STATE = 0;
                Intent intent = new Intent(thiscontext, memoEdit.class);
                Bundle bundle = new Bundle();
                bundle.putString("info", "");
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

        db = new DBAdapter(thiscontext);
        RefreshMemoList();
        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);
        listview.setOnScrollListener(this);
    }

    public void RefreshMemoList() {
        int size = memolist.size();
        if (size > 0) {
            memolist.removeAll(memolist);
            simpleadapter.notifyDataSetChanged();
            listview.setAdapter(simpleadapter);
        }
        simpleadapter = new SimpleAdapter(thiscontext, getData(), R.layout.listviewlayout, new String[] { "tv_text", "tv_date" },
                new int[] {R.id.tv_text, R.id.tv_date });
        listview.setAdapter(simpleadapter);
    }

    private List<Map<String, Object>> getData() {
        db.open();
        Memo[] memos = db.getAllDate();
        db.close();
        if(memos != null) {
            for (int i = 0; i < memos.length; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("tv_id", memos[i].id);
                map.put("tv_text", memos[i].text);
                map.put("tv_date", memos[i].date.substring(memos[i].date.indexOf('0') + 1,memos[i].date.indexOf(' ')));
                memolist.add(map);
            }
        }
        return memolist;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        memoEdit.ENTER_STATE = 1;
        String strposition = listview.getItemAtPosition(position).toString();
        String strid = strposition.substring(strposition.indexOf("tv_id=") + 6, strposition.indexOf(",", strposition.indexOf("tv_id=") + 1));
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("info", "");
        memoEdit.ID = Long.parseLong(strid);
        intent.putExtras(bundle);
        intent.setClass(thiscontext, memoEdit.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        memoEdit.ENTER_STATE = 1;
        String strposition = listview.getItemAtPosition(position).toString();
        String strid = strposition.substring(strposition.indexOf("tv_id=") + 6, strposition.indexOf(",", strposition.indexOf("tv_id=") + 1));
        memoEdit.ID = Long.parseLong(strid);
        AlertDialog.Builder builder = new AlertDialog.Builder(thiscontext);
        builder.setTitle("删除该便签");
        builder.setMessage("确认删除该便签吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.open();
                db.deleteOneData(memoEdit.ID);
                db.close();
                Intent data = new Intent();
                setResult(1, data);
                RefreshMemoList();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create();
        builder.show();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 0 || requestCode == 1 || requestCode == 2) && resultCode == 1) {
            RefreshMemoList();
        }
    }
}
