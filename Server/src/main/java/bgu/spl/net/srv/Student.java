package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.List;

public class Student extends Account{
    private Database database = Database.getInstance();
    private List<Integer> myCourses = new LinkedList<>();

    public Student(String username, String password) {
        super(username, password);
    }

    public List<Integer> getMyCourses() {
        return myCourses;
    }

    public boolean registerCourse(int courseNum){
        //check we aren't already registered to this course
        if(myCourses.contains(courseNum)){
            return false;
        }

        //kdam courses check
        List<Integer> currCourseKdam = database.getKdamCoursesOf(courseNum);
        for(Integer i: currCourseKdam){
            if(!myCourses.contains(i)){
                return false;
            }
        }

        //first course add
        if(myCourses.isEmpty()){
            myCourses.add(courseNum);
        }

        //after first course added, course add
        else {
            for (int i = 0; i < myCourses.size(); i++) {
                if (database.getCoursePos(myCourses.get(i)) > database.getCoursePos(courseNum)) {
                    myCourses.add(i, courseNum);
                    return true;
                }
            }
            myCourses.add(courseNum);
        }
        return true;
    }

    public boolean unregisterCourse(Integer courseNum){
        return myCourses.remove(courseNum);
    }

    public boolean isRegisterdCourse(int courseNum){
        return myCourses.contains(courseNum);
    }
}
