package tz.co.xhcodes.com;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class DriverPaymentMethodActivity extends AppCompatActivity {
    TextView kumbukumbu_view;
    TinyDB tinyDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Lipia Huduma");
        setContentView(R.layout.activity_driver_payment_method);
        tinyDB = new TinyDB(this);
        kumbukumbu_view = findViewById(R.id.kumbukumbu_view);
        kumbukumbu_view.setText(
                "M-PESA\n"+
                "Namba Ya Kampuni: "+tinyDB.getString("companyCode")+"\n"
                +"Kumbukumbu namba: "+tinyDB.getString("paymentCode")+"\n"
                +"Kiasi: "+tinyDB.getString("kiasi")+"\n"
        );
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(objIntent);
    }
    public void dialMpesa(View view)
    {
        String instructions = "NAMNA YA KULIPIA\n" +
                "1.Piga *150*00#\n" +
                "2.Chagua LIPA Kwa M-Pesa\n" +
                "3.Weka Namba ya Kampuni\n" +
                "4.Weka Namba: "+tinyDB.getString("companyCode")+"\n" +
                "5.Weka Namba ya kumbukumbu ya Malipo\n" +
                "6.Weka Namba "+tinyDB.getString("paymentCode")+"\n" +
                "7.Weka Kiasi\n" +
                "8.Weka "+tinyDB.getString("kiasi")+"\n" +
                "9.Weka Namba yako ya siri\n" +
                "10.Bonyeza 1 Kuthibitisha";
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverPaymentMethodActivity.this);
        builder.setMessage(instructions);
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
}
