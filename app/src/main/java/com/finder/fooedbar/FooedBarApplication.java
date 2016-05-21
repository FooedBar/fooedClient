package com.finder.fooedbar;

import com.finder.fooedbar.client.api.Session;

/**
 * Created by jasonlin on 5/21/16.
 */
public class FooedBarApplication extends android.app.Application{

    private int sessionID;
    private Session currentSession;

    {
        currentSession = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
//            currentSession = new Session(22.309638, 114.2245);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setSessionID(int id) {
        this.sessionID = id;
    }

    public int getSessionID() {
        return this.sessionID;
    }

    public Session getCurrentSession() {
        return this.currentSession;
    }

}