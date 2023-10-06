package org.jom.Controller.Supplier;

import com.google.gson.Gson;
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

@WebServlet("/estate")
public class EstateServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            EstateModel estate = gson.fromJson(bufferedReader, EstateModel.class);

            // TODO backend validations

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
