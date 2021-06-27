package tz.co.xhcodes.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lvrenyang.myprinter.WorkService;

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

public class ChooseSeatActivity extends AppCompatActivity {
    String POST_URL              = Config.baseUrl+"index.php/tickets/getMobileBusSeats";
    String POST_URL_RELEASE      = Config.baseUrl+"index.php/tickets/releaseSeat";
    String POST_URL_BOOKING_PAID = Config.baseUrl+"index.php/tickets/bookingPaid";
    JSONArray seats_list;
    String timetable_id, seat_label;
    private ProgressDialog progress;
    public static String choose_seat ="No";
    TinyDB tinyDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Chagua Siti "+BusVariables.busNumber);
        int layout_id = getResources().getIdentifier("bus_"+BusVariables.noSeats+"_"+BusVariables.buType+"_layout", "layout", getPackageName());
        setContentView(layout_id);
        tinyDB = new TinyDB(this);
        new PostClass(this).execute();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), BusesListActivity.class);
        startActivity(objIntent);
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
            progress.setMessage("Inakusanya orodha ya siti...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "bus_number="+BusVariables.busNumber+"&&company_id="+BusVariables.companyId+"&&agentId="+LoginActivity.agentId+"&&safari_date="+BusVariables.safari_date+"&&timetable_id="+BusVariables.timetableId;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dStream        = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                final StringBuilder output = new StringBuilder("");
                BufferedReader br   = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line         = "";
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
                dialogMessage("Hakuna siti kwa gari hili");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    seats_list              = jsonObj.getJSONArray("seats");
                    displaySeatLabels(seats_list);
                }
                catch (JSONException e)
                {
                    dialogMessage("Kuna tatizo, jaribu tena");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }
    private class PostReleaseTicketClass extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostReleaseTicketClass(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progress= new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Taarifa za siti zinatumwa, subiri...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL_RELEASE);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "timetable_id="+timetable_id+"&&company_id="+BusVariables.companyId+"&&agentId="+LoginActivity.agentId+"&&seat_label="+seat_label;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dStream        = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                final StringBuilder output = new StringBuilder("");
                BufferedReader br   = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line         = "";
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
            if(result.equalsIgnoreCase("FAILED"))
            {
                dialogMessage("Imeshindikana, jaribu tena!");
            }
            else if(result.equalsIgnoreCase("DONE"))
            {
               // dialogMessage("Umefanikiwa kuruhusu siti!");
                Intent intent = new Intent(ChooseSeatActivity.this, ChooseSeatActivity.class);
                startActivity(intent);
            }
        }
    }
    private class PostBookingPaidClass extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostBookingPaidClass(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progress= new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Taarifa za siti zinatumwa, subiri...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL_BOOKING_PAID);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "ticketNumber="+params[0]+"&&agentId="+LoginActivity.agentId;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dStream        = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                final StringBuilder output = new StringBuilder("");
                BufferedReader br   = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line         = "";
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
            if(result.equalsIgnoreCase("FAILED"))
            {
                dialogMessage("Imeshindikana, jaribu tena!");
            }
            else if(result.equalsIgnoreCase("DONE"))
            {
                //dialogMessage("Umefanikiwa kuhifadhi malipo!");
               // ChooseSeatActivity.this.finish();
                Intent intent = new Intent(ChooseSeatActivity.this, ChooseSeatActivity.class);
                startActivity(intent);
            }
        }
    }
    public  void selectedSeatDialog(String seatLabel){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseSeatActivity.this);
        builder.setMessage("Umechagua siti namba: " + seatLabel + "");
        builder.setCancelable(false);
        builder.setNegativeButton("Sitisha", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Endelea", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!LoginActivity.agentGroup.equalsIgnoreCase("Client") && !LoginActivity.agentGroup.equalsIgnoreCase("All")) {
                    Intent intent = new Intent(ChooseSeatActivity.this, RegisterPassangerActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(ChooseSeatActivity.this, RegisterPassangerSelfActivity.class);
                    startActivity(intent);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public  void displaySeatLabels(JSONArray seats){
        for (int i = 0; i < seats.length(); i++) {
            try
            {
                final String seatLabel      = seats.getJSONObject(i).getString("seatLabel");
                final String seatNumber     = seats.getJSONObject(i).getString("seatNumber");
                final String passengerName  = seats.getJSONObject(i).getString("passengerName");
                final String seatId         = seats.getJSONObject(i).getString("seatId");
                final String seatStatus     = seats.getJSONObject(i).getString("seatStatus");
                final String paymentStatus  = seats.getJSONObject(i).getString("paymentStatus");
                final String agent_id       = seats.getJSONObject(i).getString("agent_id");
                final String ticket_number      = seats.getJSONObject(i).getString("ticket_number");
                final String bus_seat_status    = seats.getJSONObject(i).getString("bus_seat_status"); //seat status set by system admin not booking status
                int id                      = getResources().getIdentifier("S_"+BusVariables.noSeats+""+seatNumber, "id", getPackageName());
                final Button seatBtn        = (Button)findViewById(id);
                seatBtn.setText(seatLabel);
                //seatBtn.setBackgroundColor(getResources().getColor(R.color.colorMainBg));
                seatBtn.setBackgroundColor(Color.WHITE);
                seatBtn.setTextColor(Color.BLACK);
                setMargins(seatBtn,2,2,2,2);
                if(seatStatus.equalsIgnoreCase("available") && bus_seat_status.equalsIgnoreCase("Enabled")) {
                    seatBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BusVariables.choosenSeatLabel = seatLabel;
                           if(LoginActivity.agentGroup.equalsIgnoreCase("Client") || LoginActivity.agentGroup.equalsIgnoreCase("All"))
                           {
                               selectedSeatDialog(seatLabel);
                           }
                           else
                           {
                               if(WorkService.workThread.isConnected())
                               {
                                   selectedSeatDialog(seatLabel);
                                   tinyDB.putString("has_printa", "Yes");
                               }
                               else
                               {
                                   notConnectedDialog("Hujaunganisha Printa", seatLabel);
                               }
                           }

                        }
                    });
                }
                else
                {
                        if(paymentStatus.equalsIgnoreCase("Pending") && bus_seat_status.equalsIgnoreCase("Enabled"))
                        {
                            seatBtn.setBackgroundColor(Color.CYAN);
                        }
                        else
                        {
                            seatBtn.setBackgroundColor(Color.BLACK);
                            seatBtn.setTextColor(Color.WHITE);
                        }

                        seatBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(paymentStatus.equalsIgnoreCase("Pending") && !LoginActivity.agentGroup.equalsIgnoreCase("Client") && agent_id.equalsIgnoreCase(LoginActivity.agentId)) {
                                    payBookingDialog(ticket_number);
                                }
                                else
                                {
                                    if (LoginActivity.konda.equalsIgnoreCase("No")  && !LoginActivity.bus_agent_level.equalsIgnoreCase("Inspector"))
                                    {
                                        if (LoginActivity.agentGroup.equalsIgnoreCase("Client"))
                                        {
                                            dialogMessage("Siti namba " + seatLabel + " imeshachukuliwa, chagua nyingine!");
                                        }
                                        else
                                        {
                                            if(bus_seat_status.equalsIgnoreCase("Enabled"))
                                            {
                                                dialogMessage("Siti namba " + seatLabel + " imeshachukuliwa na: " + passengerName + ", chagua nyingine!");
                                            }
                                            else
                                            {
                                                dialogMessage("Siti namba " + seatLabel + " imewekwa kwa kazi maalumu!");
                                            }
                                        }
                                    }
                                    else if (LoginActivity.konda.equalsIgnoreCase("Yes") || LoginActivity.bus_agent_level.equalsIgnoreCase("Inspector"))
                                    {
                                        if(bus_seat_status.equalsIgnoreCase("Enabled"))
                                        {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ChooseSeatActivity.this);
                                            builder.setMessage("Ruhusu siti namba " + seatLabel + " iliyochukuliwa na: " + passengerName + ", iwe wazi?");
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
                                                    //post data for releasing ticket
                                                    timetable_id = BusVariables.timetableId;
                                                    seat_label = seatLabel;
                                                    new PostReleaseTicketClass(ChooseSeatActivity.this).execute();
                                                }
                                            });
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                        }
                                        else
                                        {
                                            dialogMessage("Siti namba " + seatLabel + " imewekwa kwa kazi maalumu!");
                                        }
                                    }
                                    else
                                    {
                                        dialogMessage("Siti namba " + seatLabel + " imeshachukuliwa, chagua nyingine!");
                                    }
                                }
                            }
                        });
                }
            }
            catch (JSONException e)
            {
                System.out.println("ERROR: "+e.getMessage());
            }
        }
    }
    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseSeatActivity.this);
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
    void notConnectedDialog(String msg, final String seatLabel){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseSeatActivity.this);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setNegativeButton("Endelea Bila Printa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.cancel();
                tinyDB.putString("has_printa", "No");
                selectedSeatDialog(seatLabel);
            }
        });
        builder.setPositiveButton("Unganisha Printa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choose_seat = "Yes";
                tinyDB.putString("has_printa", "Yes");
                Intent intent = new Intent(ChooseSeatActivity.this, SearchBTActivity.class);
                startActivity(intent);
            }
        });
        builder.setNeutralButton("Sitisha", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    void payBookingDialog(final String ticket_number){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseSeatActivity.this);
        builder.setMessage("Tayari aliye book amelipia nauli?");
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
               //process booking mark it paid
                new PostBookingPaidClass(ChooseSeatActivity.this).execute(ticket_number);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
