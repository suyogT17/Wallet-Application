package com.example.suyog.codeit;
import java.sql.*;
import android.util.*;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by root on 5/11/17.
 */

public class WalletOperation {



    private static final String db = "heroku_5d7c12607dfcfbb";

    public static String getBalance(Connection con,String email) {
        PreparedStatement query;
        String amount = null;
        ResultSet rs = null;

        //String db = "heroku_5d7c12607dfcfbb";
        try {
            query = con.prepareStatement("Select wallet_balance from " + db + ".user where fb_key = ?");
            query.setString(1,email);
            Log.i("codeit: ",query.toString());
            rs=query.executeQuery();
            rs.next();
            Log.i("codeit:",String.valueOf(rs.getInt("wallet_balance")));
            return String.valueOf(rs.getInt("wallet_balance"));

        } catch (SQLException sqle) {
            Log.i("codeit:", "failed");
        }


        return null;
    }


    public static ResultSet transactionByDate(Connection con,String date){
        PreparedStatement query;
        ResultSet rs = null;
        String db = "heroku_5d7c12607dfcfbb";
        String name= getNameByEmail(con);
        if(name != null){
        try{
            query = con.prepareStatement("Select * from "+db+".transaction where DATE(tdate) = ? and " +
                    "sender_id=(select id from "+db+".user where name = ? ) ");
            query.setString(1,date);
            query.setString(2,name);
            Log.i("codeit: ",query.toString());
            rs=query.executeQuery();
            return  rs;
        }
        catch(SQLException sqle){
            Log.e("Sql Error",sqle.toString());
        }

        }
        return  null;
    }




    public static ResultSet transactionByRange(Connection con,String sdate,String edate){
        PreparedStatement query;
        ResultSet rs = null;
        String db = "heroku_5d7c12607dfcfbb";
        String name=getNameByEmail(con);
        if(name != null) {
            try {
                query = con.prepareStatement("Select * from " + db + ".transaction where DATE(tdate) between ? and ? and " +
                        "sender_id=(select id from " + db + ".user where name = ? )");
                query.setString(1, sdate);
                query.setString(2, edate);
                query.setString(3, name);
                Log.i("codeit: ",query.toString());
                rs = query.executeQuery();
                return  rs;
            } catch (SQLException sqle) {
                Log.e("Sql Error", sqle.toString());
            }
        }
        return null;

    }


    public static ResultSet transactionByReceiver(Connection con,String rname){
        PreparedStatement query;
        ResultSet rs = null;
        String db = "heroku_5d7c12607dfcfbb";
        String name=getNameByEmail(con);
        if(name != null && rname != null ) {
            try {
                query = con.prepareStatement("Select * from " + db + ".transaction where sender_id=(select id from " + db + ".user where name = ? ) " +
                        "and reciever_id =(select id from " + db + ".user where name = ? )");

                query.setString(1, name);
                query.setString(2,rname);
                Log.i("codeit: ",query.toString());
                rs = query.executeQuery();
                return  rs;
            } catch (SQLException sqle) {
                Log.e("Sql Error", sqle.toString());
            }
        }
        return null;

    }


    public static boolean updateBalance(Connection con,String email,int amount){


        try {
            PreparedStatement stmt=con.prepareStatement("Update "+db+".user set wallet_balance = ? where fb_key = ?");
            stmt.setInt(1,amount);
            stmt.setString(2,email);
            Log.i("codeit: ",stmt.toString());

            if(stmt.executeUpdate() > 0){
                return true;
            }
        } catch (SQLException e) {
           Log.i("codeit",e.toString());
        }
        return  false;

    }


    public static boolean transferBalance(Connection con,String rname,int amount){
        String sname=getNameByEmail(con);
        int sid=getIdByName(con,sname);
        int rid=getIdByName(con,rname);

        try {
            PreparedStatement stmt=con.prepareStatement("insert into transaction(tdate,sender_id,reciever_id,amount) values(now(),? ,? ,?)");
            stmt.setInt(1,sid);
            stmt.setInt(2,rid);
            stmt.setInt(3,amount);
            Log.i("codeit: ",stmt.toString());

            int res=stmt.executeUpdate();
            if(res == 1){
                FirebaseAuth auth=FirebaseAuth.getInstance();
                String email=auth.getCurrentUser().getEmail();
                Log.i("codeit: ",email);
                int oldamt=Integer.parseInt(WalletOperation.getBalance(con,email));
                int newamt=oldamt-amount;
                updateBalance(con,email,newamt);

                email=getEmailFromName(rname,con);
                Log.i("codeit: ",email);
                oldamt=Integer.parseInt(WalletOperation.getBalance(con,email));
                newamt=oldamt+amount;
                updateBalance(con,email,newamt);

                return true;
            }
        } catch (SQLException e) {
            Log.i("codeit: ",e.toString());
        }
        return true;
    }

    public static String getNameByEmail(Connection conn){
        FirebaseAuth auth=FirebaseAuth.getInstance();
        if(auth != null) {
            String password = auth.getCurrentUser().getEmail();
            try {
                PreparedStatement stmt = conn.prepareStatement("select name from " + db + ".user where fb_key = ? ");
                stmt.setString(1,password);
                Log.i("codeit: ",stmt.toString());

                ResultSet rs= stmt.executeQuery();
                rs.next();
                //Log.i("codeit:",rs.getString("name"));
                return rs.getString("name");
            }
            catch (SQLException sqle){
                Log.i("codeit:",sqle.toString());
            }
        }
        return null;
    }

    public static String getNameById(int id,Connection conn){

        try {
            PreparedStatement stmt = conn.prepareStatement("select name from " + db + ".user where id = ? ");
            stmt.setInt(1, id);
            Log.i("codeit: ",stmt.toString());

            ResultSet rs=stmt.executeQuery();
            rs.next();
            return rs.getString("name");

        }
        catch(SQLException sqle){
            Log.i("codeit: ",sqle.toString());
        }
        return null;
    }

    public static int getIdByName(Connection conn,String name){

        try {
            PreparedStatement stmt = conn.prepareStatement("select id from " + db + ".user where name = ? ");
            stmt.setString(1, name);
            Log.i("codeit: ",stmt.toString());

            ResultSet rs=stmt.executeQuery();
            rs.next();
            return rs.getInt("id");

        }
        catch(SQLException sqle){
            Log.i("codeit: ",sqle.toString());
        }
        return 0;
    }


    public static Boolean isUserExist(String name,Connection conn){

        try {
            PreparedStatement stmt = conn.prepareStatement("select count(*) as num from " + db + ".user where name= ? ");
            stmt.setString(1,name);
            Log.i("codeit: ",stmt.toString());
            ResultSet rs=stmt.executeQuery();
            rs.next();
            int num=rs.getInt("num");
            if(num == 1){
                return true;
            }
        }
        catch(SQLException sqle){
            Log.i("codeit:",sqle.toString());
        }
        return false;
    }

    public static String getEmailFromName(String name,Connection con){

        try {
            ;
            PreparedStatement stmt=con.prepareStatement("select fb_key from "+db+".user where name= ? ");
            stmt.setString(1,name);
            Log.i("codeit: ",stmt.toString());
            ResultSet rs=stmt.executeQuery();
            rs.next();
            String email=rs.getString("fb_key");
            Log.i("codeit: ",email);
            if(email != null){
                return  email;
            }
        } catch (SQLException e) {
            Log.i("codeit: ",e.toString());
        }
        return null;
    }


}
