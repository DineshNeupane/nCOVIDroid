package com.dineshneupane.ncovidroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dineshneupane.ncovidroid.DataModel.DataModel;
import com.dineshneupane.ncovidroid.R;


import java.util.List;

public class DataListRecyclerAdapter extends RecyclerView.Adapter<DataListRecyclerAdapter.DataViewHolder> {

    private List<DataModel> dataModelList;

    public class DataViewHolder extends RecyclerView.ViewHolder {
        public TextView country, lastUpdateDate, confirmedCases, totalDeaths, casesRecovered, fatality, region;

        public DataViewHolder(View view) {
            super(view);
            country = view.findViewById(R.id.country);
            lastUpdateDate = view.findViewById(R.id.lastUpdateDate);
            confirmedCases = view.findViewById(R.id.confirmedCases);
            totalDeaths = view.findViewById(R.id.totalDeaths);
            casesRecovered = view.findViewById(R.id.casesRecovered);
            fatality = view.findViewById(R.id.fatality);
            region = view.findViewById(R.id.region);
        }
    }


    public DataListRecyclerAdapter(List<DataModel> dataModelList) {
        this.dataModelList = dataModelList;
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_recycler_item, parent, false);

        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {

        DataModel dataModel = dataModelList.get(position);
        holder.country.setText(dataModel.getCountry());
        holder.lastUpdateDate.setText(dataModel.getLastUpdateDate());
        holder.confirmedCases.setText(dataModel.getConfirmedCases());
        holder.totalDeaths.setText(dataModel.getTotalDeaths());
        holder.casesRecovered.setText(dataModel.getCasesRecovered());

        if (dataModel.getRegion().isEmpty()){
            holder.region.setVisibility(View.GONE);
        }

        holder.region.setText(dataModel.getRegion());


        double percentage= 100.0*Integer.valueOf(dataModel.getTotalDeaths())/Integer.valueOf(dataModel.getConfirmedCases());
        String percentageText="• Fatality Rate: "+String.format("%2.02f", percentage) +"%";

        holder.fatality.setText(percentageText);


    }

    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    public void onRefreshAdapter(List<DataModel> dataModels) {
        dataModelList = dataModels;
        notifyDataSetChanged();
    }
}
