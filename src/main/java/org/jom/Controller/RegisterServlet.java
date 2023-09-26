package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Model.UserModel;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/signup")
public class RegisterServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("Inside");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Gson gson = new Gson();

            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            UserModel user = gson.fromJson(bufferedReader, UserModel.class);

//        String first_name = request.getParameter("fname");
//        String last_name = request.getParameter("lname");
//        String email = request.getParameter("email");
//        String password = request.getParameter("password");
//        String phone = request.getParameter("phone");
//        String address1 = request.getParameter("address1");
//        String address2 = request.getParameter("address2");
//        String address3 = request.getParameter("address3");

//        UserModel user = new UserModel(first_name,last_name,email,password,phone,address1,address2,address3);

            System.out.println(user.getFirst_name());
            System.out.println(user.getLast_name());
            System.out.println(user.getEmail());
            System.out.println(user.getPassword());
            System.out.println(user.getPhone());
            System.out.println(user.getAdd_line_1());
            System.out.println(user.getAdd_line_2());
            System.out.println(user.getAdd_line_3());

            // Check input field is empty
            if(user.getFirst_name().isEmpty()){
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.write("First name cannot be empty!");
                return;
            }if(user.getLast_name().isEmpty()){
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.write("Last name cannot be empty!");
                return;
            }
            if(user.getEmail().isEmpty()){
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.write("Email cannot be empty!");
                return;
            }
            if(user.getPassword().isEmpty()){
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.write("Password cannot be empty!");
                return;
            }
            if(user.getPhone().isEmpty()){
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.write("Contact number cannot be empty!");
                return;
            }
            if(user.getAdd_line_1().isEmpty()){
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.write("Address line 1 cannot be empty!");
                return;
            }
            if(user.getAdd_line_2().isEmpty()){
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.write("Address line 2 cannot be empty!");
                return;
            }
            if(user.getAdd_line_3().isEmpty()){
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.write("Address line 3 cannot be empty!");
                return;
            }

            // Email validation
            String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\\\.[A-Z]{2,6}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(user.getEmail());

            if(!matcher.matches()){
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.write("Enter a valid email!");
                return;
            }



            if(user.Register()){
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("Registration successfully");
                System.out.println("Registration successful");
            }else{
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("Registration unsuccessfully");
                System.out.println("Registration incorrect");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
