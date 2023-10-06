package org.jom.Controller;

import org.jom.Model.UserModel;
import com.google.gson.Gson;
import org.jom.OTP.SendEmailOTP;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            // Check input field is empty
            if(user.getFirst_name().isEmpty()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"fname\"}");
                return ;
            }if(user.getLast_name().isEmpty()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"lname\"}");
                return;
            }
            if(user.getEmail().isEmpty()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"email1\"}");
                return;
            }
            if(user.getPassword().isEmpty()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"password\"}");
                return;
            }
            if(user.getPhone().isEmpty()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"phone\"}");
                return;
            }
            if(user.getAdd_line_1().isEmpty()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"address1\"}");
                return;
            }
            if(user.getAdd_line_2().isEmpty()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"address2\"}");
                return;
            }
            if(user.getAdd_line_3().isEmpty()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"address3\"}");
                return;
            }

            if(user.getRole() == null){
                user.setRole("supplier");
            }

            // Email validation
            String regex = "[a-z0-9]+@[a-z]+\\.[a-z]{2,3}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(user.getEmail());
            if(!matcher.matches()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"email2\"}");
                System.out.println("Invalid email");
                return;
            }

            if(user.EmailExists()){
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.write("{\"message\": \"email3\"}");
                System.out.println("Email already exists");
                return;
            }

            // All validations are passed then register
            user.Register();

            if(user.getId() != 0){
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Registration successfully\",");
                out.write("\"id\": \""+ user.getId() + "\",");
                out.write("\"email\": \""+ user.getEmail() + "\",");
                out.write("\"phone\": \""+ user.getPhone() +"\"}");
                System.out.println("Registration successful");
            }else{
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"Registration unsuccessfully\"}");
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
