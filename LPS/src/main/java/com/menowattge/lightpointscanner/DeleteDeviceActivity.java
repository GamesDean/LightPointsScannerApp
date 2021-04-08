package com.menowattge.lightpointscanner;


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


public class DeleteDeviceActivity extends Activity {

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
    public static TextView devid_tw,code_tw,address_tw,latitude_tw,longitude_tw,corrente_tw;

    public CardView recap;
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


    // per creare il JSON
    public  static String  id ;
    private String  Nome_PL ;


    /**
     * Initializes the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_device);

        checkConnection();

        // total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        devid_tw = findViewById(R.id.devid_delete);
        code_tw = findViewById(R.id.code_delete);

        recap = findViewById(R.id.recap_card_delete);
        recap.setCardBackgroundColor(Color.parseColor("#edf4f0"));
        recap.setCardElevation(5);

        button = findViewById(R.id.buttonDelete);
        pd = new ProgressDialog(new ContextThemeWrapper(DeleteDeviceActivity.this,R.style.ProgressDialogCustom));

        getQrCodeData();




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
     * Preleva da QrCodeActivityDelete i dati necessari all'eliminazione del device
     */
    public void getQrCodeData(){

        //prelevo indirizzo radio D735.. e nome 15A
        String qrCodeData = getIntent().getStringExtra("qrCode_delete");
        String name = getIntent().getStringExtra("name_delete").trim();

        devid_tw.setText(qrCodeData);
        code_tw.setText("\n"+name);

    }


    /**
     *  Mostra a video un Dialog
     * @param title titolo del messaggio
     * @param message messaggio
     */
    public void createDialog(String title, String message){

        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(DeleteDeviceActivity.this,R.style.AlertDialogCustom))
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
     * Costruisce il JSON per Eliminare i dati nel portale
     * @param retrofit istanza della libreria per le chiamate API
     * @param token generato a partire da user e pass
     */
    public void postDeleteData(Retrofit retrofit,String token){

        JsonApi deletePuntoLuce = retrofit.create(JsonApi.class);

        Call<Void> call_pl = deletePuntoLuce.deleteDevices(token,id);

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
     * Eliminazione dati punto luce nel portale alla pressione del tasto "Elimina"
     */
    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> deleteData = new AsyncTask<Void, Void, Void>(){
        @Override
        protected Void doInBackground(Void... params) {

            try {
                // usate per costruire un oggetto della classe Delete in deleteData() per creare quindi il JSON per l'invio
                id  = getIntent().getStringExtra("qrCode_delete").toUpperCase();

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
                // elimino il punto luce
                postDeleteData(retrofit,token);

            } catch (final Exception e) {
                createAndShowDialogFromTask(e, "Errore");
                Log.println(Log.INFO,"conn_string","select_ko");
                pd.dismiss();
                //createDialog("Errore Eliminazione","Ripeti la scansione, se persiste, chiama l'assistenza ");

            }
            return null;
        }
    };


    /**
     * Delete an item.
     * Alla pressione del tasto ELIMINA cancella un device.
     * Viene gestito nell'xml e non qui, quindi niente "OnClickListener()"
     * @param view
     * The view that originated the call
     */
    public void deleteItem(View view) {

        if (mClient == null) {
            return;
        }
        // progress dialog
        pd.setMessage("Eliminazione in corso...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        //eliminazione dati nel portale
        runAsyncTask(deleteData);

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
     * Nel caso in cui si voglia eliminare un ulteriore device
     * Torna alla classe che permette di scansionare un altro QrCode
     */
    public void backToScan(){
        Intent intent = new Intent(getApplicationContext(),QrCodeActivityDelete.class);
        startActivity(intent);
        finish();
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
