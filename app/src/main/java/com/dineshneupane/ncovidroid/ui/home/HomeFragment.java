package com.dineshneupane.ncovidroid.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dineshneupane.ncovidroid.DataModel.DataModel;
import com.dineshneupane.ncovidroid.R;
import com.dineshneupane.ncovidroid.adapters.DataListRecyclerAdapter;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    List<List<String>> formattedRows = new ArrayList<>();

    private RecyclerView recyclerView;
    private DataListRecyclerAdapter dataListRecyclerAdapter;
    private TextView tv_totConfirmedCases, tv_totalDeaths, tv_totalRecovered, tv_fatality;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout fragmentContent;
    private MaterialButtonToggleGroup materialButtonToggleGroup;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);

        tv_totConfirmedCases = root.findViewById(R.id.tot_cnfm_cases);
        tv_totalDeaths = root.findViewById(R.id.tot_deaths);
        tv_totalRecovered = root.findViewById(R.id.cases_recovered);
        tv_fatality=root.findViewById(R.id.fatality);
        fragmentContent = root.findViewById(R.id.frag_content);
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);

        getActivity().setTitle("nCOVIDroid Data");

        // Button btn_countries=root.findViewById(R.id.btn_countries);

        materialButtonToggleGroup = root.findViewById(R.id.toggleGroup);
        prepareData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareData();
                materialButtonToggleGroup.check(R.id.btn_countries);
            }
        });

        return root;
    }


    private void prepareData() {

        String url = "";
        List<DataModel> dataModelList = new ArrayList<>();
        List<DataModel> dataModelListCountrywise = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String today = dateFormat.format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String yesterday = dateFormat.format(cal.getTime());

        if (dataExists("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/" + today + ".csv")) {
            url = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/" + today + ".csv";
        } else {
            url = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/" + yesterday + ".csv";
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                swipeRefreshLayout.setRefreshing(true);
                fragmentContent.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                int totConfirmedCases = 0;
                int totalDeaths = 0;
                int totalRecovered = 0;

                swipeRefreshLayout.setRefreshing(false);
                fragmentContent.setVisibility(View.VISIBLE);

                if (formattedRows != null) {
                    formattedRows.clear();
                }

                List<String> items = Arrays.asList(new String(response).split("\\r?\\n"));
                for (int i = 1; i < items.size(); i++) {
                    formattedRows.add(Arrays.asList(items.get(i).split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)")));
                }

                ArrayList<String> countriesName = new ArrayList<>();
                for (int i = 0; i < formattedRows.size(); i++) {
                    List<String> indV = formattedRows.get(i);

                    countriesName.add(indV.get(1));

                    DataModel allData = new DataModel(indV.get(1), "as of " + indV.get(2), indV.get(3), indV.get(4), indV.get(5), indV.get(0));
                    dataModelList.add(allData);
                    totConfirmedCases = totConfirmedCases + Integer.parseInt(indV.get(3));
                    totalDeaths = totalDeaths + Integer.parseInt(indV.get(4));
                    totalRecovered = totalRecovered + Integer.parseInt(indV.get(5));
                }


                Map<String, List<DataModel>> map = dataModelList.stream().collect(Collectors.groupingBy(DataModel::getCountry));

                map.entrySet().stream().forEach(e -> {
                            int ccs = 0, deaths = 0, recovered = 0;
                            for (int i = 0; i < e.getValue().size(); i++) {
                                ccs += Integer.parseInt(e.getValue().get(i).getConfirmedCases());
                                deaths += Integer.parseInt(e.getValue().get(i).getTotalDeaths());
                                recovered += Integer.parseInt(e.getValue().get(i).getCasesRecovered());
                            }
                            DataModel allData = new DataModel(e.getKey(), "as of today", String.valueOf(ccs), String.valueOf(deaths), String.valueOf(recovered), "• All regions");
                            dataModelListCountrywise.add(allData);
                        }
                );

                Collections.sort(dataModelListCountrywise, new Comparator<DataModel>() {
                    public int compare(DataModel obj1, DataModel obj2) {
                        return Integer.valueOf(obj2.getConfirmedCases()).compareTo(Integer.valueOf(obj1.getConfirmedCases())); // To compare integer values
                    }
                });

                HashSet<String> hashSet = new HashSet<String>();
                hashSet.addAll(countriesName);
                countriesName.clear();
                countriesName.addAll(hashSet);
                setUpRecyclerView(dataModelListCountrywise);
                materialButtonToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
                    @Override
                    public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                        if (group.getCheckedButtonId() == -1) {
                            group.check(checkedId);
                            setUpRecyclerView(dataModelListCountrywise);
                        }
                        if (group.getCheckedButtonId() == R.id.btn_countries)
                            setUpRecyclerView(dataModelListCountrywise);
                        if (group.getCheckedButtonId() == R.id.btn_provinces)
                            setUpRecyclerView(dataModelList);
                    }
                });

                tv_totConfirmedCases.setText(String.valueOf(totConfirmedCases));
                tv_totalDeaths.setText(String.valueOf(totalDeaths));
                tv_totalRecovered.setText(String.valueOf(totalRecovered));

                double percentage = 100.0 * totalDeaths / totConfirmedCases;
                String percentageText="• Fatality Rate: "+String.format("%2.02f", percentage) +"%";
                tv_fatality.setText(percentageText);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                swipeRefreshLayout.setRefreshing(false);
                //  Toast.makeText(getActivity(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public static boolean dataExists(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(URLName)
                    .openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setUpRecyclerView(List<DataModel> dm) {
        //dataListRecyclerAdapter.notifyDataSetChanged();
        dataListRecyclerAdapter = new DataListRecyclerAdapter(dm);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(dataListRecyclerAdapter);
    }
}