package com.jio.devicetracker.database.pojo.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.jio.devicetracker.database.pojo.Userdata;
import com.jio.devicetracker.network.IRequest;
import com.jio.devicetracker.util.Util;


public class LoginDataRequest implements IRequest {

    private Response.Listener sucessListener;
    private Response.ErrorListener errorListener;
    private Userdata user;

    public LoginDataRequest(Response.Listener sucessListener, Response.ErrorListener errorListener, Userdata object)
    {
        this.sucessListener = sucessListener;
        this.errorListener = errorListener;
        this.user = object;
    }
    @Override
    public String getReqParams() {
        return Util.getInstance().toJSON(user);
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public String getAction() {
        return "/accounts/api/users/login?isResponseDataAsUser=true&isPopulateGroup=true&isPopulateGroupUsers=true&isPopulateUserDevices=true&isPopulateUserDevicesAsWearableUsers=true";
    }

    @Override
    public Response.ErrorListener getErrorListener() {
        return errorListener;
    }

    @Override
    public Response.Listener<String> getSuccessListener() {
        return sucessListener;
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
