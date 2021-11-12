package com.bluecodeltd.ecap.chw.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.model.Child;

import java.util.List;

public class ChildrenAdapter extends RecyclerView.Adapter<ChildrenAdapter.ViewHolder>{

    Context context;

    List<Child> children;

    public ChildrenAdapter(List<Child> children, Context context){

        super();

        this.children = children;
        this.context = context;
    }

    @Override
    public ChildrenAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_child, parent, false);

        ChildrenAdapter.ViewHolder viewHolder = new ChildrenAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChildrenAdapter.ViewHolder holder, final int position) {

        final Child child = children.get(position);

        holder.fullName.setText(child.getFirstname() + " " + child.getLastname());


        holder.lview.setOnClickListener(v -> {

            switch (v.getId()) {

                case (R.id.itemm):

                   /* Intent intent = new Intent(context, SingleMother.class);
                    intent.putExtra("mothers",  mothers.get(position));
                    context.startActivity(intent);*/
                    break;
            }
        });

    }

    @Override
    public int getItemCount() {

        return children.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView fullName;
        RelativeLayout lview;

        public ViewHolder(View itemView) {

            super(itemView);


            fullName  = (TextView) itemView.findViewById(R.id.familyNameTextView);
            lview = (RelativeLayout) itemView.findViewById(R.id.register_columns);

        }

        // Click event for all items
        @Override
        public void onClick(View v) {

        }
    }
}
