package org.jom.Controller.StockManager;

import com.google.gson.Gson;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Model.Collection.SupplyModel;
import org.jom.Model.EmployeeModel;

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
        int employee_id = Integer.parseInt(request.getParameter("sId"));

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel stock_manager = employeeDAO.getEmployee(employee_id);

            if (stock_manager.geteId() != 0) {
                if (stock_manager.getRole().equals("stock-manager")) {

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