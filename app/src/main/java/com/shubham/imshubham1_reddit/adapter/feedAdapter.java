package com.shubham.imshubham1_reddit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.shubham.imshubham1_reddit.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.shubham.imshubham1_reddit.MainActivity.convertStreamToString;

public class feedAdapter extends RecyclerView.Adapter<feedAdapter.viewHolder> {

    public List<String> uName;
    public List<String> createTime;
    public List<String> feedTitle;
    public List<String> feedImg;
    public List<String> feedVid;
    public List<String> imgHigh;
    public List<String> imgWidth;
    public List<Boolean> vidStatus;
    public List<String> feedId;
    public List<String> posturl;
    boolean favStatus = false;

    Context context;
    FeedClickListner feedClickListner;
    public feedAdapter(List<String> uName,
                       List<String> createTime,
                       List<String> feedTitle,
                       List<String> feedImg,
                       List<String> feedVid,
                       List<String> imgHigh,
                       List<String> imgWidth,
                       List<Boolean> vidStatus,
                       List<String> feedId,
                       List<String> posturl,
                       FeedClickListner feedClickListner,
                       Context context) {
        this.uName = uName;
        this.createTime = createTime;
        this.feedTitle = feedTitle;
        this.feedImg = feedImg;
        this.feedVid = feedVid;
        this.imgHigh = imgHigh;
        this.imgWidth = imgWidth;
        this.vidStatus = vidStatus;
        this.feedId = feedId;
        this.posturl = posturl;
        this.context = context;
        this.feedClickListner = feedClickListner;
    }
public interface FeedClickListner{
        void favClickListner(String id);
        void UnFavClickListner(String id);
        void gotoPost(String url);
        void shareMe(String link);
}

    @NonNull
    @Override
    public feedAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.feed_card, viewGroup,false);
        return new feedAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final feedAdapter.viewHolder viewHolder, int i) {
        long unix_seconds = Long.parseLong(this.createTime.get(i));
        Date date = new Date(unix_seconds*1000L);
        SimpleDateFormat jdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        String time = jdf.format(date);
        viewHolder.uName.setText(this.uName.get(i));
        viewHolder.createTime.setText(time);
        viewHolder.feedTitle.setText(this.feedTitle.get(i));
        final String url = this.feedImg.get(i);
        final int imgH = Integer.parseInt(this.imgHigh.get(i));
        final int imgW = Integer.parseInt(this.imgWidth.get(i));
        final boolean isVid = this.vidStatus.get(i);
        Picasso.get()
                .load(url)
                .fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        Picasso.get()
                                .load(url)
                                .tag(url)
                                .into(viewHolder.feedImg);
                        if (imgH > imgW){
                            viewHolder.feedImg.getLayoutParams().height = 800;
                        }
                        if(!isVid) {
                            viewHolder.feedImg.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        if (isVid){
            MediaController mediaController = new MediaController(this.context);
            viewHolder.feedVid.setVisibility(View.VISIBLE);
            mediaController.setAnchorView(viewHolder.feedVid);
            viewHolder.feedVid.setVideoURI(Uri.parse(this.feedVid.get(i)));
            viewHolder.feedVid.setMediaController(mediaController);

            viewHolder.feedVid.start();

        }

        File file = new File(this.context.getCacheDir(), "favCacheFile.txt");


            //String ret = null;


                final String ret = readFile("favCacheFile.txt");
                Log.d("fav",ret);
                if (ret.contains(this.feedId.get(i))){
                    favStatus = true;
                    viewHolder.fav.setImageDrawable(this.context.getResources().getDrawable(R.drawable.gold_star_24dp));
                }



        final String feedId = this.feedId.get(i);
        final Context context1 = this.context;
        viewHolder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ret.contains(feedId)){
                    feedClickListner.favClickListner(feedId);
                    viewHolder.fav.setImageDrawable(context1.getResources().getDrawable(R.drawable.gold_star_24dp));
                } else {
                    feedClickListner.UnFavClickListner(feedId);
                    viewHolder.fav.setImageDrawable(context1.getResources().getDrawable(R.drawable.star_black_24dp));
                }

            }
        });

        final String Posturl = this.posturl.get(i);

        viewHolder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedClickListner.gotoPost(Posturl);
            }
        });

        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedClickListner.shareMe(Posturl);
            }
        });
    }

    public String readFile(String file){
        String text = "";
        //Toast.makeText(Tasks.this,String.valueOf(file),Toast.LENGTH_SHORT).show();

        try {
            FileInputStream fis = this.context.openFileInput(file);
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

    @Override
    public int getItemCount() {
        return this.uName.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView uName;
        TextView createTime;
        TextView feedTitle;
        ImageView feedImg;
        VideoView feedVid;
        ImageView fav;
        ImageView comments;
        ImageView share;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            uName = itemView.findViewById(R.id.userName);
            createTime = itemView.findViewById(R.id.createTime);
            feedTitle = itemView.findViewById(R.id.feedTitle);
            feedImg = itemView.findViewById(R.id.feedImage);
            feedVid = itemView.findViewById(R.id.feedVideo);
            fav = itemView.findViewById(R.id.fav);
            comments = itemView.findViewById(R.id.comments);
            share = itemView.findViewById(R.id.share);


        }
    }
}
