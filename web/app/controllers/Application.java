package controllers;

import models.Constants;
import models.User;
import play.mvc.*;

import java.util.List;

public class Application extends Controller {

    private static void checkTeacher(){
        checkUser();

        User u = (User) renderArgs.get("user");
        if (!u.getType().equals(Constants.User.TEACHER)){
            return;
        }
    }

    private static void checkUser(){
        if (session.contains("username")){
            User u = User.loadUser(session.get("username"));
            if (u != null){
                renderArgs.put("user", u);
                return;
            }
        }
        Secure.login();
    }

    public static void index() {
        checkUser();

        User u = (User) renderArgs.get("user");

        if (u.getType().equals(Constants.User.TEACHER)){
            List<User> students = User.loadStudents();
            render("Application/teacher.html", u, students);
        }else{
            render("Application/student.html", u);
        }
    }


    public static void removeStudent(String student) {
        checkTeacher();

        User.remove(student);
        index();
    }


    public static void setMark(String student) {
        User u = User.loadUser(student);
        render(u);
    }

    public static void doSetMark(String student, Integer mark) {
        User u = User.loadUser(student);
        int processedMark = 0;
        
        try {
        	processedMark = mark.intValue();
        
        } catch (Exception e) {
        	// At this point we probably don't have an integer
        	// TODO show a message indicating the error
        	setMark(student);
        }

        // Mark outside the right range
        if (processedMark < 0 || processedMark > 10) {
        	// TODO show a message indicating the error
        	setMark(student);
        }
        else {
        	u.setMark(processedMark);
            u.save();
            index();
        }
        
    }
}