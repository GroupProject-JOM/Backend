package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.OutletDAO;
import org.jom.Dao.Supplier.AccountDAO;
import org.jom.Model.AccountModel;
import org.jom.Model.EmployeeModel;
import org.jom.Model.EstateModel;
import org.jom.Model.OutletModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/outlet")
public class OutletServlet extends HttpServlet {
    // Add new outlet
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            OutletModel outlet = gson.fromJson(bufferedReader, OutletModel.class);

            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel employee = employeeDAO.getEmployee(outlet.getEmp_id());

            if (employee.geteId() != 0) {
                if (employee.getRole().equals("distributor") || employee.getRole().equals("admin") || employee.getRole().equals("sales-manager")) {

                    outlet.addOutlet();
                    if (outlet.getId() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Outlet added successfully\"}");
                        System.out.println("Outlet added successfully");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"Outlet is not added\"}");
                        System.out.println("Outlet is not added");
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

    // Get single outlet
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int outlet_id = Integer.parseInt(request.getParameter("id"));
        int employee_id = Integer.parseInt(request.getParameter("emp"));

        try {
            OutletDAO outletDAO = new OutletDAO();
            OutletModel outlet = outletDAO.getOutlet(outlet_id);

            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel employee = employeeDAO.getEmployee(employee_id);

            if (employee.geteId() != 0) {
                if (employee.getRole().equals("distributor") || employee.getRole().equals("admin") || employee.getRole().equals("sales-manager")) {
                    if (outlet.getId() != 0) {
                        Gson gson = new Gson();
                        // Object array to json
                        String object = gson.toJson(outlet);

                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"outlet\": " + object + "}");
                        System.out.println("Send Outlet");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"outlet\": \"No outlet\"}");
                        System.out.println("No Outlet");
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

    //update outlet
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            OutletModel outlet = gson.fromJson(bufferedReader, OutletModel.class);

            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel employee = employeeDAO.getEmployee(outlet.getEmp_id());

            if (employee.geteId() != 0) {
                if (employee.getRole().equals("distributor") || employee.getRole().equals("admin") || employee.getRole().equals("sales-manager")) {

                    if (outlet.updateOutlet()) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Outlet Updated successfully\"}");
                        System.out.println("Outlet Update successfully");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"Outlet is not updated\"}");
                        System.out.println("Outlet is not Updated");
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

    // delete outlet
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int outlet_id = Integer.parseInt(request.getParameter("id"));
        int employee_id = Integer.parseInt(request.getParameter("emp"));

        try {
            OutletDAO outletDAO = new OutletDAO();

            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel employee = employeeDAO.getEmployee(employee_id);

            if (employee.geteId() != 0) {
                if (employee.getRole().equals("distributor") || employee.getRole().equals("admin") || employee.getRole().equals("sales-manager")) {
                    if (outletDAO.deleteOutlet(outlet_id)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Delete Outlet\"}");
                        System.out.println("Delete Outlet");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"Unable to Delete Outlet\"}");
                        System.out.println("Outlet not deleted");
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
