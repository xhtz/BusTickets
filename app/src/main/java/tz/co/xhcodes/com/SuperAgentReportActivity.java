package tz.co.xhcodes.com;

import android.app.AlarmManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class SuperAgentReportActivity extends AppCompatActivity {
    String POST_URL_GETBUSES    = Config.baseUrl+"index.php/tickets/getBusesList";
    Calendar c;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMins;
    private int mSeconds;
    EditText chooseDateBox;
    public static String rdate, bus_number;
    private ProgressDialog progress;
    Spinner selectBus;
    private ArrayList<BusObject> busesList;
    final ArrayList<HashMap<String, String>> mybusesList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_super_agent_report);
        selectBus       = findViewById(R.id.selectBusSpinner);
        chooseDateBox   = findViewById(R.id.chooseDateBox);
        busesList       = new ArrayList<>();
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        am.setTimeZone("Africa/Nairobi");
        c               = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"));
        mYear           = c.get(Calendar.YEAR);
        mMonth          = c.get(Calendar.MONTH);
        mDay            = c.get(Calendar.DAY_OF_MONTH);
        chooseDateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        new PostGetBusesClass(this).execute();
    }
    public void  searchSuperAgentTickets(View view)
    {
        rdate       = chooseDateBox.getText().toString();
        bus_number  = mybusesList.get(selectBus.getSelectedItemPosition()).get("busNumber");
       if(rdate.length()>0)
       {
           Intent intent = new Intent(SuperAgentReportActivity.this, ViewSuperAgentsTicketsActivity.class);
           startActivity(intent);
       }
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
        //dpd.getDatePicker().setMinDate(new Date().getTime() - 10000);
        dpd.show();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(objIntent);
    }
    private class PostGetBusesClass extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostGetBusesClass(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progress= new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Inakusanya orodha ya magari, subiri...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL_GETBUSES);
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
                dialogMessage("Hakuna magari yaliyopatikana");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    if (jsonObj != null) {
                        JSONArray categories = jsonObj.getJSONArray("buses");
                        for (int i = 0; i < categories.length(); i++) {
                            JSONObject catObj = (JSONObject) categories.get(i);
                            BusObject cat = new BusObject(catObj.getString("busNumber"), catObj.getString("busName"));
                            busesList.add(cat);
                            map = new HashMap<>();
                            map.put("busNumber", catObj.getString("busNumber"));
                            map.put("busName", catObj.getString("busName"));
                            mybusesList.add(map);
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
        for (int i = 0; i < busesList.size(); i++) {
            sources.add(busesList.get(i).getBusName()+"-"+busesList.get(i).getBusNumber());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sources);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        selectBus.setAdapter(spinnerAdapter);
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(SuperAgentReportActivity.this);
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
