package com.taxiuser.utils;


import com.taxiuser.models.ModelCurrentBooking;

public interface onSearchingDialogListener {
    void onRequestAccepted(ModelCurrentBooking data);
    void onRequestCancel();
    void onDriverNotFound();
}
