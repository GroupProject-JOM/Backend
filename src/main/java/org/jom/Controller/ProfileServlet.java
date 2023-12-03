package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.OutletDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.EmployeeModel;
import org.jom.Model.OutletModel;
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

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    // Get user data
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int user_id = Integer.parseInt(request.getParameter("user"));

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel user = employeeDAO.getUser(user_id);

            if (user.getId() != 0) {
                Gson gson = new Gson();
                String object = gson.toJson(user); // Object array to json

                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"user\": " + object + "}");
                System.out.println("Send user");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"user\": \"No user\"}");
                System.out.println("No user");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            UserModel user = gson.fromJson(bufferedReader, UserModel.class);

            // Check input field is empty
            if (user.getFirst_name().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"fname\"}");
                return;
            }
            if (user.getLast_name().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"lname\"}");
                return;
            }
            if (user.getPhone().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"phone\"}");
                return;
            }
            if (user.getAdd_line_1().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"address1\"}");
                return;
            }
            if (user.getAdd_line_2().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"address2\"}");
                return;
            }
            if (user.getAdd_line_3().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"address3\"}");
                return;
            }

            if (user.getId() != 0) {
                UserDAO userDAO = new UserDAO();
                if (userDAO.updateProfile(user)) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"message\": \"Profile updated successfully\"}");
                    System.out.println("Profile updated successfully");
                } else {
                    response.setStatus(HttpServletResponse.SC_ACCEPTED);
                    out.write("{\"message\": \"Cannot update profile\"}");
                    System.out.println("Cannot update profile");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"Invalid User\"}");
                System.out.println("Invalid User");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
