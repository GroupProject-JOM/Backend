package org.jom.Controller.StockManager;

import com.google.gson.Gson;
import org.jom.Dao.Chat.ChatDAO;
import org.jom.Dao.UserDAO;
import org.jom.Dao.YardDAO;
import org.jom.Model.ChatModel;
import org.jom.Model.OutletModel;
import org.jom.Model.UserModel;
import org.jom.Model.YardModel;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/yards")
public class YardsServlet extends HttpServlet {
    // Load yard data
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int user_id = Integer.parseInt(request.getParameter("user"));

        try {
            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("production-manager") || user.getRole().equals("stock-manager")) {

                    YardDAO yardDAO = new YardDAO();
                    List<YardModel> yard1 = yardDAO.getYards("yard1");
                    List<YardModel> yard2 = yardDAO.getYards("yard2");
                    List<YardModel> yard3 = yardDAO.getYards("yard3");

                    Gson gson = new Gson();
                    String yard1_data = gson.toJson(yard1);
                    String yard2_data = gson.toJson(yard2);
                    String yard3_data = gson.toJson(yard3);

                    if (yard1.size() != 0 && yard2.size() != 0 && yard3.size() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"yard1\": " + yard1_data + ",\"yard2\": " + yard2_data + ",\"yard3\": " + yard3_data + "}");
                        System.out.println("Send yards data");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"messages\": \"Something went wrong on yards\"}");
                        System.out.println("Something went wrong on yards");
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

    // Load block data and yard data
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            StringBuilder requestBody = new StringBuilder();

            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            JSONObject jsonObject = new JSONObject(requestBody.toString());
            int yard_id = jsonObject.getInt("yard");
            int block_id = jsonObject.getInt("id");
            int user_id = jsonObject.getInt("user");

            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("production-manager") || user.getRole().equals("stock-manager")) {

                    YardDAO yardDAO = new YardDAO();
                    List<YardModel> yard = yardDAO.getYards("yard" + yard_id);
                    YardModel block = yardDAO.getBlockData("yard" + yard_id, block_id);

                    Gson gson = new Gson();
                    String yard_data = gson.toJson(yard);
                    String block_data = gson.toJson(block);

                    if (yard.size() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"yard\": " + yard_data + ",\"block\": " + block_data + "}");
                        System.out.println("Send yard and block");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"messages\": \"Something went wrong on yards\"}");
                        System.out.println("Something went wrong on yards and block");
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
