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

public class ChangePasswordActivity extends AppCompatActivity {
    private ProgressDialog progress;
    private static String POST_URL  = "";
    String currentpassword, newpassword;
    EditText new_pass_box, current_pwd_box;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_change_password);
        current_pwd_box = (EditText)findViewById(R.id.current_pwd_box);
        new_pass_box = (EditText)findViewById(R.id.new_pass_box);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(objIntent);
    }
    public  void changePwd(View view){
        currentpassword = current_pwd_box.getText().toString();
        newpassword     = new_pass_box.getText().toString();
        POST_URL        = Config.baseUrl + "index.php/agentspos/changeAgentPassword";
        if(currentpassword.length()>3 && newpassword.length()>3)
        {
            new PostClass(ChangePasswordActivity.this).execute();
        }
        else
        {
            dialogMessage("Umeweka password isiyofaa");
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
                String urlParameters            = "agentId="+LoginActivity.agentId+"&&currentPwd="+currentpassword+"&&newPwd="+newpassword;
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
                dialogMessage("Umekosea Password ya sasa");
            }
            else
            {
                try
                {
                    JSONObject jsonObj     = new JSONObject(result);
                    String status          =jsonObj.getString("status");
                    if(status.equalsIgnoreCase("Success"))
                    {
                        current_pwd_box.setText("");
                        new_pass_box.setText("");
                        dialogMessage("Umefanikiwa kubadili password");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
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
