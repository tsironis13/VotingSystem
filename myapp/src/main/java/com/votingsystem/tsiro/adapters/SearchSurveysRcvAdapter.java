package com.votingsystem.tsiro.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.POJO.SurveysFields;
import com.votingsystem.tsiro.votingsystem.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by giannis on 28/8/2016.
 */
public class SearchSurveysRcvAdapter extends RecyclerView.Adapter {

    private static final String debugTag = SearchSurveysRcvAdapter.class.getSimpleName();
    private List<SurveysFields> data;
    private SimpleDateFormat simpleDateFormat;

    public SearchSurveysRcvAdapter() {
        simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View view   =   LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_item, parent, false);
        vh          =   new SurveysItemViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String details;
        if (holder instanceof SurveysItemViewHolder) {
            ((SurveysItemViewHolder)holder).title.setText(data.get(position).getTitle());
            ((SurveysItemViewHolder)holder).responses.setText(String.format(Locale.getDefault(), "%d", data.get(position).getResponses()));
            Date date = new Date(data.get(position).getLastModified() * 1000L);
            details = "Τροποποιήθηκε " + simpleDateFormat.format(date);
            ((SurveysItemViewHolder)holder).details.setText(details);
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public void setData(List<SurveysFields> data) {
        this.data = data;
    }

    static class SurveysItemViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout surveyItemContainerRlt;
        private TextView title, details, responses;

        public SurveysItemViewHolder(View itemView) {
            super(itemView);
            surveyItemContainerRlt  =   (RelativeLayout) itemView.findViewById(R.id.surveyItemContainerRlt);
            title                   =   (TextView) itemView.findViewById(R.id.titleTtv);
            details                 =   (TextView) itemView.findViewById(R.id.detailsTtv);
            responses               =   (TextView) itemView.findViewById(R.id.responsesTtv);
        }

    }


}
