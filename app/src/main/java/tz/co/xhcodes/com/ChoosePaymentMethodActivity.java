package tz.co.xhcodes.com;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ChoosePaymentMethodActivity extends AppCompatActivity {
    TextView kumbukumbu_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_choose_payment_method);
        kumbukumbu_view = findViewById(R.id.kumbukumbu_view);
        kumbukumbu_view.setText(
                "M-PESA\n"+
                "Namba Ya Kampuni: "+BusVariables.paybill_number+"\n"
                +"Kumbukumbu namba: "+BusVariables.payment_token+"\n"
                +"Kiasi: "+BusVariables.routeFare+"\n"
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
                "4.Weka Namba: "+BusVariables.paybill_number+"\n" +
                "5.Weka Namba ya kumbukumbu ya Malipo\n" +
                "6.Weka Namba "+BusVariables.payment_token+"\n" +
                "7.Weka Kiasi\n" +
                "8.Weka "+BusVariables.routeFare+"\n" +
                "9.Weka Namba yako ya siri\n" +
                "10.Bonyeza 1 Kuthibitisha";
        AlertDialog.Builder builder = new AlertDialog.Builder(ChoosePaymentMethodActivity.this);
        builder.setMessage(instructions);
        builder.setCancelable(false);
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
       /* builder.setPositiveButton("Piga", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               //dial *150*00#
                String encodedHash = Uri.encode("#");
                String ussd = "*150*00" + encodedHash;
                startActivityForResult(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussd)), 1);
            }
        });*/
        AlertDialog alert = builder.create();
        alert.show();

    }
    public void payByIwachuPay(View view){
        Toast.makeText(ChoosePaymentMethodActivity.this, "Coming soon..", Toast.LENGTH_LONG).show();
    }
}
