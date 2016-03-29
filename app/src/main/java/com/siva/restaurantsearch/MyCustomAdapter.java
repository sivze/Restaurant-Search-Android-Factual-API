package com.siva.restaurantsearch;

/**
 * Created by Siva on 3/15/2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {

    private Context context;

    private ArrayList<Datum> data;

    private LayoutInflater inflater;

    private int previousPosition = 0;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public MyCustomAdapter(Context context, ArrayList<Datum> data) {

        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View view = inflater.inflate(R.layout.list_item_row, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

//    public static int generateRandom() {
//        return (int) (400 + (Math.random() * 100));
//    }

//    Drawable drawable_from_url(String url, String src_name) throws
//            java.net.MalformedURLException, java.io.IOException
//    {
//        return Drawable.createFromStream(((java.io.InputStream)
//                new java.net.URL(url).getContent()), src_name);
//    }

    MyViewHolder myViewHolder;
    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {

        if(data.get(position).name!=null && !data.get(position).name.isEmpty() && data.get(position).name.length()>0)
            myViewHolder.textview.setText(data.get(position).name);
        else
            myViewHolder.textview.setText("Unknown");

        if(data.get(position).cuisine!=null && data.get(position).cuisine.size()>0)
            myViewHolder.textview_type.setText(data.get(position).cuisine.toString());
        else
            myViewHolder.textview_type.setText("Unknown");

        if(data.get(position).address!=null && !data.get(position).address.isEmpty() && data.get(position).address.length()>0)
            myViewHolder.textview_address.setText(data.get(position).address+"\n"
                            + data.get(position).locality + ", "
                            + data.get(position).region
            );
        else
            myViewHolder.textview_address.setText("Unknown");

        myViewHolder.ratingBar.setRating((float) data.get(position).rating);
        myViewHolder.textview_rating.setText("(" + String.valueOf(data.get(position).rating) + ")");

        this.myViewHolder = myViewHolder;

        new MyAsyncTaskBgImage().execute(myViewHolder);
        new MyAsyncTaskFrontImage().execute(myViewHolder);
        //new MyAsyncTask().execute("http://lorempixel.com/" + generateRandom() + "/" + generateRandom() + "/food/",  "imageViewBg", "front");

//        Thread t1 = new Thread(new Runnable() {
//            @Override
//            public void run(){
//                try {
//                    myViewHolder.imageViewBg.setImageDrawable(drawable_from_url("http://lorempixel.com/" + generateRandom() + "/" + generateRandom() + "/city/",  "imageViewBg"));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });


//        Thread t2 = new Thread(new Runnable() {
//            @Override
//            public void run(){
//                try {
//                    myViewHolder.imageViewFront.setImageDrawable(drawable_from_url("http://lorempixel.com/" + generateRandom() + "/" + generateRandom() + "/food/",  "imageViewBg"));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        //t1.start();
//        t2.start();
//
//        try {
//            t1.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        try {
//            t2.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    myViewHolder.imageViewBg.setImageDrawable(drawable_from_url("http://lorempixel.com/" + generateRandom() + "/" + generateRandom() + "/city/",  "imageViewBg"));
//                    myViewHolder.imageViewFront.setImageDrawable(drawable_from_url("http://lorempixel.com/" + generateRandom() + "/" + generateRandom() + "/food/",  "imageViewBg"));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

//        thread.start();
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

////        try {
//            myViewHolder.imageViewBg.setImageDrawable(drawable_from_url("http://lorempixel.com/400/200/city/", "imageViewBg"));
//            myViewHolder.imageViewFront.setImageDrawable(drawable_from_url("http://lorempixel.com/400/200/food/", "imageViewFront"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if(position > previousPosition){ // We are scrolling DOWN

            AnimationUtil.animate(myViewHolder, true);

        }else{ // We are scrolling UP

            //AnimationUtil.animate(myViewHolder, false);
        }

        previousPosition = position;


//        final int currentPosition = position;
//        final Datum infoData = data.get(position);

//        myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Toast.makeText(context, "OnClick Called at position " + position, Toast.LENGTH_SHORT).show();
//                addItem(currentPosition, infoData);
//            }
//        });
//
//        myViewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                Toast.makeText(context, "OnLongClick Called at position " + position, Toast.LENGTH_SHORT).show();
//
//                removeItem(infoData);
//
//                return true;
//            }
//
//
//        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textview;
        ImageView imageViewBg;
        ImageView imageViewFront;
        TextView textview_type;
        TextView textview_address;
        RatingBar ratingBar;
        TextView textview_rating;
        ProgressBar frontImagePbar;

        public MyViewHolder(View itemView) {
            super(itemView);

            textview = (TextView) itemView.findViewById(R.id.txv_row);
            textview_type = (TextView) itemView.findViewById(R.id.txv_type);
            textview_address = (TextView) itemView.findViewById(R.id.txv_address);
            textview_rating = (TextView) itemView.findViewById(R.id.txv_rating);

            imageViewBg = (ImageView) itemView.findViewById(R.id.img_row);
            imageViewFront = (ImageView) itemView.findViewById(R.id.image_column);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ruleRatingBar);

            frontImagePbar = (ProgressBar) itemView.findViewById(R.id.imageFront_progress);
        }
    }

    // This removes the data from our Dataset and Updates the Recycler View.
    private void removeItem(Datum infoData) {

        int currPosition = data.indexOf(infoData);
        data.remove(currPosition);
        notifyItemRemoved(currPosition);
    }

    // This method adds(duplicates) a Object (item ) to our Data set as well as Recycler View.
    private void addItem(int position, Datum infoData) {

        data.add(position, infoData);
        notifyItemInserted(position);
    }

    public Bitmap downloadImage(String url, String imageViewBg) {
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            stream = getHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
            stream.close();
        }
        catch (Exception e1) {
//                e1.printStackTrace();
//                System.out.println("downloadImage"+ e1.toString());
        }
        return bitmap;
    }

    // Makes HttpURLConnection and returns InputStream

    public InputStream getHttpConnection(String urlString)  throws IOException {

        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("downloadImage" + ex.toString());
        }
        return stream;
    }

    private class MyAsyncTaskBgImage extends AsyncTask<MyViewHolder, String, Bitmap> {
        //String[] strings;
        MyViewHolder myViewHolderInternal;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Bitmap doInBackground(MyViewHolder... myViewHolders) {
            myViewHolderInternal = myViewHolders[0];
            return downloadImage("http://lorempixel.com/400/200/city/",  "imageViewBg");
        }
        protected void onPostExecute(Bitmap result) {
            if(myViewHolderInternal.imageViewBg.getDrawable()==null)
                myViewHolderInternal.imageViewBg.setImageBitmap(result);
        }
    }

    private class MyAsyncTaskFrontImage extends AsyncTask<MyViewHolder, String, Bitmap> {
        //String[] strings;
        MyViewHolder myViewHolderInternal;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Bitmap doInBackground(MyViewHolder... myViewHolders) {
            myViewHolderInternal = myViewHolders[0];
            return downloadImage("http://lorempixel.com/400/200/food/",  "imageViewBg");
        }
        protected void onPostExecute(Bitmap result) {
            if(myViewHolderInternal.imageViewFront.getDrawable()==null)
                myViewHolderInternal.imageViewFront.setImageBitmap(result);
        }


    }
}
