package services;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by hasanali on 1/10/14.
 */

public class BaseService {

    AQuery aq;


    public BaseService(Context ctx){
        aq = new AQuery(ctx);
    }

    public void get(String url, final CallBack callBack, final Object model, boolean showProgress){

        ProgressDialog progressDialog = null;
        if(showProgress){
            progressDialog = new ProgressDialog(aq.getContext());
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Loading...");
        }
        aq.progress(progressDialog).ajax(url, JSONObject.class,
                new AjaxCallback<JSONObject>() {

                    @Override
                    public void callback(String url, JSONObject json,
                                         AjaxStatus status) {
                        Object obj = model;
                        if (json != null) {
                            Gson gson = new Gson();
                            obj = gson.fromJson(json.toString(),
                                    obj.getClass());
//                            Log.e("JSON::", json.toString());
                            callBack.invoke(obj);
                        }else return;

                    }
                });
    }

    public void post(String url,HashMap<String, String> params,final CallBack callBack, final Object model, boolean showProgress){

        ProgressDialog progressDialog = null;
        if(showProgress){
            progressDialog = new ProgressDialog(aq.getContext());
            progressDialog.setMessage("Loading...");
        }
        aq.progress(progressDialog).ajax(url,params,JSONObject.class,
                new AjaxCallback<JSONObject>() {

                    @Override
                    public void callback(String url, JSONObject json,
                                         AjaxStatus status){

                        Object obj = model;
                        if (json != null) {
                            try {
                                Gson gson = new Gson();
                                obj = gson.fromJson(json.toString(),
                                        obj.getClass());
                                Log.e("JSON::", json.toString());
                                callBack.invoke(obj);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                        }else return;

                    }
                });
    }


}
