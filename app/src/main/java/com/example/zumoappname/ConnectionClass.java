package com.example.zumoappname;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.InputStream;


public class ConnectionClass extends Activity {


    // This is default if you are using JTDS driver.
    static String classs = "net.sourceforge.jtds.jdbc.Driver";
    //"com.microsoft.sqlserver.jdbc.SQLServerDriver"
    static String db = "citymonitor2";
    static String un = "citymonitor_dbadmin@citymonitoreu";
    static String password = "MonitCityMenoWatt1296";
    static String server = "citymonitoreu.database.windows.net:1433";

    String z=" ";


    public TextView textviewSQL;



    @Override
    protected  void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_conclasse);

        textviewSQL=findViewById(R.id.textViewSQL);

        uploadOracolo();

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
    public void uploadOracolo(){

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
                InputStream fIn=  this.getResources().getAssets().open("oracolo.txt");

                boolean result = con.storeFile("/ORACOLO_APP/oracolo_app.txt", fIn);
                fIn.close();
                if (result) Log.v("upload result", "succeeded");
                textviewSQL.setText("OK");
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
