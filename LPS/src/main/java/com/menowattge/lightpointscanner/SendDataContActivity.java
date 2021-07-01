package com.menowattge.lightpointscanner;

/**
 * Inserisce o aggiorna i dati del device nel portale
 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

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
import java.io.FileNotFoundException;
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


public class SendDataContActivity extends Activity {

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
    public static TextView ldn_tw,citta_tw,address_tw,latitude_tw,longitude_tw,cognome_tw,nome_tw,numeroUtenza_tw,
    numeroContratto_tw,indirizzoUtenza_tw,numeroCivico_tw,matricolaCont_tw;

    public CardView recap;


    /**
     * Progress spinner to use for table operations
     */
    private ProgressBar mProgressBar;
    private android.widget.Button button;
    private android.widget.Button buttonAgain;
    private android.widget.Button buttonExit;
    private ProgressDialog pd;

    public String citta,nome,cognome;



    public static Double latitudine;
    public static Double longitudine;

    //-------------------------
    public        String conn_string;
    public        String key="";


    // API login
    String username="tecnico@citymonitor.it";
    String password="tecnico";

    //-------- per creare il JSON------
    public  static String  id ;
    private String  Nome ; // è in pratica il numeroUtente
    private boolean Ripetitore = false;
    private String  Note ="";
    private List<String> chiaviCrittografia   = new ArrayList<>();
    private    String      id_comune="";
    private String      indirizzo ;
    private List<PostContatori.CoordinateGps> coordinateGps = new ArrayList<>();
    private PostContatori.CoordinateGps coordinate = new PostContatori.CoordinateGps();

    private String  nomeUtente;
    private String  numeroUtente;
    private String  numeroContratto;
    private String  indirizzoUtenza,numeroCivico,ldnContatore;
    public String   matricolaCont = "";
    private String  numeroSerialeRadio; // LDN (MAD067643..)

    // ------end JSON---------

    String [] cryptoKeys = new String[17]; // chiavi prelevate dal JSON con getKeys()


    /**
     * Initializes the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data_cont);

        checkConnection();

        // total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        ldn_tw = findViewById(R.id.ldn);
        citta_tw = findViewById(R.id.citta);
        address_tw = findViewById(R.id.address);
        latitude_tw = findViewById(R.id.latitude);
        longitude_tw = findViewById(R.id.longitude);

        cognome_tw = findViewById(R.id.cognome);
        nome_tw = findViewById(R.id.nome);
        numeroUtenza_tw = findViewById(R.id.num_utenza);
        numeroContratto_tw = findViewById(R.id.num_contratto);
        indirizzoUtenza_tw = findViewById(R.id.indirizzo_utente);
        numeroCivico_tw = findViewById(R.id.num_civico);
        matricolaCont_tw = findViewById(R.id.matr_contatore);

        recap = findViewById(R.id.recap_card);

        recap.setCardBackgroundColor(Color.parseColor("#edf4f0"));
        recap.setCardElevation(5);

        button = findViewById(R.id.buttonAddToDoCont);
        pd = new ProgressDialog(new ContextThemeWrapper(SendDataContActivity.this,R.style.ProgressDialogCustom));

        // prelevo i dati acquisiti dalle scansioni
        //getQrCodeDataTest();
        getQrCodeData();
        // mostro i dati a video
        showData();

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
     * Ottiene i dati delle scansioni dei qrcode
     */
    public void getQrCodeData(){
        try {

            SharedPreferences sharedPreferences = getSharedPreferences("credenziali", MODE_PRIVATE);
            username = sharedPreferences.getString("username","");
            password = sharedPreferences.getString("password","");

            Log.d("CREDENZIALI_CONT : ",username+" - "+password);

            citta = getIntent().getStringExtra("citta");
            indirizzo = getIntent().getStringExtra("indirizzo");
            latitudine = getIntent().getDoubleExtra("latitudine", 0);
            longitudine = getIntent().getDoubleExtra("longitudine", 0);

            ldnContatore = getIntent().getStringExtra("ldn");
            numeroSerialeRadio = getIntent().getStringExtra("numero_seriale_radio");

            // seconda etichetta
            cognome= getIntent().getStringExtra("cognome");
            nome =  getIntent().getStringExtra("nome");
            nomeUtente = cognome+" "+nome;

            numeroUtente = getIntent().getStringExtra("numero_utenza");
            numeroContratto = getIntent().getStringExtra("numero_contratto");
            // TODO gestire che se indirizzo vuoto allora uso quello delle coordinate
            numeroCivico = getIntent().getStringExtra("numero_civico");
            indirizzoUtenza = getIntent().getStringExtra("indirizzo_utenza")+" "+numeroCivico;


            matricolaCont=getIntent().getStringExtra("matricola_contatore");

            id=ldnContatore;
            Nome=numeroUtente;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Errore : Ripetere le Scansioni", Toast.LENGTH_LONG).show();
        }
    }

    public void getQrCodeDataTest(){
        try {
            // TODO decommentare
            citta = "Grottammare";
            indirizzo = "Via Bolivia";
            latitudine = 42.123456;
            longitudine = 14.45678;

            ldnContatore = "2434040170198708";
            numeroSerialeRadio = "123456";

            // seconda etichetta
            cognome= "Plutoz";
            nome =  "Pippoz";
            nomeUtente = cognome+" "+nome;

            numeroUtente = "7892346";
            numeroContratto = "A34B5678";
            indirizzoUtenza = "Via Bontempo";
            numeroCivico = "42";

            matricolaCont="777777C";

            id=ldnContatore;

            Nome=numeroUtente;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Errore : Ripetere le Scansioni", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Mostra a video i dati acquisiti dalle scansioni
     */
    public void showData(){

        try {

            ldn_tw.setText(ldnContatore);
            citta_tw.setText("\n" + citta);
            address_tw.setText("\n" + numeroSerialeRadio);
            latitude_tw.setText("\n" + latitudine);
            longitude_tw.setText("\n" + longitudine);
            cognome_tw.setText("\n" + cognome);
            nome_tw.setText("\n" + nome);
            numeroUtenza_tw.setText("\n" + numeroUtente);
            numeroContratto_tw.setText("\n"+numeroContratto);
            indirizzoUtenza_tw.setText("\n"+indirizzoUtenza);
            numeroCivico_tw.setText("\n"+numeroCivico);
            matricolaCont_tw.setText("\n"+matricolaCont);

        }
        catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Errore : Ripetere le Scansioni", Toast.LENGTH_LONG).show();

        }

    }


    /**
     *  Mostra a video un Dialog
     * @param title titolo del messaggio
     * @param message messaggio
     */
    public void createDialog(String title, String message){

        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(SendDataContActivity.this,R.style.AlertDialogCustom))
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
    public void login(Retrofit retrofit,String token){
        //login
        JsonApi login = retrofit.create(JsonApi.class);
        //Call<ResponseBody> call_login = login.loginWithCredentials(new LoginCredentials(username, password));
        Call<ResponseBody> call_login = login.loginWithCredentials(token);


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

        JsonApi postContatore = retrofit.create(JsonApi.class);
        Call<Void> call_pl = postContatore.postDataContatori(new PostContatori(id,Nome
                ,Ripetitore,Note,chiaviCrittografia,id_comune,indirizzoUtenza,coordinate,
                nomeUtente,numeroUtente,numeroContratto,matricolaCont,numeroSerialeRadio),token);

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

        JsonApi putContatore = retrofit.create(JsonApi.class);
        Call<Void> call_pl = putContatore.putDataContatori(new PostContatori(id,Nome
                ,Ripetitore,Note,chiaviCrittografia,id_comune,indirizzoUtenza,coordinate,
                nomeUtente,numeroUtente,numeroContratto,matricolaCont,numeroSerialeRadio),token);

        call_pl.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                String rc = String.valueOf(response.code());

                if (!response.isSuccessful()) {
                    Log.d("http_put_rc : ", rc);
                    return;
                }
                else{
                    Log.d("http_ok_put__rc : ", rc);
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
                        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(SendDataContActivity.this,R.style.AlertDialogCustom))

                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Contatore già presente")
                                .setMessage("Vuoi cancellarlo ed inserirne uno nuovo?")
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

    public void getIdComune(Retrofit retrofit,String token) {
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
                        if (entry.getValue().contains(citta)) {
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
     * Ottiene la lista delle chiavi ddi un contatore
     * @param retrofit
     * @param token
     * @param ldnContatore
     */

    public void getKeys(Retrofit retrofit,String token,String ldnContatore) {
        JsonApi jsonApi = retrofit.create(JsonApi.class);
        Call<JsonObject> call = jsonApi.getJsonContatore(ldnContatore,token);

        call.enqueue(new Callback <JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String rc = String.valueOf(response.code());
                if (!response.isSuccessful()) {
                    Log.d("http_get_keys_ko_rc : ", rc);
                    chiaviCrittografia.clear();
                    if (chiaviCrittografia.isEmpty()) {
                        for (int i = 0; i < 17; i++) {
                            chiaviCrittografia.add("00000000000000000000000000000000");
                        }
                    }
                }
                else{
                    Log.d("http_get_keys_ok_rc : ", rc);
                    //JSON in risposta, lo salvo in una stringa unica
                    String data = response.body().toString();
                    // divido gli elementi sfruttando la virgola
                    String[] pairs = data.split(",");
                    int k=0;
                    try {
                        // vedere la questione VIRGOLA nel CIVICO che sposa da 10 ad 11 posizioni
                        for (int i = 10; i < 27; i++) { // le chiavi sono 17, le trovo nell'array dalla 10 alla 27
                            if (i == 10) {
                                Log.d("crypto",pairs[i].substring(23, 55));
                                cryptoKeys[k] = pairs[i].substring(23, 55); // tolgo la scritta "ChiaviCrittografia"
                               // chiaviCrittografia.add(pairs[i].substring(23, 55));
                            } else {
                                cryptoKeys[k] = pairs[i].substring(1, 33); // tolgo virgolette
                                Log.d("crypto2",pairs[i].substring(1, 33));

                               // chiaviCrittografia.add(pairs[i].substring(1, 33));
                            }
                            k++;
                        }

                        // inserisco le singole chiavi nell' arraylist
                           for (String item : cryptoKeys) {
                                Log.d("cryptoK", item);
                                chiaviCrittografia.add(item);
                            }

                    }catch (Error e){e.printStackTrace();

                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("http_rc_fail : ", t.getMessage());
            }
        });

    }



    /**
     * Inserimento dati punto luce nel portale alla pressione del tasto "invia"
     */
    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> sendData = new AsyncTask<Void, Void, Void>(){
        @Override
        protected Void doInBackground(Void... params) {

            try {

                coordinate.setLat(latitudine);
                coordinate.setLong(longitudine);
                coordinateGps.add(coordinate);
                // DEBUG

                // debug log http
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                        .readTimeout(60,TimeUnit.SECONDS)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .build();
                // end-debug
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://citymonitor.azurewebsites.net/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();

                // prendo il token generato a partire da user e pass
                String token = LoginCredentials.getAuthToken(username,password);
                // inserisco o aggiorno il contatore

                getKeys(retrofit,token,ldnContatore); // fondamentale

                getIdComune(retrofit,token);

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
    public void addItemCont(View view) {

        // progress dialog
        pd.setMessage("Registrazione in corso...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        //inserimento dati nel portale
        runAsyncTask(sendData);

        // ad inserimento completato rendo il pulsante non cliccabile, invisibile ed il testo colorato
        //mTextNewToDo.setTextColor(Color.parseColor("#9EAFB8"));
        //button.setVisibility(View.INVISIBLE);
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



    public void backToScan(){
        Intent intent = new Intent(getApplicationContext(),InsertOrDeleteActivity.class);
        startActivity(intent);
        finish();
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
