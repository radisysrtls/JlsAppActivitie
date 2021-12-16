/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.
 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.
 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.SOSData;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

public class UpdateSOSContactRequest implements IRequest {

    private Response.Listener successListener;
    private Response.ErrorListener errorListener;
    private String userToken;
    private String deviceId;
    private SOSData sosData;
    private String phonebookId;

    public UpdateSOSContactRequest(Response.Listener successListener, Response.ErrorListener errorListener, SOSData sosData, String phonebookId, String userToken, String deviceId) {
        this.successListener = successListener;
        this.errorListener = errorListener;
        this.userToken = userToken;
        this.deviceId = deviceId;
        this.sosData = sosData;
        this.phonebookId = phonebookId;
    }

    @Override
    public String getReqParams() {
        return Util.getInstance().toJSON(sosData);
    }

    @Override
    public int getMethod() {
        return Request.Method.PUT;
    }

    @Override
    public String getAction() {
        return Constant.DELETE_DEVICE_URL + deviceId + Constant.DELETE_SOS_CONTACT_URL + phonebookId + "?ugs_token=" + userToken;
    }

    @Override
    public Response.ErrorListener getErrorListener() {
        return errorListener;
    }

    @Override
    public Response.Listener<String> getSuccessListener() {
        return successListener;
    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public boolean isHandleError() {
        return true;
    }

}
