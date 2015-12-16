package com.votingsystem.tsiro.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.votingsystem.tsiro.POJO.NavDrawerData;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by user on 27/11/2015.
 */
public class NavDrawerRcViewAdapter extends RecyclerView.Adapter<NavDrawerRcViewAdapter.NavDrawerRcViewHolder> {

    private static final String debugTag = "NavDrawerRcViewAdapter";
    private LayoutInflater inflater;
    private List<NavDrawerData> navDrawerList = Collections.emptyList();

    public NavDrawerRcViewAdapter(Context context, List<NavDrawerData> navDrawerList){
        inflater                = LayoutInflater.from(context);
        this.navDrawerList      = navDrawerList;
    }

    @Override
    public NavDrawerRcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        NavDrawerRcViewHolder navDrawerRcViewHolder = new NavDrawerRcViewHolder(view);
        return navDrawerRcViewHolder;
    }

    @Override
    public void onBindViewHolder(NavDrawerRcViewHolder holder, int position) {
        NavDrawerData navDrawerData = navDrawerList.get(position);
        holder.navDrawerImv.setImageResource(navDrawerData.iconId);
        holder.navDrawerTtv.setText(navDrawerData.category);
    }

    public List<NavDrawerData> getListItem(int item) {
        return this.navDrawerList;
    }

    @Override
    public int getItemCount() {
        return navDrawerList.size();
    }

    public class NavDrawerRcViewHolder extends RecyclerView.ViewHolder{

        private ImageView navDrawerImv;
        private TextView navDrawerTtv;

        public NavDrawerRcViewHolder(View itemView) {
            super(itemView);
            navDrawerImv = (ImageView) itemView.findViewById(R.id.navDrawerImv);
            navDrawerTtv = (TextView) itemView.findViewById(R.id.navDrawerTtv);
        }
    }
}
