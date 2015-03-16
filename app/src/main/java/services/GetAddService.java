package services;

import android.content.Context;

import Models.AdsMessage;

/**
 * Created by android on 11/11/14.
 */
public class GetAddService extends BaseService {

    public GetAddService(Context ctx) {
        super(ctx);
    }

    public void getAdd(String token, CallBack obj) {
        String url = Constants.BASE_URL + Constants.GETADS+"&auth_token="+token;
        this.get(url, obj, AdsMessage.getInstance(), false);
    }

}
