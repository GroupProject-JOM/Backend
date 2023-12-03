package org.jom.Controller.ProductionManager;

import com.google.gson.Gson;
import org.jom.Dao.ProductionDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.ProductionModel;
import org.jom.Model.UserModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/production-manager")
public class ProductionManagerServlet extends HttpServlet {
    // Get all production requests
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int user_id = Integer.parseInt(request.getParameter("user"));

        try {
            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("production-manager") || user.getRole().equals("stock-manager")) {
                    ProductionDAO productionDAO = new ProductionDAO();
                    List<ProductionModel> productionModels = productionDAO.getAllProductionRequests();

                    Gson gson = new Gson();
                    // Object array to json
                    String objectArray = gson.toJson(productionModels);

                    if (productionModels.size() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"size\": " + productionModels.size() + ",\"productions\":" + objectArray + "}");
                        System.out.println("View all Production requests");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"size\": \"0\"}");
                        System.out.println("No Production requests");
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
