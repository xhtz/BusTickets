package tz.co.xhcodes.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ReprintTicketActivity extends AppCompatActivity {
    EditText searchTicketBox;
    String searchTicketnumber;
    private ProgressDialog progress;
    private static String POST_URL;
    public static  String from_reprint = "No";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_reprint_ticket);
        searchTicketBox = findViewById(R.id.searchTicketBox);
        POST_URL        = Config.baseUrl+"index.php/tickets/searchTicketReprint";
        from_reprint    = "Yes";
    }
    public void searchTicket(View view)
    {
        searchTicketnumber = searchTicketBox.getText().toString();
        new PostClass(ReprintTicketActivity.this).execute();
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
            progress.setMessage("Tunahakiki tiketi, subiri...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "ticket_number="+searchTicketnumber+"&&agentId="+LoginActivity.agentId+"&&company_id="+LoginActivity.companyId;
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
                dialogMessage("Hakuna tiketi yenye namba: "+searchTicketnumber);
            }
            else
            {
                try
                {
                    JSONObject jsonObj  = new JSONObject(result);
                    BusVariables.noSeats            = jsonObj.getString("noSeats");
                    BusVariables.buType             = jsonObj.getString("buType");
                    BusVariables.safari_date        = jsonObj.getString("safari_date");
                    BusVariables.choosenSeatLabel   = jsonObj.getString("choosenSeatLabel");
                    BusVariables.busNumber          = jsonObj.getString("busNumber");
                    BusVariables.reportingTime      = jsonObj.getString("reportingTime");
                    BusVariables.departureTime      = jsonObj.getString("departureTime");
                    BusVariables.companyName        = jsonObj.getString("companyName");
                    BusVariables.companyAddress     = jsonObj.getString("companyAddress");
                    BusVariables.companyPhone       = jsonObj.getString("companyPhone");
                    BusVariables.TIN                = jsonObj.getString("TIN");
                    BusVariables.ticketNumber       = jsonObj.getString("ticketNumber");
                    BusVariables.timetableId        = jsonObj.getString("timetableId");
                    BusVariables.passengerName      = jsonObj.getString("passengerName");
                    BusVariables.passangerPhone     = jsonObj.getString("passangerPhone");
                    BusVariables.farePaid       = jsonObj.getString("farePaid");
                    BusVariables.passengerSex   = jsonObj.getString("passengerSex");
                    BusVariables.busLogo        = jsonObj.getString("busLogo");
                    BusVariables.busName        = jsonObj.getString("busName");
                    BusVariables.fromName       = jsonObj.getString("fromName");
                    BusVariables.toName         = jsonObj.getString("toName");
                    BusVariables.agentCode      = jsonObj.getString("agentCode");
                    BusVariables.anakoshukia    = jsonObj.getString("anakoshukia");
                    Intent intent               = new Intent(getApplicationContext(), PrintTicketActivity.class);
                    startActivity(intent);
                }
                catch (JSONException e)
                {
                    dialogMessage("Kuna tatizo, jaribu tena: "+e.getLocalizedMessage());
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(objIntent);
    }

    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(ReprintTicketActivity.this);
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
