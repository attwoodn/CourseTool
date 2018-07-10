package com.example.sarah.coursetool.Database;

/**
 * Database interface for methods related to administrative functionality.
 */
public interface InstitutionDatabaseInterface {

    /**
     * Create course in the database
     *
     * @param courseName - The name of the course
     * @param capacity - The max number of students allowed in the course
     * @param professor - The name of the professor teaching the course
     * @param departmentCode - The department code of the course, ex: "CSCI201"
     * @param description - The description of the course
     * @param prerequisites - A comma-separated list of the prerequisites, ex: "MATH101, CSCI102"
     * @param daysOfWeek - The days that the course is taught ex: "MWF"
     * @param startTime - The start time of the course, ex: "10:05" or "13:25"
     * @param endTime - The end time of the course, ex: "10:55"
     * @param startDate - The start date of the course in the format mm/dd/yyyy, ex: "01/15/2018"
     * @param endDate - The end date of the course in the format mm/dd/yyyy, ex: "01/15/2018"
     */
    String createCourse(String courseName, int capacity, String professor, String departmentCode,
                      String description, String prerequisites, String daysOfWeek, String startTime,
                      String endTime, String startDate, String endDate);

}