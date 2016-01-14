package com.github.yeriomin.workoutlog.Activity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;

import com.github.yeriomin.workoutlog.Adapter.ExerciseAdapter;
import com.github.yeriomin.workoutlog.Model.DaoMaster;
import com.github.yeriomin.workoutlog.Model.DaoSession;
import com.github.yeriomin.workoutlog.Model.Exercise;
import com.github.yeriomin.workoutlog.Model.ExerciseDao;
import com.github.yeriomin.workoutlog.Model.OpenCloseHelper;
import com.github.yeriomin.workoutlog.R;

import java.util.List;

public class ExerciseListActivity extends ListActivity {

    @Override
    protected void onResume() {
        super.onResume();
        fillExerciseList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        OpenCloseHelper.closeDbHelper();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OpenCloseHelper.closeDbHelper();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);
        ExpandableListView exercisesListView = (ExpandableListView) findViewById(android.R.id.list);
        LayoutInflater lf = this.getLayoutInflater();
        View headerView = lf.inflate(R.layout.item_exercise_header, exercisesListView, false);
        headerView.setClickable(true);
        exercisesListView.addHeaderView(headerView);
    }

    private void fillExerciseList() {
        ExpandableListView exercisesListView = (ExpandableListView) findViewById(android.R.id.list);
        final ExerciseAdapter adapter = new ExerciseAdapter(this, getExercises());
        exercisesListView.setAdapter(adapter);
    }

    private List<Exercise> getExercises() {
        SQLiteDatabase db = OpenCloseHelper.getDbHelper(getApplicationContext()).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        ExerciseDao exerciseDao = daoSession.getExerciseDao();
        return exerciseDao.queryBuilder().orderAsc(ExerciseDao.Properties.Timestamp).list();
    }

    public void gotoNextActivity(View view) {
        Intent goToNextActivity = new Intent(getApplicationContext(), AddExerciseActivity.class);
        startActivity(goToNextActivity);
    }
}
