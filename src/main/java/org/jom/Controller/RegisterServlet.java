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

@WebServlet("/signup")
public class RegisterServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            e.printStackTrace(); // Print the exception details for debugging
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
