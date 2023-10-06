package org.jom.Controller.Supplier;

import com.google.gson.Gson;
import org.jom.Model.AccountModel;
import org.jom.Model.EstateModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            AccountModel account = gson.fromJson(bufferedReader, AccountModel.class);

            // TODO backend validations

            account.addAccount();

            if(account.getId() != 0){
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Account added successfully\"}");
                System.out.println("Account added successfully");
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"Account is not added\"}");
                System.out.println("Account is not added");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
