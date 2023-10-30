package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Dao.OutletDAO;
import org.jom.Dao.Supplier.AccountDAO;
import org.jom.Model.AccountModel;
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

            // TODO backend validations and user exists


            outlet.addOutlet();

            if(outlet.getId() != 0){

                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Outlet added successfully\"}");
                System.out.println("Outlet added successfully");
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"Outlet is not added\"}");
                System.out.println("Outlet is not added");
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

        try {
            OutletDAO outletDAO = new OutletDAO();
            OutletModel outlet = outletDAO.getOutlet(outlet_id);

            Gson gson = new Gson();
            // Object array to json
            String object = gson.toJson(outlet);

            if(outlet.getId() !=0){
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"outlet\": "+ object +"}");
                System.out.println("Send Outlet");
            }else{
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                out.write("{\"outlet\": \"No outlet\"}");
                System.out.println("No Outlet");
            }
            // TODO handle

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

            // TODO backend validations and user exists


            if(outlet.updateOutlet()){

                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Outlet Updated successfully\"}");
                System.out.println("Outlet Update successfully");
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"Outlet is not updated\"}");
                System.out.println("Outlet is not Updated");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
