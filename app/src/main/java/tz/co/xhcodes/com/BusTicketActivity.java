package tz.co.xhcodes.com;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import static tz.co.xhcodes.com.R.id.selectRouteSpinnerClient;

public class BusTicketActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Calendar c;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMins;
    private int mSeconds;
    EditText chooseDateBox;
    String recordDate;

    String POST_URL_GETCOMPANIES    = Config.baseUrl+"index.php/tickets/getCompaniesList";
    String POST_URL_GETROUTES       = Config.baseUrl+"index.php/tickets/getRoutesList";
    private ProgressDialog progress;
    Spinner selectCompany, selectRoute;
    private ArrayList<Company> companiesList;
    private ArrayList<Route> routesList;
    HashMap<String, String> map;
    final ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();
    final ArrayList<HashMap<String, String>> myRoutesList = new ArrayList<HashMap<String, String>>();
    String company_id, route_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_bus_ticket);
        selectCompany   = findViewById(R.id.selectCompanySpinner);
        selectRoute     = findViewById(selectRouteSpinnerClient);
        chooseDateBox   = findViewById(R.id.chooseDateBox);
       // AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        am.setTimeZone("Africa/Nairobi");
        c               = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"));
        mYear           = c.get(Calendar.YEAR);
        mMonth          = c.get(Calendar.MONTH);
        mDay            = c.get(Calendar.DAY_OF_MONTH);
        companiesList   = new ArrayList<>();
        routesList      = new ArrayList<>();
        selectCompany.setOnItemSelectedListener(this);
        new PostGetCompaniesClass(this).execute();
        chooseDateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });


    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(objIntent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.back_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //When Sync action button is clicked
        if (id == R.id.back_btn) {
            Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(objIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void showDatePicker()
    {
        DatePickerDialog dpd = new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                chooseDateBox.setText(dayOfMonth + "-"+ (monthOfYear + 1) + "-" + year);

            }
        }, mYear, mMonth, mDay);
        //un comment line below to set minimum date to current date for android 3.0 and above
        dpd.getDatePicker().setMinDate(new Date().getTime() - 10000);
        dpd.show();
    }

    public void searchBusesTimetable(View view)
    {
        recordDate              = chooseDateBox.getText().toString();
        if(recordDate.length()>0)
        {
            route_id = myRoutesList.get(selectRoute.getSelectedItemPosition()).get("routeid");
            BusVariables.safari_date    = recordDate;
            BusVariables.companyId      = company_id;
            BusVariables.routeId        = route_id;
           // Toast.makeText(BusTicketActivity.this, route_id, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(BusTicketActivity.this, BusesListActivity.class);
            startActivity(intent);
        }
        else
        {
            dialogMessage("Chagua tarehe kwanza!");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.selectCompanySpinner)
        {
            company_id = myList.get(position).get("id");
            myRoutesList.clear();
            routesList.clear();
            new PostGetRoutesClass(BusTicketActivity.this).execute();
        }
       /* if(spinner.getId() == R.id.selectRouteSpinnerClient)
        {
            route_id = myRoutesList.get(position).get("routeid");
            Toast.makeText(BusTicketActivity.this, route_id, Toast.LENGTH_LONG).show();
        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private class PostGetCompaniesClass extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostGetCompaniesClass(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progress= new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Inakusanya orodha ya makampuni, subiri...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL_GETCOMPANIES);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "company_id="+LoginActivity.companyId+"&&agentId="+LoginActivity.agentId;
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
                dialogMessage("Hakuna makampuni");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    if (jsonObj != null) {
                        JSONArray categories = jsonObj.getJSONArray("companies");
                        for (int i = 0; i < categories.length(); i++) {
                            JSONObject catObj = (JSONObject) categories.get(i);
                            Company cat = new Company(catObj.getInt("id"), catObj.getString("name"));
                            companiesList.add(cat);
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
    /**
     * Adding spinner data
     * */
    private void populateSpinner() {
        List<String> sources = new ArrayList<String>();
        for (int i = 0; i < companiesList.size(); i++) {
            sources.add(companiesList.get(i).getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sources);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        selectCompany.setAdapter(spinnerAdapter);
    }
    private class PostGetRoutesClass extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostGetRoutesClass(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL_GETROUTES);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "company_id="+company_id+"&&agentId="+LoginActivity.agentId;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dStream        = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                final StringBuilder output = new StringBuilder("");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line     = "";
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
                dialogMessage("Hakuna orodha ya njia zilizopatikana");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    if (jsonObj != null) {
                        JSONArray categories = jsonObj.getJSONArray("routes");
                        for (int i = 0; i < categories.length(); i++) {
                            JSONObject catObj = (JSONObject) categories.get(i);
                            Route cat = new Route(catObj.getInt("routeid"), catObj.getString("fromname"), catObj.getString("toname"));
                            routesList.add(cat);
                            map = new HashMap<>();
                            map.put("routeid", catObj.getString("routeid"));
                            map.put("fromname", catObj.getString("fromname"));
                            map.put("toname", catObj.getString("toname"));
                            myRoutesList.add(map);
                        }
                        populateRoutesSpinner();
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
    private void populateRoutesSpinner() {
        List<String> sources = new ArrayList<>();
        for (int i = 0; i < routesList.size(); i++) {
            sources.add(routesList.get(i).getFromName()+"-"+routesList.get(i).getToName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sources);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        selectRoute.setAdapter(spinnerAdapter);
    }

    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(BusTicketActivity.this);
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
