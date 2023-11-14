package org.jom.Controller.StockManager;

import com.google.gson.Gson;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.Supplier.Collection.CollectionDAO;
import org.jom.Dao.Supplier.Collection.PickupDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Model.Collection.CollectorModel;
import org.jom.Model.Collection.SupplyModel;
import org.jom.Model.EmployeeModel;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/assign-collector")
public class AssignCollector extends HttpServlet {
    //Get Collectors and collection count to that day
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int employee_id = Integer.parseInt(request.getParameter("sId"));
        String date = request.getParameter("date");

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel stock_manager = employeeDAO.getEmployee(employee_id);

            if (stock_manager.geteId() != 0) {
                if (stock_manager.getRole().equals("stock-manager")) {

                    SupplyDAO supplyDAO = new SupplyDAO();
                    List<CollectorModel> collectors = supplyDAO.getCollectionCount(date);

                    Gson gson = new Gson();
                    String objectArray = gson.toJson(collectors); // Object array to json

                    if (collectors.size() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"size\": " + collectors.size() + ",\"list\":" + objectArray + "}");
                        System.out.println("Send collectors");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"size\": \"0\"}");
                        System.out.println("No collectors");
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

    //Assign collector
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
        int employee_id = jsonObject.getInt("emp");
        int stock_manager_id = jsonObject.getInt("sId");

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel stock_manager = employeeDAO.getEmployee(stock_manager_id);

            if (stock_manager.geteId() != 0) {
                if (stock_manager.getRole().equals("stock-manager")) {

                    CollectionDAO collectionDAO = new CollectionDAO();

                    if (collectionDAO.updateStatus(3, collection_id)) {
                        if (collectionDAO.assignCollector(collection_id, employee_id)) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.write("{\"message\": \"Collector assigned\"}");
                            System.out.println("Collector assigned");
                        } else {
                            response.setStatus(HttpServletResponse.SC_ACCEPTED);
                            out.write("{\"message\": \"No Collector assigned\"}");
                            System.out.println("No Collector assigned");
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"message\": \"No Collector assigned\"}");
                        System.out.println("No Collector assigned");
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
