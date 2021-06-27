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
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TreniPassangerActivity extends AppCompatActivity {
    private ProgressDialog progress;
    private static String POST_URL;
    EditText passangerTreniNameBox, passangerTreniPhoneBox, idadiWakubwaBox, idadiWatotoBox, nambakitambulishoBox;
    Spinner passengerAinaKitambulishoSelect;
    String passangerName, passangerPhone, idadiWakubwa, idadiWatoto, ainaKitambulisho, nambaKitambulisho;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Taarifa Za Abiria");
        setContentView(R.layout.activity_treni_passanger);
        POST_URL            = Config.baseUrl+"index.php/tickets/saveTreniTicket";
        passengerAinaKitambulishoSelect  = (Spinner)findViewById(R.id.passengerAinaKitambulishoSelect);
        passangerTreniNameBox   = (EditText)findViewById(R.id.passangerTreniNameBox);
        passangerTreniPhoneBox  = (EditText)findViewById(R.id.passangerTreniPhoneBox);
        idadiWakubwaBox         = (EditText)findViewById(R.id.idadiWakubwaBox);
        idadiWatotoBox          = (EditText)findViewById(R.id.idadiWatotoBox);
        nambakitambulishoBox    = (EditText)findViewById(R.id.nambakitambulishoBox);
    }
    public  void saveTicket(View view)
    {
        passangerName       = passangerTreniNameBox.getText().toString();
        passangerPhone      = passangerTreniPhoneBox.getText().toString();
        idadiWakubwa        = idadiWakubwaBox.getText().toString();
        idadiWatoto         = idadiWatotoBox.getText().toString();
        idadiWatoto         = idadiWatotoBox.getText().toString();
        ainaKitambulisho    = passengerAinaKitambulishoSelect.getSelectedItem().toString();
        nambaKitambulisho   = nambakitambulishoBox.getText().toString();
        if(passangerPhone.length()>0 && passangerPhone.length()>0 && idadiWatoto.length()>0 && idadiWakubwa.length()>0 && nambaKitambulisho.length()>0){
            new PostClass(TreniPassangerActivity.this).execute();
        }
        else
        {
            dialogMessage("Jazo taarifa zote kwanza");
        }
    }
    private String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }
        return capMatcher.appendTail(capBuffer).toString();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), BogiesListActivity.class);
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
            progress.setMessage("Inahifadhi taarifa, subiri.....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "timetable_id="+ TreniVariables.timetable_id+"&&bogie_id="+ TreniVariables.bogie_id+"&&timetable_id="+ BusVariables.timetableId+"&&passanger_name="+ passangerName+"&&mobile_number="+passangerPhone+"&&passangerPhone="+passangerPhone+"&&no_adults="+ idadiWakubwa+"&&no_childs="+idadiWatoto+"&&adult_fare_amount="+ TreniVariables.adult_fare_amount+"&&child_fare_amount="+TreniVariables.child_fare_amount+"&&line_id="+ TreniVariables.line_id+"&&no_seats="+ TreniVariables.no_seats;
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
            try
            {
                JSONObject jsonObj          = new JSONObject(result);
                final String status         = jsonObj.getString("status");
                final String ticketNumber   = jsonObj.getString("ticketNumber");
                final String payment_token  = jsonObj.getString("payment_token");
                if(status.equalsIgnoreCase("Success"))
                {
                   dialogMessage("LIPIA TIKETI KWA M-PESA:\n1.Namba ya Kampuni: 399933\n2.Namba ya Kumbukumbu: "+payment_token+"\nAsante kwa Kutumia IwachuPay");
                }
                else
                {
                    dialogMessage("Imeshindikana kuhifadhi taarifa jaribu tena!");
                }

            }
            catch (JSONException e)
            {
                dialogMessage("Jaribu tena, hakikisha kwanza una intanenti");
                Log.d("ERROR: ", e.getLocalizedMessage());
            }

        }
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(TreniPassangerActivity.this);
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
