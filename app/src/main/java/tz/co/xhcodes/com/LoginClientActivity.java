package tz.co.xhcodes.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class LoginClientActivity extends AppCompatActivity {
    private ProgressDialog progress;
    private static String POST_URL      = "";
    EditText login_phone_box, login_pass_box;
    String appUserPhone, appUserPassword;
    TinyDB tinydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_login_client);
        login_phone_box = findViewById(R.id.login_phone_box);
        login_pass_box  = findViewById(R.id.login_pass_box);
        tinydb = new TinyDB(this);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), LauncherActivity.class);
        startActivity(objIntent);
    }
    public void registerClient(View view){
        Intent intent = new Intent(LoginClientActivity.this, RegisterClientActivity.class);
        startActivity(intent);
    }
    public void recoverPassword(View view){
        Intent intent = new Intent(LoginClientActivity.this, RecoverClientAcPasswordtivity.class);
        startActivity(intent);
    }

    public void loginCient(View view){
        if(checkInternet() !=null) {
            appUserPhone = login_phone_box.getText().toString();
            appUserPassword = login_pass_box.getText().toString();
            POST_URL = Config.baseUrl + "index.php/appusers/userLogin";
            if (appUserPassword.length() > 0 && (appUserPhone.length() == 12 || appUserPhone.length() == 10)) {
                new PostClass(LoginClientActivity.this).execute();
            } else {
                dialogMessage("Weka namba ya simu sahihi na neno la siri");
            }
        }
        else
        {
            dialogMessage("Wezesha intanenti kwanza");
        }
    }
    public NetworkInfo checkInternet(){
        ConnectivityManager cm  = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni          = cm.getActiveNetworkInfo();
        return ni;
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
                String urlParameters            = "appUserPhone="+appUserPhone+"&&appUserPassword="+appUserPassword;
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
                dialogMessage("Hakuna mtumiaji mwenye taarifa ulizoweka");
            }
            else
            {
                try
                {
                    JSONObject jsonObj           = new JSONObject(result);
                    LoginActivity.appUserPhone   =jsonObj.getString("appUserPhone");
                    LoginActivity.appUserId      =jsonObj.getString("appUserId");
                    LoginActivity.appUserFullName =jsonObj.getString("appUserFullName");
                    tinydb.putString("appUserId", jsonObj.getString("appUserId"));
                    tinydb.putString("townid", jsonObj.getString("townid"));
                    tinydb.putString("townname", jsonObj.getString("townname"));
                    tinydb.putString("appUserFullName", jsonObj.getString("appUserFullName"));
                    tinydb.putString("appUserPhone", jsonObj.getString("appUserPhone"));
                    tinydb.putString("loginStatus", "logged_in");
                    Intent intent                = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                catch (JSONException e)
                {
                    dialogMessage("Jaribu tena tafadhari, angalia kama una intanenti");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginClientActivity.this);
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
