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

public class RegisterPassangerActivity extends AppCompatActivity {
    private ProgressDialog progress;
    private static String POST_URL;
    String passengerName, passengerSex, passangerPhone, farePaid, eneo_select, eneo, anakoshukia, booking, paper_ticket_number, muda_kufika, muda_kuondoka;
    EditText passangerNameBox, passangerPhoneBox, farePaidBox, eneoBox, anakoshukiaBox;
    Spinner passengerSexSelect, eneoSelect, bookingSelect, mudaKufikaSelect, mudaKuondokaSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_register_passanger);
        POST_URL            = Config.baseUrl+"index.php/tickets/saveTicket";
        passengerSexSelect  = findViewById(R.id.passengerSexSelect);
        mudaKufikaSelect    = findViewById(R.id.mudaKufikaSelect);
        mudaKuondokaSelect  = findViewById(R.id.mudaKuondokaSelect);
        eneoSelect          = findViewById(R.id.eneoSelect);
        bookingSelect       = findViewById(R.id.bookingSelect);
        passangerNameBox    = findViewById(R.id.passangerNameBox);
        eneoBox             = findViewById(R.id.eneoBox);
        passangerPhoneBox   = findViewById(R.id.passangerPhoneBox);
        farePaidBox         = findViewById(R.id.farePaidBox);
        anakoshukiaBox      = findViewById(R.id.anakoshukiaBox);
        if(LoginActivity.agentGroup.equalsIgnoreCase("Client")){
            farePaidBox.setText(BusVariables.routeFare+"");
            farePaidBox.setEnabled(false);
        }
        else if(LoginActivity.agentGroup.equalsIgnoreCase("Bus") || LoginActivity.agentGroup.equalsIgnoreCase("All"))
        {
            farePaidBox.setText(BusVariables.routeFare+"");
            farePaidBox.setEnabled(true);
        }
        else
        {
            farePaidBox.setText(BusVariables.routeFare+"");
            farePaidBox.setEnabled(false);
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), ChooseSeatActivity.class);
        startActivity(objIntent);
    }
    private String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }
        return capMatcher.appendTail(capBuffer).toString();
    }
    public  void saveTicket(View view)
    {
        paper_ticket_number = "NA";
        passengerName       = passangerNameBox.getText().toString();
        passengerName       = capitalize(passengerName);
        passangerPhone      = passangerPhoneBox.getText().toString();
        farePaid            = farePaidBox.getText().toString();
        passengerSex        = passengerSexSelect.getSelectedItem().toString();
        eneo                = eneoBox.getText().toString();
        booking             = bookingSelect.getSelectedItem().toString();
        anakoshukia         = anakoshukiaBox.getText().toString();
        eneo_select         = eneoSelect.getSelectedItem().toString();
        muda_kufika         = mudaKufikaSelect.getSelectedItem().toString();
        muda_kuondoka       = mudaKuondokaSelect.getSelectedItem().toString();
        BusVariables.passangerPhone = passangerPhone;
        BusVariables.passengerName  = passengerName;
        BusVariables.farePaid       = farePaid;
        BusVariables.passengerSex   = passengerSex;
        BusVariables.anakoshukia    = anakoshukia;
        if(passengerName.length()>0 && passangerPhone.length()>0 && farePaid.length()>0 && anakoshukia.length()>0)
        {
            if(passangerPhone.equalsIgnoreCase(LoginActivity.agentPhone))
            {
                dialogMessage("Weka namba ya abiria siyo yako");
            }
            else if(passangerPhone.length()==12 || passangerPhone.length()==10)
            {
                if(eneo_select.equalsIgnoreCase("Ndiyo") && eneo.equalsIgnoreCase("")) {
                    dialogMessage("Weka jina la kituo cha kupandia");
                }
                else
                {
                    if(eneo.equalsIgnoreCase("") || eneo_select.equalsIgnoreCase("Hapana")) {
                        eneo            = "NA";
                        muda_kufika     = "NA"; //use default timetable time
                        muda_kuondoka   = "NA"; //use default timetable time
                    }
                    else
                    {
                        if(eneo_select.equalsIgnoreCase("Ndiyo"))
                        {
                            BusVariables.reportingTime = muda_kufika;
                            BusVariables.departureTime = muda_kuondoka;
                            BusVariables.fromName      = eneo;
                            BusVariables.toName        = anakoshukia;
                        }
                    }
                    new PostClass(RegisterPassangerActivity.this).execute();
                }
            }
            else
            {
                dialogMessage("Simu siyo sahihi weka katika mfumo wa: 255XXXXXXXXX au 0XXXXXXXXX, usiweke alama ya kujumlisha(+)");
            }

        }
        else
        {
            dialogMessage("Weka taarifa zote za abiria");
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
                String urlParameters            = "bus_number="+BusVariables.busNumber+"&&seat_label="+BusVariables.choosenSeatLabel+"&&timetable_id="+BusVariables.timetableId+"&&company_id="+BusVariables.companyId+"&&passengerName="+passengerName+"&&passengerSex="+passengerSex+"&&passangerPhone="+passangerPhone+"&&safariDate="+BusVariables.safari_date+"&&farePaid="+farePaid+"&&agent_id="+LoginActivity.agentId+"&&eneo="+eneo+"&&iwachupay_code="+BusVariables.iwachupay_code+"&&isClient="+LoginActivity.agentGroup+"&&routeFare="+BusVariables.routeFare+"&&anakoshukia="+anakoshukia+"&&booking="+booking+"&&paper_ticket_number="+paper_ticket_number+"&&paybill_number="+BusVariables.paybill_number+"&&muda_kufika="+muda_kufika+"&&muda_kuondoka="+muda_kuondoka+"&&iwachu_agent_id="+LoginActivity.iwachu_agent_id;
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
            if(result.equalsIgnoreCase("Taken"))
            {
                dialogMessage("Siti imeshachukuliwa!");
            }
            else if(result.equalsIgnoreCase("Deni"))
            {
                dialogMessage("Una deni la gharama ya mfumo hujalipa");
            }
            else
            {
                try
                {
                    JSONObject jsonObj          = new JSONObject(result);
                    final String status         = jsonObj.getString("status");
                    final String ticketNumber   = jsonObj.getString("ticketNumber");
                    if(status.equalsIgnoreCase("Success"))
                    {
                        //go to print receipt page
                        BusVariables.ticketNumber   = ticketNumber;
                        BusVariables.agentCode      = LoginActivity.agentCode;
                        BusVariables.payment_token  = ticketNumber+""+BusVariables.iwachupay_code;
                        if(LoginActivity.agentGroup.equalsIgnoreCase("Client"))
                        {
                            Intent intent = new Intent(getApplicationContext(), ChoosePaymentMethodActivity.class);
                            startActivity(intent);
                        }
                        else if(LoginActivity.agentGroup.equalsIgnoreCase("Bus") || LoginActivity.agentGroup.equalsIgnoreCase("All"))
                        {
                            Intent intent = new Intent(getApplicationContext(), PrintTicketActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            dialogMessage("Kuna tatizo, jaribu tena");
                        }
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
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterPassangerActivity.this);
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
