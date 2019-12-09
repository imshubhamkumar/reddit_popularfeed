package com.shubham.imshubham1_reddit;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.shubham.imshubham1_reddit.adapter.feedAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements feedAdapter.FeedClickListner{

    public static RecyclerView feedList;
    public static RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        feedList = findViewById(R.id.feedList);
        layoutManager = new LinearLayoutManager(this);
        feedList.setHasFixedSize(true);
        feedList.setLayoutManager(layoutManager);


        FeedData fD = new FeedData();
        fD.execute("https://www.reddit.com/r/popular.json");


    }



    @Override
    public void favClickListner(String id) {

        JSONArray jo;
        JSONArray jsonArray = new JSONArray();

        List<String> list = new ArrayList<>();

        list.add(id);
        try {
        File f = new File(MainActivity.this.getFilesDir(), "favCacheFile.txt");

        if (f.exists()){
            String ret = readFile("favCacheFile.txt");
            jo = new JSONArray(ret);
            for (int i =0; i<jo.length(); i++){
                list.add((String) jo.get(i));
            }
        }


        jsonArray.put(list);



        } catch (Exception e) {
            e.printStackTrace();
        }




        createCacheFile(MainActivity.this,"favCacheFile.txt",jsonArray.toString());


    }

    @Override
    public void UnFavClickListner(String id) {
        List<String> list = new ArrayList<>();

        String ret = readFile("favCacheFile.txt");

        try {
            JSONArray jsonArray = new JSONArray(ret);
            for (int i = 0; i<jsonArray.length(); i++){
                list.add((String) jsonArray.get(i));
            }
            for (int i = 0; i<list.size(); i++){
                if (list.get(i) == id){
                    list.remove(i);
                }
            }
            JSONArray ja = new JSONArray();
            ja.put(list);

            createCacheFile(MainActivity.this,"favCacheFile.txt",ja.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(MainActivity.this,ret,Toast.LENGTH_SHORT).show();


    }

    @Override
    public void gotoPost(String url) {
        Intent in = new Intent(MainActivity.this, SingleFeed.class);
        in.putExtra("url",url);
        startActivity(in);
    }

    @Override
    public void shareMe(String link) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
        String shareMessage= "\nHey Check out this post\n\n";
        shareMessage = shareMessage + "https://www.reddit.com" + link;
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "choose one"));
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }


    public String readFile(String file){
        String text = "";
        //Toast.makeText(Tasks.this,String.valueOf(file),Toast.LENGTH_SHORT).show();
        FileInputStream fis = null;
        try {
            fis = openFileInput(file);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            text = new String(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }


    public File createCacheFile(Context context, String fileName, String json) {
        File cacheFile = new File(context.getCacheDir(), fileName);

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.close();
            Toast.makeText(MainActivity.this,"Saved",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return cacheFile;
    }


    public class FeedData extends AsyncTask<String, Void, String> {


        private String result;
        public List<String> uName;
        public List<String> createTime;
        public List<String> feedTitle;
        public List<String> feedImg;
        public List<String> feedVid;
        public List<String> imgHigh;
        public List<String> imgWidth;
        public List<Boolean> vidStatus;
        public  List<String> feedId;
        public List<String> posturl;

        @Override
        protected String doInBackground(String... urls) {
            result ="";
            URL link;

            String url = urls[0];

            try {
                link = new URL(url);
                Document doc = Jsoup.connect(String.valueOf(link)).ignoreContentType(true).get();
                result = doc.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject JO;
            uName = new ArrayList<>();
            createTime = new ArrayList<>();
            feedTitle = new ArrayList<>();
            feedImg = new ArrayList<>();
            feedVid = new ArrayList<>();
            imgWidth = new ArrayList<>();
            imgHigh = new ArrayList<>();
            vidStatus = new ArrayList<>();
            feedId = new ArrayList<>();
            posturl = new ArrayList<>();

            try {
                JO = new JSONObject(result);
                String data = JO.getString("data");
                JSONObject jo = new JSONObject(data);
                String children = jo.getString("children");

                JSONArray jsonArray = new JSONArray(children);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String innerData = jsonObject.getString("data");
                    JSONObject innerObj = new JSONObject(innerData);
                    String user = innerObj.getString("subreddit");
                    String ct = innerObj.getString("created");
                    String ft = innerObj.getString("title");
                    String fi = innerObj.getString("thumbnail");
                    String fId = innerObj.getString("id");
                    String imgH = "500";
                    String imgW = "600";
                    String vid = "none";
                    String postUrl = innerObj.getString("permalink");
                    if (innerObj.has("preview")){
                        JSONObject preview = new JSONObject(innerObj.getString("preview"));
                        JSONArray images = new JSONArray(preview.getString("images"));
                        JSONObject array = (JSONObject) images.get(0);
                        String source = array.getString("source");
                        JSONObject url = new JSONObject(source);
                        fi = url.getString("url");
                        if (url.has("height")){
                            imgH = url.getString("height");
                        }
                        if (url.has("width")){
                            imgW = url.getString("width");
                        }
                    }

                    boolean isVid = innerObj.getBoolean("is_video");

                    if (isVid){
                        JSONObject media = new JSONObject(innerObj.getString("media"));
                        JSONObject reddit = new JSONObject(media.getString("reddit_video"));

//                       JSONObject reddit = new JSONObject(media);
//                        String video = reddit.getString("reddit_video");

//                       JSONObject videoUrl = new JSONObject(video);
                        vid = reddit.getString("fallback_url");
                    }
                    uName.add(user);
                    createTime.add(ct);
                    feedTitle.add(ft);
                    feedImg.add(fi);
                    feedVid.add(vid);
                    imgWidth.add(imgW);
                    imgHigh.add(imgH);
                    vidStatus.add(isVid);
                    feedId.add(fId);
                    posturl.add(postUrl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MainActivity.adapter = new feedAdapter(uName,createTime,feedTitle,feedImg,feedVid,imgHigh,imgWidth,vidStatus,feedId,posturl, MainActivity.this,MainActivity.this);
            MainActivity.feedList.setAdapter(adapter);
        }
    }
}
