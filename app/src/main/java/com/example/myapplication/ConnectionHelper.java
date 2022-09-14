package com.example.myapplication;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;


public class ConnectionHelper
{

    String  userName, userPassword, ip, port, database;

    public Connection connectionClass()
    {
        ip = "ngknn.ru";
        database = "Employees_bd";
        userPassword = "12357";
        userName = "31П";
        port = "1433";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection connection = null;
        String connectionURl = null;

        try {

            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURl = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + "databasename=" + database + ";user=" + userName + ";password=" + userPassword + ";";
            connection = DriverManager.getConnection(connectionURl);
        }
        catch (Exception ex)
        {
            Log.e("Error", ex.getMessage());
        }
        return  connection;
    }

}
