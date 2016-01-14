package com.github.yeriomin.workoutlog.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.github.yeriomin.workoutlog.Model.Exercise;
import com.github.yeriomin.workoutlog.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ExerciseAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final HashMap<String, ArrayList<ExerciseSet>> exercises = new HashMap<String, ArrayList<ExerciseSet>>();
    private final ArrayList<String> headers = new ArrayList<String>();

    public ExerciseAdapter(Context context, List<Exercise> exercises) {
        this.context = context;
        HashMap<String, HashMap<String, ExerciseSet>> exercisesTemp = new HashMap<String, HashMap<String, ExerciseSet>>();
        HashMap<String, ExerciseSet> tempDate;
        Date date = new Date();

        for (Exercise exercise: exercises) {
            // Building a date string
            date.setTime(exercise.getTimestamp()*1000L);
            String stringDate = android.text.format.DateFormat.getDateFormat(context).format(date);
            // And type string
            String stringType = exercise.getExerciseType().getName();

            // First lets store exercises in an type-indexed hash
            tempDate = exercisesTemp.get(stringDate);
            if (null == tempDate) {
                tempDate = new HashMap<String, ExerciseSet>();
                exercisesTemp.put(stringDate, tempDate);
            }
            ExerciseSet tempSet = exercisesTemp.get(stringDate).get(stringType);
            if (null == tempSet) {
                tempSet = new ExerciseSet();
                exercisesTemp.get(stringDate).put(stringType, tempSet);
            }
            exercisesTemp.get(stringDate).get(stringType).add(exercise);

            // Saving it as a header
            if (headers.indexOf(stringDate) == -1) {
                headers.add(stringDate);
            }
        }
        // With exercises indexed we can now build an ArrayList
        for (HashMap.Entry<String, HashMap<String, ExerciseSet>> entry : exercisesTemp.entrySet()) {
            String stringDate = entry.getKey();
            this.exercises.put(stringDate, new ArrayList<ExerciseSet>());
            HashMap<String, ExerciseSet> sets = entry.getValue();
            for (HashMap.Entry<String, ExerciseSet> anotherEntry : sets.entrySet()) {
                this.exercises.get(stringDate).add(anotherEntry.getValue());
            }
        }
    }

    @Override
    public ExerciseSet getChild(int groupPosition, int childPosition) {
        return this.exercises.get(this.headers.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ExerciseSet exerciseSet = getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_exercise, null);
        }

        TextView exerciseType = (TextView) convertView.findViewById(R.id.exerciseType);
        TextView exerciseParameters = (TextView) convertView.findViewById(R.id.exerciseParameters);
        exerciseType.setText(exerciseSet.getType());
        exerciseParameters.setText(exerciseSet.getStats());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.exercises.get(this.headers.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headers.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.headers.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_group, null);
        }

        TextView header = (TextView) convertView.findViewById(R.id.group_header);
        header.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}