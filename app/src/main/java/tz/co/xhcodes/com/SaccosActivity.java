package tz.co.xhcodes.com;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SaccosActivity extends AppCompatActivity {
    String SAVINGS_BAL_URL      = "http://iwachupay.co.tz/index.php/paybills/viewSavingsBalance";
    String DEPOSIT_BAL_URL      = "http://iwachupay.co.tz/index.php/paybills/viewDepositsBalance";
    String LOAN_BAL_URL         = "http://iwachupay.co.tz/index.php/paybills/viewOutstandingSaccosLoans";
    String SAVINGS_REPORT_URL   = "http://iwachupay.co.tz/index.php/paybills/viewSaccosSavingsRepayments";
    String DEPOSITS_REPORT_URL  = "http://iwachupay.co.tz/index.php/paybills/viewSaccosDepositsRepayments";
    String LOANS_REPORT_URL     = "http://iwachupay.co.tz/index.php/paybills/viewSaccosLoansRepayments";
    JSONArray savings_list, deposits_list;
    public static JSONArray savingsreport_list;
    Context context;
    String ref_number, from_date, to_date;
    private ProgressDialog progress;
    Calendar c;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMins;
    private int mSeconds;
    SavingAdapter savingAdapter;
    public  static String reports_title = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_saccos);
        context = this;
        c               = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"));
        mYear           = c.get(Calendar.YEAR);
        mMonth          = c.get(Calendar.MONTH);
        mDay            = c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(objIntent);
    }
    public void savingsBalance(View view)
    {
        LayoutInflater li   = LayoutInflater.from(context);
        View promptsView    = li.inflate(R.layout.custom_refnumber_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set custom_refnumber_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final TextView dialog_title_view = (TextView) promptsView.findViewById(R.id.dialog_title_view);
        final EditText userInput         = (EditText) promptsView.findViewById(R.id.referencenumber_box);
        dialog_title_view.setText("View Savings Balance");
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("View",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                              //process view balance here
                                ref_number =  userInput.getText().toString();
                                new PostClass(context).execute();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
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
            progress.setMessage("Please wait....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(SAVINGS_BAL_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "ref="+ref_number;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dStream        = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                final StringBuilder output = new StringBuilder("");
                BufferedReader br   = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line         = "";
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
            if(result.equalsIgnoreCase("NF"))
            {
                dialogMessage("Try again please");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    savings_list              = jsonObj.getJSONArray("saving_details");
                    for (int i = 0; i < savings_list.length(); i++) {
                        try
                        {
                            final String bal      = savings_list.getJSONObject(i).getString("bal");
                            dialogMessage("Your Savings Balance is\n"+bal);
                        }
                        catch (Exception e)
                        {
                            dialogMessage("Try again please, Check your internet connection!");
                        }
                    }
                }
                catch (JSONException e)
                {
                    dialogMessage("Try again please, Check your internet connection");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }
    private class PostDepositClass extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostDepositClass(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progress= new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Please wait....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(DEPOSIT_BAL_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "ref="+ref_number;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dStream        = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                final StringBuilder output = new StringBuilder("");
                BufferedReader br   = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line         = "";
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
                dialogMessage("Try again please");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    savings_list              = jsonObj.getJSONArray("deposit_details");
                    for (int i = 0; i < savings_list.length(); i++) {
                        try
                        {
                            final String bal      = savings_list.getJSONObject(i).getString("bal");
                            dialogMessage("Your Deposits Balance is\n"+bal);
                        }
                        catch (Exception e)
                        {
                            dialogMessage("Try again please, Check your internet connection!");
                        }
                    }
                }
                catch (JSONException e)
                {
                    dialogMessage("Try again please, Check your internet connection");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }

    private class PostOutstandingLoanClass extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostOutstandingLoanClass(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progress= new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Please wait....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(LOAN_BAL_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "ref="+ref_number;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dStream        = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                final StringBuilder output = new StringBuilder("");
                BufferedReader br   = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line         = "";
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
                dialogMessage("Try again please");
            }
            else
            {
                try
                {
                    JSONObject jsonObj          = new JSONObject(result);
                    savings_list                = jsonObj.getJSONArray("loan_details");
                    for (int i = 0; i < savings_list.length(); i++) {
                        try
                        {
                            final String loan       = savings_list.getJSONObject(i).getString("loan");
                            final String interest   = savings_list.getJSONObject(i).getString("interest");
                            final String total      = savings_list.getJSONObject(i).getString("total");
                            dialogMessage("Outstanding Loan: "+loan+"\nOutstanding Interest: "+interest+"\nTotal Outstanding: "+total);
                        }
                        catch (Exception e)
                        {
                            dialogMessage("Try again please, Check your internet connection!");
                        }
                    }
                }
                catch (JSONException e)
                {
                    dialogMessage("Try again please, Check your internet connection");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }

    private class PostGetSavingsReportsClass extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostGetSavingsReportsClass(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progress= new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Please wait....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(SAVINGS_REPORT_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "ref="+ref_number+"&&from_date="+from_date+"&&to_date="+to_date;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dStream        = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                final StringBuilder output = new StringBuilder("");
                BufferedReader br   = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line         = "";
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
                dialogMessage("Try again please");
            }
            else
            {
                try
                {
                    JSONObject jsonObj          = new JSONObject(result);
                    savingsreport_list          = jsonObj.getJSONArray("savings_details");
                    reports_title               = "Savings Report";
                    Intent intent = new Intent(SaccosActivity.this, SavingsReportsActivity.class);
                    startActivity(intent);
                }
                catch (JSONException e)
                {
                    dialogMessage("Try again please, Check your internet connection");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }

    private class PostGetDepositsReportsClass extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostGetDepositsReportsClass(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progress= new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Please wait....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(DEPOSITS_REPORT_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "ref="+ref_number+"&&from_date="+from_date+"&&to_date="+to_date;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dStream        = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                final StringBuilder output = new StringBuilder("");
                BufferedReader br   = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line         = "";
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
                dialogMessage("Try again please");
            }
            else
            {
                try
                {
                    JSONObject jsonObj          = new JSONObject(result);
                    savingsreport_list          = jsonObj.getJSONArray("deposit_details");
                    reports_title               = "Deposits Report";
                    Intent intent = new Intent(SaccosActivity.this, SavingsReportsActivity.class);
                    startActivity(intent);
                }
                catch (JSONException e)
                {
                    dialogMessage("Try again please, Check your internet connection");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }
    private class PostGetLoanReportsClass extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public PostGetLoanReportsClass(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progress= new ProgressDialog(this.context);
            progress.setCancelable(false);
            progress.setMessage("Please wait....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(LOANS_REPORT_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "ref="+ref_number+"&&from_date="+from_date+"&&to_date="+to_date;
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dStream        = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                final StringBuilder output = new StringBuilder("");
                BufferedReader br   = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line         = "";
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
                dialogMessage("Try again please");
            }
            else
            {
                try
                {
                    JSONObject jsonObj          = new JSONObject(result);
                    savingsreport_list          = jsonObj.getJSONArray("loan_details");
                    reports_title               = "Loan Repayments";
                    Intent intent = new Intent(SaccosActivity.this, SavingsReportsActivity.class);
                    startActivity(intent);
                }
                catch (JSONException e)
                {
                    dialogMessage("Try again please, Check your internet connection");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(SaccosActivity.this);
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
    public void depositsBalance(View view)
    {
        LayoutInflater li   = LayoutInflater.from(context);
        View promptsView    = li.inflate(R.layout.custom_refnumber_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set custom_refnumber_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final TextView dialog_title_view = (TextView) promptsView.findViewById(R.id.dialog_title_view);
        final EditText userInput         = (EditText) promptsView.findViewById(R.id.referencenumber_box);
        dialog_title_view.setText("View Deposits Balance");
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("View",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                              //process view balance here
                                ref_number =  userInput.getText().toString();
                                new PostDepositClass(context).execute();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
    public void loanBalance(View view)
    {
        LayoutInflater li   = LayoutInflater.from(context);
        View promptsView    = li.inflate(R.layout.custom_refnumber_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set custom_refnumber_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final TextView dialog_title_view = (TextView) promptsView.findViewById(R.id.dialog_title_view);
        final EditText userInput         = (EditText) promptsView.findViewById(R.id.referencenumber_box);
        dialog_title_view.setText("View Oustanding Loan");
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("View",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                              //process view balance here
                                ref_number =  userInput.getText().toString();
                                new PostOutstandingLoanClass(context).execute();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
    public  void  savingsReports(View view)
    {
        LayoutInflater li   = LayoutInflater.from(context);
        View promptsView    = li.inflate(R.layout.custom_refnumberreports_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set custom_refnumberreports_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final TextView dialog_title_view = (TextView) promptsView.findViewById(R.id.dialog_title_view);
        final EditText userInput         = (EditText) promptsView.findViewById(R.id.referencenumber_box);
        final EditText fromDateBox       = (EditText) promptsView.findViewById(R.id.from_date_box);
        final EditText toDateBox         = (EditText) promptsView.findViewById(R.id.to_date_box);
        dialog_title_view.setText("View Savings Payments");
        fromDateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(fromDateBox);
            }
        });
        toDateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(toDateBox);
            }
        });
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("View",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //process view balance here
                                ref_number  =  userInput.getText().toString();
                                from_date   =  fromDateBox.getText().toString();
                                to_date     =  toDateBox.getText().toString();
                                new PostGetSavingsReportsClass(context).execute();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
    public  void  viewRepayments(View view)
    {
       LayoutInflater li   = LayoutInflater.from(context);
        View promptsView    = li.inflate(R.layout.custom_refnumberreports_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set custom_refnumberreports_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final TextView dialog_title_view = (TextView) promptsView.findViewById(R.id.dialog_title_view);
        final EditText userInput         = (EditText) promptsView.findViewById(R.id.referencenumber_box);
        final EditText fromDateBox       = (EditText) promptsView.findViewById(R.id.from_date_box);
        final EditText toDateBox         = (EditText) promptsView.findViewById(R.id.to_date_box);
        dialog_title_view.setText("View Loan Repayments");
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("View",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //process view balance here
                                ref_number  =  userInput.getText().toString();
                                from_date   =  fromDateBox.getText().toString();
                                to_date     =  toDateBox.getText().toString();
                                new PostGetLoanReportsClass(context).execute();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
    public  void  depositsReports(View view)
    {
       LayoutInflater li   = LayoutInflater.from(context);
        View promptsView    = li.inflate(R.layout.custom_refnumberreports_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set custom_refnumberreports_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final TextView dialog_title_view = (TextView) promptsView.findViewById(R.id.dialog_title_view);
        final EditText userInput         = (EditText) promptsView.findViewById(R.id.referencenumber_box);
        final EditText fromDateBox       = (EditText) promptsView.findViewById(R.id.from_date_box);
        final EditText toDateBox         = (EditText) promptsView.findViewById(R.id.to_date_box);
        dialog_title_view.setText("View Deposits Payments");
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("View",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //process view balance here
                                ref_number  =  userInput.getText().toString();
                                from_date   =  fromDateBox.getText().toString();
                                to_date     =  toDateBox.getText().toString();
                                new PostGetDepositsReportsClass(context).execute();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
    public void repayLoan(View view)
    {
        String instructions = "1.Piga *150*00#\n" +
                "2.Chagua LIPA Kwa M-Pesa\n" +
                "3.Weka Namba ya Kampuni\n" +
                "4.Weka Namba 399933 \n" +
                "5.Weka Namba ya kumbukumbu ya Malipo(Namba ya kumbukumbu ya mkopo wako)\n" +
                "6.Weka Kiasi\n" +
                "7.Weka Namba yako ya siri\n" +
                "8.Bonyeza 1 Kuthibitisha";
        AlertDialog.Builder builder = new AlertDialog.Builder(SaccosActivity.this);
        builder.setMessage(instructions);
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
    public void depositSavings(View view)
    {
        String instructions = "1.Piga *150*00#\n" +
                "2.Chagua LIPA Kwa M-Pesa\n" +
                "3.Weka Namba ya Kampuni\n" +
                "4.Weka Namba 399933 \n" +
                "5.Weka Namba ya kumbukumbu ya Malipo(Namba ya kumbukumbu ya uanachama)\n" +
                "6.Weka Kiasi\n" +
                "7.Weka Namba yako ya siri\n" +
                "8.Bonyeza 1 Kuthibitisha";
        AlertDialog.Builder builder = new AlertDialog.Builder(SaccosActivity.this);
        builder.setMessage(instructions);
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
    public void showDatePicker(final EditText datebox)
    {
        DatePickerDialog dpd = new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                datebox.setText(dayOfMonth + "-"+ (monthOfYear + 1) + "-" + year);

            }
        }, mYear, mMonth, mDay);
        //un comment line below to set minimum date to current date for android 3.0 and above
       dpd.getDatePicker().setMaxDate(new Date().getTime() - 10000);
        dpd.show();
    }
}
