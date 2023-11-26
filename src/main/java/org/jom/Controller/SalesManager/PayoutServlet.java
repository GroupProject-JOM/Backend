package org.jom.Controller.SalesManager;

import com.google.gson.Gson;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.Supplier.Collection.CollectionDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.Collection.SupplyModel;
import org.jom.Model.EmployeeModel;
import org.jom.Model.EstateModel;
import org.jom.Model.UserModel;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/payout")
public class PayoutServlet extends HttpServlet {
    //Get payout
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int collection_id = Integer.parseInt(request.getParameter("id"));
        int user_id = Integer.parseInt(request.getParameter("user"));

        try {
            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("sales-manager")) {

                    SupplyDAO supplyDAO = new SupplyDAO();
                    SupplyModel supply = supplyDAO.getPayout(collection_id);

                    Gson gson = new Gson();
                    String objectArray = gson.toJson(supply); // Object array to json

                    if (supply.getId() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"payout\": " + objectArray + "}");
                        System.out.println("Send payout");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"payout\": \"0\"}");
                        System.out.println("No payout");
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

    //Update payment status
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        StringBuilder requestBody = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        JSONObject jsonObject = new JSONObject(requestBody.toString());
        int collection_id = jsonObject.getInt("id");
        int user_id = jsonObject.getInt("user");

        UserDAO userDAO = new UserDAO();
        UserModel user = userDAO.getUserById(user_id);

        if (user.getId() != 0) {
            if (user.getRole().equals("sales-manager")) {
                try {
                    CollectionDAO collectionDAO = new CollectionDAO();

                    if (collectionDAO.updateStatus(6,collection_id)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"payment accepted\"}");
                        System.out.println("payment accepted");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"message\": \"Payment not accepted\"}");
                        System.out.println("Payment not accepted");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } finally {
                    out.close();
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
    }
}
