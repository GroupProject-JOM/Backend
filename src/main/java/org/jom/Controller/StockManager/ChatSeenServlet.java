package org.jom.Controller.StockManager;

import com.google.gson.Gson;
import org.jom.Dao.Chat.ChatDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.ChatModel;
import org.jom.Model.UserModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/seen")
public class ChatSeenServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int user_id = Integer.parseInt(request.getParameter("user"));
        int sender = Integer.parseInt(request.getParameter("id"));

        try {
            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("supplier") || user.getRole().equals("stock-manager")) {

                    if (userDAO.updateSeen(1,sender)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"messages\": \"seen\"}");
                        System.out.println("Send seen");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"messages\": \"No seen\"}");
                        System.out.println("No seen");
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
