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

public class BogiesListActivity extends AppCompatActivity {
    String POST_URL                 = Config.baseUrl+"index.php/tickets/getBogiesList";
    JSONArray bogies_list;
    private ProgressDialog progress;
    BehewAdapter bogieAdapter;
    ListView bogies_list_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Chagua Behewa");
        setContentView(R.layout.activity_bogies_list);
        bogies_list_view =(ListView)findViewById(R.id.bogies_list_view);
        new PostClass(this).execute();
        bogies_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                //String item = buses_list_view.getItemAtPosition(position).toString();
                // Toast.makeText(BusTicketActivity.this,"You selected : " + item, Toast.LENGTH_SHORT).show();
                try
                {
                    JSONObject json_obj = bogies_list.getJSONObject(position);
                    String bogie_number             = json_obj.getString("bogie_number");
                    TreniVariables.line_id          = json_obj.getString("line_id");
                    TreniVariables.safari_date      = json_obj.getString("safari_date");
                    TreniVariables.bogie_number     = json_obj.getString("bogie_number");
                    TreniVariables.bogie_id         = json_obj.getString("bogieId");
                    TreniVariables.bogie_id              = json_obj.getString("bogieId");
                    TreniVariables.timetable_id          = json_obj.getString("timetable_id");
                    TreniVariables.adult_fare_amount     = json_obj.getString("adult_fare_amount");
                    TreniVariables.child_fare_amount     = json_obj.getString("child_fare_amount");
                    TreniVariables.no_seats             = json_obj.getString("no_seats");


                    AlertDialog.Builder builder = new AlertDialog.Builder(BogiesListActivity.this);
                    builder.setMessage("Umechagua Behewa: "+bogie_number);
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
                            if(LoginActivity.agentGroup.equalsIgnoreCase("Client")) {
                                Intent intent = new Intent(parent.getContext(), TreniPassangerActivity.class);
                                parent.getContext().startActivity(intent);
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
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), TreniTicketActivity.class);
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
                String urlParameters            = "line_id="+TreniVariables.line_id+"&&from_station="+TreniVariables.from_station+"&&to_station="+TreniVariables.to_station+"&&safari_date="+TreniVariables.safari_date+"&&treni_class="+TreniVariables.treni_class;
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
                if(bogieAdapter !=null)
                {
                    bogieAdapter.clear();
                    bogieAdapter.notifyDataSetChanged();
                }
                dialogMessage("Hakuna behewa");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    bogies_list              = jsonObj.getJSONArray("bogies");
                    displayBogiesList(bogies_list);
                }
                catch (JSONException e)
                {
                    dialogMessage("Jaribu tena, hakikisha kwanza una intanenti");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }
    private void displayBogiesList(JSONArray result){
        //display chat line here
        if(result !=null)
        {
            if(result.length()>0)
            {
                if(bogieAdapter==null)
                {
                    ArrayList<Behewa> bogies_array = Behewa.fromJson(result);
                    bogieAdapter = new BehewAdapter(getBaseContext(), bogies_array);
                    bogies_list_view.setAdapter(bogieAdapter);
                }
                else
                {
                    bogieAdapter.clear();
                    ArrayList<Behewa> buses_array = Behewa.fromJson(result);
                    bogieAdapter.addAll(buses_array);
                    bogieAdapter.notifyDataSetChanged();
                }
            }

        }
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(BogiesListActivity.this);
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
