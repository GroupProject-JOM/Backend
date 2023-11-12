package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Model.SupplierModel;
import org.jom.Model.UserModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/updateEmail")
public class UpdateEmailServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            UserModel user = gson.fromJson(bufferedReader, UserModel.class);


            if(user.getEmail().isEmpty()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"email1\"}");
                return;
            }

            // Email validation
            String regex = "[a-z0-9\\.\\-]+@[a-z]+\\.[a-z]{2,3}";
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

            if(user.getId() != 0){
                    if(user.updateEmail()){
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Email updated successfully\"}");
                        System.out.println("Email updated successfully");
                    }else{
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"Failed to update email\"}");
                        System.out.println("Failed to update email");
                    }
            }else{
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"Invalid user\"}");
                System.out.println("Invalid user");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
