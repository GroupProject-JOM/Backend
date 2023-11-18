package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Dao.Chat.ChatDAO;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.ChatModel;
import org.jom.Model.EmployeeModel;
import org.jom.Model.UserModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
    // Load chat
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int user_id = Integer.parseInt(request.getParameter("user"));

        try {
            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("supplier")) {

                    ChatDAO chatDAO = new ChatDAO();
                    List<ChatModel> messages = chatDAO.loadChat(user_id,user_id);

                    Gson gson = new Gson();
                    // Object array to json
                    String object = gson.toJson(messages);

                    if (messages.size() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"messages\": " + object + "}");
                        System.out.println("Send Messages");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"messages\": \"No Messages\"}");
                        System.out.println("No Messages");
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
