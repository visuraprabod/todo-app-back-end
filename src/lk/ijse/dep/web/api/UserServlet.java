package lk.ijse.dep.web.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lk.ijse.dep.web.model.User;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContext;
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
import java.util.regex.Pattern;

@WebServlet(name = "UserServlet", urlPatterns = "/users")
public class UserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = this.getServletContext();
        BasicDataSource cp = (BasicDataSource) servletContext.getAttribute("cp");
        String contentType = req.getContentType();
//        if(contentType.equals("application/json")){
            BufferedReader reader = req.getReader();
            Jsonb jsonb = JsonbBuilder.create();
            User user = jsonb.fromJson(reader, User.class);
//        }

        System.out.println(user.getUsername());
        System.out.println(user.getEmail());
        System.out.println(user.getPassword());
        try{
            Connection connection = cp.getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT id FROM `User` ORDER BY id DESC LIMIT 1");

            ResultSet rst = pstm.executeQuery();
            String id = null;
            if(rst.next()){
               id = rst.getString(1);
            }

            int num = Integer.parseInt(id.substring(1, 4));
            id = String.format("U%03d", ++num);

            pstm = connection.prepareStatement("INSERT INTO `User` VALUES (?,?,?,?)");
            pstm.setString(1,id);
            pstm.setString(2,user.getUsername());
            pstm.setString(3, user.getEmail());
            pstm.setString(4, user.getPassword());
           if(pstm.executeUpdate()>0){
               System.out.println("Update una");
               resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
           };

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        String email = req.getParameter("email");
        String pass = req.getParameter("pass");
        try{
            Connection connection = cp.getConnection();
            PreparedStatement pstm = connection.prepareStatement("select * from `user` where email =? && user.password=?");
            pstm.setObject(1,email);
            pstm.setObject(2,pass);

            ResultSet rst = pstm.executeQuery();
            if(rst.next()){
                String uid = rst.getString(1);
                System.out.println(uid);

                resp.setContentType("text/plain");
                PrintWriter writer = resp.getWriter();
                writer.write(uid);
                writer.close();

            }else{
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
