package org.jom.Controller.Supplier;

import com.google.gson.Gson;
import org.jom.Dao.Supplier.AccountDAO;
import org.jom.Dao.Supplier.EstateDAO;
import org.jom.Model.AccountModel;
import org.jom.Model.EstateModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/accounts")
public class AccountsServlet extends HttpServlet {
    // Get all accounts
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int supplier_id = Integer.parseInt(request.getParameter("sId"));

        try {
            AccountDAO accountDAO = new AccountDAO();
            List<AccountModel> accounts = accountDAO.getAll(supplier_id);

            Gson gson = new Gson();
            // Object array to json
            String objectArray = gson.toJson(accounts);

            if(accounts.size() != 0){
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"size\": "+ accounts.size() +",\"list\":"+ objectArray+"}");
                System.out.println("View all Accounts");
            }else if(accounts.size() == 0){
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                out.write("{\"size\": \"0\"}");
                System.out.println("No Accounts");
            }else{
                // TODO handle
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
