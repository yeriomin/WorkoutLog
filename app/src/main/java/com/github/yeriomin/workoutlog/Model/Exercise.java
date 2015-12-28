package com.github.yeriomin.workoutlog.Model;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "EXERCISE".
 */
public class Exercise {

    private Long timestamp;
    private Integer reps;
    private Integer weight;
    private long typeId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient ExerciseDao myDao;

    private ExerciseType exerciseType;
    private Long exerciseType__resolvedKey;


    public Exercise() {
    }

    public Exercise(Long timestamp, Integer reps, Integer weight, long typeId) {
        this.timestamp = timestamp;
        this.reps = reps;
        this.weight = weight;
        this.typeId = typeId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getExerciseDao() : null;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    /** To-one relationship, resolved on first access. */
    public ExerciseType getExerciseType() {
        long __key = this.typeId;
        if (exerciseType__resolvedKey == null || !exerciseType__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ExerciseTypeDao targetDao = daoSession.getExerciseTypeDao();
            ExerciseType exerciseTypeNew = targetDao.load(__key);
            synchronized (this) {
                exerciseType = exerciseTypeNew;
            	exerciseType__resolvedKey = __key;
            }
        }
        return exerciseType;
    }

    public void setExerciseType(ExerciseType exerciseType) {
        if (exerciseType == null) {
            throw new DaoException("To-one property 'typeId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.exerciseType = exerciseType;
            typeId = exerciseType.getId();
            exerciseType__resolvedKey = typeId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
