package com.android.gpstest;

/**
 * Inserisce o aggiorna i dati del deivice nel portale
 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;


public class SendDataActivity extends Activity {

    /**
     * Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Table used to access data from the mobile app backend.
     */

    private MobileServiceTable<DevicesLightPointsTemp> mDevicesLightPointsTemp;


    /**
     * EditText containing the "New To Do" text
     */
    public static TextView mTextNewToDo;

    /**
     * Progress spinner to use for table operations
     */
    private ProgressBar mProgressBar;
    private android.widget.Button button;
    private android.widget.Button buttonAgain;
    private android.widget.Button buttonExit;
    private ProgressDialog pd;

    public static String name;
    public static String qrCitta;
    public static Double qrLatitudine;
    public static Double qrLongitudine;
    public static String qrAddress;
    public static String valoreCorrente;
    public        String conn_string;
    public        String key="";

    // API login
    String username="tecnico@citymonitor.it";
    String password="tecnico";

    //FTP server login
    String server ="94.177.203.9";
    int portNumber = 21;
    String ftpUser = "metering";
    String ftpPwd = "m3t3r1ng_01";
    String fileName = "/Facere/rluDB.db";

    // per creare il JSON
    public  static String  id ;
    private String  Nome_PL ;
    private String  TipoLuce = "LED";
    private boolean Ripetitore = false;
    private String  Note ="";
    private List<String> chiaviCrittografia   = new ArrayList<>();
    private    String      id_comune="";
    private String      indirizzo ;
    private List<Post.CoordinateGps> coordinateGps = new ArrayList<>();
    private Post.CoordinateGps coordinate = new Post.CoordinateGps();
    private String  TipoApparecchiatura="";
    private String  Marca="";
    private String  Modello="Meridio";
    private String  InfoQuadroElettrico="";
    private String  Palo="";
    private int     AltezzaPaloMm =0;
    private boolean Portella =false;
    private boolean Pozzetto =false;
    private boolean Terra=false ;
    private String  TecnologiaLampada = "LED";
    private double  PotenzaLampadaWatt ;
    private String  Alimentatore="" ;
    private String  LineaAlimentazione="" ;
    private boolean Telecontrollo = true;

    //ftp db
    File db_saved ;



    /**
     * Initializes the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        checkConnection();

        // total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        mTextNewToDo = findViewById(R.id.textNewToDo);
        button = findViewById(R.id.buttonAddToDo);
        pd = new ProgressDialog(new ContextThemeWrapper(SendDataActivity.this,R.style.ProgressDialogCustom));

        getQrCodeData();

        // creo un file .db nella cartella dell'app nel caso in cui dovesse servirmi per il download effttivo dall' FTP
        try {
            db_saved = new File(this.getExternalFilesDir(null), "rluDB.db");
            if (!db_saved.exists())
                db_saved.createNewFile();
            Log.d("DB_DB","creato file db");
        }catch (IOException e ){
            e.printStackTrace();
        }



        // -------------------------------------------------------------------------------------------------------------------------

        // ----------- Definisco il link dove risiede la mia app su Azure poi creo l'istanza della tabella del DB ------------------

        // -------------------------------------------------------------------------------------------------------------------------

        try {
            // Create the client instance, using the provided mobile app URL.
            mClient = new MobileServiceClient(
                    "https://menowattgeqrcodescanner.azurewebsites.net", // fondamentale
                    this).withFilter(new ProgressFilter());

            // Extend timeout from default of 10s to 120s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(120, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)
                            .build();

                    return client;
                }
            });

            // Get the remote table instance to use.
            mDevicesLightPointsTemp = mClient.getTable(DevicesLightPointsTemp.class);

            //Init local storage
            initLocalStore().get();


        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("C'è un problema con il Mobile Service. Controlla l'URL"), "Error");
        } catch (Exception e){

        }
    }


    protected  void onResume(){
        super.onResume();

        //total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


// -------------------------------------------------------------------------------------------------------------------------

    /**
     * Initializes the activity menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Select an option from the menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            refreshItemsFromTable();
        }

        return true;
    }


    /**
     * Controlla la connessione internet
     */
    public void checkConnection(){
        final Thread timeout = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            ConnectivityManager mgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo netInfo = mgr.getActiveNetworkInfo();
                            boolean isConnected = netInfo != null &&
                                    netInfo.isConnectedOrConnecting();

                            if (isConnected ) {

                            }
                            else {
                                //No internet
                                Toast.makeText(getApplicationContext(),"NO INTERNET-IMPOSSIBILE PROSEGUIRE-\nCONNETTERSI E RIAVVIARE L'APP".toUpperCase(),Toast.LENGTH_LONG).show();
                                button.setClickable(false);
                            }
                        }
                    });
                    sleep(4000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timeout.start();
    }

    /**
     * Mark an item as completed
     *
     * @param item
     *            The item to mark
     */
    public void checkItem(final DevicesLightPointsTemp item) {   // -L
        if (mClient == null) {
            return;
        }
        // Set the item as completed and update it in the table
        item.setComplete(true);
    }


    public void getQrCodeData(){

        //prelevo i dati della scansione del qrcode
        String qrCodeData = getIntent().getStringExtra("qrCode");
        // prelevo le altre info che ho trasferito tra le activity
        String qrAddress = getIntent().getStringExtra("qrIndirizzo");
        Double qrLatitudine = getIntent().getDoubleExtra("qrLatitudine",0);
        Double qrLongitudine = getIntent().getDoubleExtra("qrLongitudine",0);
        String valoreCorrente = getIntent().getStringExtra("valore_corrente");
        //Double valoreWatt = Double.parseDouble(valoreCorrente)*0.36;
        //Integer valoreWatt_ = valoreWatt.intValue();
        String name = getIntent().getStringExtra("name_").trim();

        // mostro a video i  valori soprastanti usando la formattazione HTML
        mTextNewToDo.setText(Html.fromHtml("<br />"+"<font color=#4f9e33>" + "<b>"+"ID : "+ "</b>"+"</font>"+"<font color=#656d66>"+qrCodeData+"</font>"+"<br />"+"<br />"));
        mTextNewToDo.append(Html.fromHtml("<font color=#4f9e33>" +"<b>"+"Codice : "+"</b>"+"</font>"+"<font color=#656d66>"+name+"</font>"+"<br />"+"<br />"
                                                +"<font color=#4f9e33>" +"<b>"+"Indirizzo : "+"</b>"+"</font>"+"<font color=#656d66>"+qrAddress+"</font>"+"<br />"+"<br />"
                                                +"<font color=#4f9e33>" +"<b>"+"Latitudine : "+"</b>"+"</font>"+"<font color=#656d66>"+qrLatitudine+"</font>"+"<br />"+"<br />"
                                                +"<font color=#4f9e33>" +"<b>"+"Longitudine : "+"</b>"+"</font>"+"<font color=#656d66>"+qrLongitudine+"</font>"+"<br />"+"<br />"
                                                +"<font color=#4f9e33>" +"<b>"+"Corrente : "+"</b>"+"</font>"+"<font color=#656d66>"+valoreCorrente+" W"+"</font>"+"<br />"));

    }


    /**
     *  Mostra a video un Dialog
     * @param title titolo del messaggio
     * @param message messaggio
     */
    public void createDialog(String title, String message){

        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(SendDataActivity.this,R.style.AlertDialogCustom))
                .setIcon(android.R.drawable.checkbox_on_background)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Scan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        backToScan();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        quit();
                    }
                })
                .show();
        alertDialog.setCanceledOnTouchOutside(false);
    }


    /**
     * Effettua il login al portale
     * @param retrofit
     */
    public void login(Retrofit retrofit){
        //login
        JsonApi login = retrofit.create(JsonApi.class);
        Call<ResponseBody> call_login = login.loginWithCredentials(new LoginCredentials(username, password));

        call_login.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {

                        String rc = String.valueOf(response.code());
                        Log.d("http rc_login : ",rc);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // handle error
                Log.d("http error : ",t.getMessage());
            }
        });

    }


    /**
     * Costruisce il JSON per INSERIRE i dati nel portale
     * @param retrofit istanza della libreria per le chiamate API
     * @param token generato a partire da user e pass
     * @param id_comune id del comune nel quale inserire il punto luce
     */
    public void postData(Retrofit retrofit,String token,String id_comune){

        JsonApi postPuntoLuce = retrofit.create(JsonApi.class);
        Call<Void> call_pl = postPuntoLuce.postData(new Post(id,Nome_PL,
                TipoLuce,Ripetitore,Note,chiaviCrittografia,id_comune,indirizzo,coordinate,
                TipoApparecchiatura,Marca,Modello,InfoQuadroElettrico,Palo,AltezzaPaloMm,
                Portella,Pozzetto,Terra,TecnologiaLampada,PotenzaLampadaWatt,
                Alimentatore,LineaAlimentazione,Telecontrollo),token);

        call_pl.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                String rc = String.valueOf(response.code());

                if (!response.isSuccessful()) {
                    Log.d("http_post_rc : ", rc);
                    return;
                }
                else{
                    Log.d("http_ok_post__rc : ", rc);
                    createDialog("Operazione Completata","Esegui un'altra SCAN o ESCI");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("http_failure : ",t.getMessage());
            }
        });

    }


    /**
     * Costruisce il JSON per AGGIORNARE i dati nel portale
     * @param retrofit istanza della libreria per le chiamate API
     * @param token generato a partire da user e pass
     * @param id_comune id del comune nel quale inserire il punto luce
     */
    public void putData(Retrofit retrofit,String token,String id_comune){

        JsonApi postPuntoLuce = retrofit.create(JsonApi.class);
        Call<Void> call_pl = postPuntoLuce.putData(new Post(id,Nome_PL,
                TipoLuce,Ripetitore,Note,chiaviCrittografia,id_comune,indirizzo,coordinate,
                TipoApparecchiatura,Marca,Modello,InfoQuadroElettrico,Palo,AltezzaPaloMm,
                Portella,Pozzetto,Terra,TecnologiaLampada,PotenzaLampadaWatt,
                Alimentatore,LineaAlimentazione,Telecontrollo),token);

        call_pl.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                String rc = String.valueOf(response.code());

                if (!response.isSuccessful()) {
                    Log.d("http_post_rc : ", rc);
                    return;
                }
                else{
                    Log.d("http_ok_post__rc : ", rc);
                    createDialog("Operazione Completata","Esegui un'altra SCAN o ESCI");

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("http_failure : ",t.getMessage());
            }
        });

    }


    /**
     * Prende dal portale la lista dei dispositivi, se un ID è presente, mostra il dialog e chiede
     * se effettuare  un UPDATE chiamando nel caso putData, altrimenti esegue una insert con postData
     * @param retrofit istanza della libreria per le chiamate API
     * @param token generato a partire da user e pass
     * @param id_comune id del comune nel quale inserire il punto luce
     */
    public void getDevicesListInsertOrUpdate(Retrofit retrofit,String token,String id_comune){

        JsonApi jsonApi = retrofit.create(JsonApi.class);
        Call<JsonArray> callDevices = jsonApi.getDeviceList(token);
        callDevices.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                String rc = String.valueOf(response.code());
                if (!response.isSuccessful()) {
                    Log.d("http_get_list_rc : ", rc);
                }
                else {
                    Log.d("http_get_list_rc : ", rc);
                    //JSON in risposta, lo salvo in una stringa unica
                    String data = response.body().toString();
                    String id_with_quotes = "\""+id+"\"";
                    if(data.contains(id_with_quotes)){
                        System.out.println("UPDATE");
                        pd.dismiss();
                        // device già inserito, aggiornare i suoi dati?
                        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(SendDataActivity.this,R.style.AlertDialogCustom))

                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("PL già presente")
                                .setMessage("Vuoi cancellare il PL esistente ed inserire il nuovo?")
                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        putData(retrofit,token,id_comune);                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        createDialog("Operazione Annullata","Esegui un'altra SCAN o ESCI");
                                    }
                                })
                                .show();
                                alertDialog.setCanceledOnTouchOutside(false);
                    }
                    else{
                        pd.dismiss();
                        System.out.println("INSERT");
                        postData(retrofit,token,id_comune);
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.d("http_get_list__fail : ", t.getMessage());
            }
        });

    }

    /**
     * Interroga il portale e torna una lista di tutti i comuni;
     * scorre la lista e la confronta con qrCitta prendendo il corrispondente id.
     *
     * Al termine, chiama getDevicesList()
     * @param retrofit istanza della libreria per le chiamate API
     * @param token generato a partire da user e pass
     */

    public void insertLightPoint(Retrofit retrofit,String token) {
        JsonApi jsonApi = retrofit.create(JsonApi.class);
        Call<JsonObject> call = jsonApi.getJson(token);

        call.enqueue(new Callback <JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String rc = String.valueOf(response.code());
                if (!response.isSuccessful()) {
                    Log.d("http_get_ko_rc : ", rc);
                }
                else{
                    Log.d("http_get_ok_rc : ", rc);
                    //JSON in risposta, lo salvo in una stringa unica
                    String data = response.body().toString();
                    //Uso un dizionario così li divido poi passo il comune e prendo l'ID
                    Map<String, String> myMap = new HashMap<String, String>();
                    String[] pairs = data.split(",");
                    for (int i=0;i<pairs.length;i++) {
                        String pair = pairs[i];
                        String[] keyValue = pair.split(":");
                        myMap.put(keyValue[0], keyValue[1].trim());
                    }
                    for (Map.Entry<String, String> entry : myMap.entrySet()) {
                        if (entry.getValue().contains(qrCitta)) {
                            id_comune=entry.getKey().replace("\"",""); // rimuovo le virgolette
                            System.out.println("IDCOMUNE : "+id_comune);
                        }
                    }
                    /**
                     * Ottiene la lista degli ID registrati nel portale ed a seconda
                     * triggera un metodo POST per inserire o PUT per aggiornare
                     */
                    getDevicesListInsertOrUpdate(retrofit,token,id_comune);
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("http_rc_fail : ", t.getMessage());
            }
        });

    }


    /**
     * Mostra sulla mappa il punto luce installato
     * @param citta
     * @param lat
     * @param lon
     */
    public void showLightPointOnMap(String citta, String lat,String lon){

        String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lon + " (" + citta + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }



    /**
     * Inserimento dati punto luce nel portale alla pressione del tasto "invia"
     */
    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> sendData = new AsyncTask<Void, Void, Void>(){
        @Override
        protected Void doInBackground(Void... params) {

            try {
                // usate per costruire un oggetto della classe Post in postData() per creare quindi il JSON per l'invio
                id  = getIntent().getStringExtra("qrCode").toUpperCase();
                Nome_PL = getIntent().getStringExtra("name_").trim();
                //Nome_PL = "prova_app_jk"; // DEBUG lo uso se ho un solo qrcode per i test
                qrCitta =getIntent().getStringExtra("qrCitta");
                qrLatitudine = getIntent().getDoubleExtra("qrLatitudine",0);
                qrLongitudine = getIntent().getDoubleExtra("qrLongitudine",0);
                indirizzo = getIntent().getStringExtra("qrIndirizzo");
                PotenzaLampadaWatt = Double.parseDouble( getIntent().getStringExtra("valore_corrente") );

                conn_string = selectFromTable(id); // prendo la key da DevicesLightPointsTemp dal DB di CityMonitor

                // --------------------------------------- CONN_STRING FROM FTP DB ---------------------------------------------- //
                // ------------------------------------------------------------------------------------------------------------- //
                // controllo la lunghezza della chiave così se ho timeout dal portale o altri problemi, scarico il DB dall'FTP e la prendo da lì
                if(conn_string.length()!=32){
                    Log.d("DB_DB","prendo connstring dall' FTP");
                    try{
                        downloadAndSaveFile(server,portNumber,ftpUser,ftpPwd,fileName,db_saved);
                        SQLiteDatabase db = SQLiteDatabase.openDatabase(String.valueOf(db_saved), null, 0);
                        selectConnStringFromDb(db);
                    }catch (Exception f){}

                }
                // ------------------------------------------------------------------------------------------------------------- //
                // ----------------------------------------------------- END -------------------------------------------------- //

                chiaviCrittografia.add(conn_string);
                coordinate.setLat(qrLatitudine);
                coordinate.setLong(qrLongitudine);
                coordinateGps.add(coordinate);
                // DEBUG
                //id="D735F7773C956102";  // lo uso per sovrascrivere e testare gli INSERT
                
                // debug log http
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
                // end-debug
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://citymonitor-staging.azurewebsites.net/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();

                // prendo il token generato a partire da user e pass
                String token = LoginCredentials.getAuthToken(username,password);
                // inserisco o aggiorno il punto luce
                insertLightPoint(retrofit,token);

            } catch (final Exception e) {
                //createAndShowDialogFromTask(e, "Errore");
                Log.println(Log.INFO,"conn_string","select_ko");
                pd.dismiss();
                createDialog("Errore inserimento","Ripeti la scansione, se persiste, chiama l'assistenza ");

            }
            return null;
        }
    };


    /**
     * Add a new item.
     * Alla pressione del tasto INVIA  salva nel portale i dati.
     * Viene gestito nell'xml e non qui, quindi niente "OnClickListener()"
     * @param view
     * The view that originated the call
     */
    public void addItem(View view) {

        if (mClient == null) {
            return;
        }
        // progress dialog
        pd.setMessage("Registrazione in corso...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        //inserimento dati nel portale
        runAsyncTask(sendData);

        // ad inserimento completato rendo il pulsante non cliccabile, invisibile ed il testo colorato
        mTextNewToDo.setTextColor(Color.parseColor("#9EAFB8"));
        button.setVisibility(View.INVISIBLE);
        button.setClickable(false);

    }



    // ----------------------METODI PER INSERIRE I DATI NEL DB ---TODO QUESTI SOTTO SERVONO FORSE TUTTI -----------------


    /**
     * Refresh the list with the items in the Table
     */
    private void refreshItemsFromTable() {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<DevicesLightPointsTemp> results = refreshItemsFromMobileServiceTable(); // -L


                } catch (final Exception e){

                }

                return null;
            }
        };

        runAsyncTask(task);
    }


    /**
     * Effettua una query sul db per ottenere la chiave ed assegna il valore alla varibile
     * @param rluDB il database presso il quale effettuare la query
     */
    private void selectConnStringFromDb(SQLiteDatabase rluDB){

        SQLiteDatabase db = rluDB;
        String field =" key ";
        String tableName = " RLU ";
        String query= "SELECT"+field+"FROM"+tableName+"WHERE  id_radio = ?";
        Cursor c = db.rawQuery(query,new String[]{id});
        if (c.moveToFirst()){
            do {
                 conn_string = c.getString(0);
            } while(c.moveToNext());
        }
        c.close();
        db.close();
        Log.d("DB_DB",conn_string);

    }


    /**
     * Scarica il db dal server FTP nella cartella dell'applicazione
     * @param server l'ip del server
     * @param portNumber 21
     * @param user l'user per accedere al server
     * @param password password
     * @param filename nome compreso di path del file da scaricare
     * @param localFile nome che avrà il file una volta scaricato
     * @return se tutto ok torna true
     * @throws IOException classica eccezione
     */
    private Boolean downloadAndSaveFile(String server, int portNumber, String user, String password, String filename, File localFile)
            throws IOException {
        FTPClient ftp = null;

        try {
            ftp = new FTPClient();
            ftp.connect(server, portNumber);
            Log.d("DB_DB", "Connected. Reply: " + ftp.getReplyString());
            ftp.login(user, password);
            Log.d("DB_DB", "Logged in");
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            Log.d("DB_DB", "Downloading");
            ftp.enterLocalPassiveMode();

            OutputStream outputStream = null;
            boolean success = false;
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
                success = ftp.retrieveFile(filename, outputStream);
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }

            return success;
        } finally {
            if (ftp != null) {
                ftp.logout();
                ftp.disconnect();
            }
        }
    }


    /**
     * Select key from table DeviceLightPointsTemp where Id=? Ritorna la chiave interrogando il db del portale
     * @param ID ovvero il deviceid scansionato col qrcode
     * @return torna la chiave
     * @throws ExecutionException ecc
     * @throws InterruptedException ecc
     */
    private String selectFromTable ( String ID) throws ExecutionException, InterruptedException {

        final List<DevicesLightPointsTemp> conn_string =  mDevicesLightPointsTemp.where().field("id").eq(ID).execute().get();
        for (DevicesLightPointsTemp item : conn_string) {
            key=item.getConn_string();
        }
        return  key;
    }


    private List<DevicesLightPointsTemp> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mDevicesLightPointsTemp.where().field("complete").
                eq(val(false)).execute().get();
    }


    /**
     * Initialize local storage
     * @return
     * @throws MobileServiceLocalStoreException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
                    tableDefinition.put("id", ColumnDataType.String);
                    tableDefinition.put("complete", ColumnDataType.Boolean);
                    tableDefinition.put("nome", ColumnDataType.String);
                    tableDefinition.put("city", ColumnDataType.String);
                    tableDefinition.put("conn_string", ColumnDataType.String);
                    tableDefinition.put("via", ColumnDataType.String);
                    tableDefinition.put("latitude", ColumnDataType.Integer);
                    tableDefinition.put("longitude", ColumnDataType.Integer);

                    localStore.defineTable("DevicesLightPointsTemp", tableDefinition);
                    // localStore.defineTable("Punti_Luce", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    //    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        return runAsyncTask(task);
    }



    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, title);
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    /**
     * Add a new item
     *
     * @param view
     *            The view that originated the call
     */

    public void backToScan(View view){
        Intent intent = new Intent(getApplicationContext(),PreQrCodeActivity.class);
        startActivity(intent);
    }

    public void backToScan(){
        Intent intent = new Intent(getApplicationContext(),PreQrCodeActivity.class);
        startActivity(intent);
    }

    /**
     * Add a new item
     *
     * @param view
     *            The view that originated the call
     */

    public void quit(View view){
        finishAffinity();
        System.exit(0);
    }


    public void quit(){
        finishAffinity();
        System.exit(0);
    }



    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }




}
