package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.OutletDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.EmployeeModel;
import org.jom.Model.OutletModel;
import org.jom.Model.UserModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
}
