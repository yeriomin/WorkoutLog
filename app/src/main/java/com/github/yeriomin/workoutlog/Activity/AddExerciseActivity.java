package com.github.yeriomin.workoutlog.Activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.github.yeriomin.workoutlog.Adapter.TodayAdapter;
import com.github.yeriomin.workoutlog.Model.DaoMaster;
import com.github.yeriomin.workoutlog.Model.DaoSession;
import com.github.yeriomin.workoutlog.Model.Exercise;
import com.github.yeriomin.workoutlog.Model.ExerciseDao;
import com.github.yeriomin.workoutlog.Model.ExerciseType;
import com.github.yeriomin.workoutlog.Model.ExerciseTypeDao;
import com.github.yeriomin.workoutlog.Model.OpenCloseHelper;
import com.github.yeriomin.workoutlog.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddExerciseActivity extends Activity {

    private HashMap<String, Long> typeIds = new HashMap<String, Long>();

    private TodayAdapter todayAdapter;

    private String previousType = "";

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
        setContentView(R.layout.activity_add_exercise);

        AutoCompleteTextView view = (AutoCompleteTextView) findViewById(R.id.name);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_type, R.id.exerciseType, getAllTypes());
        view.setAdapter(arrayAdapter);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                Exercise lastExercise = getLastExercise(selected);

                List<Exercise> exercisesOfChosenType = new ArrayList<Exercise>();
                if (null != lastExercise) {
                    fillSuggestions(lastExercise);
                    exercisesOfChosenType = getTodayExercises(lastExercise.getTypeId());
                }
                addToTodayList(exercisesOfChosenType);
            }
        });
    }

    public void addExercise(View view) {
        EditText inputName, inputReps, inputWeight;
        inputName = (EditText) findViewById(R.id.name);
        inputReps = (EditText) findViewById(R.id.reps);
        inputWeight = (EditText) findViewById(R.id.weight);

        String reps, weight, type;
        type = inputName.getText().toString();
        reps = inputReps.getText().toString();
        weight = inputWeight.getText().toString();

        // Validating the form
        Boolean isValid = true;
        if (type.length() == 0) {
            inputName.setError(getString(R.string.form_validation_exerciseType_required));
            isValid = false;
        }
        if (reps.length() == 0 || Integer.parseInt(reps) == 0) {
            inputReps.setError(getString(R.string.form_validation_reps_required));
            isValid = false;
        }
        if (weight.length() == 0 || Integer.parseInt(weight) == 0) {
            inputWeight.setError(getString(R.string.form_validation_weight_required));
            isValid = false;
        }
        if (!isValid) {
            return;
        }

        ExerciseType exerciseType = new ExerciseType();
        exerciseType.setName(type);

        Exercise exercise = new Exercise();
        exercise.setTimestamp(System.currentTimeMillis() / 1000L);
        exercise.setReps(Integer.parseInt(reps));
        exercise.setWeight(Integer.parseInt(weight));

        // Inserting data into db
        saveExercise(exercise, exerciseType);

        // Adding to today shortlist
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        exercises.add(exercise);
        addToTodayList(exercises);
    }

    private void fillSuggestions(Exercise exercise) {
        EditText inputReps, inputWeight;
        inputReps = (EditText) findViewById(R.id.reps);
        inputWeight = (EditText) findViewById(R.id.weight);
        inputReps.setText(String.valueOf(exercise.getReps()));
        inputWeight.setText(String.valueOf(exercise.getWeight()));
    }

    private void addToTodayList(List<Exercise> exercises) {
        String currentType = exercises.toArray().length != 0
            ? exercises.get(0).getExerciseType().getName()
            : "";
        if (null == todayAdapter) {
            initTodayList();
        } else if (currentType.compareToIgnoreCase(previousType) != 0) {
            todayAdapter.clear();
        }
        for (Exercise exercise: exercises) {
            todayAdapter.add(exercise);
        }
        previousType = currentType;
    }

    private void initTodayList() {
        todayAdapter = new TodayAdapter(getBaseContext());
        ListView exercisesListView = (ListView) findViewById(R.id.list_today);

        LayoutInflater lf = this.getLayoutInflater();
        View headerView = lf.inflate(R.layout.item_today_header, exercisesListView, false);

        exercisesListView.addHeaderView(headerView, null, false);
        exercisesListView.setAdapter(todayAdapter);
    }

    private List<String> getAllTypes() {
        SQLiteDatabase db = OpenCloseHelper.getDbHelper(getApplicationContext()).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        ExerciseTypeDao exerciseTypeDao = daoSession.getExerciseTypeDao();
        List<ExerciseType> types = exerciseTypeDao.queryBuilder().list();
        List<String> result = new ArrayList<String>();
        for (ExerciseType type : types) {
            this.typeIds.put(type.getName(), type.getId());
            result.add(type.getName());
        }
        return result;
    }

    private Exercise getLastExercise(String type) {
        SQLiteDatabase db = OpenCloseHelper.getDbHelper(getApplicationContext()).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        if (!typeIds.containsKey(type)) {
            return null;
        }

        ExerciseDao exerciseDao = daoSession.getExerciseDao();
        return exerciseDao
                .queryBuilder()
                .where(ExerciseDao.Properties.TypeId.eq(typeIds.get(type)))
                .orderDesc(ExerciseDao.Properties.Timestamp)
                .list()
                .get(0);
    }

    private List<Exercise> getTodayExercises(Long chosenTypeId) {
        SQLiteDatabase db = OpenCloseHelper.getDbHelper(getApplicationContext()).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        Date date = new Date();
        Long todayTimestamp = (System.currentTimeMillis() / 1000L)
                - date.getHours() * 60 * 60 - date.getMinutes() * 60 - date.getSeconds();
        ExerciseDao exerciseDao = daoSession.getExerciseDao();
        return exerciseDao
                .queryBuilder()
                .where(ExerciseDao.Properties.TypeId.eq(chosenTypeId))
                .where(ExerciseDao.Properties.Timestamp.gt(todayTimestamp))
                .orderAsc(ExerciseDao.Properties.Timestamp)
                .list();
    }

    private void saveExercise(Exercise exercise, ExerciseType exerciseType) {
        SQLiteDatabase db = OpenCloseHelper.getDbHelper(getApplicationContext()).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        if (!typeIds.containsKey(exerciseType.getName())) {
            ExerciseTypeDao exerciseTypeDao = daoSession.getExerciseTypeDao();
            List<ExerciseType> existingExerciseTypes = exerciseTypeDao
                    .queryBuilder()
                    .where(ExerciseTypeDao.Properties.Name.eq(exerciseType.getName()))
                    .build()
                    .list();
            if (existingExerciseTypes.isEmpty()) {
                exerciseType.setId(exerciseTypeDao.insert(exerciseType));
            } else {
                exerciseType.setId(existingExerciseTypes.get(0).getId());
            }
            typeIds.put(exerciseType.getName(), exerciseType.getId());
        }
        exercise.setTypeId(typeIds.get(exerciseType.getName()));

        ExerciseDao exerciseDao = daoSession.getExerciseDao();
        exerciseDao.insert(exercise);
    }
}
