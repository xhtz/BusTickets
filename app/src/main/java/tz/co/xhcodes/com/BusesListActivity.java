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
import android.widget.AdapterView;
import android.widget.ListView;

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

public class BusesListActivity extends AppCompatActivity {
    String POST_URL                 = Config.baseUrl+"index.php/tickets/getMobileBusesList";
    JSONArray buses_list;
    private ProgressDialog progress;
    BusAdapter busAdapter;
    ListView buses_list_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Chagua Basi "+BusVariables.safari_date);
        setContentView(R.layout.activity_buses_list);
        buses_list_view =(ListView)findViewById(R.id.buses_list_view);
        // buses_list_view.setDivider(null);
        buses_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                //String item = buses_list_view.getItemAtPosition(position).toString();
                // Toast.makeText(BusTicketActivity.this,"You selected : " + item, Toast.LENGTH_SHORT).show();
                try
                {
                    JSONObject json_obj = buses_list.getJSONObject(position);
                    String busName      = json_obj.getString("busName");
                    BusVariables.busNumber          = json_obj.getString("busNumber");
                    BusVariables.buType             = json_obj.getString("busType");
                    BusVariables.noSeats            = json_obj.getString("noSeats");
                    BusVariables.reportingTime      = json_obj.getString("reportingTime");
                    BusVariables.departureTime      = json_obj.getString("departureTime");
                    BusVariables.companyName        = json_obj.getString("companyName");
                    BusVariables.companyAddress     = json_obj.getString("companyAddress");
                    BusVariables.companyPhone       = json_obj.getString("companyPhone");
                    BusVariables.TIN                = json_obj.getString("TIN");
                    BusVariables.timetableId        = json_obj.getString("timetableId");
                    BusVariables.busLogo            = json_obj.getString("logo_name");
                    BusVariables.busName            = json_obj.getString("busName");
                    BusVariables.fromName           = json_obj.getString("fromName");
                    BusVariables.toName             = json_obj.getString("toName");
                    BusVariables.companyId          = json_obj.getString("companyId");
                    BusVariables.iwachupay_code     = json_obj.getString("iwachupay_code");
                    BusVariables.fareAmount         = json_obj.getString("fareAmount");
                    BusVariables.routeFare          = json_obj.getDouble("routeFare");
                    BusVariables.paybill_number     = json_obj.getString("paybill_number");

                    AlertDialog.Builder builder = new AlertDialog.Builder(BusesListActivity.this);
                    builder.setMessage("Umechagua "+BusVariables.busName+" - "+BusVariables.busNumber+" la tarehe "+BusVariables.safari_date+"\n"+BusVariables.fromName+"-"+BusVariables.toName);
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
                            if(LoginActivity.agentGroup.equalsIgnoreCase("Client") || LoginActivity.agentGroup.equalsIgnoreCase("All"))
                            {
                                Intent intent = new Intent(parent.getContext(), ChooseSeatActivity.class);
                                parent.getContext().startActivity(intent);
                            }
                            else
                            {
                                selectedBusDialog();
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
                catch (JSONException e)
                {
                    //e.printStackTrace();
                    Log.d("ERROR", "Json error: "+e.getLocalizedMessage());
                }
            }
        });
        new PostClass(this).execute();
    }
    public  void selectedBusDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(BusesListActivity.this);
        builder.setMessage("Unataka kufanya nini?");
        builder.setCancelable(false);
        builder.setNegativeButton("Angalia Tiketi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //view agents tickets list
                Intent intent = new Intent(BusesListActivity.this, ViewAgentsTicketsActivity.class);
                BusesListActivity.this.startActivity(intent);
            }
        });
        builder.setPositiveButton("Chagua Siti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(BusesListActivity.this, ChooseSeatActivity.class);
                BusesListActivity.this.startActivity(intent);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(LoginActivity.agentGroup.equalsIgnoreCase("Client") || LoginActivity.agentGroup.equalsIgnoreCase("All")) {
            Intent objIntent = new Intent(getApplicationContext(), BusTicketActivity.class);
            startActivity(objIntent);
        }
        else
        {
            Intent objIntent = new Intent(getApplicationContext(), BusTicketAgentActivity.class);
            startActivity(objIntent);
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
            progress.setMessage("Inakusanya orodha ya magari, subiri...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "recordDate="+BusVariables.safari_date+"&&company_id="+BusVariables.companyId+"&&agentId="+LoginActivity.agentId+"&&bus_number="+LoginActivity.bus_number+"&&route_id="+BusVariables.routeId+"&&agent_group="+LoginActivity.agentGroup;
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
                if(busAdapter !=null)
                {
                    busAdapter.clear();
                    busAdapter.notifyDataSetChanged();
                }
                dialogMessage("Hakuna magari kwa tarehe uliyochagua");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    buses_list              = jsonObj.getJSONArray("buses");
                    displayBusesList(buses_list);
                }
                catch (JSONException e)
                {
                    dialogMessage("Jaribu tena, hakikisha kwanza una intanenti");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }
    private void displayBusesList(JSONArray result){
        //display chat line here
        if(result !=null)
        {
            if(result.length()>0)
            {
                if(busAdapter==null)
                {
                    ArrayList<Bus> buses_array = Bus.fromJson(result);
                    busAdapter = new BusAdapter(getBaseContext(), buses_array);
                    buses_list_view.setAdapter(busAdapter);
                }
                else
                {
                    busAdapter.clear();
                    ArrayList<Bus> buses_array = Bus.fromJson(result);
                    busAdapter.addAll(buses_array);
                    busAdapter.notifyDataSetChanged();
                }
            }

        }
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(BusesListActivity.this);
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
