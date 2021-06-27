package tz.co.xhcodes.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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

public class LoginActivity extends AppCompatActivity {
    private ProgressDialog progress;
    private static String POST_URL      = "";
    String password, loginName;
    EditText etLoginName, etPassword;
    public static String agentCode      = "";
    public static String agentPhone     = "";
    public static String agentName      = "";
    public static String companyId      = "";
    public static String agentId        = "";
    public static String scanTicket     = "";
    public static String agentGroup     = "";
    public static String bus_agent_level  = "";
    public static String appUserPhone     = "";
    public static String appUserFullName  = "";
    public static String appUserId      = "0";
    public static String bus_number     = "NA"; //for specifying if agent is assigned individual bus to deal with
    public static String konda          = "";
    public static String superAgent     = "No";
    public static double unpaid_fee     = 0;
    public static String iwachu_agent_id  = "0";
    TinyDB tinydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_login);
        etLoginName = findViewById(R.id.login_name_box);
        etPassword  = findViewById(R.id.login_pass_box);
        tinydb      = new TinyDB(this);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), LauncherActivity.class);
        startActivity(objIntent);
    }
    public void loginAgent(View view)
    {
        if(checkInternet() !=null)
        {
            password = etPassword.getText().toString();
            loginName = etLoginName.getText().toString();
            POST_URL = Config.baseUrl + "index.php/agentspos/agentsLogin";
            if(password.length() > 0 && loginName.length() > 0)
            {
                new PostClass(this).execute();
            }
            else
            {
                dialogMessage("Ingiza jina la kuingilia na neno la siri");
            }
        }
        else
        {
            dialogMessage("Wezesha intanenti kwanza");
        }
    }
    public  void requestNewPassword(View view)
    {
        Intent intent = new Intent(LoginActivity.this, RequestPasswordActivity.class);
        startActivity(intent);
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
            progress.setMessage("Processing login...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "loginName="+loginName+"&&password="+password;
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
                dialogMessage("Hakuna wakala mwenye taarifa ulizoweka");
            }
            else
            {
                try
                {
                    JSONObject jsonObj            = new JSONObject(result);
                    LoginActivity.agentCode       =jsonObj.getString("agentCode");
                    LoginActivity.agentPhone      =jsonObj.getString("agentPhone");
                    LoginActivity.agentId         =jsonObj.getString("agentId");
                    LoginActivity.iwachu_agent_id =jsonObj.getString("agentId");
                    LoginActivity.agentName       =jsonObj.getString("agentName");
                    LoginActivity.companyId       =jsonObj.getString("company_id");
                    LoginActivity.scanTicket      =jsonObj.getString("scan_ticket");
                    LoginActivity.agentGroup      =jsonObj.getString("agent_group");
                    LoginActivity.bus_number      =jsonObj.getString("bus_number");
                    LoginActivity.konda           =jsonObj.getString("konda");
                    LoginActivity.bus_agent_level =jsonObj.getString("bus_agent_level");
                    LoginActivity.superAgent     =jsonObj.getString("superAgent");
                    double unpaidfee             = jsonObj.getDouble("unpaid_fee");
                    double bill_kesho            = jsonObj.getDouble("bill_kesho");
                    double ticket_fee            = jsonObj.getDouble("ticket_fee");
                    tinydb.putDouble("unpaid_fee", unpaidfee);
                    tinydb.putDouble("bill_kesho", bill_kesho);
                    tinydb.putDouble("ticket_fee", ticket_fee);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
    public NetworkInfo checkInternet(){
        ConnectivityManager cm  = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni          = cm.getActiveNetworkInfo();
        return ni;
    }
}
