package com.votingsystem.tsiro.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.parcel.QuestionStatsDetails;
import com.votingsystem.tsiro.parcel.Stats;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;
import com.votingsystem.tsiro.votingsystem.R;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by giannis on 16/7/2016.
 */
public class SurveyStatsFragment extends Fragment implements View.OnClickListener {

    private static final String debugTag = SurveyStatsFragment.class.getSimpleName();
    private View view;
    private LinearLayout surveyStatsFgmtContainerLlt, templateLlt;
    private SurveyDetailsData surveyDetailsData;
    private List<Integer> colorsList;
    private HashMap<Integer, Integer> colorHashMap;
    private Random random;
    private int color;

    public static SurveyStatsFragment newInstance(SurveyDetailsData surveyDetailsData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", surveyDetailsData);
        SurveyStatsFragment surveyStatsFragment = new SurveyStatsFragment();
        surveyStatsFragment.setArguments(bundle);
        return surveyStatsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) view  =   inflater.inflate(R.layout.fragment_survey_stats, container, false);
        surveyStatsFgmtContainerLlt = (LinearLayout) view.findViewById(R.id.surveyStatsFgmtContainerLlt);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            getActivity().setTitle(getActivity().getResources().getString(R.string.stats_title));

            if (getArguments() != null) {
                surveyDetailsData = getArguments().getParcelable(getActivity().getResources().getString(R.string.data_parcelable_key));
                if (surveyDetailsData != null) {
                    colorHashMap    =   new HashMap<>();
                    random          =   new Random();
                    initializeQuestionStats(surveyDetailsData);
                }
            }
        }
    }

    private void initializeQuestionStats(SurveyDetailsData surveyDetailsData) {
        for (int i = 0; i < surveyDetailsData.getQuestion().size(); i++) {
            templateLlt                         = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_survey_stats_template, (ViewGroup) getActivity().findViewById(R.id.templateLlt), false);
            TextView textView                   = (TextView) templateLlt.findViewById(R.id.questionTitleTtv);
            LinearLayout chartsLlt              = (LinearLayout) templateLlt.findViewById(R.id.chartsContainerLlt);
            chartsLlt.setTag(i);
            FloatingActionsMenu menu            = (FloatingActionsMenu) templateLlt.findViewById(R.id.FAmenu);
            menu.setTag(i);
            FloatingActionButton holePieChart   = (FloatingActionButton) templateLlt.findViewById(R.id.holePieChartFAB);
            FloatingActionButton pieChart       = (FloatingActionButton) templateLlt.findViewById(R.id.pieChartFAB);
            FloatingActionButton barChart       = (FloatingActionButton) templateLlt.findViewById(R.id.barChartFAB);
            FloatingActionButton horBarChart    = (FloatingActionButton) templateLlt.findViewById(R.id.horBarChartFAB);
            holePieChart.setOnClickListener(this);
            pieChart.setOnClickListener(this);
            barChart.setOnClickListener(this);
            horBarChart.setOnClickListener(this);

            List<Stats> stats = surveyDetailsData.getQuestion().get(i).getStats();
            textView.setText(i+1+". "+surveyDetailsData.getQuestion().get(i).getTitle());
            surveyStatsFgmtContainerLlt.addView(templateLlt);

            if (surveyDetailsData.getQuestion().get(i).getTypeId() != 800) {
                chartsLlt.addView(initializePieChart(stats, false, false));
                for (int y = 0; y < stats.size(); y++) {
                    LinearLayout statsDetailsLlt    = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.stats_details_container, (ViewGroup) getActivity().findViewById(R.id.statsDetailsLlt), false);
                    View colorView                  = statsDetailsLlt.findViewById(R.id.colorView);
                    TextView answerTtv              = (TextView) statsDetailsLlt.findViewById(R.id.answerTtv);
                    TextView percentageTtv          = (TextView) statsDetailsLlt.findViewById(R.id.percentageTtv);
                    TextView countTtv               = (TextView) statsDetailsLlt.findViewById(R.id.countTtv);

                    if (stats.get(y).getPercentage() != 0) {
                        colorView.setBackgroundColor(colorHashMap.get(stats.get(y).getIndex()));
                    }
                    answerTtv.setText(stats.get(y).getTitle());
                    percentageTtv.setText(""+stats.get(y).getPercentage());
                    countTtv.setText(""+stats.get(y).getCount());
                    surveyStatsFgmtContainerLlt.addView(statsDetailsLlt);
                }
            } else {

            }
        }
    }

    private PieChart initializePieChart(List<Stats> stats, boolean drawHole, boolean menuClicked) {
        colorsList = new ArrayList<>();
        PieChart pieChart   =   new PieChart(getActivity());
        pieChart.setDescription("");
        pieChart.setMinimumHeight((int) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0 , 200));
        pieChart.setDrawHoleEnabled(drawHole);
        pieChart.setRotationEnabled(false);
        List<PieEntry> pieEntryList = new ArrayList<>();
        for (int i = 0; i < stats.size(); i++) {
            if (stats.get(i).getPercentage() != 0) {
                PieEntry pieEntry = new PieEntry(stats.get(i).getPercentage(), "");
                pieEntryList.add(pieEntry);
                if (!menuClicked) {
                    color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                    colorHashMap.put(stats.get(i).getIndex(), color);
                    colorsList.add(color);
                } else {
                    colorsList.add(colorHashMap.get(stats.get(i).getIndex()));
                }

            }
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setDrawValues(false);
        pieDataSet.setColors(colorsList);

        PieData data = new PieData(pieDataSet);
        pieChart.setData(data);
        pieChart.animateXY(800, 800);
        pieChart.invalidate();
        return pieChart;
    }

    private BarChart initializeBarChart(List<Stats> stats) {
        colorsList = new ArrayList<>();
        BarChart barChart = new BarChart(getActivity());
        List<BarEntry>  barEntryList = new ArrayList<>();
        for (int i = 0; i < stats.size(); i++) {
            if (stats.get(i).getPercentage() != 0) {
//                BarEntry barEntry = new BarEntry(stats.get(i).getPercentage(), "");
            }
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        if (templateLlt != null  && view != null && view instanceof FloatingActionButton) {
            FloatingActionsMenu menu    = (FloatingActionsMenu) view.getParent();
            LinearLayout layout         = (LinearLayout) surveyStatsFgmtContainerLlt.findViewWithTag(menu.getTag());
            if (layout != null) layout.removeAllViews();
            switch (view.getId()) {
                case R.id.horBarChartFAB:
                    break;
                case R.id.barChartFAB:
                    break;
                case R.id.pieChartFAB:
                    if (layout != null) layout.addView(initializePieChart(surveyDetailsData.getQuestion().get((Integer) menu.getTag()).getStats(), false, true));
                    break;
                case R.id.holePieChartFAB:
                    if (layout != null) layout.addView(initializePieChart(surveyDetailsData.getQuestion().get((Integer) menu.getTag()).getStats(), true, true));
                    break;
            }
            menu.collapse();
        }
    }
}
