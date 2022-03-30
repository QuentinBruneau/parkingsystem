package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DataBaseConfig {

    private static final Logger logger = LogManager.getLogger("DataBaseConfig");

    public Connection getConnection() throws ClassNotFoundException, SQLException, IOException {

        logger.info("Create DB connection");
        InputStream inputStream = getClass().getResourceAsStream("/app.properties");
        Properties appProps = new Properties();
        appProps.load(inputStream);
        inputStream.close();
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(appProps.getProperty("db.url"),appProps.getProperty("db.user"),appProps.getProperty("db.pass"));
    }

    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
                logger.info("Closing DB connection");
            } catch (Exception e) {
                logger.error("Error while closing connection",e);
            }
        }
    }

    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (Exception e) {
                logger.error("Error while closing prepared statement",e);
            }
        }
    }

    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (Exception e) {
                logger.error("Error while closing result set",e);
            }
        }
    }
}
