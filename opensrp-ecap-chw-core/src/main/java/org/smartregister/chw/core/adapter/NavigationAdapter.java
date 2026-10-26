package org.smartregister.chw.core.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.listener.NavigationListener;
import org.smartregister.chw.core.model.NavigationOption;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.MyViewHolder> {

    private List<NavigationOption> navigationOptionList;
    private View.OnClickListener onClickListener;
    private Context context;
    private Map<String, Class> registeredActivities;
    private NavigationAdapterHost host;

    public NavigationAdapter(List<NavigationOption> navigationOptions, Activity context, Map<String, Class> registeredActivities, NavigationAdapterHost host) {
        this.navigationOptionList = navigationOptions;
        this.context = context;
        this.onClickListener = new NavigationListener(context, this);
        this.registeredActivities = registeredActivities;
        this.host = host;
    }

    public String getSelectedView() {
        return StringUtils.isBlank(host.getSelectedView()) ? CoreConstants.DrawerMenu.ALL_FAMILIES : host.getSelectedView();
    }

    public void setSelectedView(String selectedView) {
        host.setSelectedView(selectedView);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.navigation_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NavigationOption model = navigationOptionList.get(position);
        holder.tvName.setText(context.getResources().getText(model.getTitleID()));
        holder.tvName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,
                context.getResources().getDimension(R.dimen.nav_item_text_size));
        if(context.getResources().getText(model.getTitleID()).equals("Home"))
        {
            holder.tvCount.setText("");
            holder.tvCount.setVisibility(View.GONE);
        }
        else {
            holder.tvCount.setText(String.format(Locale.getDefault(), "%d", model.getRegisterCount()));
            holder.tvCount.setVisibility(View.VISIBLE);
        }

        holder.tvCount.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,
                context.getResources().getDimension(R.dimen.nav_item_count_size));

        holder.ivIcon.setImageResource(model.getResourceID());

        holder.getView().setTag(model.getMenuTitle());


        // The open register gets a solid blue pill with white text and icon, and a white count badge.
        // Icons are tinted because the register PNGs are mid-grey or blue, which vanish on the navy drawer.
        if (host.getSelectedView().equals(model.getMenuTitle())) {
            holder.getView().setBackgroundResource(R.drawable.nav_item_selected_background);
            holder.tvCount.setBackgroundResource(R.drawable.nav_count_badge_selected);
            holder.tvCount.setTextColor(ContextCompat.getColor(context, R.color.nav_selected_bg));
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.ivIcon.setImageResource(model.getResourceActiveID());
            holder.ivIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
        } else {
            holder.getView().setBackgroundResource(R.drawable.nav_item_background);
            holder.tvCount.setBackgroundResource(R.drawable.nav_count_badge);
            holder.tvCount.setTextColor(ContextCompat.getColor(context, R.color.nav_badge_text));
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.nav_text));
            holder.ivIcon.setImageResource(model.getResourceID());
            holder.ivIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.nav_icon)));
        }
    }

    @Override
    public int getItemCount() {
        return navigationOptionList.size();
    }

    public Map<String, Class> getRegisteredActivities() {
        return registeredActivities;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvCount;
        public ImageView ivIcon;

        private View myView;

        private MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvCount = view.findViewById(R.id.tvCount);
            ivIcon = view.findViewById(R.id.ivIcon);

            if (onClickListener != null) {
                view.setOnClickListener(onClickListener);
            }

            myView = view;
        }

        public View getView() {
            return myView;
        }
    }

}


