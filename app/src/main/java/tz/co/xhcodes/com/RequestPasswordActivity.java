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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestPasswordActivity extends AppCompatActivity {
    private ProgressDialog progress;
    private static String POST_URL  = "";
    private static String POST_URL_TWO  = "";
    String token, newpassword, phone;
    EditText phone_box, token_box, new_tokenpassword_box;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_request_password);
        phone_box = (EditText)findViewById(R.id.phone_box);
        token_box = (EditText)findViewById(R.id.token_box);
        new_tokenpassword_box = (EditText)findViewById(R.id.new_tokenpassword_box);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(objIntent);
    }
    public void requestToken(View view)
    {
        POST_URL        = Config.baseUrl + "index.php/agentspos/requestChangeForgottenPassword";
        phone           = phone_box.getText().toString();
        if(phone.length()==12)
        {
            new PostClass(RequestPasswordActivity.this).execute();
        }
        else
        {
            dialogMessage("Weka namba ya simu katika mfumo sahihi");
        }
    }
    public void changePwd(View view)
    {
        POST_URL_TWO        = Config.baseUrl + "index.php/agentspos/changeAgentPasswordByToken";
        token               = token_box.getText().toString();
        newpassword         = new_tokenpassword_box.getText().toString();
        if(token.length()>0 && newpassword.length()>3)
        {
            new PostChangePwdClass(RequestPasswordActivity.this).execute();
        }
        else
        {
            dialogMessage("Weka password mpya na namba ya siri uliyotumiwa");
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
                String urlParameters            = "phone="+phone;
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
                dialogMessage("Namba ya simu siyo sahihi");
            }
            else
            {
                try
                {
                    JSONObject jsonObj     = new JSONObject(result);
                    String status          =jsonObj.getString("status");
                    if(status.equalsIgnoreCase("Success"))
                    {
                        dialogMessage("Namba ya siri imetumwa kwenye simu yako, subiri");
                    }
                    else
                    {
                        dialogMessage("Imeshindikana kupata namba ya siri, jaribu tena");
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
    private class PostChangePwdClass extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostChangePwdClass(Context c)
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
                URL url = new URL(POST_URL_TWO);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "token="+token+"&&newPwd="+newpassword;
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
                dialogMessage("Namba ya siri uliyoweka siyo sahihi, angalia meseji yako tena");
            }
            else
            {
                try
                {
                    JSONObject jsonObj     = new JSONObject(result);
                    String status          =jsonObj.getString("status");
                    if(status.equalsIgnoreCase("Success"))
                    {
                        Toast.makeText(RequestPasswordActivity.this, "Ingia kwa password mpya", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RequestPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        dialogMessage("Imeshindikana kubadili password, jaribu tena");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(RequestPasswordActivity.this);
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
