package com.team.noty.gzavnili.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.team.noty.gzavnili.R;

import java.util.List;

/**
 * Created by copch on 24.08.2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ParcelViewHolder> {

    public static class ParcelViewHolder extends RecyclerView.ViewHolder {


        TextView mTxtTrackingNumber, mTxtStore, mTxtWeight, mTxtDeliveryPrice;

        ParcelViewHolder(View itemView) {
            super(itemView);
            mTxtTrackingNumber = (TextView)itemView.findViewById(R.id.txt_tracking_number);
            mTxtStore = (TextView)itemView.findViewById(R.id.txt_store);
            mTxtWeight = (TextView)itemView.findViewById(R.id.txt_weight);
            mTxtDeliveryPrice = (TextView)itemView.findViewById(R.id.txt_delivery_price);
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
    public void onBindViewHolder(ParcelViewHolder personViewHolder, int i) {
        personViewHolder.mTxtTrackingNumber.setText(parcelPaymentDatas.get(i).getTrackingNumber());
        personViewHolder.mTxtStore.setText(parcelPaymentDatas.get(i).getStore());
        personViewHolder.mTxtWeight.setText(parcelPaymentDatas.get(i).getWeight() + "kg");
        personViewHolder.mTxtDeliveryPrice.setText("$" + parcelPaymentDatas.get(i).getDeliveryPrice());

    }

    @Override
    public int getItemCount() {
        return parcelPaymentDatas.size();
    }
}
