package tz.co.xhcodes.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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

public class RegisterBodaBodaActivity extends AppCompatActivity {
    String POST_URL_TOWNS       = Config.baseUrl+"index.php/drivers/getTownsList";
    String POST_URL_REGISTER    = Config.baseUrl+"index.php/drivers/registerDriver";
    EditText jina_box, simu_box, kituo_box, reg_box, mtaa_box;
    Spinner select_town_spinner;
    private ArrayList<TownObject> townsList;
    final ArrayList<HashMap<String, String>> myList = new ArrayList<>();
    String town_id, regNumber, phoneNumber, parkingArea, driverName, driverCategory, user_id, streetName;
    HashMap<String, String> map;
    private ProgressDialog progress, progressDialog;
    TinyDB tinyDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Sajili Boda Boda");
        setContentView(R.layout.activity_register_boda_boda);
        select_town_spinner     = findViewById(R.id.select_town_spinner);
        jina_box                = findViewById(R.id.jina_box);
        simu_box                = findViewById(R.id.simu_box);
        mtaa_box                = findViewById(R.id.mtaa_box);
        kituo_box               = findViewById(R.id.kituo_box);
        reg_box                 = findViewById(R.id.reg_box);
        tinyDB                  = new TinyDB(this);
        jina_box.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        reg_box.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        townsList               = new ArrayList<>();
        new PostGetTowns(this).execute();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), BodaBodaActivity.class);
        startActivity(intent);
    }
    public void regiterBodaBoda(View view){
        town_id         = townsList.get(select_town_spinner.getSelectedItemPosition()).getId();
        regNumber       = reg_box.getText().toString();
        phoneNumber     = simu_box.getText().toString();
        parkingArea     = kituo_box.getText().toString();
        driverName      = jina_box.getText().toString();
        streetName      = mtaa_box.getText().toString();
        driverCategory  = "Boda";
        user_id         = tinyDB.getString("appUserId");
        if(regNumber.length()>0 && phoneNumber.length()>0 && parkingArea.length()>0 && driverName.length()>0 && streetName.length()>0)
        {
            if(phoneNumber.length()==12 || phoneNumber.length()==10)
            {
                String params = "user_id="+user_id+"&&town_id="+town_id+"&&regNumber="+regNumber+"&&parkingArea="+parkingArea+"&&driverName="+driverName+"&&streetName="+streetName+"&&driverCategory="+driverCategory+"&&phoneNumber="+phoneNumber;
                new RegisterDriver(RegisterBodaBodaActivity.this).execute(params);
            }
            else
            {
                dialogMessage("Weka namba ya simu iliyo sahihi");
            }
        }
        else
        {
            dialogMessage("Weka taarifa zote");
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
                }
                catch (JSONException e)
                {
                    dialogMessage("Jaribu tena, hakikisha kwanza kama una intanenti");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }
    private class RegisterDriver extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public RegisterDriver(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progressDialog= new ProgressDialog(this.context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Taarifa zako zinahifadhiwa...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL_REGISTER);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = params[0];
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
            progressDialog.dismiss();
            System.out.println(result);
            if(result.equalsIgnoreCase("Failed"))
            {
                dialogMessage("Imeshindikana kuhifadhi jaribu tena");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    String feeAmount         = jsonObj.get("feeAmount").toString();
                    String companyCode       = jsonObj.get("companyCode").toString();
                    String paymentCode       = jsonObj.get("paymentCode").toString();
                    String status            = jsonObj.get("status").toString();
                    tinyDB.putString("companyCode", companyCode);
                    tinyDB.putString("paymentCode", paymentCode);
                    tinyDB.putString("kiasi", feeAmount);
                    Intent objIntent = new Intent(getApplicationContext(), DriverPaymentMethodActivity.class);
                    startActivity(objIntent);
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
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterBodaBodaActivity.this);
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
