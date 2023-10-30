package org.jom.Controller;

import com.google.gson.Gson;
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
}
