package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.Supplier.SupplierDAO;
import org.jom.Model.EmployeeModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/sales-manager")
public class SalesManagerServlet extends HttpServlet {
    // Get sales manager dashboard content
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int employee_id = Integer.parseInt(request.getParameter("emp"));

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel employee = employeeDAO.getEmployee(employee_id);

            if (employee.geteId() != 0) {
                if (employee.getRole().equals("sales-manager")) {

                    SupplierDAO supplierDAO = new SupplierDAO();
                    int pending_payments = supplierDAO.pendingPayments();
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"payments\": " + pending_payments + "}");
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
