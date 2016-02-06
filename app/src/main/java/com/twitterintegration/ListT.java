package com.twitterintegration;

import android.app.ListActivity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

/**
 * Created by ankit on 07/02/16.
 */
public class ListT extends ListActivity {
    SearchTimeline searchTimeline;
    TweetTimelineListAdapter adapter;
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    String newTag = "wingify";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mHandler = new Handler();

        searchTimeline = new SearchTimeline.Builder().query("#wingify").build();
        adapter = new TweetTimelineListAdapter(this, searchTimeline);
        setListAdapter(adapter);


        EditText edittext = (EditText) findViewById(R.id.editText);
        edittext.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        edittext.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            onSearchAction(v, v.getText().toString());
                            return true;
                        }
                        return false;
                    }
                });

    }

    public void onSearchAction(View v, String tag) {
        newTag = tag;
        stopRepeatingTask();
        startRepeatingTask();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(ListT.this, "Refreshing data..", Toast.LENGTH_SHORT).show();

            searchTimeline = new SearchTimeline.Builder().query("#" + newTag).build();
            adapter = new TweetTimelineListAdapter(ListT.this, searchTimeline);
            setListAdapter(adapter);

            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        stopRepeatingTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopRepeatingTask();
        startRepeatingTask();
    }
}