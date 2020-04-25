package com.menowattge.lightpointscanner;

/**
 * Popola la lista con il dato appena salvato sul DB nella tabella DevicesLightPointsTemp
 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;


public class ToDoActivity extends Activity {

    /**
     * Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Table used to access data from the mobile app backend.
     */
    //private MobileServiceTable<ToDoItem> mToDoTable;  -L

    private MobileServiceTable<DevicesLightPointsTemp> mDevicesLightPointsTemp;

    //Offline Sync
    /**
     * Table used to store data locally sync with the mobile app backend.
     */
    //private MobileServiceSyncTable<ToDoItem> mToDoTable;   -L

    /**
     * Adapter to sync the items list with the view
     */
    // private ToDoItemAdapter mAdapter;



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


    /**
     * Initializes the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        checkConnection();

        //mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        mTextNewToDo = (TextView) findViewById(R.id.textNewToDo);
        button = findViewById(R.id.buttonAddToDo);

        buttonAgain = findViewById(R.id.button3);
        buttonExit = findViewById(R.id.button4);

        // Initialize the progress bar
//        mProgressBar.setVisibility(ProgressBar.GONE);

        getQrCodeData();

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

           // mToDoTable = mClient.getTable(ToDoItem.class); -L
            mDevicesLightPointsTemp = mClient.getTable(DevicesLightPointsTemp.class);

            // Offline sync table instance.
            //mToDoTable = mClient.getSyncTable("ToDoItem", ToDoItem.class);

            //Init local storage
            initLocalStore().get();

            // Create an adapter to bind the items with the view
            //mAdapter = new ToDoItemAdapter(this, R.layout.row_list_to_do);
           // ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            //listViewToDo.setAdapter(mAdapter);

            // Load the items from the mobile app backend.
           // refreshItemsFromTable();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("C'è un problema con il Mobile Service. Controlla l'URL"), "Error");
        } catch (Exception e){
      //      createAndShowDialog(e, "Error");
        }
    }




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

     /*   AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    checkItemInTable(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (item.isComplete()) {
                                mAdapter.remove(item);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);

*/
    }

    /**
     * Mark an item as completed in the Mobile Service Table
     *
     * @param item
     *            The item to mark
     */
//    public void checkItemInTable(ToDoItem item) throws ExecutionException, InterruptedException {  -L
//        mToDoTable.update(item).get();
//    }


    /**
     * Mark an item as completed in the Mobile Service Table
     *
     * @param item
     *            The item to mark
     */
    public void checkItemInTable(DevicesLightPointsTemp item) throws ExecutionException, InterruptedException {
        mDevicesLightPointsTemp.update(item).get();
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

    public static String name;
    public static String ID;
    public static String qrCitta;
    public static Double qrLatitudine;
    public static Double qrLongitudine;
    public static String qrAddress;
    public static String valoreCorrente;
    public String        conn_string;


    /**
     * Add a new item
     *
     * @param view
     *            The view that originated the call
     */

// pressione del tasto Add che salva nel DB i dati. viene gestito nell'xml e non qui, quindi niente "OnClickListener()"

    public void addItem(View view) {


        if (mClient == null) {
            return;
        }

        // -L



        // prelevo ID e nome partendo dalla combinazione dei due
        //String qrCodeData = getIntent().getStringExtra("qrCode");
         ID =  getIntent().getStringExtra("qrCode");                  //   qrCodeData.substring(0,16);

        //String name =  "PL"+value;
         name = getIntent().getStringExtra("name_").trim();

        // prelevo la citta
         qrCitta = getIntent().getStringExtra("qrCitta");
        // prelevo le coordinate
         qrLatitudine = getIntent().getDoubleExtra("qrLatitudine",0);
         qrLongitudine = getIntent().getDoubleExtra("qrLongitudine",0);
        // prelevo l'indirizzo
         qrAddress = getIntent().getStringExtra("qrIndirizzo");

         valoreCorrente = getIntent().getStringExtra("valore_corrente");

        // get conn_string
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task_select = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    conn_string=selectFromTable();
                    Log.println(Log.INFO,"conn_string","select_ok");

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "UpdateError");
                    Log.println(Log.INFO,"conn_string","select_KO");
                }
                return null;
            }
        };

        runAsyncTask(task_select);


        //invio i dati rilevati alla classe che li salva nel file di testo per inviarlo all'FTP per sicurezza
        //CheckConnectionActivity.writeToFile(this);

        int valoreCorrente_;
        int valoreCorrenteCalcolo;

        try {
            valoreCorrente_ = Integer.parseInt(valoreCorrente);
            valoreCorrenteCalcolo = valoreCorrente_*36;
        }
        catch (NumberFormatException e)
        {
            valoreCorrente_ = 0;
            valoreCorrenteCalcolo=0;
        }

        // dato che la forma canonica è "via , civico , etc " divido secondo questa logica
        String[] addressArray = qrAddress.split(",");

        String via = addressArray[0];
        String numeroCivico = addressArray[1];

        String viaCompleta = via+", "+numeroCivico;

        // /-L

        // Create a new item
       // final ToDoItem item = new ToDoItem(); -L
        final DevicesLightPointsTemp item = new DevicesLightPointsTemp();


        // imposto l'ID TODO decommentare in fase di release o test con qrcode con ID diversi
         item.setId(ID);   // FUNZIONA- COMMENTO ALTRIMENTI NON INSERISCE CAUSA PRIMARY KEY RIPETUTA

        //item.setId("D73528DC2B510777");

        // imposto il nome
        item.setName(name);
        // città
        item.setCity(qrCitta);
        // latitudine
        item.setLatitude(qrLatitudine);
        // longitudine
        item.setLongitude(qrLongitudine);
        // via
        item.setVia(viaCompleta);
        //corrente selezionata dal menù a tendina
        item.setCorrente(valoreCorrenteCalcolo);

        item.setComplete(false);

        // Insert the new item
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                   // final ToDoItem entity = addItemInTable(item); -L
                    //final DevicesLightPointsTemp entity = updateItemInTable(item);
                    //-L non ha valore di ritorno, a me non serve e così non dà Errore
                    updateItemInTable_(item);
                    //sendMail("Mail operatore-update","dati inseriti nel DB");
                    Log.println(Log.INFO,"database","update_ok");

                 /*   runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!entity.isComplete()){
                                mAdapter.add(entity);
                            }
                        }
                    });
                    */
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "UpdateError");
                    Log.println(Log.INFO,"database","update_KO!");

                    /*
                    try {
                        final DevicesLightPointsTemp entity = addItemInTable(item);
                        Log.println(Log.INFO,"database","insert_ok");


                       runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!entity.isComplete()){
                                    mAdapter.add(entity);
                                }
                            }
                        });

                    } catch (final Exception ex) {
                        createAndShowDialogFromTask(ex, "InsertError");

                    }

                    */

                }
                return null;
            }
        };

        runAsyncTask(task);

        // ad inserimento completato rendo il pulsante non cliccabile, invisibile ed il testo colorato
        mTextNewToDo.setTextColor(Color.parseColor("#9EAFB8"));
        button.setVisibility(View.INVISIBLE);
        button.setClickable(false);

        // ad inserimento completato rendo il pulsante cliccabile e visibile
        buttonAgain.setVisibility(View.VISIBLE);
        buttonAgain.setClickable(true);

        buttonExit.setVisibility(View.VISIBLE);
        buttonExit.setClickable(true);



        //textview che scrive Operazione Completata
        TextView textView_ok =  (TextView)(findViewById(R.id.textview_ok));
        textView_ok.setText("Operazione Completata");
        //mail con il resoconto della scansione
        String riepilogo = "L'operatore ha completato il suo lavoro : \n"+ID+"\n"+name+"\n"+qrCitta+"\n"+qrLatitudine+"\n"+qrLongitudine+"\n"+qrAddress+"\n"+valoreCorrenteCalcolo;
       // sendMail("Mail Operatore",riepilogo);


        Intent intent = new Intent(getApplicationContext(),CheckConnectionActivity.class);
        startActivity(intent);

    }






    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
  //  public ToDoItem addItemInTable(ToDoItem item) throws ExecutionException, InterruptedException { -L
  //      ToDoItem entity = mToDoTable.insert(item).get();
  //      return entity;
  //  }

    /**
     * Update an item to the Mobile Service Table
     *
     * @param item
     *            The item to Update
     */
    public DevicesLightPointsTemp updateItemInTable(DevicesLightPointsTemp item) throws ExecutionException, InterruptedException {

        DevicesLightPointsTemp entity = mDevicesLightPointsTemp.update(item).get();


        return entity;
    }

    /**
     * Update an item to the Mobile Service Table
     *
     * @param item
     *            The item to Update
     */
    public void updateItemInTable_(DevicesLightPointsTemp item) throws ExecutionException, InterruptedException {

         mDevicesLightPointsTemp.update(item);



    }


    /**
     * Insert an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public DevicesLightPointsTemp addItemInTable(DevicesLightPointsTemp item) throws ExecutionException, InterruptedException {

        DevicesLightPointsTemp entity = mDevicesLightPointsTemp.insert(item).get();

        return entity;
    }


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

                    //Offline Sync
                    //final List<ToDoItem> results = refreshItemsFromMobileServiceTableSyncTable();

                /*    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (DevicesLightPointsTemp item : results) { // -L
                                mAdapter.add(item);
                            }
                        }
                    });
                    */
                } catch (final Exception e){
             //       createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }



    /**
     * Select key from table
     */

    private String selectFromTable() throws ExecutionException, InterruptedException {



             String conn_string =  mDevicesLightPointsTemp.where().field("conn_string").eq(val(ID)).execute().get().toString();

             Log.d("CONNECTION_STRING : ",conn_string);

             return conn_string;
    }




    private List<DevicesLightPointsTemp> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mDevicesLightPointsTemp.where().field("complete").
                eq(val(false)).execute().get();
    }

    //Offline Sync
    /**
     * Refresh the list with the items in the Mobile Service Sync Table
     */
    /*private List<ToDoItem> refreshItemsFromMobileServiceTableSyncTable() throws ExecutionException, InterruptedException {
        //sync the data
        sync().get();
        Query query = QueryOperations.field("complete").
                eq(val(false));
        return mToDoTable.read(query).get();
    }*/

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
                   // tableDefinition.put("text", ColumnDataType.String);
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

    //Offline Sync
    /**
     * Sync the current context and the Mobile Service Sync Table
     * @return
     */
    /*
    private AsyncTask<Void, Void, Void> sync() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    MobileServiceSyncContext syncContext = mClient.getSyncContext();
                    syncContext.push().get();
                    mToDoTable.pull(null).get();
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        return runAsyncTask(task);
    }
    */

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
