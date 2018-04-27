package com.applink.ford.hellosdlandroid.views.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.applink.ford.hellosdlandroid.R;

import java.util.List;

/**
 * Created by agurz on 4/27/18.
 */

public class ProvidersListAdapter extends ArrayAdapter<String> {

    private String[] providers = {
        "TwitterProvider"
    };

    private int[] providersIcons = {
        R.mipmap.ic_twitter
    };

    public ProvidersListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
        return providers.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        View view = inflater.inflate(R.layout.listitem_provider, null, true);
        ((ImageView)view.findViewById(R.id.provider_icon)).setImageResource(providersIcons[position]);
        ((TextView)view.findViewById(R.id.provider_name)).setText(providers[position]);
        return view;
    }

}
