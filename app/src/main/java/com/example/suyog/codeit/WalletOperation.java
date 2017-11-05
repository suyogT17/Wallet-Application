package com.example.suyog.codeit;
import java.sql.*;
import android.util.*;
/**
 * Created by root on 5/11/17.
 */

public class WalletOperation {

    private final String db = "heroku_5d7c12607dfcfbb";
    public static String getBalance(Connection con,String email){
        PreparedStatement query;
        String amount = null;
        ResultSet rs = null;
        String db = "heroku_5d7c12607dfcfbb";
        try{
            query = con.prepareStatement("Select wallet_balance from "+db+".user where fb_key = '"+email+"'");
            rs = query.executeQuery();
            while(rs.next()){
                amount = rs.getString("wallet_balance");
            }
        }
        catch(SQLException sqle){
            Log.e("Sql Error",sqle.toString());
        }
        return amount;
    }

    public static ResultSet transactionByDate(Connection con,String date){
        PreparedStatement query;
        ResultSet rs = null;
        String db = "heroku_5d7c12607dfcfbb";
        try{
            query = con.prepareStatement("Select * from "+db+".transaction where DATE(tdate) = '"+date+"'");
            Log.i("query",query.toString());
            rs = query.executeQuery();
        }
        catch(SQLException sqle){
            Log.e("Sql Error",sqle.toString());
        }
        return rs;
    }

    public static ResultSet transactionByRange(Connection con,String sdate,String edate){
        PreparedStatement query;
        ResultSet rs = null;
        String db = "heroku_5d7c12607dfcfbb";
        try{
            query = con.prepareStatement("Select * from "+db+".transaction where CAST(tdate as DATE) > "+sdate +" and CAST(tdate as DATE) < "+edate);
            rs = query.executeQuery();
        }
        catch(SQLException sqle){
            Log.e("Sql Error",sqle.toString());
        }

        return rs;
    }
}
