package org.jom.Controller.ProductionManager;

import com.google.gson.Gson;
import org.jom.Dao.OutletDAO;
import org.jom.Dao.ProductionDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.OutletModel;
import org.jom.Model.ProductionModel;
import org.jom.Model.UserModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/production")
public class ProductionServlet extends HttpServlet {
    // create production request
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            ProductionModel productionModel = gson.fromJson(bufferedReader, ProductionModel.class);

            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(productionModel.getUser());

            if (user.getId() != 0) {
                if (user.getRole().equals("production-manager")) {

                    ProductionDAO productionDAO = new ProductionDAO();

                    if (productionDAO.createProductionRequest(productionModel) != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"production request added successfully\"}");
                        System.out.println("production request added successfully");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"production request is not added\"}");
                        System.out.println("production request is not added");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write("{\"message\": \"Invalid User\"}");
                    System.out.println("Invalid User");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"Invalid User\"}");
                System.out.println("Invalid User");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }

    // Get production request
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int production_id = Integer.parseInt(request.getParameter("id"));
        int user_id = Integer.parseInt(request.getParameter("user"));

        try {

            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("stock-manager") || user.getRole().equals("production-manager")) {

                    ProductionDAO productionDAO = new ProductionDAO();
                    ProductionModel productionModel = productionDAO.getProductionRequest(production_id);

                    if (productionModel.getId() != 0) {
                        Gson gson = new Gson();
                        String object = gson.toJson(productionModel);

                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"request\": " + object + "}");
                        System.out.println("Send production request");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"request\": \"No production request\"}");
                        System.out.println("No production request");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write("{\"message\": \"Invalid User\"}");
                    System.out.println("Invalid User");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"Invalid User\"}");
                System.out.println("Invalid User");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }

    //update production request
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            ProductionModel productionModel = gson.fromJson(bufferedReader, ProductionModel.class);

            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(productionModel.getUser());

            if (user.getId() != 0) {
                if (user.getRole().equals("production-manager")) {

                    ProductionDAO productionDAO = new ProductionDAO();
                    if (productionDAO.updateProductionRequest(productionModel)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Production request Updated successfully\"}");
                        System.out.println("Production request Update successfully");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"Production request is not updated\"}");
                        System.out.println("Production request is not Updated");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write("{\"message\": \"Invalid User\"}");
                    System.out.println("Invalid User");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"Invalid User\"}");
                System.out.println("Invalid User");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }

    // delete production request
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int production_id = Integer.parseInt(request.getParameter("id"));
        int user_id = Integer.parseInt(request.getParameter("user"));

        try {
            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("production-manager")) {

                    ProductionDAO productionDAO = new ProductionDAO();

                    if (productionDAO.deleteProductionRequest(production_id)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Delete production request\"}");
                        System.out.println("Delete production request");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"Unable to Delete production request\"}");
                        System.out.println("production request not deleted");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write("{\"message\": \"Invalid User\"}");
                    System.out.println("Invalid User");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"Invalid User\"}");
                System.out.println("Invalid User");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }

}
