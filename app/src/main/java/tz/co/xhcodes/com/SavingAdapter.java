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

public class SavingAdapter extends ArrayAdapter<Saving> {
    private Context this_context;
    ProgressDialog progress;
    public SavingAdapter(Context context, ArrayList<Saving> buses) {
        super(context, 0, buses);
        this.this_context = context;
    }

    private static class ViewHolder {
        TextView rdate_view;
        TextView receipt_view;
        TextView amount_view;
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final Saving saving = getItem(position);
        final ViewHolder viewHolder;
       if(convertView==null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.saving_item_layout, parent, false);
            viewHolder.rdate_view           = (TextView) convertView.findViewById(R.id.rdate_view);
            viewHolder.receipt_view         = (TextView)convertView.findViewById(R.id.receipt_view);
            viewHolder.amount_view          = (TextView)convertView.findViewById(R.id.amount_view);
            convertView.setTag(viewHolder);
        }
        else
        {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.rdate_view.setText(saving.rdate);
        viewHolder.receipt_view.setText(saving.receipt);
        viewHolder.amount_view.setText(saving.amount);
        // Return the completed view to render on screen
        return convertView;
    }
}
