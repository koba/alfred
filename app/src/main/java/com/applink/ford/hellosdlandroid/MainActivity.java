package com.applink.ford.hellosdlandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.applink.ford.hellosdlandroid.activities.TwitterLoginActivity_;
import com.applink.ford.hellosdlandroid.providers.TwitterProvider;
import com.applink.ford.hellosdlandroid.views.adapter.ProvidersListAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.reactivex.functions.Consumer;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    @ViewById(R.id.providers_list)
    ListView providersListView;

    ArrayAdapter<String> providersListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @AfterViews
    protected void init() {
        Alfred.getInstance().wakeUp();

        initProviders();
        initProvidersListView();
    }

    @Background
    protected void initProviders() {
        TwitterProvider twitter = new TwitterProvider();
        twitter.onNotificationReceived(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Alfred.getInstance().speak(s);
            }
        });
    }

    private void initProvidersListView() {
        providersListAdapter = new ProvidersListAdapter(this, android.R.layout.simple_list_item_1);
        providersListView.setAdapter(providersListAdapter);
        providersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(MainActivity.this, TwitterLoginActivity_.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
