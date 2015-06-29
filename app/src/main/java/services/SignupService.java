package services;

import android.content.Context;

import java.util.HashMap;

import Models.UserModel;

/**
 * Created by android on 11/11/14.
 */
public class SignupService extends BaseService {

    public SignupService(Context ctx) {
        super(ctx);
    }

    public void signup(String mEmail, String mPassword, String confirmPassword,String GCM_id,boolean message, CallBack obj) {
        String url = Constants.BASE_URL + Constants.SIGNUP_API;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user[email]", mEmail);
        params.put("user[password]", mPassword);
        params.put("user[gcm_id]", GCM_id);
        params.put("user[confirmation_password]", confirmPassword);
        params.put("user[time_usage]","3");

        this.post(url, params, obj, UserModel.getInstance(), message);
    }
}