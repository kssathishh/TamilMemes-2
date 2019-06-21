package funny.tamil.gag.memes;

import android.content.Context;

import java.util.ArrayList;

public class Snap {

    private int mGravity;
    private String mText;
    private ArrayList<String> mApps;
    private Context mContext;
    public Snap(int gravity, String text, ArrayList<String> apps, Context context) {
        mGravity = gravity;
        mText = text;
        mApps = apps;
        mContext = context;
    }

    public String getText(){
        return mText;
    }

    public int getGravity(){
        return mGravity;
    }

    public ArrayList<String> getApps(){
        return mApps;
    }
    public Context getContext(){
        return mContext;
    }
}