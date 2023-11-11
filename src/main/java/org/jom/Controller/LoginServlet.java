package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Dao.Supplier.SupplierDAO;
import org.jom.Model.EmployeeModel;
import org.jom.Model.LoginModel;
import org.jom.Model.SupplierModel;
import org.jom.Model.UserModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/signin")
public class LoginServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            LoginModel login = gson.fromJson(bufferedReader, LoginModel.class);
            UserModel user = login.getUser();

            if(user.getId() != 0){
                if(user.getValidity() != 0) {
                    if (user.getPassword().equals(login.getPassword())) {
                        if(user.getRole().equals("supplier")){
                            SupplierModel supplier = new SupplierModel(user.getId());
                            supplier.getSupplier();
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Login successfully\",\"page\":\"" + user.getRole() + "\",\"name\":\"" + user.getFirst_name() + "\",\"sId\":\""+ supplier.getId() +"\"}");
                        System.out.println("Login successful");
                        }else{
                            EmployeeModel employee = new EmployeeModel(user.getId(),0);
                            employee.getEIdById();
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.write("{\"message\": \"Login successfully\",\"page\":\"" + user.getRole() + "\",\"name\":\"" + user.getFirst_name() + "\",\"sId\":\""+ employee.geteId() +"\"}");
                            System.out.println("Login successful");
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"message\": \"password\"}");
                        System.out.println("Wrong password");
                    }
                }else{
                    // TODO hadle
                    System.out.println("User not validated");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write("{\"message\": \"validate\"}");
                    System.out.println("Login incorrect");
                }
            }else{
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"username\"}");
                System.out.println("Login incorrect");
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
