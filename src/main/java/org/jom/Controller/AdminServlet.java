package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.OutletDAO;
import org.jom.Dao.Supplier.Collection.CollectionDAO;
import org.jom.Dao.Supplier.SupplierDAO;
import org.jom.Model.EmployeeModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    // Dashboard content
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int admin_id = Integer.parseInt(request.getParameter("emp"));

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel admin = employeeDAO.getEmployee(admin_id);

            if (admin.geteId() != 0) {
                if (admin.getRole().equals("admin")) {

                    SupplierDAO supplierDAO = new SupplierDAO();
                    OutletDAO outletDAO = new OutletDAO();
                    CollectionDAO collectionDAO = new CollectionDAO();

                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"employees\": " + employeeDAO.rowCount() + ",\"suppliers\":" + supplierDAO.rowCount() + ",\"outlets\":" + outletDAO.rowCount() + ",\"collections\":" + collectionDAO.rowCount(2) + "}");
                    System.out.println("Send dashboard content");
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
