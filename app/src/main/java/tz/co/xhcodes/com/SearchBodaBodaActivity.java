package tz.co.xhcodes.com;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchBodaBodaActivity extends AppCompatActivity {
    Spinner select_town_spinner;
    EditText search_box;
    String POST_URL_TOWNS       = Config.baseUrl+"index.php/drivers/getTownsList";
    private ArrayList<TownObject> townsList;
    final ArrayList<HashMap<String, String>> myList = new ArrayList<>();
    HashMap<String, String> map;
    private ProgressDialog progress, progressDialog;
    TinyDB tinyDB;
    String town_id, search_term;

    String POST_URL     = Config.baseUrl+"index.php/drivers/searchDrivers";
    JSONArray drivers_list;
    DriverAdapter driverAdapter;
    ListView drivers_list_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Tafuta Boda Boda");
        setContentView(R.layout.activity_search_boda_boda);
        select_town_spinner = findViewById(R.id.select_town_spinner);
        search_box          = findViewById(R.id.search_box);
        drivers_list_view   = findViewById(R.id.drivers_list_view);
        tinyDB              = new TinyDB(this);
        townsList               = new ArrayList<>();
        new PostGetTowns(this).execute();
        search_box.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        if(ContextCompat.checkSelfPermission(SearchBodaBodaActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(SearchBodaBodaActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 111);
        }
        bodaActions(drivers_list_view);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), BodaBodaActivity.class);
        startActivity(intent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 111: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SearchBodaBodaActivity.this, "Permission enabled", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(SearchBodaBodaActivity.this, "Huwezi kupiga simu moja kwa moja kwa dereva", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void searchBodaBoda(View view)
    {
        town_id         = townsList.get(select_town_spinner.getSelectedItemPosition()).getId();
        search_term     = search_box.getText().toString();
        if(search_term.equalsIgnoreCase("")){
            search_term = "NA";
        }

        new PostClass(this).execute();
    }
    private void bodaActions(final ListView listView){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try
                {
                    Driver driver            = driverAdapter.getItem(position);
                    final String townName          = driver.townName;
                    final String regNumber         = driver.regNumber;
                    final String phoneNumber       = driver.phoneNumber;
                    final String parkingArea       = driver.parkingArea;
                    final String driverName        = driver.driverName;
                    final String streetName        = driver.streetName;
                    final String driverCategory    = driver.driverCategory;
                    final String paymentCode       = driver.paymentCode;
                    final String days              = driver.days;
                    final String regDate           = driver.regDate;
                    final String expireDate        = driver.expireDate;
                    final String companyCode       = driver.companyCode;
                    final String feeAmount         = driver.feeAmount;

                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchBodaBodaActivity.this);
                    String message = "";
                    builder.setMessage("Mpigie "+driverName+"?");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Hapana", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("Ndiyo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(ContextCompat.checkSelfPermission(SearchBodaBodaActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                            {
                                startActivityForResult(new Intent("android.intent.action.CALL", Uri.parse("tel:+" + phoneNumber)), 1);
                            }
                            else
                            {
                                dialogMessage("Ruhusu IwachuPay kupiga simu");
                            }

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();


                }
                catch (Exception e)
                {
                    Toast.makeText(SearchBodaBodaActivity.this, "Jaribu tena tafadhari", Toast.LENGTH_LONG).show();
                }



            }
        });
    }
    private class PostClass extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostClass(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progress= new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Tunakusanya orodha ya boda boda....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "user_id="+tinyDB.getString("appUserId")+"&&driverCategory=Boda&&search_term="+search_term+"&&town_id="+town_id;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dStream        = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                final StringBuilder output = new StringBuilder("");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();
                output.append(responseOutput.toString());
                result = responseOutput.toString();
            }
            catch (MalformedURLException e)
            {
                result =  e.getMessage();
            }
            catch (IOException e)
            {
                result =  e.getMessage();
            }
            return result;
        }
        protected void onPostExecute(String result)
        {
            progress.dismiss();
            System.out.println(result);
            if(result.equalsIgnoreCase("NF"))
            {
                if(driverAdapter !=null)
                {
                    driverAdapter.clear();
                    driverAdapter.notifyDataSetChanged();
                }
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    drivers_list              = jsonObj.getJSONArray("drivers");
                    displayDriversList(drivers_list);
                }
                catch (JSONException e)
                {
                    //dialogMessage("Try again please");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }
    private void displayDriversList(JSONArray result){
        //display tickets list here
        if(result !=null)
        {
            if(result.length()>0)
            {
                if(driverAdapter==null)
                {
                    ArrayList<Driver> drivers = Driver.fromJson(result);
                    driverAdapter = new DriverAdapter(getBaseContext(), drivers);
                    drivers_list_view.setAdapter(driverAdapter);
                }
                else
                {
                    driverAdapter.clear();
                    ArrayList<Driver> drivers = Driver.fromJson(result);
                    driverAdapter.addAll(drivers);
                    driverAdapter.notifyDataSetChanged();
                }

            }

        }
    }
    private class PostGetTowns extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostGetTowns(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progress= new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Inakusanya orodha ya miji, subiri...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL_TOWNS);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "appUserId="+LoginActivity.appUserId;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dStream        = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                final StringBuilder output = new StringBuilder("");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();
                output.append(responseOutput.toString());
                result = responseOutput.toString();
            }
            catch (MalformedURLException e)
            {
                result =  e.getMessage();
            }
            catch (IOException e)
            {
                result =  e.getMessage();
            }
            return result;
        }
        protected void onPostExecute(String result)
        {
            progress.dismiss();
            // System.out.println(result);
            if(result.equalsIgnoreCase("NF"))
            {
                dialogMessage("Hakuna miji");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    if (jsonObj != null) {
                        JSONArray categories = jsonObj.getJSONArray("towns");
                        for (int i = 0; i < categories.length(); i++) {
                            JSONObject catObj = (JSONObject) categories.get(i);
                            TownObject cat = new TownObject(catObj.getString("name"), catObj.getString("id"));
                            townsList.add(cat);
                            map = new HashMap<>();
                            map.put("id", catObj.getString("id"));
                            map.put("name", catObj.getString("name"));
                            myList.add(map);
                        }
                        populateSpinner();
                    }
                    //load drivers from users registered town
                    if(!tinyDB.getString("townid").equalsIgnoreCase(""))
                    {
                        search_term = "NA";
                        town_id     = tinyDB.getString("townid");
                        new PostClass(SearchBodaBodaActivity.this).execute();
                    }
                }
                catch (JSONException e)
                {
                    dialogMessage("Jaribu tena, hakikisha kwanza kama una intanenti");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }
    /**
     * Adding spinner data
     * */
    private void populateSpinner() {
        List<String> sources = new ArrayList<String>();
        for (int i = 0; i < townsList.size(); i++) {
            sources.add(townsList.get(i).getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sources);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        select_town_spinner.setAdapter(spinnerAdapter);
        //set user town if present
        if(!tinyDB.getString("townid").equalsIgnoreCase("") && !tinyDB.getString("townid").equalsIgnoreCase("0"))
        {
            //set here
            select_town_spinner.setSelection(spinnerAdapter.getPosition(tinyDB.getString("townname")));
        }
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchBodaBodaActivity.this);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
