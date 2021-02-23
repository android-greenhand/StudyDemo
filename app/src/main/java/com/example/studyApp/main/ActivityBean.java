package com.example.studyApp.main;

import android.content.Intent;

public class ActivityBean {

    public ActivityBean(String activityName, Intent nextIntent) {
        this.activityName = activityName;
        this.nextIntent = nextIntent;
    }

    public ActivityBean(String activityName, Intent nextIntent, Boolean multiDirectory) {
        this.activityName = activityName;
        this.nextIntent = nextIntent;
        this.multiDirectory = multiDirectory;
    }

    public String activityName;

    public Intent nextIntent;

    public Boolean multiDirectory = false;
}
