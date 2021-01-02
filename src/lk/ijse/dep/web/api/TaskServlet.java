package lk.ijse.dep.web.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lk.ijse.dep.web.model.Task;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "TaskServlet", urlPatterns = "/tasks")
public class TaskServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        String uid = request.getParameter("uid");

        List<Task> taskList = new ArrayList<>();
        try {
            Connection connection = cp.getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Task WHERE user_id = ?");
            pstm.setObject(1, uid);
            ResultSet resultSet = pstm.executeQuery();
            while (resultSet.next()) {
                boolean completed = resultSet.getString(5).equals("0");
                taskList.add(new Task(resultSet.getString(1), resultSet.getString(3),completed, resultSet.getInt(4)));
            }

            Jsonb jsonb = JsonbBuilder.create();
            String s = jsonb.toJson(taskList);
            response.setStatus(200);
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.close();
            connection.close();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        System.out.println(id);

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
