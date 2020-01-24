// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.jiotoken;

import android.content.Context;

import com.jio.devicetracker.util.Constant;
import com.jio.iot.sso.JiotAuthenticationManager;
import com.jio.iot.sso.JiotSSOApp;

@SuppressWarnings({"PMD.ClassNamingConventions", "UseUtilityClass"})
public final class JioToken {
    private static JiotSSOApp jioSSo;
    private static JiotAuthenticationManager mJioAuth;

    private JioToken() {

    }

    public static void getSSOIdmaToken(Context mContext)
    {
        jioSSo = new JiotSSOApp(Constant.REG_ID,Constant.REG_KEY,Constant.REG_SVC);
        mJioAuth = JiotAuthenticationManager.getAuhtenticateManager(mContext);
        mJioAuth.getSSOToken(Constant.App_Name, jioSSo, new JiotokenHandler());
    }
}
