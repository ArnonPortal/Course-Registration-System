package bgu.spl.net.srv;

import java.util.*;

public class Course {
    private int numOfMaxStudents;
    private String courseName;
    private Vector<Integer> kdamCourses;
    private SortedSet<String> registeredStudents = new TreeSet<>();
    private int position; //in order to keep the courses order as in the courses file

    public Course(int numOfMaxStudents, String courseName, Vector<Integer> kdamCourses, int position){
        this.numOfMaxStudents = numOfMaxStudents;
        this.kdamCourses = kdamCourses;
        this.courseName = courseName;
        this.position = position;
    }

    public Vector<Integer> getKdamCourses() {
        return kdamCourses;
    }

    public SortedSet<String> getRegisteredStudents() {
        return registeredStudents;
    }

    public int getNumOfMaxStudents() {
        return numOfMaxStudents;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCurrRegisteredNum(){
        return registeredStudents.size();
    }

    public int getPosition() {
        return position;
    }

    private boolean hasFreeSeats(){
        return (numOfMaxStudents - registeredStudents.size()) > 0;
    }

    public boolean registerStudent(String name){
        if(registeredStudents.contains(name) | !hasFreeSeats())
            return false;

        registeredStudents.add(name);
        return true;
    }

    public boolean unregisterStudent(String name){
        return registeredStudents.remove(name);
    }
}
