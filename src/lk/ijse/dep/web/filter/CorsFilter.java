package lk.ijse.dep.web.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "corsFilter",urlPatterns = "/*")
public class CorsFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.setHeader("Access-Control-Allow-Origin","http://localhost:4200");
        response.setHeader("Access-Control-Allow-Headers","Content-type");
        response.setHeader("Access-Control-Allow-Methods","GET,PUT,POST,DELETE");
        chain.doFilter(request,response);
    }
}
