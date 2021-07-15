package bgu.spl.net.srv;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {
    public static Database instance = null;
    private HashMap<String, Student> students = new HashMap<>();
    private HashMap<String, Admin> admins = new HashMap<>();
    private HashMap<Integer, Course> courses = new HashMap<>();
    private final Object courseLock = new Object();
    private final Object accountLock = new Object();

    private Database() {
        initialize("./Courses.txt");
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * loades the courses from the file path specified
     * into the Database, returns true if successful.
     */
    boolean initialize(String coursesFilePath) {
        // TODO: implement
        try {
            int posInCoursesFile = 0;
            Scanner myReader = new Scanner(new File(coursesFilePath));
            while (myReader.hasNextLine()) {
                String currLine = myReader.nextLine();
                String[] courseInfo = currLine.split("\\|");
                Integer courseNum = Integer.parseInt(courseInfo[0]);
                String courseName = courseInfo[1];
                String[] kdamCoursesNum = courseInfo[2].substring(1, courseInfo[2].length() - 1).split(",");
                Vector<Integer> kdamCourses = new Vector<>();
                if (courseInfo[2].length() != 2) {
                    for (String s : kdamCoursesNum) {
                        kdamCourses.add(Integer.parseInt(s));
                    }
                }
                int maxStudentNum = Integer.parseInt(courseInfo[3]);
                Course course = new Course(maxStudentNum, courseName, kdamCourses, posInCoursesFile);
                courses.put(courseNum, course);
                posInCoursesFile++;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    //===========================Validations======================================
    public boolean isAdmin(Account account) {
        return admins.containsKey(account.getUsername());
    }

    public boolean courseExist(int courseNum) {
        return courses.containsKey(courseNum);
    }

    public boolean loginValidation(String inputUsername, String inputPassword) {
        return getAccount(inputUsername) != null && getAccount(inputUsername).getPassword().equals(inputPassword);
    }

    //=================================Getters=====================================
    public Account getAccount(String username) {
        if (admins.containsKey(username)) {
            return admins.get(username);
        }
        return students.get(username); //return null if not contain username
    }

    public Course getCourse(int courseNum) {
        return courses.get(courseNum);
    }

    public List<Integer> getKdamCoursesOf(int courseNum) {
        return courses.get(courseNum).getKdamCourses();
    }

    public String getCourseStatus(int courseNum) {
        synchronized (courseLock) { //in case of course modifying
            Course course = courses.get(courseNum);
            String courseName = "Course: (" + courseNum + ")" + " " + course.getCourseName();
            String seatAvailable = "Seat Available: " + (course.getNumOfMaxStudents() - course.getCurrRegisteredNum()) + "/" + course.getNumOfMaxStudents();
            String studentRegistered = "Student Registered: " + course.getRegisteredStudents().toString().replace(" ", "");
            return courseName + "\n" + seatAvailable + "\n" + studentRegistered;
        }
    }

    public String getStudentStatus(String username) {
        synchronized (accountLock) { //in case of account modifying
            String student = "Student: " + username;
            String studentCourses = "Courses: " + students.get(username).getMyCourses().toString().replace(" ", "");
            return student + "\n" + studentCourses;
        }
    }

    public int getCoursePos(int courseNum) {
        return courses.get(courseNum).getPosition();
    }

    //==================================Modifiers================================
    public boolean registerCourse(String username, int courseNum) {
        if (courses.get(courseNum) == null) {
            return false;
        }
        synchronized (accountLock) { //in case of account access/modifying
            synchronized (courseLock) { //in case of course access/modifying
                return  students.get(username).registerCourse(courseNum) &&
                         courses.get(courseNum).registerStudent(username);
            }
        }
    }

    public boolean unregisterCourse(String username, int courseNum) {
        if (courses.get(courseNum) == null) {
            return false;
        }
        synchronized (accountLock) { //in case of account access/modifying
            synchronized (courseLock) { //in case of course access/modifying
                return students.get(username).unregisterCourse(courseNum) &&
                        courses.get(courseNum).unregisterStudent(username);
            }
        }
    }

    public boolean signUpStudent(Account account) {
        synchronized (accountLock) { //in case of account access/modifying
            if (students.containsKey(account.getUsername()) | admins.containsKey(account.getUsername())) {
                return false;
            }
            students.put(account.getUsername(), (Student) account);
            return true;
        }
    }

    public boolean signUpAdmin(Account account) {
        synchronized (accountLock) { //in case of account access/modifying
            if (students.containsKey(account.getUsername()) | admins.containsKey(account.getUsername())) {
                return false;
            }
            admins.put(account.getUsername(), (Admin) account);
            return true;
        }
    }
}
