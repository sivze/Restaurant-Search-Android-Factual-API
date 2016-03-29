package com.siva.restaurantsearch;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.search.material.library.MaterialSearchView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView no_results_msg;
    ProgressBar pBar;
    MyCustomAdapter adapter;
    RestaurantsData rdata;
    RestaurantsModel msg = null;
    private MaterialSearchView searchView;
    static String apiKey = "yourAPIKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        no_results_msg = (TextView) findViewById(R.id.no_results);
        loadSearchBar();

        pBar = (ProgressBar) findViewById(R.id.marker_progress);
        pBar.bringToFront();
    }

    private void loadSearchBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    //loadData(query);
                    new JSONParse().execute(query);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                if(msg!=null && msg.response.data.size()>0) {
                    msg.response.data.clear();
                    adapter.notifyDataSetChanged();
                }
                no_results_msg.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSearchViewClosed() {

            }
        });

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String text = ((TextView)view).getText().toString();// second method
                try {
                    new JSONParse().execute(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        SearchAdapter adapter = new SearchAdapter();
        searchView.setAdapter(adapter);

//
}

    public class JSONParse extends AsyncTask<String, Integer, String> {
        String query;

        @Override
        protected void onPreExecute() {
            searchView.closeSearch();
            pBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection c = null;
            String queryValue = null;
            query = strings[0];
            try {
                queryValue = URLEncoder.encode(strings[0], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String finalQuery;
            if(strings.length==1)
                finalQuery = "http://api.v3.factual.com/t/restaurants-us?filters={%22$and%22:[{%22locality%22:%22"+queryValue+"%22}]}&KEY="+apiKey;
            else
                finalQuery = "http://api.v3.factual.com/t/restaurants-us?q="+queryValue+"&KEY="+apiKey;
            try {
//            /final String encodedURL = URLEncoder.encode(url, "UTF-8");
                URL u = new URL(finalQuery);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Content-length", "0");
                c.setUseCaches(false);
                c.setAllowUserInteraction(false);
                //c.setConnectTimeout(timeout);
                //c.setReadTimeout(timeout);
                c.connect();
                int status = c.getResponseCode();

                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        return sb.toString();
                }

            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            loadDataFromResult(json, query);
        }
    }
    int count = 0;
    private  void  loadDataFromResult(String jsonData, String query){
        msg = new Gson().fromJson(jsonData, RestaurantsModel.class);

        if (msg!=null && msg.response.data.size()>0) {
            recyclerView = (RecyclerView) findViewById(R.id.recycleView);
            adapter = new MyCustomAdapter(this, msg.response.data);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Vertical Orientation By Default
            count = 0;
        }
        else {
            if(count==0) {
                new JSONParse().execute(query, "second");
                count=1;
            }
            else {
                no_results_msg.setVisibility(View.VISIBLE);
                count=0;
            }

        }
        pBar.setVisibility(View.INVISIBLE);
    }

//    private void loadData(String query) throws Exception {
//        pBar.setVisibility(View.VISIBLE);
//        final String queryValue = URLEncoder.encode(query, "UTF-8");
//
//        final String finalQuery = "http://api.v3.factual.com/t/restaurants-us?filters={%22$and%22:[{%22locality%22:%22"+queryValue+"%22}]}&KEY=4ISAwImLMMXhkkS8WfxO16etavBKsNTiCPRX5D24&limit=4";
//        searchView.closeSearch();
//
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    rdata = new RestaurantsData();
//                    String data = rdata.getJSON(finalQuery);
//                    msg = new Gson().fromJson(data, RestaurantsModel.class);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        thread.start();
//
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        if (msg!=null && msg.response.data.size()>0) {
//            recyclerView = (RecyclerView) findViewById(R.id.recycleView);
//            adapter = new MyCustomAdapter(this, msg.response.data);
//            recyclerView.setAdapter(adapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Vertical Orientation By Default
//        }
//        else {
//            no_results_msg.setVisibility(View.VISIBLE);
//        }
//        pBar.setVisibility(View.INVISIBLE);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private class SearchAdapter extends BaseAdapter implements Filterable {

        private ArrayList<String> data;

        private String[] typeAheadData;

        LayoutInflater inflater;

        public SearchAdapter() {
            inflater = LayoutInflater.from(MainActivity.this);
            data = new ArrayList<String>();
            typeAheadData = getResources().getStringArray(R.array.state_array_full);
        }


        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (!TextUtils.isEmpty(constraint)) {
                        // Retrieve the autocomplete results.
                        List<String> searchData = new ArrayList<>();

                        for (String str : typeAheadData) {
                            if (str.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                                searchData.add(str);
                            }
                        }

                        // Assign the data to the FilterResults
                        filterResults.values = searchData;
                        filterResults.count = searchData.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.values != null) {
                        data = (ArrayList<String>) results.values;
                        notifyDataSetChanged();
                    }
                }
            };
            return filter;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder mViewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                mViewHolder = new MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (MyViewHolder) convertView.getTag();
            }

            String currentListData = (String) getItem(position);

            mViewHolder.textView.setText(currentListData);

            return convertView;
        }


        private class MyViewHolder {
            TextView textView;

            public MyViewHolder(View convertView) {
                textView = (TextView) convertView.findViewById(android.R.id.text1);
            }
        }
    }
}


