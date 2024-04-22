package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Auth.JwtUtils;
import org.jom.Dao.*;
import org.jom.Email.SendEmail;
import org.jom.Model.*;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/employee")
public class EmployeeServlet extends HttpServlet {
    // add employee
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get all cookies from the request
        Cookie[] cookies = request.getCookies();
        JwtUtils jwtUtils = new JwtUtils();

        if (!jwtUtils.CheckJWT(cookies)) {
            if (jwtUtils.CheckRefresh(cookies))
                response.addCookie(jwtUtils.getNewJWT(cookies));
            else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                return;
            }
        }

        // get auth payload data
        JSONObject jsonObject = jwtUtils.getAuthPayload();
        int user_id = (int) jsonObject.get("user");
        String role = (String) jsonObject.get("page");

        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            EmployeeModel employee = gson.fromJson(bufferedReader, EmployeeModel.class);
            employee.setEmp(user_id);

            if (user_id != 0) {
                if (role.equals("admin")) {

                    // Check input field is empty
                    if (employee.getFirst_name().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"fname\"}");
                        return;
                    }
                    if (employee.getLast_name().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"lname\"}");
                        return;
                    }
                    if (employee.getEmail().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"email1\"}");
                        return;
                    }
                    if (employee.getPhone().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"phone\"}");
                        return;
                    }
                    if (employee.getAdd_line_1().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"address1\"}");
                        return;
                    }
                    if (employee.getAdd_line_2().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"address2\"}");
                        return;
                    }
                    if (employee.getAdd_line_3().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"address3\"}");
                        return;
                    }
                    if (employee.getRole().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"role\"}");
                        return;
                    }

                    int count = 0;
                    String roles[] = {"collector", "distributor", "stock-manager", "production-manager", "sales-manager"};
                    for (String r : roles) {
                        if (!employee.getRole().equals(r)) {
                            count++;
                        } else break;
                        if (count >= 5) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.write("{\"message\": \"roleV\"}");
                            return;
                        }
                    }

                    if (employee.getDob().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"dob\"}");
                        return;
                    }
                    if (employee.getNic().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"nic\"}");
                        return;
                    }

                    // Email validation
                    String regex = "[a-z0-9\\.\\-]+@[a-z]+\\.[a-z]{2,3}";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(employee.getEmail());
                    if (!matcher.matches()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"email2\"}");
                        System.out.println("Invalid email");
                        return;
                    }

                    if (employee.EmailExists()) {
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        out.write("{\"message\": \"email3\"}");
                        System.out.println("Email already exists");
                        return;
                    }

                    if (employee.NICExists()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"NIC\"}");
                        System.out.println("NIC already exists");
                        return;
                    }

                    //Generate and send new password to email
                    SendEmail sendEmail = new SendEmail();
                    String password = SendEmail.SendPassword(employee.getEmail(), employee.getFirst_name());
                    System.out.println(password);

                    employee.setPassword(password); //Save password in db

                    employee.setValidity(1); // Mark as validate user

                    // All validations are passed then register
                    employee.Register();

                    //collector to collector table
                    if (employee.getRole().equals("collector")) {
                        CollectorDAO collectorDAO = new CollectorDAO();
                        if (collectorDAO.register(employee.getId())) {
                            System.out.println("Collector added successfully");
                        }
                    } else if (employee.getRole().equals("distributor")) {
                        DistributorDAO distributorDAO = new DistributorDAO();
                        if (distributorDAO.register(employee.getId())) {
                            System.out.println("Distributor added successfully");
                        }
                    }

                    if (employee.geteId() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Registration successfully\"}");
                        System.out.println("Registration successful");
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                        out.write("{\"message\": \"Registration unsuccessfully\"}");
                        System.out.println("Registration incorrect");
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

    // Get single employee
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get all cookies from the request
        Cookie[] cookies = request.getCookies();
        JwtUtils jwtUtils = new JwtUtils();

        if (!jwtUtils.CheckJWT(cookies)) {
            if (jwtUtils.CheckRefresh(cookies))
                response.addCookie(jwtUtils.getNewJWT(cookies));
            else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                return;
            }
        }

        // get auth payload data
        JSONObject jsonObject = jwtUtils.getAuthPayload();
        int user_id = (int) jsonObject.get("user");
        String role = (String) jsonObject.get("page");

        int employee_id = Integer.parseInt(request.getParameter("id"));

        try {
            if (user_id != 0) {
                if (role.equals("admin")) {

                    EmployeeDAO employeeDAO = new EmployeeDAO();
                    EmployeeModel employee = employeeDAO.getEmployee(employee_id);

                    Gson gson = new Gson();
                    // Object array to json
                    String object = gson.toJson(employee);

                    if (employee.geteId() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"employee\": " + object + "}");
                        System.out.println("Send employee");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"employee\": \"No employee\"}");
                        System.out.println("No employee");
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

    //update employee
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get all cookies from the request
        Cookie[] cookies = request.getCookies();
        JwtUtils jwtUtils = new JwtUtils();

        if (!jwtUtils.CheckJWT(cookies)) {
            if (jwtUtils.CheckRefresh(cookies))
                response.addCookie(jwtUtils.getNewJWT(cookies));
            else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                return;
            }
        }

        // get auth payload data
        JSONObject jsonObject = jwtUtils.getAuthPayload();
        int user_id = (int) jsonObject.get("user");
        String role = (String) jsonObject.get("page");

        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            EmployeeModel employee = gson.fromJson(bufferedReader, EmployeeModel.class);
            employee.setEmp(user_id);

            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(employee.getEmp());

            if (user_id != 0) {
                if (role.equals("admin")) {

                    // Check input field is empty
                    if (employee.getFirst_name().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"fname\"}");
                        return;
                    }
                    if (employee.getLast_name().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"lname\"}");
                        return;
                    }
                    if (employee.getPhone().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"phone\"}");
                        return;
                    }
                    if (employee.getAdd_line_1().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"address1\"}");
                        return;
                    }
                    if (employee.getAdd_line_2().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"address2\"}");
                        return;
                    }
                    if (employee.getAdd_line_3().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"address3\"}");
                        return;
                    }
                    if (employee.getRole().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"role\"}");
                        return;
                    }

                    int count = 0;
                    String roles[] = {"collector", "distributor", "stock-manager", "production-manager", "sales-manager"};
                    for (String r : roles) {
                        if (!employee.getRole().equals(r)) {
                            count++;
                        } else break;
                        if (count >= 5) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.write("{\"message\": \"roleV\"}");
                            return;
                        }
                    }

                    if (employee.getDob().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"dob\"}");
                        return;
                    }
                    if (employee.getNic().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"nic\"}");
                        return;
                    }

                    if (employee.NICExists()) {
                        if (employee.getEId() != employee.geteId()) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.write("{\"message\": \"NIC\"}");
                            System.out.println("NIC already exists");
                            return;
                        }
                    }

                    employee.getUserId();

                    if (employee.updateUser()) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Employee Updated successfully\"}");
                        System.out.println("Employee Update successfully");
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                        out.write("{\"message\": \"Employee is not updated\"}");
                        System.out.println("Employee is not Updated");
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

    // delete Employee
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get all cookies from the request
        Cookie[] cookies = request.getCookies();
        JwtUtils jwtUtils = new JwtUtils();

        if (!jwtUtils.CheckJWT(cookies)) {
            if (jwtUtils.CheckRefresh(cookies))
                response.addCookie(jwtUtils.getNewJWT(cookies));
            else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                return;
            }
        }

        // get auth payload data
        JSONObject jsonObject = jwtUtils.getAuthPayload();
        int user_id = (int) jsonObject.get("user");
        String role = (String) jsonObject.get("page");

        int employeeId = Integer.parseInt(request.getParameter("id"));

        try {
            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user_id != 0) {
                if (role.equals("admin")) {

                    EmployeeDAO employeeDAO = new EmployeeDAO();
                    EmployeeModel employee = new EmployeeModel(employeeId);
                    employee.getUserId();
                    if (employeeDAO.deleteUser(employee.getId())) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Delete employee\"}");
                        System.out.println("Delete employee");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"message\": \"Unable to Delete employee\"}");
                        System.out.println("employee not deleted");
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
