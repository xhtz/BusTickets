package tz.co.xhcodes.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iwachu on 12/7/16.
 */

public class TicketAdapterClient extends ArrayAdapter<Ticket> {
    private Context this_context;
    ProgressDialog progress;
    public TicketAdapterClient(Context context, ArrayList<Ticket> tickets) {
        super(context, 0, tickets);
        this.this_context = context;
    }

    private static class ViewHolder {
        TextView bus_name_view;
        TextView mysafari_date_view;
        TextView myseat_number_view;
        TextView myticket_number_view;
        TextView view_btn;
        TextView pay_btn;
        TextView status_view;
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final Ticket ticket = getItem(position);
        final ViewHolder viewHolder;
       if(convertView==null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.myticket_item_layout, parent, false);
            viewHolder.bus_name_view         = convertView.findViewById(R.id.bus_name_view);
            viewHolder.mysafari_date_view    = convertView.findViewById(R.id.mysafari_date_view);
           // viewHolder.myseat_number_view    = convertView.findViewById(R.id.myseat_number_view);
            viewHolder.myticket_number_view  = convertView.findViewById(R.id.myticket_number_view);
            viewHolder.view_btn              = convertView.findViewById(R.id.view_btn);
            viewHolder.pay_btn               = convertView.findViewById(R.id.pay_btn);
            viewHolder.status_view           = convertView.findViewById(R.id.status_view);
            convertView.setTag(viewHolder);
        }
        else
        {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
            if(ticket !=null) {
                if (ticket.payment_status.equalsIgnoreCase("Paid")) {
                    viewHolder.pay_btn.setVisibility(View.GONE);
                    viewHolder.status_view.setBackgroundColor(Color.WHITE);
                    viewHolder.status_view.setText("Malipo: UMELIPA\nKiasi Nauli: " + ticket.fareLabel);
                }
                else
                {
                    viewHolder.pay_btn.setVisibility(View.GONE);
                    if(!ticket.status.equalsIgnoreCase("Expired")) {
                        viewHolder.status_view.setBackgroundColor(Color.GREEN);
                        viewHolder.status_view.setText("Malipo: HUJALIPA \nKiasi Nauli: " + ticket.fareLabel);
                    }
                    else
                    {
                        viewHolder.status_view.setBackgroundColor(Color.RED);
                        viewHolder.status_view.setText("Muda wa Kulipia Umeshapita");
                    }
                }
            }

        viewHolder.bus_name_view.setText(ticket.count+": "+ticket.busName+"-"+ticket.busNumber+" \n"+ticket.route);
        viewHolder.mysafari_date_view.setText("Tarehe: "+ticket.safari_date+"\nMuda: Kufika: "+ticket.reportingTime+" Kuondoka: "+ticket.departureTime);
        //viewHolder.myseat_number_view.setText("Namba Ya Siti: "+ticket.seatLabel);
        viewHolder.myticket_number_view.setText("Namba Ya Tiketi: "+ticket.ticketNumber+"  Siti: "+ticket.seatLabel);
        // Return the completed view to render on screen
        return convertView;
    }
}
