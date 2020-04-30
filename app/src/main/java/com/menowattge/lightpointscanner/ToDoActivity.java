package com.menowattge.lightpointscanner;

/**
 * Popola la lista con il dato appena salvato sul DB nella tabella DevicesLightPointsTemp
 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
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

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;


public class ToDoActivity extends Activity {

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





    // TODO spostare ad inizio classe

    public static String name;

    public static String qrCitta;
    public static Double qrLatitudine;
    public static Double qrLongitudine;
    public static String qrAddress;
    public static String valoreCorrente;
    public        String conn_string;
    public        String key="";

    String username="xxxxxxx";
    String password="xxxxxxx";

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

    /**
     * Initializes the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        checkConnection();

        mTextNewToDo = findViewById(R.id.textNewToDo);
        button = findViewById(R.id.buttonAddToDo);

        buttonAgain = findViewById(R.id.button3);
        buttonExit = findViewById(R.id.button4);


        getQrCodeData();

        // -------------------------------------------------------------------------------------------------------------------------

        // ----------- Definisco il link dove risiede la mia app su Azure poi creo l'istanza della tabella del DB ------------------

        // -------------------------------------------------------------------------------------------------------------------------

        try {
            // Create the client instance, using the provided mobile app URL.
            mClient = new MobileServiceClient(
                    "https://menowattgeqrcodescanner.azurewebsites.net", // fondamentale
                    this).withFilter(new ProgressFilter());

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
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
        String name = getIntent().getStringExtra("name_").trim();

        // mostro a video i  valori soprastanti
        mTextNewToDo.setText(qrCodeData);
        mTextNewToDo.append("\ncodice : "+name+"\nindirizzo : "+qrAddress+"\nlatitudine : "+qrLatitudine+"\nlongitudine : "+qrLongitudine+"\ncorrente : "+valoreCorrente+"\n");

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
     * Costruisce il JSON per inserire i dati nel portale
     * @param retrofit
     * @param token
     * @param id_comune
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
                    //textview che scrive Operazione Completata
                    // Ad inserimento avvenuto, mostro il device inserito sulla mappa
                     showLightPointOnMap(qrCitta,qrLatitudine.toString(),qrLongitudine.toString());
                    TextView textView_ok = (findViewById(R.id.textview_ok));

                    textView_ok.setText("Operazione Completata");
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
     * @param retrofit
     * @param token
     * @param id_comune
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
                    //textview che scrive Operazione Completata
                    // Ad inserimento avvenuto, mostro il device inserito sulla mappa
                    showLightPointOnMap(qrCitta,qrLatitudine.toString(),qrLongitudine.toString());
                    TextView textView_ok = (findViewById(R.id.textview_ok));
                    textView_ok.setText("Operazione Completata");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("http_failure : ",t.getMessage());
            }
        });

    }


    public void getDevicesList(Retrofit retrofit,String token,String id_comune){

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
                        // device già inserito, aggiornare i suoi dati?
                        AlertDialog alertDialog = new AlertDialog.Builder(ToDoActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("PL già inserito")
                                .setMessage("Vuoi aggiornarlo?")
                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        putData(retrofit,token,id_comune);                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(getApplicationContext(),"Operazione Annullata",Toast.LENGTH_LONG).show();
                                        TextView textView_ok = (findViewById(R.id.textview_ok));

                                        textView_ok.setText("Esegui un'altra scan o esci");
                                    }
                                })
                                .show();

                    }



                    else{
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
     * Get della lista dei device registrati nel portale
     * in base alla presenza o meno inserimento o aggiornamento
     * dei dati del punto luce nel portale e visualizzazione su maps
     * @param retrofit
     * @param token
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
                    getDevicesList(retrofit,token,id_comune);
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("http_rc_fail : ", t.getMessage());
            }
        });

    }

    GoogleMap googleMap = null;

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
     *
     * Inserimento dati punto luce nel portale
     *
     */

    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task_post = new AsyncTask<Void, Void, Void>(){
        @Override
        protected Void doInBackground(Void... params) {

            try {
                // usate per costruire un oggetto della classe Post in postData() per creare quindi il JSON per l'invio
                id  = getIntent().getStringExtra("qrCode").toUpperCase();
                Nome_PL = getIntent().getStringExtra("name_").trim();
                Log.d("Nome_PL : ",Nome_PL);
                Nome_PL = "prova_app_y"; // TODO delete -> per ora rinomino perche ho un solo qr code con nome gia inserito
                //qrCitta = "Grottammare";//getIntent().getStringExtra("qrCitta");
                qrCitta =getIntent().getStringExtra("qrCitta");
                qrLatitudine = getIntent().getDoubleExtra("qrLatitudine",0);
                qrLongitudine = getIntent().getDoubleExtra("qrLongitudine",0);
                indirizzo = getIntent().getStringExtra("qrIndirizzo");
                PotenzaLampadaWatt = Double.parseDouble( getIntent().getStringExtra("valore_corrente") );
                conn_string = selectFromTable(id); // prendo la key da DevicesLightPointsTemp dal DB di CityMonitor
                chiaviCrittografia.add(conn_string);
                coordinate.setLat(qrLatitudine);
                coordinate.setLong(qrLongitudine);
                coordinateGps.add(coordinate);

               // id="D735D9193D944102";  // lo uso per sovrascrivere e testare gli INSERT


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
                insertLightPoint(retrofit,token);

            } catch (final Exception e) {
                createAndShowDialogFromTask(e, "PostError");
                Log.println(Log.INFO,"conn_string","select_KO");
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

        TextView textView_ok = (findViewById(R.id.textview_ok));

        textView_ok.setText("Registrazione in corso...");

        //inserimento dati nel portale
        runAsyncTask(task_post);

        // ad inserimento completato rendo il pulsante non cliccabile, invisibile ed il testo colorato
        mTextNewToDo.setTextColor(Color.parseColor("#9EAFB8"));
        button.setVisibility(View.INVISIBLE);
        button.setClickable(false);
        // ad inserimento completato rendo il pulsante cliccabile e visibile
        buttonAgain.setVisibility(View.VISIBLE);
        buttonAgain.setClickable(true);

        buttonExit.setVisibility(View.VISIBLE);
        buttonExit.setClickable(true);

    }



    // ----------------------METODI PER INSERIRE I DATI NEL DB ---TODO QUESTI SOTTO SERVONO FORSE TUTTI -----------------


    /**
     * Refresh the list with the items in the Table
     */
    private void refreshItemsFromTable() {

        /*
        Get the items that weren't marked as completed and add them in the
        adapter
        */

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
     * Select key from table DeviceLightPointsTemp where Id=?
     * @param ID
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
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
