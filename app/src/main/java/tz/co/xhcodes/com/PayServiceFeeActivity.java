package tz.co.xhcodes.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PayServiceFeeActivity extends AppCompatActivity {
    private ProgressDialog progress;
    private static String POST_URL      = "";
    private static String POST_URL_KESHO_BILL      = "";
    double unpaid_fee = 0;
    double bill_kesho = 0;
    TinyDB tinyDB;
    TextView pay_instructions_view, kesho_bill_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_pay_service_fee);
        pay_instructions_view   = findViewById(R.id.pay_instructions_view);
        kesho_bill_view         = findViewById(R.id.kesho_bill_view);
        tinyDB                  = new TinyDB(PayServiceFeeActivity.this);
        POST_URL                = Config.baseUrl+"index.php/tickets/currentUnPaidFee";
        POST_URL_KESHO_BILL     = Config.baseUrl+"index.php/tickets/keshoBill";
        new PostClass(PayServiceFeeActivity.this).execute();
        new PostKeshoBillClass(PayServiceFeeActivity.this).execute();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(objIntent);

    }
    private class PostClass extends AsyncTask<String, Void, String> {
        private final Context context;
        String result = "";

        public PostClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Inatafuta kiasi unachodiwa sasa....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(POST_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String urlParameters = "company_id=" + LoginActivity.companyId + "&&agent_id=" + LoginActivity.agentId;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                final StringBuilder output = new StringBuilder("");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();
                output.append(responseOutput.toString());
                result = responseOutput.toString();
            } catch (MalformedURLException e) {
                result = e.getMessage();
            } catch (IOException e) {
                result = e.getMessage();
            }
            return result;
        }

        protected void onPostExecute(String result) {
            progress.dismiss();
           try
           {
               unpaid_fee = Double.parseDouble(result);
               tinyDB.putDouble("unpaid_fee", unpaid_fee);
                String instruction = "1.Piga *150*00#\n" +
               "2.Chagua LIPA Kwa M-Pesa\n" +
               "3.Weka Namba ya Kampuni\n" +
               "4.Weka Namba: 399933\n" +
               "5.Weka Namba ya kumbukumbu ya Malipo\n" +
               "6.Weka Namba "+LoginActivity.agentCode+"000030\n" +
               "7.Weka Kiasi\n" +
               "8.Weka Namba yako ya siri\n" +
               "9.Bonyeza 1 Kuthibitisha";
                if(unpaid_fee>0)
                {
                    instruction +="\n\nKiasi unachodaiwa mpaka sasa ni: "+unpaid_fee;
                }

               pay_instructions_view.setText(instruction);
           }
           catch (Exception e)
           {
               //pay_instructions_view.setText("Jaribu tena tafadhari");
           }
        }
    }
    private class PostKeshoBillClass extends AsyncTask<String, Void, String> {
        private final Context context;
        String result = "";

        public PostKeshoBillClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(POST_URL_KESHO_BILL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String urlParameters = "company_id=" + LoginActivity.companyId + "&&agent_id=" + LoginActivity.agentId;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                final StringBuilder output = new StringBuilder("");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();
                output.append(responseOutput.toString());
                result = responseOutput.toString();
            } catch (MalformedURLException e) {
                result = e.getMessage();
            } catch (IOException e) {
                result = e.getMessage();
            }
            return result;
        }

        protected void onPostExecute(String result) {
           try
           {
               bill_kesho = Double.parseDouble(result);
               tinyDB.putDouble("bill_kesho", bill_kesho);

                if(bill_kesho>0)
                {
                    kesho_bill_view.setText("Gharama ya mfumo ambayo unatakiwa uwe umelipa mpaka kesho ni: \n"+bill_kesho);
                }

           }
           catch (Exception e)
           {
               //do nothing
           }
        }
    }

    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(PayServiceFeeActivity.this);
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

