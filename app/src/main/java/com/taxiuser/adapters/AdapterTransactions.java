package com.taxiuser.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taxiuser.R;
import com.taxiuser.databinding.AdapterTransactionsBinding;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.models.ModelTransactions;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.SharedPref;

import java.util.ArrayList;

public class AdapterTransactions extends RecyclerView.Adapter<AdapterTransactions.MyPlanViewHolder> {

    Context mContext;
    ArrayList<ModelTransactions.Result> plansList;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public AdapterTransactions(Context mContext, ArrayList<ModelTransactions.Result> plansList) {
        this.mContext = mContext;
        this.plansList = plansList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public MyPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterTransactionsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.adapter_transactions, parent, false);
        return new MyPlanViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPlanViewHolder holder, int position) {

        ModelTransactions.Result data = plansList.get(position);

        holder.binding.tvDate.setText(data.getDate_time().split(" ")[0].trim());
        double amount = Double.parseDouble(data.getAmount()) * AppConstant.CURRENT_CURRENCY_VALUE;
        holder.binding.tvAmount.setText(AppConstant.CURRENCY + " " + String.format("%.2f", amount));

        if ("Credit".equals(data.getTransaction_type())) {
            holder.binding.tvDebitCredit.setText("Credit");
            holder.binding.ivSendRecive.setImageResource(R.drawable.credit_icon);
            holder.binding.tvDebitCredit.setTextColor(ContextCompat.getColor(mContext, R.color.green_spalsh));
        } else {
            holder.binding.ivSendRecive.setImageResource(R.drawable.debit_icon);
            holder.binding.tvDebitCredit.setText("Debit");
            holder.binding.tvDebitCredit.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }

    }

    @Override
    public int getItemCount() {
        return plansList == null ? 0 : plansList.size();
    }

    public class MyPlanViewHolder extends RecyclerView.ViewHolder {

        AdapterTransactionsBinding binding;

        public MyPlanViewHolder(AdapterTransactionsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
