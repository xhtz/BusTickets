package tz.co.xhcodes.com;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lvrenyang.io.Pos;
import com.lvrenyang.myprinter.Global;
import com.lvrenyang.myprinter.WorkService;
import com.lvrenyang.utils.DataUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class PrintTicketActivity extends AppCompatActivity {
    TextView passangeNameView;
    ImageView busLogoImageView;
    Pos pos = new Pos();
    TinyDB tinyDB;
    Button print_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //setContentView(R.layout.activity_register_passanger);
        setContentView(R.layout.activity_print_ticket);
        print_btn   = (Button)findViewById(R.id.print_btn);
        tinyDB      = new TinyDB(this);
        busLogoImageView = (ImageView)findViewById(R.id.busLogoImageView);
        passangeNameView = (TextView)findViewById(R.id.passangeNameView);
        passangeNameView.setText(
                "Jina La Abiria: "
                +BusVariables.passengerName+
                "\nNamba Ya Tiketi: "+BusVariables.ticketNumber+
                "\nNamba Ya Siti: "+BusVariables.choosenSeatLabel
            );
        if(!BusVariables.busLogo.equalsIgnoreCase("NA")){
            Picasso.with(PrintTicketActivity.this).cancelRequest(busLogoImageView);
            Picasso.with(PrintTicketActivity.this)
                    .load(Config.baseUrl + "images/buslogos/" + BusVariables.busLogo)
                    .fit()
                    .into(busLogoImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError() {
                            //error loading image
                        }
                    });
        }
        if(tinyDB.getString("has_printa").equalsIgnoreCase("No")){
            print_btn.setVisibility(View.GONE);
        }
        else{
            print_btn.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(ReprintTicketActivity.from_reprint.equalsIgnoreCase("Yes"))
        {
            Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(objIntent);
        }
        else
        {
            Intent objIntent = new Intent(getApplicationContext(), ChooseSeatActivity.class);
            startActivity(objIntent);
        }
    }
    public  void printTicket(View view)
    {
        printLogo();
        pos.POS_S_Align(0);
        printText("\n");
        printBoldText(BusVariables.busName+"\n");
        pos.POS_S_Align(0);
        printText("................................\n");
        printText(BusVariables.companyAddress+"\n");
        printText("Simu: "+BusVariables.companyPhone+"\n");
        printText("TIN: "+BusVariables.TIN+"\n");
        printText("................................\n");
        if(BusVariables.passengerName.length()>15) {
            printText("Passanger/Abiria:\n");
            printText(BusVariables.passengerName + "\n");
        }
        else
        {
            printText("Passanger/Abiria:"+BusVariables.passengerName+"\n");
        }
        printText("Bus Number/Gari namba:"+BusVariables.busNumber+"\n");
        printText("From/Kutoka:"+BusVariables.fromName+"\n");
        printText("To/Kwenda:"+BusVariables.toName+"\n");
        printText("Destination/Atakaposhukia:\n  "+BusVariables.anakoshukia+"\n");
        printText("Amount/Kiasi:"+amountFormat(Double.parseDouble(BusVariables.farePaid))+"\n");
        printText("................................\n");
        printText("Issued By/Imetolewa na:\n");
        printText("  "+LoginActivity.agentName+"\n  "+LoginActivity.agentPhone+"\n");
        printText("Date of Travel/Tarehe ya Safari:\n");
        printText("  "+BusVariables.safari_date+"\n");
        printText("Reporting Time/Muda wa Kufika:\n");
        printText("  "+BusVariables.reportingTime+"\n");
        printText("Departure Time/Muda wa Kuondoka:\n");
        printText("  "+BusVariables.departureTime+"\n");
        printText("Seat No/Kiti Namba: "+BusVariables.choosenSeatLabel+"\n");
        printText("Ticket No/Tiketi#: "+BusVariables.ticketNumber+"\n");
        printText("................................\n");
        printBarCode(BusVariables.ticketNumber);
        printText("................................\n");
        printText("1. Hii tiketi imetolewa kutumika siku na wakati uliopewa\n");
        printText("2. Abiria hatorudishiwa pesa endapo ataahirisha safari, ila itakapobidi fedha yake itakatwa kwa asilimia 30\n");
        printText("3. Chunga mzigo wako ndani, Mzigo wowote ndani ukipotea hatutahusika\n");
        printText("4. Abiria usikubali kupewa chakula au kinywaji na mtu usiyemjua, ukiwa na wasiwasi na mtu juu ya hili toa taarifa kwa wafanyakazi wa ndani ya gari mapema\n");
        printText("................................\n");
        printIwachuPayLogo();
        printText("\n\n");
        printText("\n\n");

    }
    void printBarCode(String barString)
    {
        if (WorkService.workThread.isConnected())
        {
            //print qrcode
            String strQrcode    = barString;
            int nWidthX         = 4;
            int necl            = 1; //error correction level
            Bundle data2        = new Bundle();
            data2.putString(Global.STRPARA1, strQrcode);
            data2.putInt(Global.INTPARA1, nWidthX);// 宽度控制单个模块宽度
            data2.putInt(Global.INTPARA2, 12); // 版本控制模块数量
            data2.putInt(Global.INTPARA3, necl);
            WorkService.workThread.handleCmd(Global.CMD_POS_SETQRCODE, data2);
        }
        else
        {
            dialogMessage("Printer not connected");
        }
    }
    void printLogo(){
        Bitmap mBitmap = ((BitmapDrawable) busLogoImageView.getDrawable()).getBitmap();
        int nPaperWidth = 384;
        if (mBitmap != null) {
            if (WorkService.workThread.isConnected()) {
                Bundle data = new Bundle();
                // data.putParcelable(Global.OBJECT1, mBitmap);
                data.putParcelable(Global.PARCE1, mBitmap);
                data.putInt(Global.INTPARA1, nPaperWidth);
                data.putInt(Global.INTPARA2, 0);
                WorkService.workThread.handleCmd(
                        Global.CMD_POS_PRINTPICTURE, data);
            } else {
                Toast.makeText(this, Global.toast_notconnect, Toast.LENGTH_SHORT).show();
            }
        }
    }
    void printIwachuPayLogo(){
        Bitmap mBitmap = getLogo();
        int nPaperWidth = 384;
        if (mBitmap != null) {
            if (WorkService.workThread.isConnected()) {
                Bundle data = new Bundle();
                // data.putParcelable(Global.OBJECT1, mBitmap);
                data.putParcelable(Global.PARCE1, mBitmap);
                data.putInt(Global.INTPARA1, nPaperWidth);
                data.putInt(Global.INTPARA2, 0);
                WorkService.workThread.handleCmd(
                        Global.CMD_POS_PRINTPICTURE, data);
            } else {
                Toast.makeText(this, Global.toast_notconnect, Toast.LENGTH_SHORT).show();
            }
        }
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
    void printBoldText(String str){
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
            data.putInt(Global.INTPARA5, 0x08);
            data.putInt(Global.INTPARA2, buffer.length);
            WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
        }
        else
        {
            dialogMessage("Unganisha printa");
        }
    }
    void dialogMessage(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(PrintTicketActivity.this);
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
    public static String amountFormat(Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(price);
    }
}
