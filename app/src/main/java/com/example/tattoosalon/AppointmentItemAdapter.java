package com.example.tattoosalon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AppointmentItemAdapter extends RecyclerView.Adapter<AppointmentItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<AppointmentItem> mAppointmentItemData = new ArrayList<>();
    private ArrayList<AppointmentItem> mAppointmentItemDataAll = new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;

    public AppointmentItemAdapter(ArrayList<AppointmentItem> mSAppointmentItemData, Context mContext) {
        this.mAppointmentItemData = mSAppointmentItemData;
        this.mAppointmentItemDataAll = mSAppointmentItemData;
        this.mContext = mContext;
    }

    @Override
    public AppointmentItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.appointment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentItemAdapter.ViewHolder holder, int position) {
        AppointmentItem currentItem = mAppointmentItemData.get(position);

        holder.bindTo(currentItem);


        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mAppointmentItemData.size();
    }

    @Override
    public Filter getFilter() {
        return AppointmentFilter;
    }

    private Filter AppointmentFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<AppointmentItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                results.count = mAppointmentItemDataAll.size();
                results.values = mAppointmentItemDataAll;
            } else {
                String filterPattern = charSequence.toString();
                for (AppointmentItem item : mAppointmentItemDataAll) {
                    if (item.getDate().contains(filterPattern) || item.getTime().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mAppointmentItemData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mDateText;
        private TextView mTimeText;
        private AppointmentItem item;

        ViewHolder(View itemView) {
            super(itemView);


            mDateText = itemView.findViewById(R.id.date);
            mTimeText = itemView.findViewById(R.id.time);

            itemView.findViewById(R.id.makeAppointment).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AppointmentListActivity) mContext).updateAlertIcon(item);
                }
            });
        }

        void bindTo(AppointmentItem currentItem) {
            item = currentItem;
            mDateText.setText(currentItem.getDate());
            mTimeText.setText(currentItem.getTime());
        }
    }
}
