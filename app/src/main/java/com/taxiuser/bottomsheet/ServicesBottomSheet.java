package com.taxiuser.bottomsheet;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taxiuser.R;
import com.taxiuser.activities.RideOptionAct;
import com.taxiuser.adapters.AdapterCarTypes;
import com.taxiuser.adapters.AdapterService;
import com.taxiuser.databinding.FragmentServiceBinding;
import com.taxiuser.models.CityModel;
import com.taxiuser.models.ModelCar;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.models.ModelServicess;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.SharedPref;
import com.taxiuser.utils.retrofitutils.Api;
import com.taxiuser.utils.retrofitutils.ApiFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicesBottomSheet  extends BottomSheetDialogFragment {
    public String TAG = "ServicesBottomSheet";
    FragmentServiceBinding binding;
    BottomSheetDialog dialog;
    private BottomSheetBehavior<View> mBehavior;
    ServListener listener;
    Api apiInterface;
    String city="",PickUplat="",PickUplon="",DropOffLat="",DropOffLon="",selectedService="";
    ModelLogin modelLogin;
    SharedPref sharedPref;

    public ServicesBottomSheet(String city,String PickUplat,String PickUplon,String DropOffLat,String DropOffLon) {
        this.city = city;
        this.PickUplat = PickUplat;
        this.PickUplon = PickUplon;
        this.DropOffLat = DropOffLat;
        this.DropOffLon = DropOffLon;

    }


    public ServicesBottomSheet callBack(ServListener listener) {
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),R.layout.fragment_service,null,false);
        dialog.setContentView(binding.getRoot());
        mBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        initViews();
        return dialog;
    }

    private void initViews() {
        sharedPref = SharedPref.getInstance(getActivity());
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        getCar();


        binding.ivBack.setOnClickListener(v -> dismiss());

        binding.btnApply.setOnClickListener(v -> {
            if (selectedService.equals(""))
                Toast.makeText(getActivity(), getString(R.string.please_select_service_type), Toast.LENGTH_SHORT).show();

            else{
                listener.onService(1,selectedService);  // AddCommaValues()
                dismiss();
            }
        });
    }

    private void getCar() {
        HashMap<String, String> param = new HashMap<>();
        param.put("lat", DropOffLat);
        param.put("lon",DropOffLon);
        param.put("city",city);
        Log.e("fsdafsfadsf", "param = " + param);
        Api  apiInterface = ApiFactory.getClientWithoutHeader(getActivity()).create(Api.class);
        Call<ResponseBody> call = apiInterface.getAllServices(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject object = new JSONObject(responseString);
                    Log.e(TAG, "GetServicess Response = " + responseString);
                    if (object.getString("status").equals("1")) {
                        binding.tvNotFound.setVisibility(View.GONE);
                        ArrayList<ModelServicess.Result> arrayList = new ArrayList<>();
                            ModelServicess modelServicess = new Gson().fromJson(responseString, ModelServicess.class);
                            arrayList.addAll(modelServicess.getResult());
                            arrayList.get(0).setSelected(true);
                            binding.rvServices.setAdapter(new AdapterService(getActivity(), arrayList).Callback(ServicesBottomSheet.this::onSelectCar));
                           // onSelectCar(cars.get(0), "basic_car");
                        }
                    else {
                        binding.tvNotFound.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }


    private void onSelectCar(ModelCar car, String name) {
      selectedService =  name ;

    }


}
