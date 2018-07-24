package com.example.sarah.coursetool.CourseListing;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sarah.coursetool.BaseNavigationActivity;
import com.example.sarah.coursetool.Course.ScheduledCourse;
import com.example.sarah.coursetool.Database.RealDatabase;
import com.example.sarah.coursetool.R;
import com.example.sarah.coursetool.UserProfile.StudentProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Listings extends BaseNavigationActivity {

    Spinner spinner;
    DataGenerator dataGenerator = DataGenerator.getGenerator();
    ArrayList<CourseListing> inputData = new ArrayList<CourseListing>();
    RecyclerView.Adapter viewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);

        Log.d("Hello","Loaded");
        dataGenerator.getAllCourses(inputData);
        RecyclerView courseListContainer = (RecyclerView) findViewById(R.id.recycleView);
        courseListContainer.setHasFixedSize(true);
        RecyclerView.LayoutManager listContainerManager = new LinearLayoutManager(this);
        courseListContainer.setLayoutManager(listContainerManager);
        viewAdapter = new viewAdapter(inputData, getApplicationContext());
        courseListContainer.setAdapter(viewAdapter);

        spinner=(Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = spinner.getItemAtPosition(position).toString();
                Log.d("Hello",s);
                //String Fall="Fall"
                switch (s) {
                    case "All":
                        dataGenerator.getAllCourses(inputData);
                        viewAdapter.notifyDataSetChanged();
                        break;
                    case "Fall":
                        dataGenerator.getFallCourses(inputData);
                        viewAdapter.notifyDataSetChanged();
                        break;
                    case "Winter":
                        dataGenerator.getWinterCourses(inputData);
                        viewAdapter.notifyDataSetChanged();
                        break;
                    case "Summer":
                        dataGenerator.getSummerCourses(inputData);
                        viewAdapter.notifyDataSetChanged();
                        break;
                    case "CSCI":
                        dataGenerator.getCSCICourses(inputData);
                        viewAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void closePopup(View v) {
        ViewGroup g = (ViewGroup) v.getParent().getParent();
        View x = g.getChildAt(2);
        TextView message = findViewById(R.id.enrollMessage);
        message.setText("");
        TextView enrolled = findViewById(R.id.courseSpotsLeft);
        enrolled.setText("35 of 60 seats remaining");
        x.setVisibility(View.GONE);
    }

    public void enrollStudent(View v){
        View vh = findViewById(R.id.courseInfoContainer);
        CourseListing listing = (CourseListing) vh.getTag();
        Button button = findViewById(R.id.enrollmentButton);
        TextView preReqs = findViewById(R.id.courseSpotsLeft);
        TextView courseIDMain = findViewById(R.id.courseIdMain);
        TextView courseDeptMain = findViewById(R.id.courseDeptMain);
        String course = courseDeptMain.toString().concat(" "+ courseIDMain.toString());
        int schedID = courseIDMain.getId();
        TextView enrolled = findViewById(R.id.courseSpotsLeft);
        RealDatabase conn = RealDatabase.getDatabase();
        int counter = 0;
        while(conn.snapshotIsNull() && counter < 2) {
            try {
                counter++;
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        StudentProfile profile;
        try {
            profile = (StudentProfile) conn.getUserProfile();
        } catch (TimeoutException e) {
            Log.d("Timeouterror",e.toString());
            return;
        }
        TextView message = findViewById(R.id.enrollMessage);
        HashMap<String, ScheduledCourse> courses = profile.getEnrolledCourses();
        if (listing.capacity - listing.enrolled < 1) {
            message.setText("Enrollment failed - no empty seats");
            return;
        }

        //Method call written by HA&NS on 07/22/18
        //calls the preReq method, if the check fails the message will indicate which preReqs are missing, and the method will return
        String preReqString;
        preReqString = checkPreReqs(profile, listing);
            if(!preReqString.equals("pass")) {
                message.setText(preReqString);
                return;
            }

        for(Map.Entry<String, ScheduledCourse> scheduledCourse:courses.entrySet()) {
            CourseListing inputCourse = new CourseListing(scheduledCourse.getValue());
            if (inputCourse.courseTitle.equals(listing.courseTitle)) {
                message.setText("Enrollment failed - already enrolled");
                return;
            }
        }
        for(Map.Entry<String, ScheduledCourse> scheduledCourse:courses.entrySet()) {
            CourseListing inputCourse = new CourseListing(scheduledCourse.getValue());
            if (listing.courseStartTime.before(inputCourse.courseEndTime) && listing.courseEndTime.after(inputCourse.courseStartTime)) {
                for (int dayCount = 0; dayCount < 7; dayCount++) {
                    if (listing.courseDays[dayCount] == 1 && inputCourse.courseDays[dayCount] == 1) {
                        if (listing.courseStartTime.getHours() < inputCourse.courseStartTime.getHours()) {
                            if (listing.courseEndTime.getHours() > inputCourse.courseStartTime.getHours()) {
                                message.setText("Enrollment failed - time conflict");
                                return;
                            } else if (listing.courseEndTime.getHours() == inputCourse.courseStartTime.getHours()) {
                                if (listing.courseEndTime.getMinutes() > inputCourse.courseStartTime.getMinutes()) {
                                    message.setText("Enrollment failed - time conflict");
                                    return;
                                }
                            }
                        }
                        if (listing.courseStartTime.getHours() == inputCourse.courseStartTime.getHours()) {
                            if (listing.courseEndTime.getHours() == inputCourse.courseEndTime.getHours() && listing.courseStartTime.getHours() == listing.courseEndTime.getHours()) {
                                if (listing.courseStartTime.getMinutes() >= inputCourse.courseStartTime.getMinutes()) {
                                    if (listing.courseStartTime.getMinutes() < inputCourse.courseEndTime.getMinutes()) {
                                        message.setText("Enrollment failed - time conflict");
                                        return;
                                    }
                                } else if (listing.courseStartTime.getMinutes() < inputCourse.courseStartTime.getMinutes()) {
                                    if (listing.courseEndTime.getMinutes() > inputCourse.courseStartTime.getMinutes()) {
                                        message.setText("Enrollment failed - time conflict");
                                        return;
                                    }
                                }
                            } else if (listing.courseEndTime.getHours() < inputCourse.courseEndTime.getHours()) {
                                if (listing.courseEndTime.getHours() == inputCourse.courseStartTime.getHours()) {
                                    if (listing.courseEndTime.getMinutes() > inputCourse.courseStartTime.getMinutes()) {
                                        message.setText("Enrollment failed - time conflict");
                                        return;
                                    }
                                } else {
                                    message.setText("Enrollment failed - time conflict");
                                    return;
                                }
                            } else if (listing.courseEndTime.getHours() > inputCourse.courseEndTime.getHours()) {
                                if (listing.courseStartTime.getHours() < inputCourse.courseEndTime.getHours()) {
                                    message.setText("Enrollment failed - time conflict");
                                    return;
                                } else if (listing.courseStartTime.getHours() == inputCourse.courseEndTime.getHours()) {
                                    if (listing.courseStartTime.getMinutes() < inputCourse.courseEndTime.getMinutes()) {
                                        message.setText("Enrollment failed - time conflict");
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        try {
            Log.d("monkey13", listing.courseTitle);
            conn.enroll(listing.courseTitle);
        } catch (TimeoutException e) {
            message.setText("Failed to enroll due to database timeout");
            return;
        }
        listing.enrolled++;
        enrolled.setText(""+(listing.capacity - listing.enrolled)+" of "+listing.capacity+" seats remaining");
        message.setText("Enrollment Successful");
        viewAdapter.notifyDataSetChanged();
    }

    /**
     * Method will check if user has completed preReqs for course they are attempting to enroll in
     * The method will return a string pass if check passes, but upon failure method will return a list of preReqs the user must complete
     * @param profile
     * @param attemptToEnroll
     * @return
     * Created by HA&NS on 07/22/18
     */
    public String checkPreReqs(StudentProfile profile, CourseListing attemptToEnroll){
        int id;
        int grade;
        int flag = 0;
        int commaCount = 0;
        String errMsg = "Enrollment failed - pre reqs not met: ";

        for (int m = 0; m < attemptToEnroll.getPreqSize(); m++) {
        id = attemptToEnroll.getPrereq(m).getCourseID();
        grade = profile.getCourseGrade(id);
            if (grade == 0) {
                if (flag == 0) {
                    flag = 1;
                    commaCount++;
                }
                if (commaCount == 1) {
                    errMsg += id;
                } else {
                    errMsg += ", " + id;
                }
            }
        }
        if (flag == 1) {
            errMsg += ".";
            return errMsg;
        } else {
            return "pass";
        }
    }
}
