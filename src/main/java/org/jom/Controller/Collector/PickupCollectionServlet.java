package org.jom.Controller.Collector;

import com.google.gson.Gson;
import org.jom.Dao.CocoRateDAO;
import org.jom.Dao.CollectorDAO;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.Supplier.Collection.CollectionDAO;
import org.jom.Dao.Supplier.Collection.PickupDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.CocoModel;
import org.jom.Model.Collection.SupplyModel;
import org.jom.Model.EmployeeModel;
import org.jom.Model.UserModel;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import static java.lang.Float.parseFloat;

@WebServlet("/pickup-collection")
public class PickupCollectionServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int user_id = Integer.parseInt(request.getParameter("user"));
        int collection_id = Integer.parseInt(request.getParameter("id"));

        try {
            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("collector") || user.getRole().equals("admin")) {
                    SupplyDAO supplyDAO = new SupplyDAO();
                    SupplyModel collection = supplyDAO.getCollection(collection_id);

                    Gson gson = new Gson();
                    String object = gson.toJson(collection); // Object array to json

                    if (collection.getId() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"collection\": " + object + "}");
                        System.out.println("Send collection");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"collection\": \"No collection\"}");
                        System.out.println("No collection");
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

    // Complete collection
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
        int final_amount = jsonObject.getInt("amount");
        int collection_id = jsonObject.getInt("id");
        int user_id = jsonObject.getInt("user");

        UserDAO userDAO = new UserDAO();
        UserModel user = userDAO.getUserById(user_id);

        //check if emails are correct
        if (user.getRole().equals("collector") || user.getRole().equals("admin")) {

            if (user.getRole().equals("admin")) {
                SupplyDAO supplyDAO = new SupplyDAO();
                user_id = supplyDAO.getCollectorUserIDByCollectionID(collection_id);
            }

            try {
                PickupDAO pickupDAO = new PickupDAO();
                CollectionDAO collectionDAO = new CollectionDAO();
                CollectorDAO collectorDAO = new CollectorDAO();

                CocoRateDAO cocoRateDAO = new CocoRateDAO();
                CocoModel cocoRate = cocoRateDAO.getLastRecord();

                float value = final_amount*parseFloat(cocoRate.getPrice());

                if (pickupDAO.updateCollectedDate(collection_id) && collectionDAO.updateFinalAmount(final_amount,value ,collection_id) && collectorDAO.updateTodayAmount(final_amount, user_id)) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"message\": \"Collection Completed\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    out.write("{\"message\": \"Not completed\"}");
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
        }

    }
}
