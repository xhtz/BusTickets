package tz.co.xhcodes.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.ArrayList;

/**
 * Created by iwachu on 12/7/16.
 */

public class BehewAdapter extends ArrayAdapter<Behewa> {
    private Context this_context;
    ProgressDialog progress;
    public BehewAdapter(Context context, ArrayList<Behewa> bogies) {
        super(context, 0, bogies);
        this.this_context = context;
    }

    private static class ViewHolder {
        ImageView bus_logo_view;
        TextView bogie_name_view;
        TextView route_name_view;
        TextView seats_view;
        TextView reporttime_view;
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final Behewa bogie = getItem(position);
        final ViewHolder viewHolder;
       if(convertView==null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bogie_item_layout, parent, false);
            viewHolder.bus_logo_view           = (ImageView) convertView.findViewById(R.id.bus_logo_view);
            viewHolder.bogie_name_view         = (TextView)convertView.findViewById(R.id.bogie_name_view);
            viewHolder.route_name_view         = (TextView)convertView.findViewById(R.id.route_name_view);
            viewHolder.seats_view              = (TextView)convertView.findViewById(R.id.seats_view);
            viewHolder.reporttime_view         = (TextView)convertView.findViewById(R.id.reporttime_view);
          convertView.setTag(viewHolder);
        }
        else
        {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.bogie_name_view.setText("Behewa: "+bogie.bogie_number);
        viewHolder.route_name_view.setText("Kutoka: "+bogie.from_station_name+"\nKwenda: "+bogie.to_station_name);
        viewHolder.seats_view.setText("Jumla Siti: "+bogie.no_seats+",  Zilizobaki: "+bogie.no_available_seats+"\nNauli Mkubwa: "+ bogie.adult_fare+"\nNauli Mtoto: "+bogie.child_fare);
        viewHolder.reporttime_view.setText("Tarehe: "+bogie.safari_date+" Muda Kufika: "+bogie.arrive_time+"\nKuondoka: "+bogie.departure_time);
        // Return the completed view to render on screen
        return convertView;
    }
}
