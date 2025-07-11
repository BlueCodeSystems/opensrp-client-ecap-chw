package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.model.Items;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{

    Context context;

    List<Items> items;

    public ItemAdapter(List<Items> items, Context context){

        super();

        this.items = items;
        this.context = context;
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_flag, parent, false);

        ItemAdapter.ViewHolder viewHolder = new ItemAdapter.ViewHolder(v, viewType);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemAdapter.ViewHolder holder, final int position) {

        final Items myitems =  items.get(position);


        holder.txtVcaid.setText(myitems.getVcaid());
        holder.txtFormname.setText(myitems.getFormname());
        holder.txtUser.setText(myitems.getUser());

        if (myitems.getStatus().equals("1")){

            holder.imgFlag.setColorFilter(ContextCompat.getColor(context, org.smartregister.chw.core.R.color.orange), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if(myitems.getStatus().equals("2")) {

            holder.imgFlag.setColorFilter(ContextCompat.getColor(context, org.smartregister.chw.core.R.color.colorGreen), android.graphics.PorterDuff.Mode.MULTIPLY);

        }


    /*    holder.lview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {


                    case (R.id.itemm):

                        if (myitems.getVideo().equals("0")){

                            Intent intent = new Intent(context, NewsDetails.class);
                            intent.putExtra("pic",  items.get(position));
                            context.startActivity(intent);

                        } else {

                            Intent intent = new Intent(context, NewsVideo.class);
                            intent.putExtra("video",  items.get(position));
                            context.startActivity(intent);

                        }

                        break;
                }
            }

        });*/

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        TextView txtVcaid, txtFormname, txtUser;

        RelativeLayout relativeLayout;

        ImageView imgFlag;


        public ViewHolder(View itemView, int viewType) {

            super(itemView);

            txtVcaid  = itemView.findViewById(R.id.vcaid);
            txtFormname  = itemView.findViewById(R.id.formname);
            txtUser =  itemView.findViewById(R.id.user);
            relativeLayout =  itemView.findViewById(R.id.ritem);
            imgFlag = itemView.findViewById(R.id.imgFlag);

        }

        // Click event for all items
        @Override
        public void onClick(View v) {

        }
    }
}
