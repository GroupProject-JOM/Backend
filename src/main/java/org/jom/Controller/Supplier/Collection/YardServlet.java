package org.jom.Controller.Supplier.Collection;

import com.google.gson.Gson;
import org.jom.Dao.Supplier.Collection.CollectionDAO;
import org.jom.Model.Collection.PickupModel;
import org.jom.Model.Collection.YardModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/yard")
public class YardServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            YardModel yard = gson.fromJson(bufferedReader, YardModel.class);

            if(yard.getSupplier_id() == 0){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                System.out.println("UnAuthorized");
                return;
            }

            if(yard.getCollection_id() == 0){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                System.out.println("UnAuthorized");
                return;
            }

            // TODO backend validations and user exists

            yard.addYard();

            if(yard.getId() != 0){
                CollectionDAO collectionDAO = new CollectionDAO();
                if(collectionDAO.updateStatus(1,yard.getCollection_id())) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"message\": \"Yard request added successfully\"}");
                    System.out.println("Yard request added successfully");
                }
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"Yard request is not added\"}");
                System.out.println("Yard request is not added");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
