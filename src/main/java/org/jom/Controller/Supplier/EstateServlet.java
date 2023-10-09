package org.jom.Controller.Supplier;

import com.google.gson.Gson;
import org.jom.Dao.Supplier.EstateDAO;
import org.jom.Model.EstateModel;
import org.jom.Model.LoginModel;
import org.jom.Model.UserModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/estate")
public class EstateServlet extends HttpServlet {
    // Add new estate
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            EstateModel estate = gson.fromJson(bufferedReader, EstateModel.class);

            if(estate.getSupplier_id() == 0){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                System.out.println("UnAuthorized");
                return;
            }
            // TODO backend validations and user exists


            estate.addEstate();

            if(estate.getId() != 0){

                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Estate added successfully\"}");
                System.out.println("Estate added successfully");
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"Estate is not added\"}");
                System.out.println("Estate is not added");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }

    // Get all estates
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            int supplier_id =1;
            EstateDAO estateDAO = new EstateDAO();
            List<EstateModel> estates = estateDAO.getAll(supplier_id);

            Gson gson = new Gson();
            // Object array to json
            String objectArray = gson.toJson(estates);

            if(estates.size() != 0){
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"size\": "+ estates.size() +",\"list\":"+ objectArray+"}");
                System.out.println("View all");
            }else if(estates.size() == 0){
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                out.write("{\"size\": \"0\"}");
                System.out.println("No Estates");
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
