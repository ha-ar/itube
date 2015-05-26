//package co.vector.itube;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.androidquery.AQuery;
//import com.androidquery.callback.AjaxCallback;
//import com.androidquery.callback.AjaxStatus;
//import com.google.api.client.http.HttpRequest;
//import com.google.api.client.http.HttpRequestInitializer;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.services.youtube.YouTube;
//import com.google.api.services.youtube.model.SearchListResponse;
//import com.google.api.services.youtube.model.SearchResult;
//import com.google.gson.Gson;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import Models.DurationModel;
//import Models.GetAllByCategoryModel;
//import Models.VideoItem;
//
//public class YoutubeConnector {
//	private YouTube youtube;
//    GetAllByCategoryModel obj;
//    AQuery aq;
//
//    GetAllByCategoryModel.Videos video;
//	private YouTube.Search.List query;
//
//	// Your developer key goes here
//	public static final String KEY = "AIzaSyBTarcxurznZvnN9kZW9ekWe7JyQsLSLCo";
//
//	public YoutubeConnector(Context context) {
//        aq= new AQuery(context);
//		youtube = new YouTube.Builder(new NetHttpTransport(),
//				new JacksonFactory(), new HttpRequestInitializer() {
//			@Override
//			public void initialize(HttpRequest hr) throws IOException {}
//		}).setApplicationName(context.getString(R.string.app_name)).build();
//
//		try{
//			query = youtube.search().list("id,snippet");
//			query.setKey(KEY);
//			query.setMaxResults(50l);
//			query.setType("video");
////			query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
//		}catch(IOException e){
//			Log.d("YC", "Could not initialize: "+e);
//		}
//	}
//
//	public List<VideoItem> search(String keywords){
//		query.setQ(keywords);
//		try{
//			SearchListResponse response = query.execute();
//			Log.e("response", response.toPrettyString());
//			final List<SearchResult> results = response.getItems();
//
//			List<VideoItem> items = new ArrayList<VideoItem>();
//            obj = GetAllByCategoryModel.getInstance();
//            obj.category.max_result_count = response.getPageInfo().getTotalResults();
//
//			for(SearchResult result:results){
//                video = new GetAllByCategoryModel.Videos();
//                video.title = result.getSnippet().getTitle();
//                video.description = result.getSnippet().getDescription();
//                video.thumbnails.add(result.getSnippet().getThumbnails().getDefault().getUrl());
//                video.player_url = result.getId().getVideoId();
//                video.uploaded_at = result.getSnippet().getPublishedAt().toStringRfc3339();
//
//                String url = "https://www.googleapis.com/youtube/v3/videos?id="+video.player_url+"&part=contentDetails&key=AIzaSyBTarcxurznZvnN9kZW9ekWe7JyQsLSLCo";
//                aq.ajax(url, String.class,
//                        new AjaxCallback<String>() {
//                            @Override
//                            public void callback(String url, String json,
//                                                 AjaxStatus status) {
//                                if (json != null) {
//                                    Gson gson = new Gson();
//                                    DurationModel obj1 = new DurationModel();
//                                    obj1 = gson.fromJson(json.toString(),
//                                            DurationModel.class);
//                                    DurationModel.getInstance().setList(
//                                            obj1);
//                                    video.duration = getTimeFromString(DurationModel.getInstance().items.get(0).contentDetails.duration);
//
//                                    SongsListViewFragment.songListViewAdapter.notifyDataSetChanged();
//                                    Log.e("Time",video.duration);
//                                    }
//                            }
//                        });
//
//                obj.category.videos.add(video);
////				VideoItem item = new VideoItem();
////				item.setTitle(result.getSnippet().getTitle());
////				item.setDescription(result.getSnippet().getDescription());
////				item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
////				item.setId(result.getId().getVideoId());
////				items.add(item);
//			}
//            Log.e("Youtube Connector size", GetAllByCategoryModel.getInstance().category.videos.size()+"");
//			return items;
//		}catch(IOException e){
//			Log.d("YC", "Could not search: "+e);
//			return null;
//		}
//	}
//    private String getTimeFromString(String duration) {
//        // TODO Auto-generated method stub
//        String time = "";
//        boolean hourexists = false, minutesexists = false, secondsexists = false;
//        if (duration.contains("H"))
//            hourexists = true;
//        if (duration.contains("M"))
//            minutesexists = true;
//        if (duration.contains("S"))
//            secondsexists = true;
//        if (hourexists) {
//            String hour = "";
//            hour = duration.substring(duration.indexOf("T") + 1,
//                    duration.indexOf("H"));
//            if (hour.length() == 1)
//                hour = "0" + hour;
//            time += hour + ":";
//        }
//        if (minutesexists) {
//            String minutes = "";
//            if (hourexists)
//                minutes = duration.substring(duration.indexOf("H") + 1,
//                        duration.indexOf("M"));
//            else
//                minutes = duration.substring(duration.indexOf("T") + 1,
//                        duration.indexOf("M"));
//            if (minutes.length() == 1)
//                minutes = "0" + minutes;
//            time += minutes + ":";
//        } else {
//            time += "00:";
//        }
//        if (secondsexists) {
//            String seconds = "";
//            if (hourexists) {
//                if (minutesexists)
//                    seconds = duration.substring(duration.indexOf("M") + 1,
//                            duration.indexOf("S"));
//                else
//                    seconds = duration.substring(duration.indexOf("H") + 1,
//                            duration.indexOf("S"));
//            } else if (minutesexists)
//                seconds = duration.substring(duration.indexOf("M") + 1,
//                        duration.indexOf("S"));
//            else
//                seconds = duration.substring(duration.indexOf("T") + 1,
//                        duration.indexOf("S"));
//            if (seconds.length() == 1)
//                seconds = "0" + seconds;
//            time += seconds;
//        }
//        return time;
//    }
//}
