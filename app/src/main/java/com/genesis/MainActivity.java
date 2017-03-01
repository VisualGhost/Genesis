package com.genesis;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.genesis.loading.DataLoadingController;

public class MainActivity extends AppCompatActivity {

    private DataLoadingController mLoadingController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.results_id);
        mLoadingController = new DataLoadingController(this, savedInstanceState, textView);

        if (mLoadingController.isDataLoaded()) {
            textView.setText(mLoadingController.getResult());
        } else {
            mLoadingController.subscribeOn(getLastCustomNonConfigurationInstance());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mLoadingController.saveLoadingState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadingController.clear();
        ((CustomApplication) getApplication()).getRefWatcher().watch(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mLoadingController.getConnectableObservable();
    }
}
