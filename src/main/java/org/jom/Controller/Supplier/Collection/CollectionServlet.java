package org.jom.Controller.Supplier.Collection;

import com.google.gson.Gson;
import org.jom.Dao.Supplier.AccountDAO;
import org.jom.Dao.Supplier.Collection.CollectionDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Model.AccountModel;
import org.jom.Model.Collection.CollectionModel;
import org.jom.Model.Collection.SupplyModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/collection")
public class CollectionServlet extends HttpServlet {
    // Add new collection
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            CollectionModel collection = gson.fromJson(bufferedReader, CollectionModel.class);

            if(collection.getSupplier_id() == 0){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                System.out.println("UnAuthorized");
                return;
            }

            // TODO backend validations and user exists


            collection.addCollection();

            if(collection.getId() != 0){
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Supply request added successfully\",\"id\":\""+collection.getId()+"\"}");
                System.out.println("Supply request added successfully");
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"Supply request is not added\"}");
                System.out.println("Supply request is not added");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int supplier_id = Integer.parseInt(request.getParameter("sId"));

        try {
            SupplyDAO supplyDAO = new SupplyDAO();
            List<SupplyModel> supplies = supplyDAO.getAll(supplier_id);

            Gson gson = new Gson();
            // Object array to json
            String objectArray = gson.toJson(supplies);

            if (supplies.size() != 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"size\": " + supplies.size() + ",\"list\":" + objectArray + "}");
                System.out.println("Ongoing table supplies");
            } else if (supplies.size() == 0) {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                out.write("{\"size\": \"0\"}");
                System.out.println("No Supplies");
            } else {
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
