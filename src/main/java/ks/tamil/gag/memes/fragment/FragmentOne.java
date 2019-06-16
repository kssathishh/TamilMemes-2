package ks.tamil.gag.memes.fragment;


import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import ks.tamil.gag.memes.MainActivity;
import ks.tamil.gag.memes.R;
import ks.tamil.gag.memes.adapter.GridViewAdapter;


public class FragmentOne  extends Fragment {

    private ListView gridView;

    private TypedArray allImages;
    boolean flag_loading = false;

    final ArrayList<String> allDOcumentReference = new ArrayList<>();
    final ArrayList<String> allDrawableImages = new ArrayList<>();
    final ArrayList<String> allDesc = new ArrayList<>();
    final ArrayList<String> all_category = new ArrayList<>();
    final ArrayList<String> all_timestamp = new ArrayList<>();
    final ArrayList<Integer> all_upvotes = new ArrayList<>();
    final ArrayList<Integer> all_downvotes = new ArrayList<>();



    DocumentSnapshot lastVisible;
    GridViewAdapter gridViewAdapter;
    String category_public = "All";
    boolean end = false;
    SwipeRefreshLayout pullToRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_one, null);
        getAllWidgets(rootView);
        setAdapter("All");


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

                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(flag_loading == false && end == false)
                    {
                        flag_loading = true;
                        additems(category_public);
                    }
                }
            }
        });

        pullToRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

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

        if(category.contains("All"))
        {
            query =   collRef .orderBy("upvote", Query.Direction.DESCENDING);
        }
        else {
            query = collRef.whereArrayContains("category", category)
                    .orderBy("upvote", Query.Direction.DESCENDING);

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

                                allDrawableImages.add(document.get("link").toString());
                                allDOcumentReference.add(document.getReference().getPath());
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
            allDOcumentReference.clear();
            allDrawableImages.clear();
            allDesc.clear();

            all_category.clear();
            all_timestamp.clear();
            all_upvotes.clear();
            all_downvotes.clear();



            //----
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.v("tag3",category);
        CollectionReference collRef = db.collection("collection1");

        Query query;

    if(category.contains("All"))
    {
    query =   collRef .orderBy("upvote", Query.Direction.DESCENDING);
    }
    else {
    query = collRef.whereArrayContains("category", category)
            .orderBy("upvote", Query.Direction.DESCENDING);

    }
            Log.i("tagg2-f1",category );
       query
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

                                Log.d("TAG1", document.getReference().getPath());

                                allDOcumentReference.add( document.getReference().getPath());
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

                            gridViewAdapter = new GridViewAdapter(MainActivity.getInstance(), allDrawableImages,allDesc,all_category,all_timestamp,all_upvotes,all_downvotes,allDOcumentReference);
                                Log.w("tag2", allDrawableImages.size()+"");

                                gridView.setAdapter(gridViewAdapter);

                                pullToRefresh.setRefreshing(false);
                                flag_loading = false;


                        } else {
                            Log.w("TAG1", "Error getting documents.", task.getException());
                        }
                    }

                });





        }catch (Exception e){e.printStackTrace();}

    }


}
