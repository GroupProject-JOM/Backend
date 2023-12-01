package org.jom.Controller.StockManager;

import com.google.gson.Gson;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.Collection.CollectionModel;
import org.jom.Model.Collection.SupplyModel;
import org.jom.Model.UserModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@WebServlet("/all-collections")
public class AllCollectionsServlet extends HttpServlet {
    //Get all collections
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
                    List<SupplyModel> accepted = supplyDAO.getAccepted();
                    List<SupplyModel> rejected = supplyDAO.getRejected();
                    List<SupplyModel> completed = supplyDAO.getCompleted();

                    Gson gson = new Gson();
                    String accepted_array = gson.toJson(accepted);
                    String rejected_array = gson.toJson(rejected);
                    String completed_array = gson.toJson(completed);

                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"accepted\": " + accepted_array + ",\"rejected\":" + rejected_array + ",\"completed\":" + completed_array + "}");
                    System.out.println("Supplier dashboard tables contents");

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
