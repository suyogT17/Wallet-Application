package com.example.suyog.codeit;

import android.util.Log;

import java.sql.*;

/**
 * Created by root on 5/11/17.
 */

public class AddUser {
    public static boolean addUser(Connection con , String name,String email,String contact,String location ,String bank, String acc){
        PreparedStatement preparedStatement = null;
        try{
            java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String currentTime = sdf.format(dt);
        preparedStatement = con.prepareStatement("insert into  heroku_5d7c12607dfcfbb.user values (default, ?, ?, ?, ? , ?, ?, ?, ?, ?)");

        preparedStatement.setString(1, email);
        preparedStatement.setString(2, name);
        preparedStatement.setString(3, location);
        preparedStatement.setString(4, currentTime);
        preparedStatement.setString(5, contact);
        preparedStatement.setInt(6,1000);
        preparedStatement.setString(7,currentTime);
        preparedStatement.setString(8,bank);
        preparedStatement.setString(9,acc);
        preparedStatement.executeUpdate();

    } catch (Exception e) {
        Log.e("Register","failed");
        return false;

    }
    return true;
    }
}
