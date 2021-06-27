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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.TimeZone;


public class ViewCollectionsActivity extends AppCompatActivity {
    String POST_URL     = Config.baseUrl+"index.php/tickets/getAgentCollections";
    JSONArray tickets_list;
    private ProgressDialog progress;
    TicketAdapter ticketAdapter;
    ListView tickets_list_view;
    TextView sum_view;
    View tickets_footer_view;

    EditText fromDateBox, toDateBox;
    String fromDate, toDate;
    Calendar c;
    private int mYear;
    private int mMonth;
    private int mDay;
    TinyDB tinydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Makusanyo Yangu");
        setContentView(R.layout.activity_view_collections);
        tickets_footer_view = getLayoutInflater().inflate(R.layout.agent_tickets_footer, null);
        sum_view            = tickets_footer_view.findViewById(R.id.total_fare_view);
        tickets_list_view   = (ListView)findViewById(R.id.tickets_list_view);
        fromDateBox         = (EditText)findViewById(R.id.chooseDateBox);
        toDateBox           = (EditText)findViewById(R.id.choosetoDateBox);
        tinydb              = new TinyDB(this);
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        am.setTimeZone("Africa/Nairobi");
        c               = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"));
        mYear           = c.get(Calendar.YEAR);
        mMonth          = c.get(Calendar.MONTH);
        mDay            = c.get(Calendar.DAY_OF_MONTH);
        fromDateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(fromDateBox);
            }
        });
        toDateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(toDateBox);
            }
        });
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(objIntent);
    }
    public void showDatePicker(final EditText dateBox)
    {
        DatePickerDialog dpd = new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateBox.setText(dayOfMonth + "-"+ (monthOfYear + 1) + "-" + year);

            }
        }, mYear, mMonth, mDay);
        //un comment line below to set maximum date to current date for android 3.0 and above
        dpd.getDatePicker().setMaxDate(new Date().getTime() - 10000);
        dpd.show();
    }

    public void searchCollections(View view){
        fromDate    = fromDateBox.getText().toString();
        toDate      = toDateBox.getText().toString();
        if(!fromDate.equalsIgnoreCase("") && !toDate.equalsIgnoreCase("")){
            new PostClass(ViewCollectionsActivity.this).execute();
        }
        else
        {
            dialogMessage("Choose from date and to date");
        }
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
            progress.setMessage("Inakusanya orodha ya makusanyo, subiri...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "from_date="+fromDate+"&&to_date="+toDate+"&&company_id="+LoginActivity.companyId+"&&agentId="+LoginActivity.agentId+"&&bus_agent_level="+LoginActivity.bus_agent_level+"&&konda="+LoginActivity.konda;
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
                if(ticketAdapter !=null)
                {
                    ticketAdapter.clear();
                    ticketAdapter.notifyDataSetChanged();
                }
                dialogMessage("Hakuna tiketi zilizopatikana");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    tickets_list              = jsonObj.getJSONArray("ticketdetails");
                    displayTicketList(tickets_list);
                }
                catch (JSONException e)
                {
                    dialogMessage("Jaribu tena");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }
    private void displayTicketList(JSONArray result){
        //display chat line here
        if(result !=null)
        {
            if(result.length()>0)
            {
                if(ticketAdapter==null)
                {
                    ArrayList<Ticket> tickets_array = Ticket.fromJson(result);
                    ticketAdapter = new TicketAdapter(getBaseContext(), tickets_array);
                    tickets_list_view.setAdapter(ticketAdapter);
                }
                else
                {
                    ticketAdapter.clear();
                    ArrayList<Ticket> tickets_array = Ticket.fromJson(result);
                    ticketAdapter.addAll(tickets_array);
                    ticketAdapter.notifyDataSetChanged();
                }
                try
                {
                    JSONObject json_obj = tickets_list.getJSONObject(0);
                    BusVariables.total_fare = json_obj.getString("totalFare");
                    sum_view.setText(BusVariables.total_fare);
                    tickets_list_view.addFooterView(tickets_footer_view);
                }
                catch (Exception e)
                {
                    //ingore total label
                }

            }

        }
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewCollectionsActivity.this);
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
