package tz.co.xhcodes.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iwachu on 12/7/16.
 */

public class TicketAdapter extends ArrayAdapter<Ticket> {
    private Context this_context;
    ProgressDialog progress;
    public TicketAdapter(Context context, ArrayList<Ticket> tickets) {
        super(context, 0, tickets);
        this.this_context = context;
    }

    private static class ViewHolder {
        TextView passenger_name_view;
        TextView ticket_number_view;
        TextView fare_label_view;
        TextView agent_name_view;
        TextView seat_number_view;
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final Ticket ticket = getItem(position);
        final ViewHolder viewHolder;
       if(convertView==null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ticket_item_layout, parent, false);
            viewHolder.passenger_name_view          = convertView.findViewById(R.id.passenger_name_view);
            viewHolder.seat_number_view             = convertView.findViewById(R.id.seat_number_view);
            viewHolder.ticket_number_view           = convertView.findViewById(R.id.ticket_number_view);
            viewHolder.fare_label_view              = convertView.findViewById(R.id.fare_label_view);
            viewHolder.agent_name_view              = convertView.findViewById(R.id.agent_name_view);
            convertView.setTag(viewHolder);
        }
        else
        {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.passenger_name_view.setText(ticket.count+": "+ticket.passengerName+" \n"+ticket.route);
        viewHolder.ticket_number_view.setText("Namba ya Tiketi: "+ticket.ticketNumber+" Tarehe: "+ticket.safari_date);
        viewHolder.seat_number_view.setText("Namba ya Siti: "+ticket.seatLabel);
        viewHolder.agent_name_view.setText("Ajenti: "+ticket.agentName+"\nAnakoshukia: "+ticket.anakoshukia);
        viewHolder.fare_label_view.setText(ticket.fareLabel);
        // Return the completed view to render on screen
        return convertView;
    }
}
