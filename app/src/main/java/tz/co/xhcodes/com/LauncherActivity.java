package tz.co.xhcodes.com;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

public class LauncherActivity extends AppCompatActivity {
    Spinner select_user_category;
    String user_category;
    TinyDB tinydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_launcher);
        //select_user_category = findViewById(R.id.select_user_category);
        tinydb = new TinyDB(this);
    }
    @Override
    public void onResume(){
        super.onResume();
        /*Resources res = getApplicationContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale("sw");
        res.updateConfiguration(conf, dm);*/
    }
    public void continueLogin(View view)
    {
        user_category = select_user_category.getSelectedItem().toString();
        if(user_category.equalsIgnoreCase("Wakala"))
        {
            Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else if(user_category.equalsIgnoreCase("Mteja"))
        {
            LoginActivity.bus_agent_level  = "Client";
            LoginActivity.agentGroup       = "Client";
            LoginActivity.agentId          = "12"; //iwachuPay id in database, agents table, change this according to database internal id
            LoginActivity.konda            = "No";
            LoginActivity.companyId        = "0";
            LoginActivity.iwachu_agent_id  = "0";
            Intent intent = new Intent(LauncherActivity.this, LoginClientActivity.class);
            startActivity(intent);
        }
    }
    public void continueClientLogin(View view)
    {
        LoginActivity.bus_agent_level  = "Client";
        LoginActivity.agentGroup       = "Client";
        LoginActivity.agentId       = "12"; //iwachuPay id in database, agents table, change this according to database internal id
        LoginActivity.konda         = "No";
        LoginActivity.companyId     = "0";
        //Intent intent = new Intent(LauncherActivity.this, LoginClientActivity.class);
        Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
        startActivity(intent);
    }
    public void continueAgentLogin(View view)
    {
        Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
