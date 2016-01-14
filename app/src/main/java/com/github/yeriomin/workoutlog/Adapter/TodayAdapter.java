package com.github.yeriomin.workoutlog.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.yeriomin.workoutlog.Model.Exercise;
import com.github.yeriomin.workoutlog.R;

import java.util.ArrayList;

public class TodayAdapter extends ArrayAdapter<Exercise>{

    private final ArrayList<Exercise> exercises = new ArrayList<Exercise>();

    public TodayAdapter(Context context) {
        super(context, R.layout.item_today, new ArrayList<Exercise>());
    }

    @Override
    public void add(Exercise exercise) {
        super.add(exercise);
        this.exercises.add(exercise);
    }

    @Override
    public void clear() {
        super.clear();
        this.exercises.clear();
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_today, parent, false);
        }
        TextView textView = (TextView) v.findViewById(R.id.today_reps_and_weight);

        Exercise exercise = exercises.get(position);
        textView.setText(getContext().getString(
                R.string.today_reps_and_weight,
                exercise.getWeight(),
                exercise.getReps()
        ));
        return v;
    }
}
