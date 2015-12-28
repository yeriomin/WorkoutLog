package com.github.yeriomin.workoutlog.Adapter;

import com.github.yeriomin.workoutlog.Model.Exercise;

import java.util.ArrayList;

public class ExerciseSet {

    private final ArrayList<Exercise> exercises = new ArrayList<Exercise>();

    public void add(Exercise exercise) {
        exercises.add(exercise);
    }

    public String getType() {
        return exercises.get(0).getExerciseType().getName();
    }

    public String getStats() {
        String result = "";
        for (Exercise exercise: exercises) {
            if (result.length() != 0) {
                result = result.concat(", ");
            }
            result = result.concat(String.valueOf(exercise.getWeight())
                    + "×" + String.valueOf(exercise.getReps()));
        }
        return result;
    }
}
