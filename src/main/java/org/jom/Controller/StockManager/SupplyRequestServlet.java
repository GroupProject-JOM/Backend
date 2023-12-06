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
import java.util.List;

@WebServlet("/supply-request")
public class SupplyRequestServlet extends HttpServlet {
    //Get relevant request
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int collection_id = Integer.parseInt(request.getParameter("id"));
        int user_id = Integer.parseInt(request.getParameter("user"));

        try {
            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("stock-manager") || user.getRole().equals("admin") || user.getRole().equals("supplier")) {

                    SupplyDAO supplyDAO = new SupplyDAO();
                    SupplyModel supply = supplyDAO.getSupply(collection_id);

                    Gson gson = new Gson();
                    String objectArray = gson.toJson(supply); // Object array to json

                    if (supply.getId() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"request\": " + objectArray + "}");
                        System.out.println("Send supply request");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"request\": \"0\"}");
                        System.out.println("No supply request");
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
