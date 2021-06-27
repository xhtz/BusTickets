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
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.TimeZone;

import static tz.co.xhcodes.com.R.id.chooseDateBox;

public class ClientTicketsActivity extends AppCompatActivity {
    String POST_URL     = Config.baseUrl+"index.php/appusers/userTickets";
    JSONArray tickets_list;
    private ProgressDialog progress;
    TicketAdapterClient ticketAdapterClient;
    ListView tickets_list_view;
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
        getSupportActionBar().setTitle("Tiketi Zangu");
        setContentView(R.layout.activity_client_tickets);
        tickets_list_view   = findViewById(R.id.mytickets_list_view);
        fromDateBox         = findViewById(chooseDateBox);
        toDateBox           = findViewById(R.id.choosetoDateBox);
        tinydb              = new TinyDB(this);
        //AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
       // am.setTimeZone("Africa/Nairobi");
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
        tickets_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    Ticket ticket           = ticketAdapterClient.getItem(position);
                    String ticketNumber     = ticket.ticketNumber;
                    String payment_status   = ticket.payment_status;
                    String status           = ticket.status;
                    String payment_token    = ticket.payment_token;
                    String farePaid         = ticket.farePaid;
                    String paybill_number   = ticket.paybill_number;
                    String iwachupay_code   = ticket.iwachupay_code;
                    if(payment_status.equalsIgnoreCase("Pending") && !status.equalsIgnoreCase("Expired")){
                        BusVariables.ticketNumber   = ticketNumber;
                        BusVariables.paybill_number = paybill_number;
                        BusVariables.routeFare      = Double.parseDouble(farePaid);
                        BusVariables.agentCode      = LoginActivity.agentCode;
                        BusVariables.payment_token  = ticketNumber+""+iwachupay_code;
                        AlertDialog.Builder builder = new AlertDialog.Builder(ClientTicketsActivity.this);
                        builder.setMessage("Lipia tiketi namba "+ticketNumber+"?");
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
                                Intent intent               = new Intent(getApplicationContext(), ChoosePaymentMethodActivity.class);
                                startActivity(intent);
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                    else
                    {
                        if(!status.equalsIgnoreCase("Expired") && payment_status.equalsIgnoreCase("Paid")) {
                            dialogMessage("Tayari umeshalipia tiketi hii");
                        }
                        else
                        {
                            dialogMessage("Muda wa Kulipa umeshapita");
                        }
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(ClientTicketsActivity.this, "Jaribu tena tafadhari", Toast.LENGTH_LONG).show();
                }



            }
        });
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(objIntent);
    }

    public void searchMyTickets(View view){
        fromDate    = fromDateBox.getText().toString();
        toDate      = toDateBox.getText().toString();
        if(fromDate.length()>0 && toDate.length()>0)
        {
            new PostClass(ClientTicketsActivity.this).execute();
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
            progress.setMessage("Collecting tickets list...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "appUserId="+tinydb.getString("appUserId")+"&&fromDate="+fromDate+"&&toDate="+toDate;
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
                if(ticketAdapterClient !=null)
                {
                    ticketAdapterClient.clear();
                    ticketAdapterClient.notifyDataSetChanged();
                }
                dialogMessage("No tickets found");
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
                    dialogMessage("Try again please");
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
                if(ticketAdapterClient==null)
                {
                    ArrayList<Ticket> tickets_array = Ticket.fromJson(result);
                    ticketAdapterClient = new TicketAdapterClient(getBaseContext(), tickets_array);
                    tickets_list_view.setAdapter(ticketAdapterClient);
                }
                else
                {
                    ticketAdapterClient.clear();
                    ArrayList<Ticket> tickets_array = Ticket.fromJson(result);
                    ticketAdapterClient.addAll(tickets_array);
                    ticketAdapterClient.notifyDataSetChanged();
                }

            }

        }
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(ClientTicketsActivity.this);
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
    public void showDatePicker(final EditText dateBox)
    {
        DatePickerDialog dpd = new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateBox.setText(dayOfMonth + "-"+ (monthOfYear + 1) + "-" + year);

            }
        }, mYear, mMonth, mDay);
        //un comment line below to set minimum date to current date for android 3.0 and above
        //dpd.getDatePicker().setMinDate(new Date().getTime() - 10000);
        dpd.show();
    }
}
