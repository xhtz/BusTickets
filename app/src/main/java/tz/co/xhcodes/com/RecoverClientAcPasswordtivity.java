package tz.co.xhcodes.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RecoverClientAcPasswordtivity extends AppCompatActivity {
    EditText login_phone_box;
    private ProgressDialog progress;
    private static String POST_URL = "";
    String simu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle("Omba Password Mpya");
        setContentView(R.layout.activity_recover_client_ac_passwordtivity);
        login_phone_box = findViewById(R.id.login_phone_box);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), LoginClientActivity.class);
        startActivity(objIntent);
    }
    public void requestPassword(View view){
        simu = login_phone_box.getText().toString();
        POST_URL = Config.baseUrl + "index.php/appusers/requestPassword";
        if(simu.length()==10 || simu.length()==12)
        {
            new PostClass(RecoverClientAcPasswordtivity.this).execute();
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
            progress.setMessage("Ombi linatumwa, subiri....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "simu="+simu;
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
                dialogMessage("Hakuna mtumiaji mwenye simu uliyoweka");
            }
            else if(result.equalsIgnoreCase("DONE"))
            {
                Toast.makeText(RecoverClientAcPasswordtivity.this, "Ujumbe umetumwa kwenye simu", Toast.LENGTH_LONG);
                Intent objIntent = new Intent(getApplicationContext(), LoginClientActivity.class);
                startActivity(objIntent);
            }
            else if(result.equalsIgnoreCase("FAILED"))
            {
                dialogMessage("Jaribu tena tafadhari");
            }
            else
            {
                dialogMessage("Jaribu tena tafadhari");
            }
        }
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(RecoverClientAcPasswordtivity.this);
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
