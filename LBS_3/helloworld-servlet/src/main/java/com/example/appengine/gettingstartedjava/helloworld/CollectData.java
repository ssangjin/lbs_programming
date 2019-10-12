package com.example.appengine.gettingstartedjava.helloworld;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@SuppressWarnings("serial")
@WebServlet(name = "collectdata", value = "/collectdata")
public class CollectData extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("euc-kr");
        PrintWriter out = resp.getWriter();
        out.println("Hello, world - 데이터 수집");
    }
}
