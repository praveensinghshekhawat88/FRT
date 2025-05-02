package com.callmangement.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.callmangement.R;
import com.callmangement.model.inventrory.ModelPartsList;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {
    Activity context;
    List<ModelPartsList> list;
    LayoutInflater inflater;

    public SpinnerAdapter(Activity context,List<ModelPartsList> list){
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.spinner_dropdown_item, null);
        TextView tvName = view.findViewById(R.id.textSpinner);
        if (!list.get(position).isVisibleItemFlag())
            tvName.setVisibility(View.VISIBLE);
        else {
            tvName.setVisibility(View.GONE);
            tvName.setMaxHeight(0);
            tvName.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        tvName.setText(list.get(position).getItemName());
        return view;

    }
}
