package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.OutletDAO;
import org.jom.Dao.Supplier.AccountDAO;
import org.jom.Dao.Supplier.EstateDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/outlets")
public class OutletsServlet extends HttpServlet {
    // Get all outlets
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int user_id = Integer.parseInt(request.getParameter("user"));

        try {
            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("distributor") || user.getRole().equals("admin") || user.getRole().equals("sales-manager")) {
                    OutletDAO outletDAO = new OutletDAO();
                    List<OutletModel> outlets = outletDAO.getAll();

                    Gson gson = new Gson();
                    // Object array to json
                    String objectArray = gson.toJson(outlets);

                    if (outlets.size() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"size\": " + outlets.size() + ",\"list\":" + objectArray + "}");
                        System.out.println("View all Outlets");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"size\": \"0\"}");
                        System.out.println("No Outlet");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write("{\"message\": \"Invalid User\"}");
                    System.out.println("Invalid User");
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
