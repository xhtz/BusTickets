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
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lvrenyang.myprinter.Global;
import com.lvrenyang.myprinter.WorkService;
import com.lvrenyang.utils.DataUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static Handler mHandler = null;
    TinyDB tinydb;
    TextView profile_name_view;
    //client tickets variables
    String POST_URL     = Config.baseUrl+"index.php/appusers/userTicketsTop";
    JSONArray tickets_list;
    private ProgressDialog progress;
    TicketAdapterClient ticketAdapterClient;
    ListView tickets_list_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        tinydb = new TinyDB(this);

        ChooseSeatActivity.choose_seat      = "No";
        ReprintTicketActivity.from_reprint  = "No";
        if(LoginActivity.agentGroup.equalsIgnoreCase("Client"))
        {
            setContentView(R.layout.activity_main_client);
            profile_name_view   = findViewById(R.id.profile_name_view);
            tickets_list_view   = findViewById(R.id.mytickets_list_view);

            if(tinydb.getString("loginStatus").equalsIgnoreCase("logged_in")){
                ticketActions(tickets_list_view);
                new PostClass(this).execute();
                profile_name_view.setText(tinydb.getString("appUserFullName"));
            }
            else if(!tinydb.getString("loginStatus").equalsIgnoreCase("logged_in")){
                profile_name_view.setText("Karibu");
                Intent intent = new Intent(MainActivity.this, LoginClientActivity.class);
                startActivity(intent);
            }
        }
        else
        {
                setContentView(R.layout.activity_main_bus_agent);
                final TextView bill_view = findViewById(R.id.bill_view);
                final double bill_kesho = tinydb.getDouble("bill_kesho", 0);
                if(bill_kesho>0)
                {
                    double ticket_fee   = tinydb.getDouble("ticket_fee", 0);
                    int idadi_ticket    = 0;
                    if(ticket_fee>0) {
                        idadi_ticket = (int) (bill_kesho / ticket_fee);
                    }
                    bill_view.setText("Unatakiwa kulipa Tshs"+bill_kesho+" ikiwa ni gharama ya matumizi ya mfumo kwa tiketi "+idadi_ticket+" ulizokata. Mwisho wa kulipa ni kesho");
                }
                else
                {
                    bill_view.setVisibility(View.GONE);
                }

            //start print service
            mHandler = new MHandler(this);
            WorkService.addHandler(mHandler);
            if (null == WorkService.workThread) {
                Intent intent = new Intent(this, WorkService.class);
                startService(intent);
            }
            Settings.startBluetoth();
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
            progress.setMessage("Tunakusanya orodha ya tiketi zako....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL(POST_URL);
                HttpURLConnection connection    = (HttpURLConnection)url.openConnection();
                String urlParameters            = "appUserId="+tinydb.getString("appUserId");
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
                if(ticketAdapterClient !=null)
                {
                    ticketAdapterClient.clear();
                    ticketAdapterClient.notifyDataSetChanged();
                }
                // dialogMessage("No tickets found");
            }
            else
            {
                try
                {
                    JSONObject jsonObj      = new JSONObject(result);
                    tickets_list              = jsonObj.getJSONArray("ticketdetails");
                    displayTicketList(tickets_list);
                }
                catch (JSONException e)
                {
                    // dialogMessage("Try again please");
                    Log.d("ERROR: ", e.getLocalizedMessage());
                }

            }
        }
    }
    private void displayTicketList(JSONArray result){
        //display tickets list here
        if(result !=null)
        {
            if(result.length()>0)
            {
                if(ticketAdapterClient==null)
                {
                    ArrayList<Ticket> tickets_array = Ticket.fromJson(result);
                    ticketAdapterClient = new TicketAdapterClient(getBaseContext(), tickets_array);
                    tickets_list_view.setAdapter(ticketAdapterClient);
                }
                else
                {
                    ticketAdapterClient.clear();
                    ArrayList<Ticket> tickets_array = Ticket.fromJson(result);
                    ticketAdapterClient.addAll(tickets_array);
                    ticketAdapterClient.notifyDataSetChanged();
                }

            }

        }
    }

    private void ticketActions(final ListView listView){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try
                {
                    Ticket ticket           = ticketAdapterClient.getItem(position);
                    String ticketNumber     = ticket.ticketNumber;
                    String payment_status   = ticket.payment_status;
                    String status           = ticket.status;
                    String payment_token    = ticket.payment_token;
                    String farePaid         = ticket.farePaid;
                    String paybill_number   = ticket.paybill_number;
                    String iwachupay_code   = ticket.iwachupay_code;
                    if(payment_status.equalsIgnoreCase("Pending") && !status.equalsIgnoreCase("Expired")){
                        BusVariables.ticketNumber   = ticketNumber;
                        BusVariables.paybill_number = paybill_number;
                        BusVariables.routeFare      = Double.parseDouble(farePaid);
                        BusVariables.agentCode      = LoginActivity.agentCode;
                        BusVariables.payment_token  = ticketNumber+""+iwachupay_code;
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Lipia tiketi namba "+ticketNumber+"?");
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
                                Intent intent               = new Intent(getApplicationContext(), ChoosePaymentMethodActivity.class);
                                startActivity(intent);
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                    else
                    {
                        if(!status.equalsIgnoreCase("Expired") && payment_status.equalsIgnoreCase("Paid")) {
                            dialogMessage("Tayari umeshalipia tiketi hii");
                        }
                        else
                        {
                            dialogMessage("Muda wa Kulipa umeshapita");
                        }
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "Jaribu tena tafadhari", Toast.LENGTH_LONG).show();
                }



            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //When Sync action button is clicked
        if (id == R.id.share_btn) {
            Intent sendIntent = new Intent();
            String msg = "Download XHTickets From Play Store";
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        }
        else if(id==R.id.logout_btn){
            logoutUser();
        }
        else if(id==R.id.my_account_btn){
            if(LoginActivity.agentGroup.equalsIgnoreCase("Client")) {
                userAccount();
            }
            else
            {
                dialogMessage("For any account changes ask your account supervisor");
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if(!LoginActivity.agentGroup.equalsIgnoreCase("Client"))
        {
            Toast.makeText(getApplicationContext(), "Gusa 'Ondoka' kama unataka kufunga", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), LauncherActivity.class);
            startActivity(intent);
        }
    }
    public void payBill(View view){
        if (WorkService.workThread.isConnected()) {
            Intent intent = new Intent(getApplicationContext(), PayBillActivity.class);
            startActivity(intent);
        }
        else
        {
            dialogMessage("Unganisha printa kwanza");
        }
    }
    public void bookBusTicket(View view){
        if(LoginActivity.agentGroup.equalsIgnoreCase("Client")  || LoginActivity.agentGroup.equalsIgnoreCase("All"))
        {
            if(LoginActivity.agentGroup.equalsIgnoreCase("Client") && tinydb.getString("loginStatus").equalsIgnoreCase("logged_in")){
                Intent intent = new Intent(getApplicationContext(), BusTicketActivity.class);
                startActivity(intent);
            }
            else if(LoginActivity.agentGroup.equalsIgnoreCase("Client") && !tinydb.getString("loginStatus").equalsIgnoreCase("logged_in")){
              //  dialogMessage("Ingia kwanza kwenye akaunti yako au jisajili");
                Intent intent = new Intent(MainActivity.this, LoginClientActivity.class);
                startActivity(intent);
            }
            else if(LoginActivity.agentGroup.equalsIgnoreCase("All")){
                Intent intent = new Intent(getApplicationContext(), BusTicketActivity.class);
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(getApplicationContext(), LauncherActivity.class);
                startActivity(intent);
            }

        }
        else if(LoginActivity.agentGroup.equalsIgnoreCase("Bus"))
        {
            double unpaid_fee = tinydb.getDouble("unpaid_fee", 0);
            if(unpaid_fee<=0)
            {
                if(WorkService.workThread.isConnected())
                {
                    if(LoginActivity.agentGroup.equalsIgnoreCase("Bus"))
                    {

                            Intent intent = new Intent(getApplicationContext(), BusTicketAgentActivity.class);
                            startActivity(intent);

                    }
                    else
                    {
                        dialogMessage("Hujasajiliwa kukata tiketi");
                    }
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Hujaunganisha Printa");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Unganisha", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), SearchBTActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.setPositiveButton("Endelea", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), BusTicketAgentActivity.class);
                            startActivity(intent);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            else
            {
                dialogMessage("Unadaiwa kiasi cha "+unpaid_fee+" gharama ya matumizi ya mfumo, Lipa kwanza ili uendelee kutumia mfumo");
            }
        }
        else
        {
            dialogMessage("Kuna tatizo, jaribu tena");
        }
    }

    public void reprintTicket(View view){
       if(WorkService.workThread.isConnected())
       {
           tinydb.putString("has_printa", "Yes");
           if(LoginActivity.agentGroup.equalsIgnoreCase("Bus") || LoginActivity.agentGroup.equalsIgnoreCase("All")){
               tinydb.putString("has_printa", "Yes");
                Intent intent = new Intent(getApplicationContext(), ReprintTicketActivity.class);
                startActivity(intent);
            }
            else
            {
                dialogMessage("Hujasajiliwa kukata tiketi");
            }
       }
        else
        {
            dialogMessage("Unganisha printa kwanza");
        }
    }
    public void scanTicket(View view){
        //if(LoginActivity.bus_agent_level.equalsIgnoreCase("Inspector"))
        //{
            if(LoginActivity.agentGroup.equalsIgnoreCase("Bus"))
           {
                Intent intent = new Intent(MainActivity.this, ScanTicketActivity.class);
                startActivity(intent);
            }
            else
            {
               dialogMessage("Huruhusiwi kuhakiki tiketi!");
            }
       /* }
        else
        {
            dialogMessage("Huruhusiwi kukagua tiketi");
        }*/
    }
    public void viewSuperAgentReport(View view)
    {
        if(LoginActivity.superAgent.equalsIgnoreCase("Yes"))
        {
            Intent intent = new Intent(MainActivity.this, SuperAgentReportActivity.class);
            startActivity(intent);
        }
        else
        {
            dialogMessage("Wewe siyo wakala mkuu");
        }
    }
    public void viewCollections(View view)
    {
        if(LoginActivity.agentGroup.equalsIgnoreCase("Bus")) {
            Intent intent = new Intent(MainActivity.this, ViewCollectionsActivity.class);
            startActivity(intent);
        }
        else
        {
            dialogMessage("Wasiliana na mtoa huduma");
        }

    }
    public void agentCollections(View view)
    {
        if(LoginActivity.agentGroup.equalsIgnoreCase("All")) {
            Intent intent = new Intent(MainActivity.this, ViewAgentCollectionsActivity.class);
            startActivity(intent);
        }
        else
        {
            dialogMessage("Wasiliana na mtoa huduma");
        }

    }
    public void payServiceFee(View view)
    {
        Intent intent = new Intent(MainActivity.this, PayServiceFeeActivity.class);
        startActivity(intent);
    }
    public void getHelp(View view)
    {
        dialogMessage("Tupigie au andika ujumbe kwenda: +255752447051\nAu barua pepe kwenda: support@iwachupay.co.tz");
    }
    public void paySavings(View view){
        if (WorkService.workThread.isConnected()) {
            Intent intent = new Intent(getApplicationContext(), PaySavingsActivity.class);
            startActivity(intent);
        }
        else
        {
            dialogMessage("Unganisha printa kwanza");
        }
    }
    public void payLoan(View view){
        dialogMessage("Coming soon..");
    }
    public void connectPrinter(View view)
    {
        if(!WorkService.workThread.isConnected())
        {
            Intent intent = new Intent(getApplicationContext(), SearchBTActivity.class);
            startActivity(intent);
        }
        else
        {
            dialogMessage("Printa tayari imeshaunganishwa");
        }
    }
    public void testPrinter(View view)
    {
        try
        {
            printText("\n" +
                    "\nIwachuPay\n\n Powered By: Iwachu Company Ltd" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n");
        }
        catch (Exception e)
        {
            dialogMessage("Failed: "+e.getMessage());
        }
    }
    public void userAccount(){
        Intent intent = new Intent(MainActivity.this, EditClientAccountActivity.class);
        startActivity(intent);
    }
    public void logoutUser()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Ni kweli unataka kutoka?");
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
                tinydb.putString("loginStatus", "logged_out");
                if(!LoginActivity.agentGroup.equalsIgnoreCase("Client")) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, LauncherActivity.class);
                    startActivity(intent);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
    void printText(String str){
        if (WorkService.workThread.isConnected()) {
            byte header[] = null;
            byte strbuf[] = null;
            header = new byte[] { 0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39, 00 };
            try {
                strbuf = str.getBytes();
            } catch (Exception e) {
                Toast.makeText(this,"Error printing: "+e.getMessage(),Toast.LENGTH_LONG).show();
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
            dialogMessage("Hujaunganisha printa");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WorkService.delHandler(mHandler);
        mHandler = null;
    }
    static class MHandler extends Handler {

        WeakReference<MainActivity> mActivity;

        MHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity theActivity = mActivity.get();
            switch (msg.what) {

                case Global.CMD_POS_WRITERESULT: {
                    try {
                        int result = msg.arg1;
                        Toast.makeText(theActivity, (result == 1) ? Global.toast_success : Global.toast_fail, Toast.LENGTH_SHORT).show();
                        break;
                    }catch (Exception e){}
                }

            }
        }
    }



    public void myTickets(View view){
        if(tinydb.getString("loginStatus").equalsIgnoreCase("logged_in")) {
            Intent intent = new Intent(MainActivity.this, ClientTicketsActivity.class);
            startActivity(intent);
        }
        else{
            dialogMessage("Ingia kwanza kwenye akaunti yako au jisajili");
        }
    }
    public void pataMsaada(View view){
        Intent intent = new Intent(MainActivity.this, ClientHelpActivity.class);
        startActivity(intent);
    }

    public void changeAgentPassword(View view){
        Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
    }



    public void registerClient(View view){
        Intent intent = new Intent(MainActivity.this, RegisterClientActivity.class);
        startActivity(intent);
    }
    public void loginCient(View view){
        Intent intent = new Intent(MainActivity.this, LoginClientActivity.class);
        startActivity(intent);
    }
    public NetworkInfo checkInternet(){
        ConnectivityManager cm  = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni          = cm.getActiveNetworkInfo();
        return ni;
    }
}
