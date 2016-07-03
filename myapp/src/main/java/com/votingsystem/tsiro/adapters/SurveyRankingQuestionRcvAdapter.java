package com.votingsystem.tsiro.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.interfaces.ItemTouchHelperAdapter;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by giannis on 27/6/2016.
 */
public class SurveyRankingQuestionRcvAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    private static final String debugTag = SurveyRankingQuestionRcvAdapter.class.getSimpleName();
    private List<String> answers;
    private Context context;

    public SurveyRankingQuestionRcvAdapter(List<String> answers) {
        this.answers = answers;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new AnswerItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_question_answer_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) ((AnswerItemViewHolder)holder).rankingQuestionItemLlt.setElevation((float) MyApplication.convertPixelToDpAndViceVersa(this.context, 0, 2));
        ((AnswerItemViewHolder)holder).answerTtv.setText(answers.get(position));
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(answers, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(answers, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    static class AnswerItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout rankingQuestionItemLlt;
        private TextView answerTtv;

        public AnswerItemViewHolder(View itemView) {
            super(itemView);
            rankingQuestionItemLlt  = (LinearLayout) itemView.findViewById(R.id.rankingQuestionItemLlt);
            answerTtv               = (TextView) itemView.findViewById(R.id.answerTtv);
        }
    }
}
