package com.github.yeriomin.workoutlog.Model;

import android.content.Context;

public class OpenCloseHelper {

    static private DaoMaster.DevOpenHelper dbHelper;

    static public DaoMaster.DevOpenHelper getDbHelper(Context context) {
        if (null == dbHelper) {
            String dbFileName = "WorkoutLog.db";
            dbHelper = new DaoMaster.DevOpenHelper(context, dbFileName, null);
        }
        return dbHelper;
    }

    static public void closeDbHelper() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
