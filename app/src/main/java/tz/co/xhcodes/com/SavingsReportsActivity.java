package tz.co.xhcodes.com;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;

import java.util.ArrayList;

public class SavingsReportsActivity extends AppCompatActivity {
    JSONArray savingsreport_list;
    ListView reports_list_view;
    SavingAdapter savingAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(SaccosActivity.reports_title);
        setContentView(R.layout.activity_savings_reports);
        reports_list_view = (ListView)findViewById(R.id.reports_list_view);
        displayReportsList(SaccosActivity.savingsreport_list, reports_list_view);
    }
    private void displayReportsList(JSONArray result, ListView reports_list_view){
        //display chat line here
        if(result !=null)
        {
            if(result.length()>0)
            {
                if(savingAdapter==null)
                {
                    ArrayList<Saving> reports_array = Saving.fromJson(result);
                    savingAdapter = new SavingAdapter(getBaseContext(), reports_array);
                    reports_list_view.setAdapter(savingAdapter);
                }
                else
                {
                    savingAdapter.clear();
                    ArrayList<Saving> reports_array = Saving.fromJson(result);
                    savingAdapter.addAll(reports_array);
                    savingAdapter.notifyDataSetChanged();
                }
            }

        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent objIntent = new Intent(getApplicationContext(), SaccosActivity.class);
        startActivity(objIntent);
    }
}
