package org.jom.Controller.StockManager;

import com.google.gson.Gson;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.Collection.SupplyModel;
import org.jom.Model.EmployeeModel;
import org.jom.Model.UserModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/supply-requests")
public class SupplyRequestsServlet extends HttpServlet {
    //Get All supply requests
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int user_id = Integer.parseInt(request.getParameter("user"));

        try {
            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("stock-manager")) {

                    SupplyDAO supplyDAO = new SupplyDAO();
                    List<SupplyModel> supplies = supplyDAO.getAll();

                    Gson gson = new Gson();
                    String objectArray = gson.toJson(supplies); // Object array to json

                    if (supplies.size() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"size\": " + supplies.size() + ",\"list\":" + objectArray + "}");
                        System.out.println("Send all supply requests");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"size\": \"0\"}");
                        System.out.println("No Supplies");
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
