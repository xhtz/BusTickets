package tz.co.xhcodes.com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by iwachu on 12/7/16.
 */

public class DriverAdapter extends ArrayAdapter<Driver> {
    private Context this_context;
    public DriverAdapter(Context context, ArrayList<Driver> drivers) {
        super(context, 0, drivers);
        this.this_context = context;
    }

    private static class ViewHolder {
        ImageView driver_logo_view;
        TextView driver_name_view;
        TextView reg_number_view;
        TextView town_name_view;
        TextView street_kituo_view;
        TextView simu_view;
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final Driver driver = getItem(position);
        final ViewHolder viewHolder;
       if(convertView==null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.driver_item_layout, parent, false);
            viewHolder.driver_logo_view         = convertView.findViewById(R.id.driver_logo_view);
            viewHolder.driver_name_view         = convertView.findViewById(R.id.driver_name_view);
            viewHolder.reg_number_view          = convertView.findViewById(R.id.reg_number_view);
            viewHolder.town_name_view           = convertView.findViewById(R.id.town_name_view);
            viewHolder.street_kituo_view        = convertView.findViewById(R.id.street_kituo_view);
            viewHolder.simu_view                = convertView.findViewById(R.id.simu_view);
          convertView.setTag(viewHolder);
        }
        else
        {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.driver_name_view.setText(driver.driverName);
        viewHolder.reg_number_view.setText("Namba Ya Usajili: "+driver.regNumber);
        viewHolder.town_name_view.setText(driver.townName);
        viewHolder.street_kituo_view.setText("Mtaa: "+driver.streetName+"\nKituo: "+driver.parkingArea);
        viewHolder.simu_view.setText("Simu: "+driver.phoneNumber);
        if(driver.driverCategory.equalsIgnoreCase("Boda"))
        {
            Picasso.with(getContext()).cancelRequest(viewHolder.driver_logo_view);
            Picasso.with(getContext())
                    .load(R.drawable.boda)
                    .fit()
                    .into(viewHolder.driver_logo_view, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError() {
                            //error loading image
                        }
                    });
        }
        else if(driver.driverCategory.equalsIgnoreCase("Bajaj"))
        {
            Picasso.with(getContext()).cancelRequest(viewHolder.driver_logo_view);
            Picasso.with(getContext())
                    .load(R.drawable.bajaj_icon)
                    .fit()
                    .into(viewHolder.driver_logo_view, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError() {
                            //error loading image
                        }
                    });
        }
        else if(driver.driverCategory.equalsIgnoreCase("Taxi"))
        {
            Picasso.with(getContext()).cancelRequest(viewHolder.driver_logo_view);
            Picasso.with(getContext())
                    .load(R.drawable.taxi_2)
                    .fit()
                    .into(viewHolder.driver_logo_view, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError() {
                            //error loading image
                        }
                    });
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
