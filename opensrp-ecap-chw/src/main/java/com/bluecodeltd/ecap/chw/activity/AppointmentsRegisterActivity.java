package com.bluecodeltd.ecap.chw.activity;

import com.bluecodeltd.ecap.chw.util.ServiceRegister;

public class AppointmentsRegisterActivity extends ServiceRegisterActivity {

    @Override
    public ServiceRegister getServiceRegister() {
        return ServiceRegister.APPOINTMENTS;
    }
}
