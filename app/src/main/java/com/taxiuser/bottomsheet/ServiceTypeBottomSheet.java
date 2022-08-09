package com.taxiuser.bottomsheet;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.taxiuser.R;
import com.taxiuser.adapters.AdapterService;
import com.taxiuser.adapters.AdapterType;
import com.taxiuser.databinding.FragmentServiceBinding;
import com.taxiuser.databinding.FragmentServiceTypeBinding;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.models.ServiceTypeModel;
import com.taxiuser.utils.SharedPref;
import com.taxiuser.utils.retrofitutils.Api;

import java.util.ArrayList;

public class ServiceTypeBottomSheet extends BottomSheetDialogFragment {
    public String TAG = "ServiceTypeBottomSheet";
    FragmentServiceTypeBinding binding;
    BottomSheetDialog dialog;
    private BottomSheetBehavior<View> mBehavior;
    ServiceTypeListener listener;
    Api apiInterface;
    String selectedService="",selectedServiceType="";
    ArrayList<ServiceTypeModel>arrayList;

    public ServiceTypeBottomSheet(String selectedService ) {
        this.selectedService = selectedService;
    }


    public ServiceTypeBottomSheet callBack(ServiceTypeListener listener) {
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_service_type,null,false);
        dialog.setContentView(binding.getRoot());
        mBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        initViews();
        return dialog;
    }

    private void initViews() {
        arrayList = new ArrayList<>();
        arrayList.add(new ServiceTypeModel("1","Hourly rate",false));
        arrayList.add(new ServiceTypeModel("2","Fixed price for pickup to designation",false));
        arrayList.add(new ServiceTypeModel("3","Distance + Time",false));

        binding.rvServices.setAdapter(new AdapterType(getActivity(), arrayList).Callback(ServiceTypeBottomSheet.this::onSelectType));


        binding.ivBack.setOnClickListener(v -> dismiss());

        binding.btnApply.setOnClickListener(v -> {
            if (selectedServiceType.equals(""))
                Toast.makeText(getActivity(), getString(R.string.please_select_service), Toast.LENGTH_SHORT).show();

            else{
                listener.onType(selectedServiceType,selectedService);  // AddCommaValues()
                dismiss();
            }
        });
    }

    private void onSelectType(String s) {
        selectedServiceType = s;
    }

}
