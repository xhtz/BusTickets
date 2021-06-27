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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import static tz.co.xhcodes.com.R.id.selectLineSpinner;

public class TreniTicketActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Calendar c;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMins;
    private int mSeconds;
    EditText chooseDateBox;
    String recordDate;

    String POST_URL_LINES           = Config.baseUrl+"index.php/tickets/getLinesList";
    String POST_URL_STATIONS        = Config.baseUrl+"index.php/tickets/getStationsList";
    private ProgressDialog progress;
    Spinner selectLine, selectFromStation, selectToStation, treniClassSelect;
    private ArrayList<Line> linesList;
    private ArrayList<Station> fromStationsList;
    private ArrayList<Station> toStationsList;
    HashMap<String, String> map;
    final ArrayList<HashMap<String, String>> myLinesList = new ArrayList<HashMap<String, String>>();
    final ArrayList<HashMap<String, String>> myFromStationsList = new ArrayList<>();
    final ArrayList<HashMap<String, String>> myToStationsList = new ArrayList<>();
    String line_id, from_station, to_station, treni_class;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Nunua Tiketi Treni");
        setContentView(R.layout.activity_treni_ticket);
        selectLine          = (Spinner)findViewById(selectLineSpinner);
        selectFromStation   = (Spinner)findViewById(R.id.fromStation);
        selectToStation     = (Spinner)findViewById(R.id.toStation);
        treniClassSelect    = (Spinner)findViewById(R.id.classSelect);
        linesList           = new ArrayList<>();
        fromStationsList    = new ArrayList<>();
        toStationsList      = new ArrayList<>();
        chooseDateBox   = (EditText)findViewById(R.id.chooseDateBox);
        c               = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"));
        mYear           = c.get(Calendar.YEAR);
        mMonth          = c.get(Calendar.MONTH);
        mDay            = c.get(Calendar.DAY_OF_MONTH);
        new PostGetLinesClass(this).execute();
        new PostGetStationsClass(this).execute();
        chooseDateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == selectLineSpinner)
        {
            line_id = myLinesList.get(position).get("id");
            Toast.makeText(TreniTicketActivity.this, "Line: "+line_id, Toast.LENGTH_LONG).show();
        }
        if(spinner.getId() == R.id.fromStation)
        {
            from_station = myFromStationsList.get(position).get("id");
        }
        if(spinner.getId() == R.id.toStation)
        {
            to_station = myToStationsList.get(position).get("id");
        }

        if(spinner.getId() == R.id.classSelect)
        {
            treni_class = treniClassSelect.getSelectedItem().toString();
        }

    }
    public void searchTreniBogies(View view){
        line_id         = myLinesList.get(selectLine.getSelectedItemPosition()).get("id");
        from_station    = myFromStationsList.get(selectFromStation.getSelectedItemPosition()).get("id");
        to_station      = myToStationsList.get(selectToStation.getSelectedItemPosition()).get("id");
        treni_class     = treniClassSelect.getSelectedItem().toString();
        TreniVariables.line_id      = line_id;
        TreniVariables.from_station  = from_station;
        TreniVariables.treni_class   = treni_class;
        TreniVariables.to_station   = to_station;
        TreniVariables.safari_date  = chooseDateBox.getText().toString();
        Intent objIntent = new Intent(getApplicationContext(), BogiesListActivity.class);
        startActivity(objIntent);
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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

    private class PostGetLinesClass extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostGetLinesClass(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progress= new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Inakusanya orodha ya njia, subiri...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL_LINES);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "company_id="+LoginActivity.companyId;
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
                dialogMessage("Hakuna njia");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    if (jsonObj != null) {
                        JSONArray categories = jsonObj.getJSONArray("lines");
                        for (int i = 0; i < categories.length(); i++) {
                            JSONObject catObj = (JSONObject) categories.get(i);
                            Line cat = new Line(catObj.getInt("lineId"), catObj.getString("line_name"));
                            linesList.add(cat);
                            map = new HashMap<>();
                            map.put("id", catObj.getString("lineId"));
                            map.put("name", catObj.getString("line_name"));
                            myLinesList.add(map);
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

    private class PostGetStationsClass extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostGetStationsClass(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
           /* progress= new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Inakusanya orodha ya njia, subiri...");
            progress.show();*/
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL_STATIONS);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "company_id="+LoginActivity.companyId;
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
            //progress.dismiss();
            System.out.println(result);
            if(result.equalsIgnoreCase("NF"))
            {
                dialogMessage("Hakuna vituo");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    if (jsonObj != null) {
                        JSONArray categories = jsonObj.getJSONArray("stations");
                        for (int i = 0; i < categories.length(); i++) {
                            JSONObject catObj = (JSONObject) categories.get(i);

                            Station cat = new Station(catObj.getInt("stationId"), catObj.getString("station_name"));
                            fromStationsList.add(cat);
                            map = new HashMap<>();
                            map.put("id", catObj.getString("stationId"));
                            map.put("name", catObj.getString("station_name"));
                            myFromStationsList.add(map);

                            Station cat2 = new Station(catObj.getInt("stationId"), catObj.getString("station_name"));
                            toStationsList.add(cat2);
                            map = new HashMap<>();
                            map.put("id", catObj.getString("stationId"));
                            map.put("name", catObj.getString("station_name"));
                            myToStationsList.add(map);


                        }
                        populateStationsSpinner();
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
        for (int i = 0; i < linesList.size(); i++) {
            sources.add(linesList.get(i).getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sources);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        selectLine.setAdapter(spinnerAdapter);
    }
    /**
     * Adding spinner data
     * */
    private void populateStationsSpinner() {
        List<String> stations_from = new ArrayList<String>();
        for (int i = 0; i < fromStationsList.size(); i++) {
            stations_from.add(fromStationsList.get(i).getName());
        }

        List<String> stations_to = new ArrayList<String>();
        for (int i = 0; i < fromStationsList.size(); i++) {
            stations_to.add(fromStationsList.get(i).getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stations_from);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stations_to);
        // Drop down layout style - list view with radio button
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        selectFromStation.setAdapter(spinnerAdapter);
        selectToStation.setAdapter(spinnerAdapter2);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(objIntent);
    }


    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(TreniTicketActivity.this);
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
