package services;

import android.content.Context;
import android.util.Log;

import Models.DurationModel;
import Models.GetAllByCategoryModel;
import co.vector.itube.BaseClass;

/**
 * Created by android on 11/11/14.
 */
public class GetByAllCategoryService extends BaseService {

    public GetByAllCategoryService(Context ctx) {
        super(ctx);
    }

    public void getbycategory(String category,String duration,String pagetoken,boolean message,CallBack obj) {
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="+category+"&maxResults=50&order=relevance&type=video&pageToken="+pagetoken+"&videoDuration="+"Any"+"&key="+BaseClass.KEY;
        this.get(url, obj, GetAllByCategoryModel.getInstance(), message);
        Log.e("Url",url);
    }
    public void getbysearch(String search_query,String pagetoken,boolean message,CallBack obj) {
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="+search_query+"&maxResults=50&order=relevance&type=video&pageToken="+pagetoken+"&videoDuration=Any&key="+BaseClass.KEY;
        this.get(url, obj, GetAllByCategoryModel.getInstance(), message);
        Log.e("Url",url);
    }
    public void getduration(String videoid,boolean message,CallBack obj) {
        String url = "https://www.googleapis.com/youtube/v3/videos?id="+videoid+"&part=contentDetails&key="+BaseClass.KEY;
        this.get(url, obj, DurationModel.getInstance(), message);
        Log.e("Url",url);
    }
}
