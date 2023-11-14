package org.jom.Controller.StockManager;

import com.google.gson.Gson;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/accept-request")
public class AcceptRequestServlet extends HttpServlet {
    //Accept request
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int collection_id = Integer.parseInt(request.getParameter("id"));
        int employee_id = Integer.parseInt(request.getParameter("sId"));

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel stock_manager = employeeDAO.getEmployee(employee_id);

            if (stock_manager.geteId() != 0) {
                if (stock_manager.getRole().equals("stock-manager")) {

                    SupplyDAO supplyDAO = new SupplyDAO();

                    if (supplyDAO.acceptSupply(collection_id)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Supply request accepted\"}");
                        System.out.println("Supply request accepted");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"message\": \"Not accepted\"}");
                        System.out.println("Not accepted");
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

    //Decline request
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
        int employee_id = jsonObject.getInt("sId");

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel stock_manager = employeeDAO.getEmployee(employee_id);

            if (stock_manager.geteId() != 0) {
                if (stock_manager.getRole().equals("stock-manager")) {

                    SupplyDAO supplyDAO = new SupplyDAO();

                    if (supplyDAO.rejectSupply(collection_id)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Supply request rejected\"}");
                        System.out.println("Supply request rejected");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"message\": \"Not rejected\"}");
                        System.out.println("Not rejected");
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
