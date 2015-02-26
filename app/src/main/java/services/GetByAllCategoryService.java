package services;

import android.content.Context;
import android.util.Log;

import Models.GetAllByCategoryModel;

/**
 * Created by android on 11/11/14.
 */
public class GetByAllCategoryService extends BaseService {

    public GetByAllCategoryService(Context ctx) {
        super(ctx);
    }

    public void getbycategory(String category,String duration,String token,int index,boolean message,CallBack obj) {
        String url = Constants.BASE_URL + Constants.GETALLCATEGORY_API+"category="+category+"&duration="+duration+"&auth_token="+token+"&page="+index;
        this.get(url, obj, GetAllByCategoryModel.getInstance(), message);
        Log.e("Url",url);
    }
    public void getbysearch(String search_query,String token,int index,boolean message,CallBack obj) {
        String url = Constants.BASE_URL + Constants.GETALLSEARCH_API+"auth_token="+token+"&query="+search_query+"&page="+index;
        this.get(url, obj, GetAllByCategoryModel.getInstance(), message);
        Log.e("Url",url);
    }
}
