package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.model.SopDocumentModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SopDocumentsAdapter extends RecyclerView.Adapter<SopDocumentsAdapter.Holder> {

    public interface Listener {
        void onOpen(SopDocumentModel doc);
    }

    private final Context context;
    private final Listener listener;
    private final List<SopDocumentModel> items = new ArrayList<>();

    public SopDocumentsAdapter(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setItems(List<SopDocumentModel> docs) {
        items.clear();
        if (docs != null) items.addAll(docs);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_sop_document, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        SopDocumentModel doc = items.get(position);
        String title = doc != null ? doc.getDisplayName() : null;
        holder.title.setText(!TextUtils.isEmpty(title) ? title : "Document");
        holder.subtitle.setText(formatSize(doc != null ? doc.getSizeBytes() : 0));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && doc != null) listener.onOpen(doc);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;

        Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.sop_title);
            subtitle = itemView.findViewById(R.id.sop_subtitle);
        }
    }

    private static String formatSize(long bytes) {
        if (bytes <= 0) return "";
        double kb = bytes / 1024.0;
        double mb = kb / 1024.0;
        DecimalFormat df = new DecimalFormat("#.##");
        if (mb >= 1) return df.format(mb) + " MB";
        if (kb >= 1) return df.format(kb) + " KB";
        return bytes + " B";
    }
}

