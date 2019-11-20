package eay.flackr;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rdo.endlessrecyclerviewadapter.EndlessRecyclerViewScrollListener;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import eay.flackr.model.Model;


public class MainActivity extends AppCompatActivity {

    public static ArrayList<Model> list = new ArrayList<>();
    public static ArrayList<String> list2 = new ArrayList<String>();
    public Model model;
    public RecyclerView recyclerView;
    private String key = "71b249c53a2e04abe57c1b3dd472f02f";
    private String extras = "last_update";
    private String per_page="20";
    private int pageT = 1;
    public static int width;
    public static int height;
    private String url ;
    Boolean isScrolling =false;
    ProgressBar progressBar;
    Button back,prev;
    TextView pageNumber;
    int currentItems,totalItems,scrollOutItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        screen();
        sendReq();
        
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                clearData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageT=pageT+1;
                        if(pageT == 1000) {
                            Toast.makeText(MainActivity.this, "Son Sayfadasınız..", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        pageNumber.setText(pageT+"");
                        sendReq2();
                        progressBar.setVisibility(View.GONE);
                    }
                },2000);
            }
        });
        
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(pageT == 1) {
                            Toast.makeText(MainActivity.this, "İlk Sayfadasınız..", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        clearData();
                        pageT=pageT-1;
                        pageNumber.setText(pageT+"");
                        sendReq2();
                        progressBar.setVisibility(View.GONE);
                    }
                },2000);
            }
        });
    }

    private void init(){
        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        progressBar =findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        back=findViewById(R.id.back);
        prev=findViewById(R.id.prev);
        pageNumber=findViewById(R.id.textView);
    }

    private String getUrl (){

        url ="https://www.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key="+key+"&extras="+extras+"&per_page="+per_page+"&page="+pageT+"&format=json&nojsoncallback=1";
       return url;
    }


    private void sendReq(){
        AsyncHttpClient client = new AsyncHttpClient();
        url = getUrl();
        client.get(url,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                model = new Gson().fromJson(response.toString(), Model.class);
                list.add(model);

                for(int i=0; i<list.get(0).getPhotos().getPhoto().size(); i++){
                String link = addList(list.get(pageT-1).getPhotos().getPhoto().get(i).getServer(),list.get(pageT-1).getPhotos().getPhoto().get(i).getId(),list.get(pageT-1).getPhotos().getPhoto().get(i).getSecret());
                list2.add(link);
                }

                if(pageT==1)setRecylerViewAdapter();
                else mAdapter.notifyDataSetChanged();

            }
        });
    }

    private void sendReq2(){
        AsyncHttpClient client = new AsyncHttpClient();
        url = getUrl();
        client.get(url,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                model = new Gson().fromJson(response.toString(), Model.class);
                list.add(model);

                for(int i=0; i<list.get(0).getPhotos().getPhoto().size(); i++){

                    String link = addList(list.get(pageT-1).getPhotos().getPhoto().get(i).getServer(),list.get(pageT-1).getPhotos().getPhoto().get(i).getId(),list.get(pageT-1).getPhotos().getPhoto().get(i).getSecret());
                    list2.add(link);

                }
                setRecylerViewAdapter();
            }
        });
    }

    private String addList (String server, String id, String secret){
        String link ;
        link = "https://farm1.staticflickr.com/"+server+"/"+id+"_"+secret+"_z.jpg";
        return link;
    }

    AdapterClass mAdapter;
    private void setRecylerViewAdapter(){
        mAdapter = new AdapterClass(list2);

        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                String s = String.valueOf(list2.get(position));
                Intent intent = new Intent(MainActivity.this,Detail.class);
                intent.putExtra("send_string",s);
                clearData();
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling=true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems=mLayoutManager.getChildCount();
                totalItems=mLayoutManager.getItemCount();
                scrollOutItems= ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems==totalItems)){
                    isScrolling=false;
                    fetchData();
                }
            }
        });
    }
    private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pageT=pageT+1;
                sendReq();
                pageNumber.setText(pageT+"");
                progressBar.setVisibility(View.GONE);
            }
        },2000);
    }
    public void clearData() {
        list2.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void screen(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height=size.y/4;
    }
}
