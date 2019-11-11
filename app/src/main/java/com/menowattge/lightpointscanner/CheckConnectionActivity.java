package com.menowattge.lightpointscanner;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;


public class CheckConnectionActivity extends Activity {







    @Override
    protected  void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);


        writeToFile(this);
        uploadOracolo();
        finish();


    }



    public  void writeToFile( Context context) {

        String data = getAndPrintData();
        System.out.println("data : "+data);

        File file = new File(CheckConnectionActivity.this.getFilesDir(), "data");
        if (!file.exists()) {
            file.mkdir();
        }

        try {
            File gpxfile = new File(file, "info_palo");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
           // Toast.makeText(this, "Saved your text", Toast.LENGTH_LONG).show();
        } catch (Exception e) { }
    }







    public static String getAndPrintData(){

        String nome= ToDoActivity.name;
        String ID = ToDoActivity.ID;
        String citta=ToDoActivity.qrCitta;
        Double Lat=ToDoActivity.qrLatitudine;
        Double Lon=ToDoActivity.qrLongitudine;
        String addr=ToDoActivity.qrAddress;
        String corrente=ToDoActivity.valoreCorrente;


        String data = nome+" "+ID+" "+citta+" "+Lat+" "+Lon+" "+addr+" "+corrente;


        return  data;

    }








    /*
    public String ReadFromfile(String fileName, Context context) {
        StringBuilder ReturnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets().open(fileName);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                ReturnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return ReturnString.toString();
    }
*/

    /**
     * upload nell'ftp un file di testo contenente un valore booleano
     */
    public  void uploadOracolo(){

        String yourFilePath = CheckConnectionActivity.this.getFilesDir() + "/data/info_palo";
        File yourFile = new File( yourFilePath );
        FileInputStream fileInputStream=null ;

        try {
             fileInputStream = new FileInputStream(yourFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FTPClient con ;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try
        {
            con = new FTPClient();
            con.connect("94.177.203.9");

            if (con.login("metering", "m3t3r1ng_01"))
            {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE);
                //InputStream fIn=  this.getResources().getAssets().open("oracolo.txt");

                String nome= ToDoActivity.name;
                String citta=ToDoActivity.qrCitta;

                boolean result = con.storeFile("/ORACOLO_APP/"+nome+"_"+citta+".txt", fileInputStream);

               // fIn.close();
                if (result) Log.v("upload result", "succeeded");
                con.logout();
                con.disconnect();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }





    }




/*
    public void ConnectToDatabase(){
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Connection DbConn;
            String ConnURL;
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + server + ";" + "database=" + db + ";user=" + un + ";password=" + password + ";"+"encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            DbConn = DriverManager.getConnection(ConnURL);
            Log.w("Connection","open");
            Statement stmt = DbConn.createStatement();
            ResultSet reset = stmt.executeQuery("select DeviceAddress from citymonitor2.dbo.Devices");
            textviewSQL.setText(reset.getString(1));
            DbConn.close();
        } catch (Exception e)
        {
            Log.w("Error connection","" + e.getMessage());
        }
    }
*/


}
