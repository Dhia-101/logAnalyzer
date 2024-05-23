package com.test.http.resources;

import com.test.db.MySqlConnection;
import com.test.http.responses.MessageResponse;
import com.test.http.responses.MetricResponse;
import com.test.http.utils.ResponseUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.time.LocalDateTime;

@Path("/metrics")
public class MetricsResource {
    private static final Logger LOGGER = Logger.getLogger(MetricsResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDashboardMetric(
) {
        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            connection = MySqlConnection.getConnection();

            String sql = " SELECT logLevel, COUNT(*) AS logLevelCount From logs GROUP BY logLevel";

            st = connection.prepareStatement(sql);

            rs = st.executeQuery();

            final MetricResponse response = new MetricResponse();

            while (rs.next()) {
                MetricResponse.LogLevelMetric metric = new MetricResponse.LogLevelMetric();

                metric.setLogLevel(rs.getString("logLevel"));
                metric.setLogLevelCount(rs.getLong("logLevelCount"));

                response.addLogLevelMetric(metric);

            }

            response.setLastTime(Timestamp.valueOf(LocalDateTime.now()));

            return Response.ok().entity(ResponseUtils.toJson(response)).build();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);

            return Response.status(500)
                    .entity(ResponseUtils.toJson(new MessageResponse(false, "connection error")))
                    .build();
        } finally {
            MySqlConnection.close(rs);
            MySqlConnection.close(st);
            MySqlConnection.close(connection);
        }
    }
}