package org.jom.Controller.Supplier;

import com.google.gson.Gson;
import org.jom.Dao.Supplier.EstateDAO;
import org.jom.Model.EstateModel;

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
    // Get single estates
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int supplier_id = Integer.parseInt(request.getParameter("sId"));
        int estate_id = Integer.parseInt(request.getParameter("id"));

        try {
            EstateDAO estateDAO = new EstateDAO();
            EstateModel estate = estateDAO.getEstate(supplier_id,estate_id);

            Gson gson = new Gson();
            // Object array to json
            String object = gson.toJson(estate);

            if(estate.getId() !=0){
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"estate\": "+ object +"}");
                System.out.println("Send Estate");
            }else{
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                out.write("{\"estate\": \"No estate\"}");
                System.out.println("No Estate");
            }
                // TODO handle

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }

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

}
