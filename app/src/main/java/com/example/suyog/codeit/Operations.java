package com.example.suyog.codeit;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonElement;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;

import static java.lang.String.valueOf;

/**
 * Created by Suyog on 11/4/2017.
 */

public class Operations {

    final static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final static String email = mAuth.getCurrentUser().getEmail();
    public static String doOperations(ai.api.model.Result result) {
        String message = result.getFulfillment().getSpeech().trim();
        Connection con = Database.connect();
        if(message.contains("balance")){

            String balance = WalletOperation.getBalance(con,email);
            String amount=null;
            amount=WalletOperation.getBalance(con,email);
            Log.i("codeit:","amount:"+amount);
            if(amount != null) {
                message = "Wallet Balance: " + amount;
            }
            else{
                message="Sorry Unable to fetch Balance";
            }

            ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            return message;
        }
        else if(message.contains("transactions")) {
            String date=null;
            Log.i("codeit: ","history1");
            HashMap<String, JsonElement> resultParameters = result.getParameters();
            for (Map.Entry<String, JsonElement> entry : resultParameters.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if (key.equals("date")) {
                    date = value.toString(); //you will get either date or startdate/enddate format according to that find transactions
                    date = convertToString(date);//and set them in dummy transactions
                    Log.i("codeit:","date: "+date);
                    String data = "";
                    try {
                        ResultSet rs = WalletOperation.transactionByDate(Database.connect(), date);
                        if (rs != null) {
                            while (rs.next()) {
                                data = data + "Transaction id: " + rs.getInt("transact_id") + "\n" + "Sender: " + WalletOperation.getNameById(Integer.parseInt(rs.getString("sender_id")),con) + "\n" + "Receiver: " +
                                        WalletOperation.getNameById(Integer.parseInt(rs.getString("reciever_id")),con) + "\n" + "Amount: " + rs.getInt("amount") + "\n\n";
                            }
                            message=message+"\n"+data;
                        }
                        else {
                            data = "no transaction for " + date;
                            message=message+"\n"+data;
                        }
                    } catch (SQLException sqle) {
                        Log.e("DB error", sqle.toString());
                    }
                }
                else{
                    message="Sorry Unable to fetch Transactions";
                }
                ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                return message;
            }
        }
        else if(message.contains("transaction history")){
            String date=null;
            String data="\n";
            Log.i("codeit: ","history");
            HashMap<String, JsonElement> resultParameters = result.getParameters();
            for (Map.Entry<String, JsonElement> entry : resultParameters.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                Log.i("codeit: ",key);
                if (key.equals("period")) {
                    date = value.toString();
                    date = convertToString(date);
                    String dates[] = date.split("/");
                    String start = dates[0];
                    String end = dates[1];
                    Log.i("codeit:", start + " " + end);
                    try {
                        ResultSet rs = WalletOperation.transactionByRange(Database.connect(), start, end);
                        if (rs != null) {
                            while (rs.next()) {
                                data = data + "Transaction id: " + rs.getInt("transact_id") + "\n" + "Sender: " + WalletOperation.getNameById(Integer.parseInt(rs.getString("sender_id")), con) + "\n" + "Receiver: " +
                                        WalletOperation.getNameById(Integer.parseInt(rs.getString("reciever_id")), con) + "\n" + "Amount: " + rs.getInt("amount") +"\n"+"Date: " + rs.getDate("tdate") + "\n\n";

                            }
                            message = message + "\n" + data;
                        } else {
                            message = "no transaction from " + start + " to " + end;

                        }
                    } catch (SQLException sqle) {
                        Log.e("DB error", sqle.toString());
                    }

                } else {
                    message = "Sorry unable to fetch Transactions";
                }

                ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);

                return message;
                 }
            }





        else if(message.contains("transfer") | message.contains("pay")){

            int newamount=0;
            String name=null,amount=null;
            HashMap<String, JsonElement> resultParameters = result.getParameters();
            for (Map.Entry<String, JsonElement> entry : resultParameters.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if(key.equals("amount")){
                    amount = value.toString();
                    amount=convertToString(amount);//this is the amount that needs to transfer minus this from total


                }
                if(key.equals("name")){
                    name=value.toString();
                    name=convertToString(name);  //name of the user if there are 2 then handle condition and print log only
                }
            }

            if(amount != null && name != null){
                newamount=Integer.parseInt(amount);
                if(WalletOperation.isUserExist(name,con)){
                    if(Integer.parseInt(WalletOperation.getBalance(con,email)) > newamount){
                        if(WalletOperation.transferBalance(con,name,newamount)){
                            message="Transaction Successful.\n"+"Updated Balance: "+WalletOperation.getBalance(con,email);
                        }
                        else{
                            message="Sorry Transaction Unsuccessful!";
                        }
                    }
                    else{
                        message ="Sorry you dont have sufficeint balance";
                    }
                }
                else{
                    message="No Such user";
                }
            }



            ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            return message;


        }
        else if(message.contains("spend")){
            String data="\n";
            String username=null;
            HashMap<String, JsonElement> resultParameters = result.getParameters();
            for (Map.Entry<String, JsonElement> entry : resultParameters.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if (key.equals("username")) {

                    username = value.toString();
                    username = convertToString(username);
                    try {
                        ResultSet rs = WalletOperation.transactionByReceiver(Database.connect(), username);
                        if (rs != null && WalletOperation.isUserExist(username,con)) {
                            while (rs.next()) {
                                data = data + "Transaction id: " + rs.getInt("transact_id") + "\n" + "Sender: " + WalletOperation.getNameById(Integer.parseInt(rs.getString("sender_id")), con) + "\n" + "Receiver: " +
                                        WalletOperation.getNameById(Integer.parseInt(rs.getString("reciever_id")), con) + "\n" + "Amount: " + rs.getInt("amount") + "\n" + "Date: " + rs.getDate("tdate") + "\n\n";

                            }
                            if(data != ""){
                            message = message + "\n" + data;
                            }
                            else{
                            message = message +"\n"+"No Transactions With this User ";
                            }
                        } else {
                            message = "No Such User";

                        }
                    } catch (SQLException sqle) {
                        Log.e("DB error", sqle.toString());
                    }


                } else {
                    message = "Sorry Unable to fetch Transactions";
                }


                ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                return message;

            }
        }
        else if(message.contains("add")){
            String email=null;
            HashMap<String, JsonElement> resultParameters = result.getParameters();
            for (Map.Entry<String, JsonElement> entry : resultParameters.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if(key.equals("email")){

                    email=value.toString();//fname of user
                    email=convertToString(email);
                }


            }

            ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            return message;

        }

        else if(message.contains("credited") | message.contains("deposited")){

            String amtDeposit = null;
            String totalWalletStat=null;//store total + amtDeposit
            HashMap<String, JsonElement> resultParameters = result.getParameters();
            for (Map.Entry<String, JsonElement> entry : resultParameters.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if (key.equals("money")) {

                    amtDeposit = value.toString();//fname of user
                    amtDeposit = convertToString(amtDeposit);//amoutnwant to deposit
                    int oldamt=Integer.parseInt(WalletOperation.getBalance(con,email));
                    int newamt=oldamt+Integer.parseInt(amtDeposit);
                    if (WalletOperation.updateBalance(con, email, newamt)) {
                        message = "Wallet Balance Updated \n New Wallet Balance: " + WalletOperation.getBalance(con, email);
                    } else {
                        message = "Sorry Unable update wallet Balance!!!";
                    }

                }



                ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                return message;
            }
        }
        else{

            ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            return message;

        }

        return null;

    }


    /**
     * this method removes the start and end double quotes
     *
     * @param value
     * @return string
     */

    public static String convertToString(String value){
        value = value.replaceAll("^\"|\"$", "");
        Log.i("codeit:",value);;
        return value;
    }
}
