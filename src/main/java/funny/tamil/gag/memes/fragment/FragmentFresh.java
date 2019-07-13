package funny.tamil.gag.memes.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.annotation.Nullable;

import funny.tamil.gag.memes.MainActivity;
import funny.tamil.gag.memes.R;
import funny.tamil.gag.memes.adapter.GridViewAdapter;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class FragmentFresh extends Fragment {

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
    ArrayList <QueryDocumentSnapshot> all_document = new ArrayList<>();

    DocumentSnapshot lastVisible;
    GridViewAdapter gridViewAdapter;
    String category_public = "Home";
    boolean end = false;
    SwipeRefreshLayout pullToRefresh;

    Button btn_new_post;
    private FirebaseAnalytics mFirebaseAnalytics;
    boolean first_time = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_one, null);
        getAllWidgets(rootView);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());


        pullToRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.pullToRefresh);
        btn_new_post = rootView.findViewById(R.id.btn_new_posts);

        setAdapter("Home");


        ((MainActivity)getActivity()).setFragmentRefreshListener_2(new MainActivity.FragmentRefreshListener_2() {
            @Override
            public void onRefresh_ftwo(String category) {
                category_public = category;
                setAdapter(category_public);
            }
        });




        

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(firstVisibleItem+visibleItemCount == (totalItemCount-3) && totalItemCount!=0)
                {
                    if(flag_loading == false && end == false)
                    {
                        flag_loading = true;
                        additems(category_public);
                        Bundle params = new Bundle();
                        params.putString("activity", "FragmentFresh");
                        params.putString("event_name", "Add 10 more items");
                        mFirebaseAnalytics.logEvent("Fragment_Fresh_10_more", params);
                    }
                }
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Bundle params = new Bundle();
                params.putString("activity", "FragmentFresh");
                params.putString("event_name", "Pull To Refresh");
                mFirebaseAnalytics.logEvent("Fragment_Fresh_pull2refresh", params);

                setAdapter(category_public);
                //gridViewAdapter.notifyDataSetChanged();

                end = false;
                btn_new_post.setVisibility(View.GONE);
            }
        });

        btn_new_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle params = new Bundle();
                params.putString("activity", "FragmentFresh");
                params.putString("event_name", "New Post Button");
                mFirebaseAnalytics.logEvent("Fragment_Fresh_new_post_btn", params);

                setAdapter(category_public);


                end = false;
                btn_new_post.setVisibility(View.GONE);
            }
        });










        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collRef = db.collection("collection1");

        Query query;
        query = collRef.orderBy("time", Query.Direction.DESCENDING);
        query.limit(10)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                     if(first_time)
                                         first_time = false;
                                     else
                                         btn_new_post.setVisibility(View.VISIBLE);

                                     break;
                                case MODIFIED:
                                     break;
                                case REMOVED:
                                    break;
                            }
                        }

                    }
                });





        return rootView;
    }

    private void additems(String category) {

try {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Log.v("tag3", category);
    CollectionReference collRef = db.collection("collection1");

    Query query;

    if (category.contains("Home")) {
        query = collRef.orderBy("time", Query.Direction.DESCENDING);
    } else {
        query = collRef.whereArrayContains("category", category)
                .orderBy("time", Query.Direction.DESCENDING);

    }

    query
            .startAfter(lastVisible)
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
                            //for loading Native ads
                            all_document.add(null);
                            allDrawableImages.add(null);
                            allDocumentReference.add(null);
                            allDesc.add(null);
                            all_category.add(null);
                            all_timestamp.add(null);
                            all_upvotes.add(null);
                            all_downvotes.add(null);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                         //   Log.d("TAG1", document.get("link").toString());



                            all_document.add(document);

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

    private void setAdapter(String category) {

       try {
           pullToRefresh.setRefreshing(true);
           gridView.smoothScrollToPosition(0);
       }catch (Exception e){e.printStackTrace();}

        try {
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

                if (category.contains("Home")) {
                    query = collRef.orderBy("time", Query.Direction.DESCENDING);
                }
                else {
                    query = collRef.whereArrayContains("category", category)
                            .orderBy("time", Query.Direction.DESCENDING);

                }
                Log.i("tagg2-f2", category);
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


    all_document.add(document);

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


                                    }
                                    gridViewAdapter = new GridViewAdapter(MainActivity.getInstance(), allDrawableImages, allDesc, all_category, all_timestamp, all_upvotes, all_downvotes, allDocumentReference,all_document);
                                    Log.w("tag22-f2", allDrawableImages.size() + "");

                                    gridView.setAdapter(   gridViewAdapter);

                                    pullToRefresh.setRefreshing(false);
                                    btn_new_post.setVisibility(View.GONE);

                                    flag_loading = false;


                                } else {
                                    Log.w("TAG1", "Error getting documents.", task.getException());
                                }
                            }

                        });



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
