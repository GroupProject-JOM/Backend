package org.jom.Controller.Supplier.Collection;

import com.google.gson.Gson;
import org.jom.Dao.Supplier.AccountDAO;
import org.jom.Dao.Supplier.Collection.CollectionDAO;
import org.jom.Dao.Supplier.Collection.PickupDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Dao.Supplier.Collection.YardDAO;
import org.jom.Model.Collection.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

            if (collection.getSupplier_id() == 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                System.out.println("UnAuthorized");
                return;
            }

            // TODO backend validations and user exists


            collection.addCollection();

            if (collection.getId() != 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Supply request added successfully\",\"id\":\"" + collection.getId() + "\"}");
                System.out.println("Supply request added successfully");
            } else {
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
//    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("application/json");
//        PrintWriter out = response.getWriter();
//
//        int supplier_id = Integer.parseInt(request.getParameter("sId"));
//        int id = Integer.parseInt(request.getParameter("id"));
//
//        try {
//            CollectionDAO collectionDAO = new CollectionDAO();
//            CollectionSingleViewModel collection = collectionDAO.getCollection(id, supplier_id);
//
//            Gson gson = new Gson();
//            // Object array to json
//            String objectArray = gson.toJson(collection);
//
//            if (collection.getCollection_id() != 0) {
//                response.setStatus(HttpServletResponse.SC_OK);
//                out.write("{\"collection\": " + objectArray + " }");
//                System.out.println("Collection sent");
//            } else {
//                response.setStatus(HttpServletResponse.SC_ACCEPTED);
//                out.write("{\"collection\": \"No collection\"}");
//                System.out.println("No collection");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        } finally {
//            out.close();
//        }
//    }

    // delete collection
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int supplier_id = Integer.parseInt(request.getParameter("sId"));
        int collection_id = Integer.parseInt(request.getParameter("id"));

        try {
            CollectionDAO collectionDAO = new CollectionDAO();

            Date currentDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);

            calendar.add(Calendar.DAY_OF_MONTH, -7);
            Date day_before_week = calendar.getTime();

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date next_day = calendar.getTime();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String min_date = dateFormat.format(day_before_week);
            String max_date = dateFormat.format(next_day);

            if (collectionDAO.deleteCollection(supplier_id, collection_id,min_date,max_date)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Delete Collection\"}");
                System.out.println("Delete Collection");
            } else {
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

    // Edit collection
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            CollectionModel collection = gson.fromJson(bufferedReader, CollectionModel.class);

            if (collection.getSupplier_id() == 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                System.out.println("UnAuthorized");
                return;
            }

            // TODO backend validations and user exists

            String objectArray = gson.toJson(collection);
            System.out.println(objectArray);

            CollectionDAO collectionDAO = new CollectionDAO();
            PickupDAO pickupDAO = new PickupDAO();
            YardDAO yardDAO = new YardDAO();

            if (collectionDAO.updateCollection(collection)) {
                if (collection.getSupply_method().equals("pickup")) {
                    PickupModel pickup = pickupDAO.getPickup(collection.getId(), collection.getSupplier_id());

                    pickup.setDate(collection.getDate());
                    pickup.setTime(collection.getTime());
                    pickup.setEstate_id(collection.getEstate());
                    pickup.setAccount_id(collection.getAccount());
                    pickup.setCollection_id(collection.getId());
                    pickup.setSupplier_id(collection.getSupplier_id());

                    if (pickup.getId() != 0) {
                        if (pickupDAO.updateCollection(pickup)) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.write("{\"message\": \"Supply request edited successfully\"}");
                            System.out.println("Supply request edited successfully");
                        } else {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.write("{\"message\": \"Supply request is not edited\"}");
                            System.out.println("Supply request is not edited");
                        }
                    } else {
                        pickup.addPickup();

                        if (pickup.getId() != 0 && yardDAO.deleteYard(collection.getId(), collection.getSupplier_id())) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.write("{\"message\": \"Supply request edited successfully\"}");
                            System.out.println("Supply request edited successfully");
                        } else {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.write("{\"message\": \"Supply request is not edited\"}");
                            System.out.println("Supply request is not edited");
                        }
                    }
                } else {
                    YardModel yard = yardDAO.getYard(collection.getId(), collection.getSupplier_id());

                    yard.setDate(collection.getDate());
                    yard.setTime(collection.getTime());
                    yard.setAccount_id(collection.getAccount());
                    yard.setCollection_id(collection.getId());
                    yard.setSupplier_id(collection.getSupplier_id());

                    if (yard.getId() != 0) {
                        if (yardDAO.updateCollection(yard)) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.write("{\"message\": \"Supply request edited successfully\"}");
                            System.out.println("Supply request edited successfully");
                        } else {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.write("{\"message\": \"Supply request is not edited\"}");
                            System.out.println("Supply request is not edited");
                        }
                    } else {
                        yard.addYard();

                        if (yard.getId() != 0 && pickupDAO.deletePickup(collection.getId(), collection.getSupplier_id())) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.write("{\"message\": \"Supply request edited successfully\"}");
                            System.out.println("Supply request edited successfully");
                        } else {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.write("{\"message\": \"Supply request is not edited\"}");
                            System.out.println("Supply request is not edited");
                        }
                    }
                }

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"Supply request is not edited\"}");
                System.out.println("Supply request is not edited");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
