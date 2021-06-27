package tz.co.xhcodes.com;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lvrenyang.io.Pos;
import com.lvrenyang.myprinter.Global;
import com.lvrenyang.myprinter.WorkService;
import com.lvrenyang.utils.DataUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class PaySavingsActivity extends AppCompatActivity {
    private ProgressDialog progress, progressdialog;
    private static String POST_URL, VERIFY_URL;
    private Button etPrintButton;
    private String clientCode, amount, refNumber;
    private EditText etCompanyNumber, etRefNumber, etAmount;
    Pos pos = new Pos();
    Calendar c;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMins;
    private int mSeconds;
    private String printTime;
    public static String client_company_name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_pay_savings);

        etCompanyNumber = (EditText)findViewById(R.id.clientnumber_box);
        etRefNumber     = (EditText)findViewById(R.id.referencenumber_box);
        etAmount        = (EditText)findViewById(R.id.amountpaid_box);
        etPrintButton   = (Button)findViewById(R.id.printReceiptBtn);
        etPrintButton.setEnabled(false);
        //set system timezone
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        am.setTimeZone("Africa/Nairobi");
        c       = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.back_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //When Sync action button is clicked
        if (id == R.id.back_btn) {
            Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(objIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void processPaySavings(View view){
        clientCode  = etCompanyNumber.getText().toString();
        refNumber   = etRefNumber.getText().toString();
        amount      = etAmount.getText().toString();
        POST_URL    = Config.baseUrl+"index.php/agentspos/agentsPosPayments";
        VERIFY_URL  = Config.baseUrl+"index.php/agentspos/verifyClient";
        if(clientCode.length()>0 && amount.length()>0 && refNumber.length()>0)
        {
            etPrintButton.setEnabled(false);
            new VerifyClient(this).execute();
        }
        else
        {
            dialogMessage("Weka namba ya taasisi, Namba ya bill na kiasi");
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
            progress.setMessage("Inachaka malipo, subiri...");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "clientCode="+clientCode+"&&agentCode="+LoginActivity.agentCode+"&&refNumber="+refNumber+"AK&&amount="+amount;
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
            if(result.equalsIgnoreCase("BNE"))
            {
                dialogMessage("Salio la wakala halitoshi");
            }
            else if(result.equalsIgnoreCase("AAB"))
            {
                dialogMessage("Akaunti ya wakala imezuiliwa");
            }
            else if(result.equalsIgnoreCase("0"))
            {
                dialogMessage("Namba ya mteja siyo sahihi");
            }
            else if(result.equalsIgnoreCase("1"))
            {
                dialogMessage("Umefanikiwa kutuma, print risiti");
                etCompanyNumber.setText("");
                etRefNumber.setText("");
                etAmount.setText("");
                etPrintButton.setEnabled(true);
            }
            else if(result.equalsIgnoreCase("2"))
            {
                dialogMessage("Imeshindikana kutuma malipo, jaribu tena");
            }
            else if(result.equalsIgnoreCase("3"))
            {
                dialogMessage("Kiasi ni kikubwa kuliko bill");
            }
            else
            {
                //dispaly message here if anything unknown happens
                dialogMessage("Kuna tatizo limetokea, jaribu tena");

            }
        }
    }
    private class VerifyClient extends AsyncTask<String, Void, String>
    {
        private final Context context;
        String result = "";
        public VerifyClient(Context c)
        {
            this.context = c;
        }
        protected void onPreExecute()
        {
            progressdialog= new ProgressDialog(this.context);
            progressdialog.setCancelable(false);
            progressdialog.setMessage("Tunathibitisha namba ya taasisi, subiri...");
            progressdialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(VERIFY_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "clientCode="+clientCode;
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
        protected void onPostExecute(final String result)
        {
            progressdialog.dismiss();
            if(result.equalsIgnoreCase("NF"))
            {
                dialogMessage("Namba ya taasisi siyo sahihi");
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(PaySavingsActivity.this);
                builder.setMessage("Lipa bill kwenda: "+ result);
                builder.setCancelable(false);
                builder.setNegativeButton("Hapana", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Ndiyo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        client_company_name = result;
                        new PostClass(context).execute();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(PaySavingsActivity.this);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public  void printReceipt(View view){
        c       = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"));
        mYear   = c.get(Calendar.YEAR);
        mMonth  = c.get(Calendar.MONTH);
        mDay    = c.get(Calendar.DAY_OF_MONTH);
        mHour   = c.get(Calendar.HOUR_OF_DAY);
        mMins   = c.get(Calendar.MINUTE);
        mSeconds   = c.get(Calendar.SECOND);
        printTime = mDay + "-" + (mMonth + 1) + "-" + mYear+" "+mHour+":"+mMins+":"+mSeconds;
        printLogo();
        pos.POS_S_Align(0);
        printText("\n");
        printText("--------Payments Receipt--------\n");
        printText("\n");
        printText("Paid to: "+client_company_name+" \r\n");
        printText("AC: "+clientCode+" \r\n");
        printText("Reference Number: "+refNumber+" \r\n");
        printText("Amount Paid: "+amountFormat(Double.parseDouble(amount))+" \r\n");
        printText("Agent Number: "+LoginActivity.agentCode+" \r\n");
        printText("---"+printTime+"---\n\n");
        printText("-Powered By: Iwachu Company Ltd-\n");
        printText("------www.iwachu.co.tz------\n");
        printText("\n\n");
        printText("\n\n");
    }
    private Bitmap getLogo()
    {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.iwachupay);
        return bitmap;
    }

    void printText(String str){
        if (WorkService.workThread.isConnected())
        {
            byte header[] = null;
            byte strbuf[] = null;
            header = new byte[] { 0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39, 00 };
            try {
                strbuf = str.getBytes();
            }
            catch (Exception e)
            {
                dialogMessage("Imeshindikana ku printi, jaribu tena?");
            }
            byte buffer[] = DataUtils.byteArraysToBytes(new byte[][]{header, strbuf});
            Bundle data = new Bundle();
            data.putByteArray(Global.BYTESPARA1, buffer);
            data.putInt(Global.INTPARA1, 0);
            data.putInt(Global.INTPARA2, buffer.length);
            WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
        }
        else
        {
            dialogMessage("Unganisha printa");
        }
    }
    void printLogo(){
        Bitmap mBitmap = getLogo();
        int nPaperWidth = 384;
        if (mBitmap != null) {
            if (WorkService.workThread.isConnected()) {
                Bundle data = new Bundle();
                data.putParcelable(Global.PARCE1, mBitmap);
                data.putInt(Global.INTPARA1, nPaperWidth);
                data.putInt(Global.INTPARA2, 0);
                WorkService.workThread.handleCmd(
                        Global.CMD_POS_PRINTPICTURE, data);
            } else {
                Toast.makeText(this, Global.toast_notconnect,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static String amountFormat(Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(price);
    }
}
