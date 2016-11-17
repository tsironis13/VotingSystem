package com.votingsystem.tsiro.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.rey.material.widget.Button;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.interfaces.OnLoadMoreListener;
import com.votingsystem.tsiro.parcel.SurveyData;
import com.votingsystem.tsiro.votingsystem.R;
import java.util.List;
import java.util.Locale;

/**
 * Created by giannis on 20/6/2016.
 */
public class SurveysRcvAdapter extends RecyclerView.Adapter {

    private static final String debugTag = SurveysRcvAdapter.class.getSimpleName();
    private List<SurveyData> data;
    private final int VIEW_ITEM = 1;
    private int visibleThreshold = 8;
    private int pages = 1;
    private int lastVisibleItem, totalItemCount, VIEW_PROG;
    private boolean isLoading, onSwipe;
    private OnLoadMoreListener onLoadMoreListener;
    private String type;

    public SurveysRcvAdapter(final List<SurveyData> data, RecyclerView recyclerView, final String type) {
        this.data   =   data;
        this.type   =   type;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (onSwipe) {
                        pages = 1;
                        isLoading = false;
                    }
                    totalItemCount  =   linearLayoutManager.getItemCount();
                    lastVisibleItem =   linearLayoutManager.findLastVisibleItemPosition();
//                    Log.e(debugTag, "Is Loading: "+isLoading+" Total items: "+totalItemCount+" Last visible item: "+lastVisibleItem+" Visible Threshold: "+visibleThreshold);

                    // !onSwipe because onScrolled callback is called even when recycler view is not scrolled and just onSwiped
                    if (!onSwipe && !isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                        if (onLoadMoreListener != null) onLoadMoreListener.onLoadMore(++pages);
                        isLoading = true;
                    }
                    onSwipe = false;
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View view;
        if (viewType == VIEW_ITEM) {
            if (!type.equals("under_process")) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_item, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.under_process_survey_item, parent, false);
            }
            vh = new SurveysItemViewHolder(view);
//        } else if (viewType == VIEW_PROG) {
//            view    =   LayoutInflater.from(parent.getContext()).inflate(R.layout.surveys_recyclerview_loader, parent, false);
//            vh      =   new ProgressViewHolder(view);
//        } else {
        } else {
            view    =   LayoutInflater.from(parent.getContext()).inflate(R.layout.list_surveys_load_more_error_template, parent, false);
            vh      =   new ErrorViewHolder(view);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String details;
        if (holder instanceof SurveysItemViewHolder) {
            ((SurveysItemViewHolder) holder).title.setText(data.get(position).getTitle());
            if (type.equals("pending")) {
                details = "Ημ. έναρξης " + data.get(position).getActiveSince();
                ((SurveysItemViewHolder) holder).details.setText(details);
                ((SurveysItemViewHolder) holder).responses.setVisibility(View.GONE);
            } else if (type.equals("completed") || type.equals("ongoing") || type.equals("approved")) {
                details = "Ημ. λήξης " + data.get(position).getValidUntil();
                ((SurveysItemViewHolder) holder).details.setText(details);
            } else {
                details = "Ημ. δημιουργίας " + data.get(position).getCreatedDate();
                ((SurveysItemViewHolder) holder).details.setText(details);
            }
            if (type.equals("ongoing") || type.equals("approved")) {
                if (!data.get(position).getIsAnswered()) {
                    ((SurveysItemViewHolder) holder).answered.setVisibility(View.VISIBLE);
                    ((SurveysItemViewHolder) holder).surveyItemContainerRlt.setTag(0);
                } else {
                    ((SurveysItemViewHolder) holder).surveyItemContainerRlt.setTag(0);
                    ((SurveysItemViewHolder) holder).surveyItemContainerRlt.setTag(1);
                }
                if (data.get(position).getIsAnswered() && ((SurveysItemViewHolder) holder).answered.getVisibility() == View.VISIBLE)
                    ((SurveysItemViewHolder) holder).answered.setVisibility(View.INVISIBLE);
            }
            ((SurveysItemViewHolder) holder).responses.setText(String.format(Locale.getDefault(), "%d", data.get(position).getResponses()));
        }
//        } else if (holder instanceof ProgressViewHolder) {
            //PROGRESS VIEW REMOVED
//            ((ProgressViewHolder)holder).progressView.start();
        else {
            ((ErrorViewHolder)holder).retryBtn.setTransformationMethod(null);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return -1;
//            if (connectionStatus != AppConfig.NO_CONNECTION) {
//                return VIEW_PROG;
//            } else {
//                return -1;
//            }
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public void onSwipeToRefresh() {
        onSwipe = true;
    }

    public void refreshData(List<SurveyData> data) {
        this.data = data;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener1) {
        this.onLoadMoreListener = onLoadMoreListener1;
    }

    public void setLoaded() {
        isLoading = false;
    }

    private static class SurveysItemViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout surveyItemContainerRlt;
        private TextView title, details, responses, answered;

        SurveysItemViewHolder(View itemView) {
            super(itemView);
            surveyItemContainerRlt  =   (RelativeLayout) itemView.findViewById(R.id.surveyItemContainerRlt);
            title                   =   (TextView) itemView.findViewById(R.id.titleTtv);
            details                 =   (TextView) itemView.findViewById(R.id.detailsTtv);
            responses               =   (TextView) itemView.findViewById(R.id.responsesTtv);
            answered                =   (TextView) itemView.findViewById(R.id.answeredTtv);
        }
    }

//    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
//        private ProgressView progressView;
//
//        ProgressViewHolder(View view) {
//            super(view);
//            progressView = (ProgressView) view.findViewById(R.id.loader);
//        }
//    }

    private static class ErrorViewHolder extends RecyclerView.ViewHolder {
        private Button retryBtn;

        ErrorViewHolder(View view) {
            super(view);
            retryBtn = (Button) view.findViewById(R.id.lretryBtn);
        }
    }
}
