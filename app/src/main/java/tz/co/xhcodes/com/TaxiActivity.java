package tz.co.xhcodes.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

public class TaxiActivity extends AppCompatActivity {
    String POST_URL     = Config.baseUrl+"index.php/drivers/myDrivers";
    JSONArray drivers_list;
    private ProgressDialog progress;
    DriverAdapter driverAdapter;
    ListView drivers_list_view;
    TinyDB tinydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Taxi");
        setContentView(R.layout.activity_taxi);
        drivers_list_view = findViewById(R.id.drivers_list_view);
        tinydb = new TinyDB(this);
        if(tinydb.getString("loginStatus").equalsIgnoreCase("logged_in")){
            bodaActions(drivers_list_view);
            new PostClass(this).execute();
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
    public void registerTaxi(View view){
        if(LoginActivity.agentGroup.equalsIgnoreCase("Client") && !tinydb.getString("loginStatus").equalsIgnoreCase("logged_in")){
            dialogMessage("Ingia kwanza kwenye akaunti yako au jisajili kama mtumiaji ndipo usajili taxi");
        }
        else {
            Intent intent = new Intent(getApplicationContext(), RegisterTaxiActivity.class);
            startActivity(intent);
        }
    }
    public void searchTaxi(View view){
        Intent intent = new Intent(getApplicationContext(), SearchTaxiActivity.class);
        startActivity(intent);
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(TaxiActivity.this);
                    String message = "";
                    if(days.equalsIgnoreCase("0"))
                    {
                        message += "Muda wa kuonekana kwa abiria umekwisha. Unataka kulipia taxi hii "+regNumber+"?";
                    }
                    else
                    {
                        message += "Zimebaki siku "+days+" za kuonekana kwa abiria. Unataka kulipia taxi hii  "+regNumber+"?";
                    }
                    builder.setMessage(message);
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
                            tinydb.putString("companyCode", companyCode);
                            tinydb.putString("paymentCode", paymentCode);
                            tinydb.putString("kiasi", feeAmount);
                            Intent intent            = new Intent(getApplicationContext(), DriverPaymentMethodActivity.class);
                            startActivity(intent);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();


                }
                catch (Exception e)
                {
                    Toast.makeText(TaxiActivity.this, "Jaribu tena tafadhari", Toast.LENGTH_LONG).show();
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
            progress.setMessage("Tunakusanya orodha ya taxi zako....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "user_id="+tinydb.getString("appUserId")+"&&driverCategory=Taxi";
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
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(TaxiActivity.this);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
