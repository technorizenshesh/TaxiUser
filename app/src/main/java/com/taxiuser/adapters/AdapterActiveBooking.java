package com.taxiuser.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taxiuser.R;
import com.taxiuser.activities.DriverFeedbackAct;
import com.taxiuser.activities.EndUserAct;
import com.taxiuser.activities.TrackAct;
import com.taxiuser.databinding.ItemRideHistoryBinding;
import com.taxiuser.models.ModelActiveBooking;
import com.taxiuser.models.ModelCurrentBooking;
import com.taxiuser.models.ModelCurrentBookingResult;
import com.taxiuser.utils.MyApplication;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.retrofitutils.Api;
import com.taxiuser.utils.retrofitutils.ApiFactory;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterActiveBooking extends RecyclerView.Adapter<AdapterActiveBooking.MyRideHolder> {

    Context mContext;
    ArrayList<ModelActiveBooking.Result> historyList;

    public AdapterActiveBooking(Context mContext, ArrayList<ModelActiveBooking.Result> historyList) {
        this.mContext = mContext;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRideHistoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.item_ride_history, parent, false);
        return new MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRideHolder holder, int position) {

        ModelActiveBooking.Result data = historyList.get(position);

        Log.e("paramparam", "param = " + data.getPicklatertime());
        Log.e("paramparam", "param = " + data.getPicklaterdate());
        Log.e("paramparam", "BookType = " + data.getBooktype());
        Log.e("paramparam", "BookType = " + data.getBooked_seats());

        holder.binding.tvFrom.setText(data.getPicuplocation());
        holder.binding.etDestination.setText(data.getDropofflocation());

        if (data.getBooktype().equals("POOL")) {
            holder.binding.tvPoolText.setVisibility(View.VISIBLE);
            holder.binding.tvPoolText.setText("Pool Booking For " + data.getBooked_seats() + " Passenger");
        } else {
            holder.binding.tvPoolText.setVisibility(View.GONE);
        }

        if (data.getBooktype().equals("NOW")) {
            holder.binding.tvDateTime.setText(data.getAccept_time());
        } else {
            holder.binding.tvDateTime.setText(data.getPicklaterdate() + " " + data.getPicklatertime());
        }

        holder.binding.tvStatus.setText(data.getStatus());

        holder.binding.getRoot().setOnClickListener(v -> {

            if("POOL".equalsIgnoreCase(data.getBooktype())) {
                if (data.getStatus().equals("Accept") ||
                        data.getStatus().equals("Start") ||
                        data.getStatus().equals("End")) {

                    if (data.getBooktype().equals("POOL")) {
                        getBookingDetails(data.getId());
                    } else {
                        Date date1 = null, date2 = null;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {

                            date1 = sdf.parse(data.getPicklaterdate());
                            date2 = sdf.parse(ProjectUtil.getCurrentDateNEW());

                            if (date1.equals(date2)) {
                                if (checktiming(data.getPicklatertime())) {
                                    getBookingDetails(data.getId());
                                } else {
                                    MyApplication.showAlert(mContext, " " + mContext.getString(R.string.your_booking_is) + " " + data.getPicklatertime());
                                }
                            } else if (date1.before(date2)) {
                                if (checktiming(data.getPicklatertime())) {
                                    getBookingDetails(data.getId());
                                } else {
                                    MyApplication.showAlert(mContext, mContext.getString(R.string.your_booking_is) + " " + data.getPicklatertime());
                                }
                            } else {
                                MyApplication.showAlert(mContext, mContext.getString(R.string.not_allowed_to_access_trip) + " " + data.getPicklaterdate());
                            }

                        } catch (ParseException e) {
                            Log.e("sdfsfdsffsd", "Date Exception = " + e.getMessage());
                            getBookingDetails(data.getId());
                            e.printStackTrace();
                        }

                    }

                }
            } else {
                getBookingDetails(data.getId());
            }


        });

    }

    private boolean checktiming(String time) {

        String pattern = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        Log.e("adasdasdasd", "book Time = " + time);
        Log.e("adasdasdasd", "Current Time = " + ProjectUtil.getCurrentTime());

        try {

            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(ProjectUtil.getCurrentTime());

            Log.e("adasdasdasd", "Current Time = " + date1.before(date2));

            if (date1.before(date2)) {
                return true;
            } else if (date1.equals(date2)) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;

    }

    private void getBookingDetails(String requestId) {

        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));

        HashMap<String, String> param = new HashMap<>();
        param.put("request_id", requestId);
        param.put("type", "USER");

        Log.e("paramparam", "param = " + param);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCurrentBookingDetails(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("getCurrentBooking", "getCurrentBooking = " + responseString);
                        Log.e("getCurrentBooking", "getCurrentBooking = " + responseString);
                        Type listType = new TypeToken<ModelCurrentBooking>(){}.getType();
                        ModelCurrentBooking data = new GsonBuilder().create().fromJson(responseString, listType);
                        if (data.getStatus().equals(1)) {
                            ModelCurrentBookingResult result = data.getResult().get(0);
                            Log.e("getUserRatingStatus", "getUserRatingStatus = " + result.getUserRatingStatus());
                            Log.e("getUserRatingStatus", "ModelCurrentBookingResult = " + result.getPayment_status());
                            if (result.getStatus().equalsIgnoreCase("Accept")) {
                                Intent k = new Intent(mContext, TrackAct.class);
                                k.putExtra("data", data);
                                mContext.startActivity(k);
                            } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                Intent k = new Intent(mContext, TrackAct.class);
                                k.putExtra("data", data);
                                mContext.startActivity(k);
                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                Intent k = new Intent(mContext, TrackAct.class);
                                k.putExtra("data", data);
                                mContext.startActivity(k);
                            } else if (result.getStatus().equalsIgnoreCase("End")) {
                                if ("Success".equals(result.getPayment_status())) {
                                    if (null == result.getUserRatingStatus() ||
                                            "".equals(result.getUserRatingStatus())) {
                                        Intent j = new Intent(mContext, DriverFeedbackAct.class);
                                        j.putExtra("data", data);
                                        mContext.startActivity(j);
                                    } else {
                                        MyApplication.showAlert(mContext,"Payment Confirmation Pending From Driver Side");
                                    }
                                } else {
                                    Intent j = new Intent(mContext, EndUserAct.class);
                                    j.putExtra("data", data);
                                    mContext.startActivity(j);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    @Override
    public int getItemCount() {
        return historyList == null ? 0 : historyList.size();
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        ItemRideHistoryBinding binding;

        public MyRideHolder(@NonNull ItemRideHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}