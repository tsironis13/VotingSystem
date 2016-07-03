package com.votingsystem.tsiro.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.List;

/**
 * Created by giannis on 29/6/2016.
 */
public class SurveyListPickerQuestionRcvAdapter extends RecyclerView.Adapter {

    private static final String debugTag = SurveyRankingQuestionRcvAdapter.class.getSimpleName();
    private List<String> answers;

    public SurveyListPickerQuestionRcvAdapter(List<String> answers) {
        this.answers = answers;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AnswerItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_picker_question_answer_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Log.e(debugTag, ((AnswerItemViewHolder)holder).answerTtv+"");
        ((AnswerItemViewHolder)holder).answerTtv.setText(answers.get(position));
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }


    static class AnswerItemViewHolder extends RecyclerView.ViewHolder {

        private TextView answerTtv;

        public AnswerItemViewHolder(View itemView) {
            super(itemView);
            answerTtv = (TextView) itemView.findViewById(R.id.answerTtv);
        }
    }
}
