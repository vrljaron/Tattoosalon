package com.example.tattoosalon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReserveAppointmentListAdapter extends RecyclerView.Adapter<ReserveAppointmentListAdapter.ViewHolder> {
    private ArrayList<AppointmentItem> mAppointmentItemData = new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;

    public ReserveAppointmentListAdapter(ArrayList<AppointmentItem> mAppointmentItemData, Context mContext) {
        this.mAppointmentItemData = mAppointmentItemData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ReserveAppointmentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReserveAppointmentListAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.reserve_appointment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentItem currentItem = mAppointmentItemData.get(position);

        holder.bindTo(currentItem);


        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row_from_left);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mAppointmentItemData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mDateText;
        private final TextView mTimeText;
        private AppointmentItem item;

        ViewHolder(View itemView) {
            super(itemView);


            mDateText = itemView.findViewById(R.id.date);
            mTimeText = itemView.findViewById(R.id.time);

            itemView.findViewById(R.id.reserveAppointment).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ReserveAppointmentListActivity) mContext).reserveAppointment(item);
                }
            });

            itemView.findViewById(R.id.deleteAppointment).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ReserveAppointmentListActivity) mContext).deleteAppointment(item);
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
