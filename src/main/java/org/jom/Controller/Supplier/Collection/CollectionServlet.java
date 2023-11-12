package org.jom.Controller.Supplier.Collection;

import com.google.gson.Gson;
import org.jom.Dao.Supplier.AccountDAO;
import org.jom.Dao.Supplier.Collection.CollectionDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Model.Collection.CollectionModel;
import org.jom.Model.Collection.CollectionSingleViewModel;
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

    // Get collection
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int supplier_id = Integer.parseInt(request.getParameter("sId"));
        int id = Integer.parseInt(request.getParameter("id"));

        try {
            CollectionDAO collectionDAO = new CollectionDAO();
            CollectionSingleViewModel collection = collectionDAO.getCollection(id);

            Gson gson = new Gson();
            // Object array to json
            String objectArray = gson.toJson(collection);

            if (collection.getCollection_id() != 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"collection\": " + objectArray + " }");
                System.out.println("Collection sent");
            } else if (collection.getCollection_id() == 0) {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                out.write("{\"collection\": \"No collection\"}");
                System.out.println("No collection");
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

    // delete collection
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int supplier_id = Integer.parseInt(request.getParameter("sId"));
        int collection_id = Integer.parseInt(request.getParameter("id"));

        try {
            CollectionDAO collectionDAO = new CollectionDAO();

            if(collectionDAO.deleteCollection(supplier_id,collection_id)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Delete Collection\"}");
                System.out.println("Delete Collection");
            }else {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                out.write("{\"message\": \"Unable to Delete Collection\"}");
                System.out.println("Collection not deleted");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
