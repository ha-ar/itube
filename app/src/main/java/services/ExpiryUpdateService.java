package services;

import android.content.Context;
import android.util.Log;

import Models.AdsMessage;


/**
 * Created by android on 10/10/14.
 */
public class ExpiryUpdateService extends BaseService {

    public ExpiryUpdateService(Context ctx) {
        super(ctx);
    }

    public void expiryUpdate(String minutes,boolean message,String token, CallBack obj){
        String url = Constants.BASE_URL + Constants.UPDATE_EXPIRY_API+"?auth_token=" + token+"&user[expiry]=" + minutes;
        Log.e("url", url);
        this.get(url, obj, AdsMessage.getInstance(), message);
    }
}
