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
import java.io.BufferedReader;
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
        String uid = request.getParameter("uid");

        BufferedReader reader = request.getReader();
        Jsonb jsonb = JsonbBuilder.create();
        Task task = jsonb.fromJson(reader, Task.class);


        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        try {
            Connection connection = cp.getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Task WHERE user_id=? ORDER BY id DESC LIMIT 1");
            pstm.setObject(1,uid);
            ResultSet resultSet = pstm.executeQuery();
            String tid = null;
            if(resultSet.next()){
                System.out.println(resultSet.getString(1));
                tid = resultSet.getString(1);
                int id = Integer.parseInt(tid.substring(1, 4));
                tid = String.format("T%03d", ++id);

                pstm = connection.prepareStatement("INSERT INTO Task VALUES (?,?,?,?,?)");
                pstm.setObject(1,tid);
                pstm.setObject(2,uid);
                pstm.setObject(3,task.getText());
                pstm.setObject(4,task.getPriority());
                pstm.setObject(5,0);

                if(pstm.executeUpdate()>0){
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    PrintWriter writer = response.getWriter();
                    writer.write(tid);
                    writer.close();
                }else{
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }

            }else{
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            connection.close();
        } catch (SQLException throwables) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throwables.printStackTrace();
        }

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
                boolean completed = resultSet.getString(5).equals("1");
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
        String uid = req.getParameter("uid");
        System.out.println(uid);

        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        Connection connection = null;
        try {
            connection = cp.getConnection();
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM TASK WHERE user_id=? && id=?");
            pstm.setObject(1,uid);
            pstm.setObject(2,id);
            if(pstm.executeUpdate()>0){
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else{
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

            connection.close();

        } catch (SQLException throwables) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throwables.printStackTrace();

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = req.getParameter("uid");
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        Jsonb jsonb = JsonbBuilder.create();
        Task task = jsonb.fromJson(req.getReader(), Task.class);

        System.out.println(task);


        try {
            Connection connection = cp.getConnection();
            PreparedStatement pstm = connection.prepareStatement("UPDATE Task SET taskDesc=?, taskPriority=?, completed=? WHERE user_id=? && id=?");
            pstm.setObject(1,task.getText());
            pstm.setObject(2,task.getPriority());
            pstm.setObject(3,task.isCompleted()? 1:0);
            pstm.setObject(4,uid);
            pstm.setObject(5,task.getId());

            if(pstm.executeUpdate()>0){
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else{
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (SQLException throwables) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throwables.printStackTrace();
        }

    }
}
