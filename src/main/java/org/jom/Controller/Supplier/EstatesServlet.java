package org.jom.Controller.Supplier;

import com.google.gson.Gson;
import org.jom.Auth.JwtUtils;
import org.jom.Dao.Supplier.EstateDAO;
import org.jom.Model.EstateModel;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/estates")
public class EstatesServlet extends HttpServlet {

    // Get all estates
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get all cookies from the request
        Cookie[] cookies = request.getCookies();
        JwtUtils jwtUtils = new JwtUtils();

        if (!jwtUtils.CheckJWT(cookies)) {
            if (jwtUtils.CheckRefresh(cookies))
                response.addCookie(jwtUtils.getNewJWT(cookies));
            else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                return;
            }
        }

        // get auth payload data
        JSONObject jsonObject = jwtUtils.getAuthPayload();
        int supplier_id = (int) jsonObject.get("sId");

        try {
            EstateDAO estateDAO = new EstateDAO();
            List<EstateModel> estates = estateDAO.getAll(supplier_id);

            Gson gson = new Gson();
            // Object array to json
            String objectArray = gson.toJson(estates);

            if(estates.size() != 0){
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"size\": "+ estates.size() +",\"list\":"+ objectArray+"}");
                System.out.println("View all Estates");
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
