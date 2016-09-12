package com.votingsystem.tsiro.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.rey.material.app.Dialog;
import com.rey.material.widget.PopupWindow;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.parcel.QuestionStatsDetails;
import com.votingsystem.tsiro.parcel.Stats;
import com.votingsystem.tsiro.parcel.SurveyDetailsData;
import com.votingsystem.tsiro.votingsystem.R;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private List<String> freeAnswersList;
    private HashMap<Integer, Integer> colorHashMap, columnMatrixViewColors;
    private HashMap<Integer, List<String>> freeAnswersMap;
    private HashMap<Integer, HashMap<Integer, List<Integer>>> matrixHashData;
    private HashMap<Integer, Integer> currentlyHASH;
    private Random random;
    private int color, freeAnswersIndex;

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
            freeAnswersMap = new HashMap<>();

            if (getArguments() != null) {
                surveyDetailsData = getArguments().getParcelable(getActivity().getResources().getString(R.string.data_parcelable_key));
                if (surveyDetailsData != null) {
                    currentlyHASH   =   new HashMap<>();
                    matrixHashData  =   new HashMap<>();
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
            LinearLayout noAnswersLlt           = (LinearLayout) templateLlt.findViewById(R.id.noAnswersLlt);
            LinearLayout answeredSkippedLlt     = (LinearLayout) templateLlt.findViewById(R.id.answeredSkippedLlt);
            TextView answeredByTtv              = (TextView) templateLlt.findViewById(R.id.answeredByTtv);
            TextView skippedTtv                 = (TextView) templateLlt.findViewById(R.id.skippedTtv);
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
            textView.setText(getResources().getString(R.string.question_title, i+1, surveyDetailsData.getQuestion().get(i).getTitle()));
            surveyStatsFgmtContainerLlt.addView(templateLlt);

            int type_id = surveyDetailsData.getQuestion().get(i).getTypeId();
            if (type_id == 100 || type_id == 200 || type_id == 400 || type_id == 500 || type_id == 600) {
                if (surveyDetailsData.getQuestion().get(i).getNoAnswers()) {
                    chartsLlt.setVisibility(View.GONE);
                    menu.setVisibility(View.GONE);
                    noAnswersLlt.setVisibility(View.VISIBLE);
                } else {
                    answeredByTtv.setText(getResources().getString(R.string.answered_by, surveyDetailsData.getQuestion().get(i).getAnswered()));
                    skippedTtv.setText(getResources().getString(R.string.skipped, surveyDetailsData.getQuestion().get(i).getSkipped()));
                    chartsLlt.addView(initializePieChart(stats, false, false, 1, null));
                    for (int y = 0; y < stats.size(); y++) {
                        LinearLayout statsDetailsLlt    = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.stats_details_container, (ViewGroup) getActivity().findViewById(R.id.statsDetailsLlt), false);
                        View colorView                  = statsDetailsLlt.findViewById(R.id.colorView);
                        TextView answerTtv              = (TextView) statsDetailsLlt.findViewById(R.id.answerTtv);
                        TextView percentageTtv          = (TextView) statsDetailsLlt.findViewById(R.id.percentageTtv);
                        TextView countTtv               = (TextView) statsDetailsLlt.findViewById(R.id.countTtv);

                        if (stats.get(y).getPercentage() != 0) {
                            colorView.setBackgroundColor(colorHashMap.get(stats.get(y).getIndex()));
                            setBoldTextColor(answerTtv);
                            setBoldTextColor(percentageTtv);
                            setBoldTextColor(countTtv);
                        }
                        answerTtv.setText(stats.get(y).getTitle());
                        percentageTtv.setText(getResources().getString(R.string.persentage_text, stats.get(y).getPercentage()));
                        countTtv.setText(getResources().getString(R.string.just_text, stats.get(y).getCount()));
                        surveyStatsFgmtContainerLlt.addView(statsDetailsLlt);
                    }
                }

            } else if (type_id == 300) { //RANKING
                if (surveyDetailsData.getQuestion().get(i).getNoAnswers()) {
                    chartsLlt.setVisibility(View.GONE);
                    menu.setVisibility(View.GONE);
                    noAnswersLlt.setVisibility(View.VISIBLE);
                } else {
                    answeredByTtv.setText(getResources().getString(R.string.answered_by, surveyDetailsData.getQuestion().get(i).getAnswered()));
                    skippedTtv.setText(getResources().getString(R.string.skipped, surveyDetailsData.getQuestion().get(i).getSkipped()));
                    chartsLlt.addView(initializePieChart(stats, false, false, 2, null));
                    for (int y = 0; y < stats.size(); y++) {
                        LinearLayout statsDetailsLlt    = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.stats_details_container, (ViewGroup) getActivity().findViewById(R.id.statsDetailsLlt), false);
                        View colorView                  = statsDetailsLlt.findViewById(R.id.colorView);
                        TextView answerTtv              = (TextView) statsDetailsLlt.findViewById(R.id.answerTtv);
                        TextView countTtv               = (TextView) statsDetailsLlt.findViewById(R.id.countTtv);

                        colorView.setBackgroundColor(colorHashMap.get(stats.get(y).getIndex()));
                        setBoldTextColor(answerTtv);
                        setBoldTextColor(countTtv);
                        answerTtv.setText(stats.get(y).getTitle());
                        countTtv.setText(getResources().getString(R.string.just_text, stats.get(y).getRankingPoints()));
                        surveyStatsFgmtContainerLlt.addView(statsDetailsLlt);
                    }
                }
            } else if (type_id == 800) { //FREE ANSWER
                if (surveyDetailsData.getQuestion().get(i).getNoAnswers()) {
                    chartsLlt.setVisibility(View.GONE);
                    menu.setVisibility(View.GONE);
                    noAnswersLlt.setVisibility(View.VISIBLE);
                } else {
                    freeAnswersIndex++;
                    freeAnswersList = surveyDetailsData.getQuestion().get(i).getFreeAnswers();
                    freeAnswersMap.put(freeAnswersIndex, freeAnswersList);

                    answeredSkippedLlt.setVisibility(View.GONE);
                    chartsLlt.setVisibility(View.GONE);
                    menu.setVisibility(View.GONE);
                    LinearLayout freeAnswersLlt = (LinearLayout) templateLlt.findViewById(R.id.freeAnswerLlt);
                    freeAnswersLlt.setTag(freeAnswersIndex);
                    freeAnswersLlt.setOnClickListener(this);
                    TextView responsesTtv = (TextView) templateLlt.findViewById(R.id.freeAnswersResponsesTtv);
                    freeAnswersLlt.setVisibility(View.VISIBLE);
                    responsesTtv.setText(getResources().getString(R.string.responses_text, freeAnswersList.size()));
                }
            } else { //MATRIX
                Log.e(debugTag, "MATRIX");
                if (surveyDetailsData.getQuestion().get(i).getNoAnswers()) {
                    chartsLlt.setVisibility(View.GONE);
                    menu.setVisibility(View.GONE);
                    noAnswersLlt.setVisibility(View.VISIBLE);
                } else {
                    boolean isSelected = false;
                    answeredByTtv.setText(getResources().getString(R.string.answered_by, surveyDetailsData.getQuestion().get(i).getAnswered()));
                    skippedTtv.setText(getResources().getString(R.string.skipped, surveyDetailsData.getQuestion().get(i).getSkipped()));
                    columnMatrixViewColors = new HashMap<>();
                    columnMatrixViewColors.put(0, Color.argb(255, 255, 87, 34));
                    columnMatrixViewColors.put(1, Color.argb(255, 33, 150, 243));
                    columnMatrixViewColors.put(2, Color.argb(255, 25, 118, 210));
                    columnMatrixViewColors.put(3, Color.argb(255, 198, 40, 40));
                    columnMatrixViewColors.put(4, Color.argb(255, 66, 66, 66));

//                    currentlyHASH.put((Integer) menu.getTag(), 1000);

                    HorizontalScrollView tableLayoutContainer   = (HorizontalScrollView) LayoutInflater.from(getActivity()).inflate(R.layout.matrix_table, (ViewGroup) getActivity().findViewById(R.id.matrixTableContainer), false);
                    LinearLayout tableLayout                    = (LinearLayout) tableLayoutContainer.findViewById(R.id.matrixTableLayout);
                    tableLayout.setTag(menu.getTag());
//                    TextView countPerRowLabelTtv                = (TextView) tableLayoutContainer.findViewById(R.id.countPerRowLabelTtv);
                    View sdisagreeLabelView                     = tableLayoutContainer.findViewById(R.id.sdisagreeLabelView);
                    View disagreeLabelView                      = tableLayoutContainer.findViewById(R.id.disagreeLabelView);
                    View neutralLabelView                       = tableLayoutContainer.findViewById(R.id.neutralLabelView);
                    View agreeLabelView                         = tableLayoutContainer.findViewById(R.id.agreeLabelView);
                    View sagreeLabelView                        = tableLayoutContainer.findViewById(R.id.sagreeLabelView);
                    sdisagreeLabelView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.accentColor));
                    disagreeLabelView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primaryColor));
                    neutralLabelView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primaryColorDark));
                    agreeLabelView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.sb_error_text));
                    sagreeLabelView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.last_created_date_text_color));

                    HashMap<Integer, List<Integer>> tempHashData = new HashMap<>();
                    int row_tag, first_row_index_to_show_data;
                    row_tag = first_row_index_to_show_data = 1000;
                    for (int q = 0; q < stats.size(); q++) {
                        List<Integer> integerListRow    = new ArrayList<>();
                        int countPerRow = 0;
                        LinearLayout tableRow   = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.table_row_template, (ViewGroup) getActivity().findViewById(R.id.tableRow), false);
                        tableRow.setOnClickListener(this);
                        tableRow.setTag(row_tag);
                        TextView answerTtv      = (TextView) tableRow.findViewById(R.id.answerTtv);
                        answerTtv.setText(stats.get(q).getTitle());
                        TextView countPerRowTtv = (TextView) tableRow.findViewById(R.id.countPerRowTtv);

                        TextView sDisagreeCountTtv  = (TextView) tableRow.findViewById(R.id.sDisagreeCountTtv);
                        TextView sDisagreePerseTtv  = (TextView) tableRow.findViewById(R.id.sDisagreePersentageTtv);

                        TextView disagreeCountTtv   = (TextView) tableRow.findViewById(R.id.disagreeCountTtv);
                        TextView disagreePerseTtv   = (TextView) tableRow.findViewById(R.id.disagreePersentageTtv);

                        TextView neutralCountTtv    = (TextView) tableRow.findViewById(R.id.neutralCountTtv);
                        TextView neutralPerseTtv    = (TextView) tableRow.findViewById(R.id.neutralPersentageTtv);

                        TextView agreeCountTtv      = (TextView) tableRow.findViewById(R.id.agreeCountTtv);
                        TextView agreePerseTtv      = (TextView) tableRow.findViewById(R.id.agreePersentageTtv);

                        TextView sAgreeCountTtv     = (TextView) tableRow.findViewById(R.id.sAgreeCountTtv);
                        TextView sAgreePerseTtv     = (TextView) tableRow.findViewById(R.id.sAgreePersentageTtv);

                        for (int y = 0; y < stats.get(q).getMatrixStats().size(); y++) {
                            if (stats.get(q).getMatrixStats().get(y).getCount() != 0) {
//                            Log.e(debugTag, "COUNT: "+stats.get(q).getMatrixStats().get(y).getCount()+ " PERSENTAGE: "+stats.get(q).getMatrixStats().get(y).getPercentage());
                                countPerRow+=stats.get(q).getMatrixStats().get(y).getCount();
                            }
                            integerListRow.add(stats.get(q).getMatrixStats().get(y).getCount());
                            switch (y) {
                                case 0:
                                    if (stats.get(q).getMatrixStats().get(y).getCount() != 0) setBoldTextColor(sDisagreeCountTtv);

                                    sDisagreeCountTtv.setText(getResources().getString(R.string.just_text, stats.get(q).getMatrixStats().get(y).getCount()));
                                    sDisagreePerseTtv.setText(getResources().getString(R.string.persentage_text, stats.get(q).getMatrixStats().get(y).getPercentage()));
                                    break;
                                case 1:
                                    if (stats.get(q).getMatrixStats().get(y).getCount() != 0) setBoldTextColor(disagreeCountTtv);

                                    disagreeCountTtv.setText(getResources().getString(R.string.just_text, stats.get(q).getMatrixStats().get(y).getCount()));
                                    disagreePerseTtv.setText(getResources().getString(R.string.persentage_text, stats.get(q).getMatrixStats().get(y).getPercentage()));
                                    break;
                                case 2:
                                    if (stats.get(q).getMatrixStats().get(y).getCount() != 0) setBoldTextColor(neutralCountTtv);

                                    neutralCountTtv.setText(getResources().getString(R.string.just_text, stats.get(q).getMatrixStats().get(y).getCount()));
                                    neutralPerseTtv.setText(getResources().getString(R.string.persentage_text, stats.get(q).getMatrixStats().get(y).getPercentage()));
                                    break;
                                case 3:
                                    if (stats.get(q).getMatrixStats().get(y).getCount() != 0) setBoldTextColor(agreeCountTtv);

                                    agreeCountTtv.setText(getResources().getString(R.string.just_text, stats.get(q).getMatrixStats().get(y).getCount()));
                                    agreePerseTtv.setText(getResources().getString(R.string.persentage_text, stats.get(q).getMatrixStats().get(y).getPercentage()));
                                    break;
                                case 4:
                                    if (stats.get(q).getMatrixStats().get(y).getCount() != 0) setBoldTextColor(sAgreeCountTtv);

                                    sAgreeCountTtv.setText(getResources().getString(R.string.just_text, stats.get(q).getMatrixStats().get(y).getCount()));
                                    sAgreePerseTtv.setText(getResources().getString(R.string.persentage_text, stats.get(q).getMatrixStats().get(y).getPercentage()));
                                    break;
                            }
                        }
                        tempHashData.put(row_tag, integerListRow);
                        matrixHashData.put((Integer) menu.getTag(), tempHashData);
                        if (countPerRow != 0) {
                            if (!isSelected) {
                                answerTtv.setTag(getResources().getString(R.string.selected));
                                setBoldTextColor(answerTtv);
                            }
                            setBoldTextColor(countPerRowTtv);
                            isSelected = true;
                        } else {
                            first_row_index_to_show_data = Integer.valueOf(100+String.valueOf(q+1));
                        }
                        countPerRowTtv.setText(getResources().getString(R.string.just_text, countPerRow));
                        tableLayout.addView(tableRow);
                        row_tag++;
                    }
//                    for (Map.Entry<Integer, HashMap<Integer, List<Integer>>> entry : matrixHashData.entrySet()) {
//                        for (Map.Entry<Integer, List<Integer>> entry1 : entry.getValue().entrySet()) {
//                            Log.e(debugTag, "Key = " + entry1.getKey() + ", Value = " + entry1.getValue());
//                        }
//                        Log.e(debugTag, "Key = " + entry.getKey() + ", Value = " + entry.getValue());
//                    }
//                    Log.e(debugTag,  matrixHashData.get(menu.getTag()).get(1001)+"");
                    Log.e(debugTag, first_row_index_to_show_data+"");
                    currentlyHASH.put((Integer) menu.getTag(), first_row_index_to_show_data);
                    if (stats.size() != 0) chartsLlt.addView(initializePieChart(null, false, false, 3, matrixHashData.get(menu.getTag()).get(first_row_index_to_show_data)));
                    surveyStatsFgmtContainerLlt.addView(tableLayoutContainer);

                }
            }
        }
    }

    private void setBoldTextColor(View view) {
        if (view != null && view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.no_surveys_txv_color));
        }
    }

    private PieChart initializePieChart(List<Stats> stats, boolean drawHole, boolean menuClicked, int type, List<Integer> matrixData) {
        colorsList = new ArrayList<>();
        PieChart pieChart   =   new PieChart(getActivity());
        pieChart.setDescription("");
        pieChart.setMinimumHeight((int) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0 , 200));
        pieChart.setDrawHoleEnabled(drawHole);
        pieChart.setRotationEnabled(false);
        List<PieEntry> pieEntryList = new ArrayList<>();

        if (matrixData != null && type == 3) {
            for (int z = 0; z < matrixData.size(); z++) {
                if (matrixData.get(z) != 0) {
                    PieEntry pieEntry = new PieEntry(matrixData.get(z), "");
                    pieEntryList.add(pieEntry);
                    colorsList.add(columnMatrixViewColors.get(z));
                }
            }
        } else {
            for (int i = 0; i < stats.size(); i++) {
                if (type == 1) {
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

                } else {
                    PieEntry pieEntry = new PieEntry(stats.get(i).getRankingPoints(), "");
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

    private BarChart initializeBarChart(List<Stats> stats, int type, List<Integer> matrixData) {
        float offset = 0f;
        colorsList = new ArrayList<>();
        BarChart barChart = new BarChart(getActivity());
        barChart.setDescription("");
        barChart.setFitBars(true);
        barChart.setMinimumHeight((int) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0 , 200));
        List<BarEntry> barEntryList = new ArrayList<>();

        barChart.getXAxis().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinValue(0f);
        if (type == 1) barChart.getAxisLeft().setAxisMaxValue(100f);

        if (matrixData != null && type == 3) {
            for (int i = 0; i < matrixData.size(); i++) {
                if (matrixData.get(i) != 0) {
                    BarEntry barEntry = new BarEntry(offset, matrixData.get(i));
                    barEntryList.add(barEntry);
                    if (matrixData.get(i) != 0) colorsList.add(columnMatrixViewColors.get(i));
                    offset+=0.6f;
                }
            }
        } else {
            for (int i = 0; i < stats.size(); i++) {
                if (type == 1) {
                    if (stats.get(i).getPercentage() != 0) {
                        BarEntry barEntry = new BarEntry(offset, stats.get(i).getPercentage());
                        barEntryList.add(barEntry);
                        colorsList.add(colorHashMap.get(stats.get(i).getIndex()));
                        offset+=0.6f;
                    }
                } else {
                    BarEntry barEntry = new BarEntry(offset, stats.get(i).getRankingPoints());
                    barEntryList.add(barEntry);
                    colorsList.add(colorHashMap.get(stats.get(i).getIndex()));
                    offset+=0.6f;
                }
            }
        }
        BarDataSet dataSet  = new BarDataSet(barEntryList, "");
        dataSet.setDrawValues(false);
        dataSet.setColors(colorsList);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.2f);
        barChart.setData(barData);
        barChart.animateXY(800, 800);
        barChart.invalidate();
        return barChart;
    }

    private HorizontalBarChart initializeHorBarChart(List<Stats> stats, int type, List<Integer> matrixData) {
        float offset = 0f;
        colorsList = new ArrayList<>();
        HorizontalBarChart horizontalBarChart = new HorizontalBarChart(getActivity());
        horizontalBarChart.setDescription("");
        horizontalBarChart.setFitBars(true);
        horizontalBarChart.setMinimumHeight((int) MyApplication.convertPixelToDpAndViceVersa(getActivity(), 0 , 200));
        List<BarEntry> barEntryList = new ArrayList<>();

        horizontalBarChart.getXAxis().setEnabled(false);
        horizontalBarChart.getAxisRight().setEnabled(false);
        horizontalBarChart.getAxisLeft().setAxisMinValue(0f);
        if (type == 1) horizontalBarChart.getAxisLeft().setAxisMaxValue(100f);

        if (matrixData != null && type == 3) {
            for (int i = 0; i < matrixData.size(); i++) {
                if (matrixData.get(i) != 0) {
                    BarEntry barEntry = new BarEntry(offset, matrixData.get(i));
                    barEntryList.add(barEntry);
                    if (matrixData.get(i) != 0) colorsList.add(columnMatrixViewColors.get(i));
                    offset+=0.6f;
                }
            }
        } else {
            for (int i = 0; i < stats.size(); i++) {
                if (type == 1) {
                    if (stats.get(i).getPercentage() != 0) {
                        BarEntry barEntry = new BarEntry(offset, stats.get(i).getPercentage());
                        barEntryList.add(barEntry);
                        colorsList.add(colorHashMap.get(stats.get(i).getIndex()));
                        offset+=0.6f;
                    }
                } else {
                    BarEntry barEntry = new BarEntry(offset, stats.get(i).getRankingPoints());
                    barEntryList.add(barEntry);
                    colorsList.add(colorHashMap.get(stats.get(i).getIndex()));
                    offset+=0.6f;
                }

            }
        }
        BarDataSet dataSet  = new BarDataSet(barEntryList, "");
        dataSet.setDrawValues(false);
        dataSet.setColors(colorsList);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.2f);
        horizontalBarChart.setData(barData);
        horizontalBarChart.animateXY(800, 800);
        horizontalBarChart.invalidate();
        return horizontalBarChart;
    }

    @Override
    public void onClick(View view) {
        if (view != null && templateLlt != null) {
            if (view.getId() == R.id.freeAnswerLlt) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.free_answers_dialog);
                ListView listView = (ListView) dialog.findViewById(R.id.freeAnswersLstv);
                ArrayAdapter<String> adapter = new ArrayAdapter<String> (getActivity(), android.R.layout.simple_list_item_1, freeAnswersMap.get(view.getTag()));
                listView.setAdapter(adapter);
                dialog.show();
            } else if (view instanceof FloatingActionButton) {
                FloatingActionsMenu menu    = (FloatingActionsMenu) view.getParent();
                LinearLayout layoutC        = (LinearLayout) surveyStatsFgmtContainerLlt.findViewWithTag(menu.getTag());
                int type;
                if (surveyDetailsData.getQuestion().get((Integer) menu.getTag()).getTypeId() == 300) {
                    type = 2;
                } else if (surveyDetailsData.getQuestion().get((Integer) menu.getTag()).getTypeId() == 700) {
                    type = 3;
                } else {
                    type = 1;
                }
                if (layoutC != null) layoutC.removeAllViews();

                switch (view.getId()) {
                    case R.id.horBarChartFAB:
                        if (layoutC != null)
                            if (type != 3) {
                                layoutC.addView(initializeHorBarChart(surveyDetailsData.getQuestion().get((Integer) menu.getTag()).getStats(), type, null));
                            } else {
                                Log.e(debugTag, "MENU TAG: "+menu.getTag()+" ");
                                Log.e(debugTag, surveyDetailsData.getQuestion().get((Integer) menu.getTag()).getStats()+"");
                                Log.e(debugTag, matrixHashData.get(menu.getTag()).get(currentlyHASH.get(menu.getTag()))+"");
                                layoutC.addView(initializeHorBarChart(surveyDetailsData.getQuestion().get((Integer) menu.getTag()).getStats(), type, matrixHashData.get(menu.getTag()).get(currentlyHASH.get(menu.getTag()))));
                            }
                        break;
                    case R.id.barChartFAB:
                        if (layoutC != null)
                            if (type != 3) {
                                layoutC.addView(initializeBarChart(surveyDetailsData.getQuestion().get((Integer) menu.getTag()).getStats(), type, null));
                            } else {
                                layoutC.addView(initializeBarChart(surveyDetailsData.getQuestion().get((Integer) menu.getTag()).getStats(), type, matrixHashData.get(menu.getTag()).get(currentlyHASH.get(menu.getTag()))));
                            }
                        break;
                    case R.id.pieChartFAB:
                        if (layoutC != null)
                            if (type != 3) {
                                layoutC.addView(initializePieChart(surveyDetailsData.getQuestion().get((Integer) menu.getTag()).getStats(), false, true, type, null));
                            } else {
                                layoutC.addView(initializePieChart(surveyDetailsData.getQuestion().get((Integer) menu.getTag()).getStats(), false, true, type, matrixHashData.get(menu.getTag()).get(currentlyHASH.get(menu.getTag()))));
                            }
                        break;
                    case R.id.holePieChartFAB:
                        if (layoutC != null)
                            if (type != 3) {
                                layoutC.addView(initializePieChart(surveyDetailsData.getQuestion().get((Integer) menu.getTag()).getStats(), true, true, type, null));
                            } else {
                                layoutC.addView(initializePieChart(surveyDetailsData.getQuestion().get((Integer) menu.getTag()).getStats(), true, true, type, matrixHashData.get(menu.getTag()).get(currentlyHASH.get(menu.getTag()))));
                            }
                        break;
                    case R.id.freeAnswerLlt:
                        Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.free_answers_dialog);
                        ListView listView = (ListView) dialog.findViewById(R.id.freeAnswersLstv);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, freeAnswersMap.get(view.getTag()));
                        listView.setAdapter(adapter);
                        dialog.show();
                        break;
                    case R.id.tableRow:

                }
                menu.collapse();
            } else {
                LinearLayout linearLayout    = (LinearLayout) view.getParent();
                LinearLayout layout          = (LinearLayout) surveyStatsFgmtContainerLlt.findViewWithTag(linearLayout.getTag());
                layout.removeAllViews();
                TextView selectedTtv    = (TextView) linearLayout.findViewWithTag(getResources().getString(R.string.selected));
                if (selectedTtv != null) {
                    selectedTtv.setTag("");
                    selectedTtv.setTextColor(ContextCompat.getColor(getActivity(), R.color.accentColor));
                }
                TextView textView = (TextView) view.findViewById(R.id.answerTtv);
                if (textView != null) {
                    textView.setTag(getResources().getString(R.string.selected));
                    setBoldTextColor(textView);
                }
                currentlyHASH.put((Integer) layout.getTag(), (Integer) view.getTag());
                layout.addView(initializePieChart(null, false, true, 3, matrixHashData.get(layout.getTag()).get(currentlyHASH.get(layout.getTag()))));
            }
        }
    }
}
