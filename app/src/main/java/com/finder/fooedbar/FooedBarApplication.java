package com.finder.fooedbar;

/**
 * Created by jasonlin on 5/21/16.
 */
public class FooedBarApplication extends android.app.Application{

    private int sessionID;


    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void setSessionID(int id) {
        this.sessionID = id;
    }

    public int getSessionID() {
        return this.sessionID;
    }

}