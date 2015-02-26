package co.vector.itube;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.google.android.youtube.player.YouTubePlayer;
import com.parse.Parse;
import com.parse.ParseInstallation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by hasanali on 27/08/14.
 */
public class BaseClass extends Application {
    /**
     * Init preferences
     */
    YouTubePlayer youTubePlayer;
    public SharedPreferences appSharedPrefs;
    public SharedPreferences.Editor prefsEditor;
    private int Position = 0;
    public String SHARED_NAME = "co.vector.farseireader";

    //Shared preferences tags
    private String IsFromFavorites = "IsFromFavorites";
    private String AUTH_TOKEN = "auth_token";
    //public String GCM_Key = "AIzaSyDWe2sO7GCnbX6dF-bYK7HJIeedfXG1vnA";
    public String Push_APP_Id = "FjdgRPtkE8UODPptRr8YARPyLs5g1M9GtFEaTdR4";
    public String Push_Client_Id = "DgTj6kGzsgX2Z92gH5FKEdyfm3V5ZzwgXijarSll";
    private String Category = "Category";
    private String NewCategory = "NewCategory";
    private String Duration = "Duration";
    private String Language = "English";
    private String VideoId = "VideoId";
    private String VideoTitle = "VideoTitle";
    private String VideoDuration = "VideoDuration";
    private String DataBy = "DataBy";
    private long CheckDuration = 0L;
    private String VideoUploadDate = "VideoUploadDate";
    private String VideoAuthor = "VideoAuthor";
    private String VideoPlayerlink = "VideoPlayerlink";
    private String VideoViewer = "VideoViewer";
    private String VideoThumbnail = "VideoThumbnail";
    private ArrayList<String> SongsList = new ArrayList<String>();
    private ArrayList<String> SaveList = new ArrayList<String>();
    ArrayList<SpecifiedKeyData> specifiedKeyData;

    @Override
    public void onCreate() {
        super.onCreate();

        /* initialize preferences for future use */
        this.appSharedPrefs = getSharedPreferences(SHARED_NAME,
                Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
        Parse.initialize(getApplicationContext(), Push_APP_Id, Push_Client_Id);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        specifiedKeyData = SetSpecifiedData();
//        ParseQuery query = ParseInstallation.getQuery();
//        query.whereEqualTo("deviceType", "android");
//        ParsePush androidPush = new ParsePush();
//        androidPush.setMessage("Your suitcase has been filled with tiny robots!");
//        androidPush.setQuery(query);
//        androidPush.sendInBackground();
    }

    public static boolean isTabletDevice(Context activityContext) {
        boolean xlarge = ((activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);

        if (xlarge) {
            DisplayMetrics metrics = new DisplayMetrics();
            Activity activity = (Activity) activityContext;
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
                    || metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
                    || metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
                    || metrics.densityDpi == DisplayMetrics.DENSITY_TV
                    || metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
                return true;
            }
        }
        return false;
    }
    public String TimeAgo (long time){

        List<Long> times = Arrays.asList(
                TimeUnit.DAYS.toMillis(365),
                TimeUnit.DAYS.toMillis(30),
                TimeUnit.DAYS.toMillis(1),
                TimeUnit.HOURS.toMillis(1),
                TimeUnit.MINUTES.toMillis(1),
                TimeUnit.SECONDS.toMillis(1));
        List<String> timesString = Arrays.asList("year", "month", "day", "hour", "minute", "second");
        return  toDuration(time,times,timesString);
    }
    public String toDuration(long duration,List<Long> times,List<String> timesString) {

        StringBuffer res = new StringBuffer();
        for (int i = 0; i < times.size(); i++) {
            Long current = times.get(i);
            long temp = duration / current;
            if (temp > 0) {
                res.append(temp).append(" ").append(timesString.get(i)).append(temp > 1 ? "s" : "").append(" ago");
                break;
            }
        }
        if ("".equals(res.toString()))
            return "0 second ago";
        else
            return res.toString();
    }
    public static Long getTimeStampToMilli(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date obj = sdf.parse(date);
            return obj.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0l;
    }
    public static int getDpValue(int val, Context ctx){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, val, ctx.getResources()
                        .getDisplayMetrics());
    }
    public void setAUTH_TOKEN(String uuid){
        prefsEditor.putString(AUTH_TOKEN, uuid).commit();
    }
    public String getAUTH_TOKEN(){
        return appSharedPrefs.getString(AUTH_TOKEN, "");
    }

//    public void setGCM_Key(String gcmKey){
//        prefsEditor.putString(GCM_Key, gcmKey).commit();
//    }
//    public String getGCM_Key(){
//        return appSharedPrefs.getString(GCM_Key, "");
//    }

    public void setDuration(String duration){
        prefsEditor.putString(Duration, duration).commit();
    }
    public String getDuration(){
        return appSharedPrefs.getString(Duration, "");
    }

    public void setSaveList(ArrayList save){
        this.SaveList= save;
    }
    public ArrayList getSaveList(){
        return SaveList;
    }

    public void setArrayList(ArrayList list){
        this.SongsList= list;
    }
    public ArrayList getArrayList(){
        return SongsList;
    }

    public void setIsFromFavorites(String favorites){
        this.IsFromFavorites= favorites;
    }
    public String getIsFromFavorites(){
        return IsFromFavorites;
    }

    public void setDataBy(String dataBy){
        this.DataBy= dataBy;
    }
    public String getDataBy(){
        return DataBy;
    }

    public void setCheckDuration(long checkDuration){
        this.CheckDuration= checkDuration;
    }
    public long getCheckDuration(){
        return CheckDuration;
    }


    public void setLanguage(String language){
        prefsEditor.putString(Language, language).commit();
    }
    public String getLanguage(){
        return appSharedPrefs.getString(Language, "");
    }

    public void setCategory(String category){
        prefsEditor.putString(Category, category).commit();
    }
    public String getCategory(){
        return appSharedPrefs.getString(Category, "");
    }

    public void setVideoAuthor(String videoAuthor){
        prefsEditor.putString(VideoAuthor, videoAuthor).commit();
    }
    public String getVideoAuthor(){
        return appSharedPrefs.getString(VideoAuthor, "");
    }

    public void setNewCategory(String newcategory){
        prefsEditor.putString(NewCategory, newcategory).commit();
    }
    public String getNewCategory(){
        return appSharedPrefs.getString(NewCategory, "");
    }

    public void setVideoThumbnail(String thumbnail){
        prefsEditor.putString(VideoThumbnail, thumbnail).commit();
    }
    public String getVideoThumbnail(){
        return appSharedPrefs.getString(VideoThumbnail, "");
    }

    public void setVideoId(String videoid){
        prefsEditor.putString(VideoId, videoid).commit();
    }
    public String getVideoId(){
        return appSharedPrefs.getString(VideoId, "");
    }

    public void setVideoTitle(String videotitle){
        prefsEditor.putString(VideoTitle, videotitle).commit();
    }
    public String getVideoTitle(){
        return appSharedPrefs.getString(VideoTitle, "");
    }

    public void setVideoDuraion(String videoduration){
        prefsEditor.putString(VideoDuration, videoduration).commit();
    }
    public String getVideoDurtion(){
        return appSharedPrefs.getString(VideoDuration, "");
    }

    public void setVideoPlayerLink(String videolink){
        prefsEditor.putString(VideoPlayerlink, videolink).commit();
    }
    public String getVideoUploadDate(){
        return appSharedPrefs.getString(VideoUploadDate, "");
    }

    public void setVideoUploadDate(String videoUploadDate){
        prefsEditor.putString(VideoUploadDate, videoUploadDate).commit();
    }
    public String getVideoPlayerLink(){
        return appSharedPrefs.getString(VideoPlayerlink, "");
    }

    public void setVideoViewer(String videoviewer){
        prefsEditor.putString(VideoViewer, videoviewer).commit();
    }
    public String getVideoViewer(){
        return appSharedPrefs.getString(VideoViewer, "");
    }

    public void setPosition(int position){
        this.Position= position;
    }
    public int getPosition(){
        return Position;
    }

    public void setYOUTUBE(YouTubePlayer view){
        this.youTubePlayer= view;
    }
    public YouTubePlayer getYOUTUBE(){
        return youTubePlayer;
    }

    public void clearSharedPrefs(){
        prefsEditor.clear().commit();
    }

    public ArrayList<SpecifiedKeyData> SetSpecifiedData()
    {
        SpecifiedKeyData specifiedKeyData;
        ArrayList<SpecifiedKeyData> itemsArrayList = new ArrayList<SpecifiedKeyData>();

        specifiedKeyData= new SpecifiedKeyData();
        specifiedKeyData.setLanguage("Iranian");
        specifiedKeyData.setMovie("فیلم سینمایی");
        specifiedKeyData.setCartons("کارتون ایرانی");
        specifiedKeyData.setMusic("موزيك ايرانى");
        specifiedKeyData.setDocumentries("مستند فارسى");
        itemsArrayList.add(specifiedKeyData);

        specifiedKeyData= new SpecifiedKeyData();
        specifiedKeyData.setLanguage("Arabic");
        specifiedKeyData.setMovie("افلام");
        specifiedKeyData.setCartons("كارتون عربى");
        specifiedKeyData.setMusic(" موزيك عربى");
        specifiedKeyData.setDocumentries("فيلم وثائقي");
        itemsArrayList.add(specifiedKeyData);

        specifiedKeyData= new SpecifiedKeyData();
        specifiedKeyData.setLanguage("Hindi");
        specifiedKeyData.setMovie("Hindi Movies");
        specifiedKeyData.setCartons("Hindi Cartoons");
        specifiedKeyData.setMusic("Hindi Songs");
        specifiedKeyData.setDocumentries("Hindi Documentary");
        itemsArrayList.add(specifiedKeyData);

        specifiedKeyData= new SpecifiedKeyData();
        specifiedKeyData.setLanguage("Turkish");
        specifiedKeyData.setMovie("Türk Fılmı");
        specifiedKeyData.setCartons("çizgi film");
        specifiedKeyData.setMusic("Turkish Music");
        specifiedKeyData.setDocumentries("Belgesel");
        itemsArrayList.add(specifiedKeyData);

        specifiedKeyData= new SpecifiedKeyData();
        specifiedKeyData.setLanguage("Spanish");
        specifiedKeyData.setMovie("películas");
        specifiedKeyData.setCartons("dibujos");
        specifiedKeyData.setMusic("Música");
        specifiedKeyData.setDocumentries("documental");
        itemsArrayList.add(specifiedKeyData);

        specifiedKeyData= new SpecifiedKeyData();
        specifiedKeyData.setLanguage("English");
        specifiedKeyData.setMovie("English Movies");
        specifiedKeyData.setCartons("English Cartoons");
        specifiedKeyData.setMusic("English Music");
        specifiedKeyData.setDocumentries("English Documentary");
        itemsArrayList.add(specifiedKeyData);

        specifiedKeyData= new SpecifiedKeyData();
        specifiedKeyData.setLanguage("German");
        specifiedKeyData.setMovie("Film auf deutsch");
        specifiedKeyData.setCartons("Zeichentrick Filme ");
        specifiedKeyData.setMusic("Musik");
        specifiedKeyData.setDocumentries("Dokumentation");
        itemsArrayList.add(specifiedKeyData);

        specifiedKeyData= new SpecifiedKeyData();
        specifiedKeyData.setLanguage("French");
        specifiedKeyData.setMovie(" Film de action");
        specifiedKeyData.setCartons("Dessins animés");
        specifiedKeyData.setMusic("Musique");
        specifiedKeyData.setDocumentries("Documentaire");
        itemsArrayList.add(specifiedKeyData);

        specifiedKeyData= new SpecifiedKeyData();
        specifiedKeyData.setLanguage("Italian");
        specifiedKeyData.setMovie("Film Italiano");
        specifiedKeyData.setCartons(" Cartoni animati");
        specifiedKeyData.setMusic("Musica");
        specifiedKeyData.setDocumentries("Documentario");
        itemsArrayList.add(specifiedKeyData);

        specifiedKeyData= new SpecifiedKeyData();
        specifiedKeyData.setLanguage("Russian");
        specifiedKeyData.setMovie("Российские фильмы");
        specifiedKeyData.setCartons("Мультфильмы");
        specifiedKeyData.setMusic("Russian folk music");
        specifiedKeyData.setDocumentries("Документальные фильмы");
        itemsArrayList.add(specifiedKeyData);
        return  itemsArrayList;
    }
}
