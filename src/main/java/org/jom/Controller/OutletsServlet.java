package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Dao.OutletDAO;
import org.jom.Dao.Supplier.AccountDAO;
import org.jom.Dao.Supplier.EstateDAO;
import org.jom.Model.AccountModel;
import org.jom.Model.EstateModel;
import org.jom.Model.OutletModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/outlets")
public class OutletsServlet extends HttpServlet {
    // Get all outlets
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            OutletDAO outletDAO = new OutletDAO();
            List<OutletModel> outlets = outletDAO.getAll();

            Gson gson = new Gson();
            // Object array to json
            String objectArray = gson.toJson(outlets);

            if(outlets.size() != 0){
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"size\": "+ outlets.size() +",\"list\":"+ objectArray+"}");
                System.out.println("View all Outlets");
            }else if(outlets.size() == 0){
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                out.write("{\"size\": \"0\"}");
                System.out.println("No Outlet");
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
