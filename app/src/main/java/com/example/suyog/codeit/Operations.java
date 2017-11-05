package com.example.suyog.codeit;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonElement;

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
        if(message.contains("balance")){
            Connection con = Database.connect();
            String balance = WalletOperation.getBalance(con,email);
            String amount=null;
            HashMap<String, JsonElement> resultParameters = result.getParameters();
            for (Map.Entry<String, JsonElement> entry : resultParameters.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if(key.equals("amount")){
                    amount = value.toString();
                }

            }

            //message=message.replace(amount,balance);//get the balance into total from db
            message = message+balance;
            ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            return message;
        }
        else if(message.contains("transaction")) {
            String date=null;

            HashMap<String, JsonElement> resultParameters = result.getParameters();
            for (Map.Entry<String, JsonElement> entry : resultParameters.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if (key.equals("date")) {
                    date=value.toString(); //you will get either date or startdate/enddate format according to that find transactions
                    date=convertToString(date);//and set them in dummy transactions
                }
                String data="";
                try {
                    ResultSet rs = WalletOperation.transactionByDate(Database.connect(), date);
                    while (rs.next()) {
                        data = data + rs.getString("sender_id") + " " + rs.getString("reciever_id") + "\n";
                    }
                }
                catch(SQLException sqle){
                    Log.e("DB error",sqle.toString());
                }
                ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                message = message + "\n" + data;
                return message;
            }
        }
        else if(message.contains("transaction history")){
            String date=null;
            String data=null;

            HashMap<String, JsonElement> resultParameters = result.getParameters();
            for (Map.Entry<String, JsonElement> entry : resultParameters.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if (key.equals("period")) {
                    date=value.toString(); //you will get either date or startdate/enddate format according to that find transactions
                    date=convertToString(date);
                    String dates[]=date.split("/"); //and set them in dummy transactions
                    String start=dates[0];//start date
                    String end =dates[1];//end date
                    Log.i("codeit:",start+" "+end);
                    data=""+start+" "+end;//for testing
                }

                ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                message = message + "\n" + data;
                return message;
            }




        }
        else if(message.contains("transfer") | message.contains("pay")){

            int total=5000;
            String amount=null;
            HashMap<String, JsonElement> resultParameters = result.getParameters();
            for (Map.Entry<String, JsonElement> entry : resultParameters.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if(key.equals("amount")){
                    amount = value.toString();
                    int newamount=Integer.parseInt(convertToString(amount));//this is the amount that needs to transfer minus this from total
                    total = total - newamount;
                    message=message+"\n Wallet Balance: "+total;

                }
                if(key.equals("name")){
                    String name=value.toString();
                    convertToString(name);  //name of the user if there are 2 then handle condition and print log only
                }

            }


            ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            return message;


        }
        else if(message.contains("spend")){
            String dummyTransactions = "khdskdskjdsk\nksjkdjd\nksjdkdjsddksj\nksjddsjdk";
            String username=null;
            HashMap<String, JsonElement> resultParameters = result.getParameters();
            for (Map.Entry<String, JsonElement> entry : resultParameters.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if(key.equals("username")){

                  username=value.toString(); // you have to calculate amount spend on this username set that in dummy transaction

                }

            }

            ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            return message+"\n"+dummyTransactions;

        }
        else if(message.contains("add")){
            String fname,lname;
            HashMap<String, JsonElement> resultParameters = result.getParameters();
            for (Map.Entry<String, JsonElement> entry : resultParameters.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if(key.equals("firstname")){

                    fname=value.toString();//fname of user

                }
                if(key.equals("lname")){
                    lname=value.toString();//lname of user
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
                if(key.equals("money")){

                    amtDeposit=value.toString();//fname of user
                    convertToString(amtDeposit);//amoutnwant to deposit
                }

            }
            message=message+totalWalletStat;
            ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            return message;
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
