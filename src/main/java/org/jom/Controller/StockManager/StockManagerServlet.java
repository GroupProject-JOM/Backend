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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/stock-manager")
public class StockManagerServlet extends HttpServlet {
    //Get Dashboard content
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int employee_id = Integer.parseInt(request.getParameter("sId"));

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel stock_manager = employeeDAO.getEmployee(employee_id);

            if (stock_manager.geteId() != 0) {
                if (stock_manager.getRole().equals("stock-manager")) {

                    SupplyDAO supplyDAO = new SupplyDAO();
                    List<SupplyModel> supplies = supplyDAO.getAll();

                    Gson gson = new Gson();

                    if (supplies.size() != 0) {
                        if (supplies.size() > 4) {
                            List<SupplyModel> firstFour = new ArrayList<>(supplies.subList(0, 4));
                            String objectArray = gson.toJson(firstFour); // Object array to json
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.write("{\"size\": " + supplies.size() + ",\"list\":" + objectArray + "}");
                            System.out.println("Stock manager dashboard tables contents");
                        } else {
                            String objectArray = gson.toJson(supplies); // Object array to json
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.write("{\"size\": " + supplies.size() + ",\"list\":" + objectArray + "}");
                            System.out.println("Stock manager dashboard tables contents");
                        }
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
