package com.example.suyog.codeit;
import java.sql.*;
import android.util.*;
/**
 * Created by root on 5/11/17.
 */


public class Database {

    private static final String url = "jdbc:mysql://us-cdbr-iron-east-05.cleardb.net:3306/heroku_5d7c12607dfcfbb";

    private static final String user = "USER_NAME";

    private static final String password = "PASSWORD";



    public static Connection connect(){

        Connection con = null;
        try {

            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url,user,password);

            Log.d("DB connect","Success");


        } catch (Exception e) {
            Log.e("DB Error",e.toString());

        }

        return con;

    }

}
