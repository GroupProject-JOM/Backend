package org.jom.Controller.Collector;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@WebServlet("/pickup-collection")
public class PickupCollectionServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int employee_id = Integer.parseInt(request.getParameter("sId"));
        int collection_id = Integer.parseInt(request.getParameter("id"));

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel collector = employeeDAO.getEmployee(employee_id);

            if (collector.geteId() != 0) {
                if (collector.getRole().equals("collector")) {
                    SupplyDAO supplyDAO = new SupplyDAO();
                    SupplyModel collection = supplyDAO.getCollection(collection_id);

                    Gson gson = new Gson();
                    String object = gson.toJson(collection); // Object array to json

                    if(collection.getId() !=0){
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"collection\": "+ object +"}");
                        System.out.println("Send collection");
                    }else{
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
}
