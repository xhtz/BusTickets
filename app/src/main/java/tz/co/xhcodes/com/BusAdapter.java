package tz.co.xhcodes.com;

import android.app.ProgressDialog;
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

public class BusAdapter extends ArrayAdapter<Bus> {
    private Context this_context;
    ProgressDialog progress;
    public BusAdapter(Context context, ArrayList<Bus> buses) {
        super(context, 0, buses);
        this.this_context = context;
    }

    private static class ViewHolder {
        ImageView bus_logo_view;
        TextView bus_name_view;
        TextView route_name_view;
        TextView seats_view;
        TextView bus_number_view;
        TextView reporttime_view;
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final Bus bus = getItem(position);
        final ViewHolder viewHolder;
       if(convertView==null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bus_item_layout, parent, false);
            viewHolder.bus_logo_view           = (ImageView) convertView.findViewById(R.id.bus_logo_view);
            viewHolder.bus_name_view           = (TextView)convertView.findViewById(R.id.bus_name_view);
            viewHolder.route_name_view         = (TextView)convertView.findViewById(R.id.route_name_view);
            viewHolder.seats_view              = (TextView)convertView.findViewById(R.id.seats_view);
            viewHolder.bus_number_view         = (TextView)convertView.findViewById(R.id.bus_number_view);
            viewHolder.reporttime_view         = (TextView)convertView.findViewById(R.id.reporttime_view);
          convertView.setTag(viewHolder);
        }
        else
        {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.bus_name_view.setText(bus.busName);
        viewHolder.bus_number_view.setText("Namba Ya Gari: "+bus.busNumber);
        viewHolder.route_name_view.setText(bus.fromName+" - "+bus.toName);
        viewHolder.seats_view.setText("Jumla Siti: "+bus.noSeats+",  Zilizobaki: "+bus.seatsAvailable+"\nNauli: "+bus.fareAmount);
        viewHolder.reporttime_view.setText("Muda: "+bus.reportingTime);
        if(!bus.busLogo.equalsIgnoreCase("NA")){
            Picasso.with(getContext()).cancelRequest(viewHolder.bus_logo_view);
            Picasso.with(getContext())
                    .load(Config.baseUrl + "images/buslogos/" + bus.busLogo)
                    .fit()
                    .into(viewHolder.bus_logo_view, new Callback() {
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
