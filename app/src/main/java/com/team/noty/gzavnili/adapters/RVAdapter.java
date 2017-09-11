package com.team.noty.gzavnili.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team.noty.gzavnili.R;

import java.util.List;

/**
 * Created by copch on 24.08.2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ParcelViewHolder> {

    public static class ParcelViewHolder extends RecyclerView.ViewHolder {


        TextView mTxtTrackingNumber, mTxtStore, mTxtWeight, mTxtDeliveryPrice;
        ImageView btnDeleteParcel;
        LinearLayout linearLayout;

        ParcelViewHolder(View itemView) {
            super(itemView);
            mTxtTrackingNumber = (TextView)itemView.findViewById(R.id.txt_tracking_number);
            mTxtStore = (TextView)itemView.findViewById(R.id.txt_store);
            mTxtWeight = (TextView)itemView.findViewById(R.id.txt_weight);
            mTxtDeliveryPrice = (TextView)itemView.findViewById(R.id.txt_delivery_price);

            btnDeleteParcel = (ImageView) itemView.findViewById(R.id.btn_delete_parcel);

            linearLayout = (LinearLayout) itemView.findViewById(R.id.layout_delivery_price);
        }
    }

    List<ParcelPaymentData> parcelPaymentDatas;

    public RVAdapter(List<ParcelPaymentData> persons){
        this.parcelPaymentDatas = persons;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ParcelViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_for_item_recycler, viewGroup, false);
        ParcelViewHolder pvh = new ParcelViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ParcelViewHolder parcelViewHolder, final int i) {
        parcelViewHolder.mTxtTrackingNumber.setText(parcelPaymentDatas.get(i).getTrackingNumber());
        parcelViewHolder.mTxtStore.setText(parcelPaymentDatas.get(i).getStore());
        parcelViewHolder.mTxtWeight.setText(parcelPaymentDatas.get(i).getWeight() + "kg");
        if (parcelPaymentDatas.get(i).getDeliveryPrice() != null) {
            parcelViewHolder.mTxtDeliveryPrice.setText("$" + parcelPaymentDatas.get(i).getDeliveryPrice());
            parcelViewHolder.linearLayout.setVisibility(View.VISIBLE);
        }
        else {
            parcelViewHolder.linearLayout.setVisibility(View.GONE);
        }

        if (parcelPaymentDatas.size() == 1){
            parcelViewHolder.btnDeleteParcel.setVisibility(View.GONE);
        }
        else {
            parcelViewHolder.btnDeleteParcel.setVisibility(View.VISIBLE);
        }

        parcelViewHolder.btnDeleteParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parcelPaymentDatas.remove(i);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return parcelPaymentDatas.size();
    }
}
