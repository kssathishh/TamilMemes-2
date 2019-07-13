package funny.tamil.gag.memes.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import funny.tamil.gag.memes.MainActivity;
import funny.tamil.gag.memes.R;
import funny.tamil.gag.memes.adapter.GridViewAdapter;


public class FragmentTrending extends Fragment {

    private ListView gridView;
    public static final String FAVORITES = "funny.tamil.gag.memes.favorites";
    public static final String LIKE = "like";
    public static final String DISLIKE = "dislike";
    private TypedArray allImages;
    boolean flag_loading = false;

    final ArrayList<String> allDocumentReference = new ArrayList<>();
    final ArrayList<String> allDrawableImages = new ArrayList<>();
    final ArrayList<String> allDesc = new ArrayList<>();
    final ArrayList<String> all_category = new ArrayList<>();
    final ArrayList<String> all_timestamp = new ArrayList<>();
    final ArrayList<Integer> all_upvotes = new ArrayList<>();
    final ArrayList<Integer> all_downvotes = new ArrayList<>();
    final ArrayList<QueryDocumentSnapshot> all_document = new ArrayList<>();



    DocumentSnapshot lastVisible;
    GridViewAdapter gridViewAdapter;
    String category_public = "Home";
    boolean end = false;
    SwipeRefreshLayout pullToRefresh;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_one, null);
        getAllWidgets(rootView);
        setAdapter("Home");


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        ((MainActivity)getActivity()).setFragmentRefreshListener_1(new MainActivity.FragmentRefreshListener_1() {
            @Override
            public void onRefresh_fone(String category) {
                category_public = category;
                setAdapter(category_public);
            }


        });



        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(firstVisibleItem+visibleItemCount == (totalItemCount-2) && totalItemCount!=0)
                {
                    if(flag_loading == false && end == false)
                    {
                        flag_loading = true;
                        additems(category_public);

                        Bundle params = new Bundle();
                        params.putString("activity", "FragmentTrending");
                        params.putString("event_name", "Add 10 more itmes");
                        mFirebaseAnalytics.logEvent("Fragment_Trending_10_more", params);

                    }
                }
            }
        });

        pullToRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Bundle params = new Bundle();
                params.putString("activity", "FragmentTrending");
                params.putString("event_name", "Pull To Refresh");
                mFirebaseAnalytics.logEvent("Fragment_Trending_pull2refresh", params);

                setAdapter(category_public);
                //gridViewAdapter.notifyDataSetChanged();

                 end = false;
            }
        });


        return rootView;
    }

    private void additems(String category) {


try{

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.v("tag3",category);
        CollectionReference collRef = db.collection("collection1");

        Query query;
    java.sql.Timestamp stamp = new java.sql.Timestamp(System.currentTimeMillis() - (3600 * 1000 * 24));
    Date date = new Date(stamp.getTime());

    if (category.contains("Home")) {

        query = collRef
                .orderBy("time", Query.Direction.ASCENDING)
                .orderBy("upvote", Query.Direction.DESCENDING)
                .orderBy("downvote", Query.Direction.ASCENDING)
                .whereGreaterThan("time", date);


        //.endBefore("time",date);


    } else {
        query = collRef.whereArrayContains("category", category)
                .orderBy("time", Query.Direction.ASCENDING)
                .orderBy("upvote", Query.Direction.DESCENDING)
                .orderBy("downvote", Query.Direction.ASCENDING)
                .whereGreaterThan("time", date);

    }

        query
                .startAfter(lastVisible)
                .limit(getResources().getInteger(R.integer.page_limit))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        try{
                            lastVisible = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() -1);
                        } catch (ArrayIndexOutOfBoundsException e)
                        {
                            end = true;
                        }

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG1", document.get("link").toString());
                                all_document.add(document);

                                allDrawableImages.add(document.get("link").toString());
                                allDocumentReference.add(document.getReference().getPath());
                                if(document.get("description")==null)
                                    allDesc.add("");
                                else
                                    allDesc.add(document.get("description").toString());

                                all_category.add(document.get("category").toString());
                                all_timestamp.add(document.get("time").toString());
                                all_upvotes.add(Integer.parseInt(document.get("upvote")+""));
                                all_downvotes.add(Integer.parseInt(document.get("downvote")+""));

                                flag_loading = false;
                                gridViewAdapter.notifyDataSetChanged();





                            }
                        } else {
                            Log.w("TAG1", "Error getting documents.", task.getException());
                        }
                    }

                });



    }catch (Exception e){e.printStackTrace();}











    }

    private void getAllWidgets(View view) {
        gridView = (ListView) view.findViewById(R.id.gridViewFragmentOne);
    }

    private void setAdapter(String category)
    {
        try{
        //--
            allDocumentReference.clear();
            allDrawableImages.clear();
            allDesc.clear();
all_document.clear();
            all_category.clear();
            all_timestamp.clear();
            all_upvotes.clear();
            all_downvotes.clear();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if(category.contains("Favorites"))
            {
                ArrayList<String> likedDocs =   getArrayList(LIKE);

                for(int i = 1;i<likedDocs.size();i++) {

                    DocumentReference docRef = db.document(likedDocs.get(i));
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Log.d("tagg8", "DocumentSnapshot data: " + document.getData());

                                if (document.exists()) {

                                    all_document.add((QueryDocumentSnapshot) document);

                                    allDrawableImages.add(document.get("link").toString());
                                    allDocumentReference.add(document.getReference().getPath());
                                    if (document.get("description") == null)
                                        allDesc.add("");
                                    else
                                        allDesc.add(document.get("description").toString());

                                    all_category.add(document.get("category").toString() + "");
                                    all_timestamp.add(document.get("time").toString());
                                    all_upvotes.add(Integer.parseInt(document.get("upvote") + ""));
                                    all_downvotes.add(Integer.parseInt(document.get("downvote") + ""));

                                    Log.d("tagg8", allDrawableImages.size()+"-all_list");

                                }
                            }
                            gridViewAdapter.notifyDataSetChanged();
                        }
                    });
                }

                gridViewAdapter = new GridViewAdapter(MainActivity.getInstance(), allDrawableImages, allDesc, all_category, all_timestamp, all_upvotes, all_downvotes, allDocumentReference,all_document);
                Log.w("tagg8", gridViewAdapter.getCount()+" adapter - count");

                gridView.setAdapter(gridViewAdapter);

                pullToRefresh.setRefreshing(false);
                flag_loading = false;


            }

            else {
                //----

                Log.v("tag3", category);
                CollectionReference collRef = db.collection("collection1");

                Query query;

                java.sql.Timestamp stamp = new java.sql.Timestamp(System.currentTimeMillis() - (3600 * 1000 * 24));
                Date date = new Date(stamp.getTime());


                Log.i("tagg7 -timestamp", System.currentTimeMillis() + "-" + stamp);

                if (category.contains("Home")) {

                    query = collRef
                            .orderBy("time", Query.Direction.ASCENDING)
                            .orderBy("upvote", Query.Direction.DESCENDING)
                            .orderBy("downvote", Query.Direction.ASCENDING)
                            .whereGreaterThan("time", date);


                    //.endBefore("time",date);


                } else {
                    query = collRef.whereArrayContains("category", category)
                            .orderBy("time", Query.Direction.ASCENDING)
                            .orderBy("upvote", Query.Direction.DESCENDING)
                            .orderBy("downvote", Query.Direction.ASCENDING)
                            .whereGreaterThan("time", date);

                }
                Log.i("tagg2-f1", category);
                query
                        .limit(getResources().getInteger(R.integer.page_limit))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                try {
                                    lastVisible = queryDocumentSnapshots.getDocuments()
                                            .get(queryDocumentSnapshots.size() - 1);

                                } catch (ArrayIndexOutOfBoundsException e) {
                                    end = true;
                                }
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        Log.d("TAG1", document.getReference().getPath());
                                        all_document.add(document);

                                        allDocumentReference.add(document.getReference().getPath());
                                        allDrawableImages.add(document.get("link").toString());

                                        if (document.get("description") == null)
                                            allDesc.add("");
                                        else
                                            allDesc.add(document.get("description").toString());

                                        all_category.add(document.get("category").toString());
                                        all_timestamp.add(document.get("time").toString());
                                        all_upvotes.add(Integer.parseInt(document.get("upvote") + ""));
                                        all_downvotes.add(Integer.parseInt(document.get("downvote") + ""));
                                    }

                                    Log.w("tag22-f1", allDrawableImages.size() + "");

                                    gridViewAdapter = new GridViewAdapter(MainActivity.getInstance(), allDrawableImages, allDesc, all_category, all_timestamp, all_upvotes, all_downvotes, allDocumentReference,all_document);
                                    Log.w("tag2", allDrawableImages.size() + "");

                                    gridView.setAdapter(gridViewAdapter);

                                    pullToRefresh.setRefreshing(false);
                                    flag_loading = false;


                                } else {
                                    Log.w("TAG1", "Error getting documents.", task.getException());
                                }
                            }

                        });


            }


        }catch (Exception e){e.printStackTrace();}

    }
    public ArrayList<String> getArrayList(String key) {
        SharedPreferences prefs = getContext().getSharedPreferences(FAVORITES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

}
