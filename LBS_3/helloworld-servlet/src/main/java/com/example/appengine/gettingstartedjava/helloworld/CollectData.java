package com.example.appengine.gettingstartedjava.helloworld;

import com.example.appengine.gettingstartedjava.helloworld.daos.DatastoreDao;
import com.example.appengine.gettingstartedjava.helloworld.daos.LocationDataDao;
import com.example.appengine.gettingstartedjava.helloworld.data.LocationData;

import javax.servlet.ServletException;
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
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setCharacterEncoding("euc-kr");
        PrintWriter out = resp.getWriter();

        try {
            // [START LocationDataBuilder]
            LocationData locationData = new LocationData.Builder()
                    .userId(req.getParameter(LocationData.USER_ID))   // form parameter
                    .latitude(Double.parseDouble(req.getParameter(LocationData.LATITUDE)))
                    .longitude(Double.parseDouble(req.getParameter(LocationData.LONGITUDE)))
                    .provider(req.getParameter(LocationData.PROVIDER))
                    .accuracy(Float.parseFloat(req.getParameter(LocationData.ACCURACY)))
                    .build();
            // [END LocationDataBuilder]

            LocationDataDao dao = dao = new DatastoreDao();
            try {
                Long id = dao.createLocationData(locationData);
                out.println("Data created. ID = " + id.toString() + " LocationData:" + locationData.toString());
            } catch (Exception e) {
                throw new ServletException("Error creating LocationData", e);
            }

        } catch (Exception e) {
            out.println(e.toString());
        }

    }
}
