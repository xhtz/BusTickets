package tz.co.xhcodes.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;
import java.util.List;

public class RegisterClientActivity extends AppCompatActivity {
    String POST_URL_TOWNS       = Config.baseUrl+"index.php/drivers/getTownsList";
    EditText fullname_box, simu_box, pass_box;
    String appUserFullName, appUserPhone, appUserPassword, POST_URL, deviceId, townid;
    private ProgressDialog progress;
    private ArrayList<TownObject> townsList;
    final ArrayList<HashMap<String, String>> myList = new ArrayList<>();
    HashMap<String, String> map;
    Spinner select_town_spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_register_client);
        fullname_box    = findViewById(R.id.fullname_box);
        simu_box        = findViewById(R.id.simu_box);
        pass_box        = findViewById(R.id.pass_box);
        townsList       = new ArrayList<>();
        select_town_spinner   = findViewById(R.id.select_town_spinner);
        new PostGetTowns(this).execute();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), LoginClientActivity.class);
        startActivity(objIntent);
    }
    private class PostGetTowns extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostGetTowns(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progress= new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Inakusanya orodha ya miji, subiri...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL_TOWNS);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "appUserId="+LoginActivity.appUserId;
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
            System.out.println("RESULT: "+result);
            if(result.equalsIgnoreCase("NF"))
            {
                dialogMessage("Hakuna miji");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    if (jsonObj != null) {
                        JSONArray categories = jsonObj.getJSONArray("towns");
                        for (int i = 0; i < categories.length(); i++) {
                            JSONObject catObj = (JSONObject) categories.get(i);
                            TownObject cat = new TownObject(catObj.getString("name"), catObj.getString("id"));
                            townsList.add(cat);
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
        for (int i = 0; i < townsList.size(); i++) {
            sources.add(townsList.get(i).getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sources);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        select_town_spinner.setAdapter(spinnerAdapter);
    }
    public void registerCient(View view)
    {
        deviceId       = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        appUserFullName = fullname_box.getText().toString();
        appUserPhone    = simu_box.getText().toString();
        appUserPassword = pass_box.getText().toString();
        townid          = townsList.get(select_town_spinner.getSelectedItemPosition()).getId();
        POST_URL        = Config.baseUrl + "index.php/appusers/registerUser";
        if(appUserFullName.length()>1 && appUserPassword.length()>1 && (appUserPhone.length()==12 || appUserPhone.length()==10))
        {
            new PostClass(RegisterClientActivity.this).execute();
        }
        else
        {
            dialogMessage("Weka namba ya simu sahihi, jina na neno la siri");
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
            progress.setMessage("Processing registration....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "appUserFullName="+appUserFullName+"&&appUserPhone="+appUserPhone+"&&appUserPassword="+appUserPassword+"&&deviceId="+deviceId+"&&townid="+townid;
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
            if(result.equalsIgnoreCase("FD"))
            {
                dialogMessage("Namba ya simu imeshasajiliwa");
            }
            else
            {
                try
                {
                    JSONObject jsonObj           = new JSONObject(result);
                    String status                = jsonObj.getString("status");
                    if(status.equalsIgnoreCase("Saved")) {
                        Toast.makeText(getApplicationContext(), "Umefanikiwa kusajili, Ingia sasa", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), LoginClientActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        dialogMessage("Jaribu tena, kwa sasa hujafanikiwa");
                    }
                }
                catch (JSONException e)
                {
                    dialogMessage("Jaribu tena, angalia kama una intanenti");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterClientActivity.this);
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
