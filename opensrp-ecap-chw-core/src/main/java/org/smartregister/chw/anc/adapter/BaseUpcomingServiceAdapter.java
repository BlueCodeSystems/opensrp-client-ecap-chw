package org.smartregister.chw.anc.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.opensrp_chw_anc.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BaseUpcomingServiceAdapter extends RecyclerView.Adapter<BaseUpcomingServiceAdapter.MyViewHolder> {

    private List<BaseUpcomingService> serviceList;
    private Context context;
    private LayoutInflater layoutInflater;

    public BaseUpcomingServiceAdapter(Context context, List<BaseUpcomingService> serviceList) {
        this.serviceList = serviceList;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseUpcomingServiceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View v = layoutInflater.inflate(R.layout.upcoming_service_item, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseUpcomingServiceAdapter.MyViewHolder holder, int i) {
        BaseUpcomingService service = serviceList.get(i);

        holder.tvDue.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(service.getServiceDate()));
        int period = Days.daysBetween(new DateTime(service.getOverDueDate()).toLocalDate(), new DateTime().toLocalDate()).getDays();

        if(period >= 0 ){
            holder.tvOverdue.setText(context.getString(R.string.days_overdue, String.valueOf(period)));
            holder.tvOverdue.setTextColor(context.getResources().getColor(R.color.vaccine_red_bg_end));
        }else {
            int periodDue = Days.daysBetween(new DateTime(service.getServiceDate()).toLocalDate(), new DateTime().toLocalDate()).getDays();

            if(periodDue >= 0){
                holder.tvOverdue.setText(context.getString(R.string.days_due, String.valueOf(Math.abs(periodDue))));
            }
            else {
                holder.tvOverdue.setText(context.getString(R.string.days_until_due, String.valueOf(Math.abs(periodDue))));
            }

            holder.tvOverdue.setTextColor(context.getResources().getColor(R.color.grey));
        }
        // add the titles
        inflateTitles(holder.linearLayoutTitle, service.getServiceNames());
        inflateSubtext(holder.linearLayoutSubTitles, service.getUpcomingServiceList());
    }

    private void inflateTitles(LinearLayout parent, List<String> titles){
        if(titles == null || titles.size() == 0)
            return;

        for(String s : titles){
            TextView textView = (TextView) layoutInflater.inflate(R.layout.upcoming_service_item_name, null);
            textView.setText(s);
            parent.addView(textView);
        }
    }

    private void inflateSubtext(LinearLayout parent, List<BaseUpcomingService> subtexts){
        if(subtexts == null || subtexts.size() == 0)
            return;

        for(BaseUpcomingService service : subtexts){
            TextView textView = (TextView) layoutInflater.inflate(R.layout.upcoming_service_item_subtext, null);
            textView.setText(service.getServiceName());
            parent.addView(textView);
        }
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDue, tvOverdue;
        private LinearLayout linearLayoutTitle, linearLayoutSubTitles;
        private View myView;

        private MyViewHolder(View view) {
            super(view);
            tvDue = view.findViewById(R.id.due_date);
            tvOverdue = view.findViewById(R.id.overdue_state);
            linearLayoutTitle = view.findViewById(R.id.linearLayoutTitle);
            linearLayoutSubTitles = view.findViewById(R.id.linearLayoutSubTitles);
            myView = view;
        }

        public View getView() {
            return myView;
        }
    }
}
