package org.smartregister.chw.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.CoreCommunityRespondersRegisterActivity;
import org.smartregister.chw.core.model.CommunityResponderModel;

import java.util.List;

public class CommunityResponderCustomAdapter extends ArrayAdapter<CommunityResponderModel> implements View.OnClickListener {

    private Context mContext;
    private int lastPosition = -1;
    private CoreCommunityRespondersRegisterActivity activity;

    public CommunityResponderCustomAdapter(List<CommunityResponderModel> data, Context context, CoreCommunityRespondersRegisterActivity activity) {
        super(context, 0, data);
        this.mContext = context;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        CommunityResponderModel object = getItem(position);
        activity.showPopUpMenu(v, object);
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        CommunityResponderModel communityResponderModel = getItem(position);
        View view = convertView;
        ViewHolder viewHolder;
        final View result;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = view.findViewById(R.id.responder_name);
            viewHolder.txtPhoneNumber = view.findViewById(R.id.responder_phone);
            viewHolder.editDelete = view.findViewById(R.id.edit_delete);
            result = view;
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result = view;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.txtName.setText(communityResponderModel.getResponderName());
        viewHolder.txtPhoneNumber.setText(communityResponderModel.getResponderPhoneNumber());
        viewHolder.editDelete.setOnClickListener(this);
        viewHolder.editDelete.setTag(position);
        return view;
    }

    private static class ViewHolder {
        private TextView txtName;
        private TextView txtPhoneNumber;
        private ImageView editDelete;
    }
}
