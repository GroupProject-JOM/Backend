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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {
    // add account
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

    // Get single account
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int supplier_id = Integer.parseInt(request.getParameter("sId"));
        int account_id = Integer.parseInt(request.getParameter("id"));

        try {
            AccountDAO accountDAO = new AccountDAO();
            AccountModel account = accountDAO.getAccount(supplier_id,account_id);

            Gson gson = new Gson();
            // Object array to json
            String object = gson.toJson(account);

            if(account.getId() !=0){
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"account\": "+ object +"}");
                System.out.println("Send Account");
            }else{
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                out.write("{\"account\": \"No account\"}");
                System.out.println("No Account");
            }
            // TODO handle

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }

    //update account
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            AccountModel account = gson.fromJson(bufferedReader, AccountModel.class);

            if(account.getSupplier_id() == 0){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                System.out.println("UnAuthorized");
                return;
            }
            // TODO backend validations and user exists


            if(account.updateAccount()){

                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Account Updated successfully\"}");
                System.out.println("Account Update successfully");
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"Account is not updated\"}");
                System.out.println("Account is not Updated");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }

    // delete account
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int supplier_id = Integer.parseInt(request.getParameter("sId"));
        int account_id = Integer.parseInt(request.getParameter("id"));

        try {
            AccountDAO accountDAO = new AccountDAO();

            if(accountDAO.deleteAccount(supplier_id,account_id)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Delete Account\"}");
                System.out.println("Delete Account");
            }else {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                out.write("{\"message\": \"Unable to Delete Account\"}");
                System.out.println("Account not deleted");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
