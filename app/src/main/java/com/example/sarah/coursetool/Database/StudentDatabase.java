package com.example.sarah.coursetool.Database;

import com.example.sarah.coursetool.Course.ScheduledCourse;
import com.example.sarah.coursetool.UserProfile.Profile;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

/**
 * A proxy class for performing database operations related to student functionality.
 */
public class StudentDatabase implements UserDatabase {
    RealDatabase database;

    /**
     * Constructs new StudentDatabase. Normal used by a loginDatabase when correct credentials are supplied.
     * @param sourceDatabase
     *
     * @author jdeman
     * @author nattwood
     * @date 7/10/2018
     */
    public StudentDatabase(RealDatabase sourceDatabase) {
        database = sourceDatabase;
    }

    @Override
    public Profile getUserProfile() throws TimeoutException {
        return database.getUserProfile();
    }

    @Override
    public HashMap<String, ScheduledCourse> getScheduledCourses() throws TimeoutException {
        return database.getScheduledCourses();
    }

    @Override
    public void enroll(String key) throws InvalidParameterException, TimeoutException {
        database.enroll(key);
    }

    @Override
    public void unenrollFromCourse(String key) throws InvalidParameterException, TimeoutException {
        database.unenrollFromCourse(key);
    }
}
