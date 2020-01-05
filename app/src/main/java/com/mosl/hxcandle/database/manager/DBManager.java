package com.mosl.hxcandle.database.manager;

import com.mosl.hxcandle.application.MyApplication;
import com.mosl.hxcandle.database.DaoMaster;
import com.mosl.hxcandle.database.DaoSession;

/**
 * Created by hongrong on 2016/10/8.
 */
public class DBManager {

    private static DBManager instance;
    private DaoMaster master;
    private DaoSession session;

    public DBManager(){
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(MyApplication.getAppContext(),"hxcandle_db",null);
        master = new DaoMaster(devOpenHelper.getWritableDatabase());
        session = master.newSession();
    }

    public static DBManager getInstance(){
        if (instance == null){
            instance = new DBManager();
        }
        return instance;
    }

    public DaoMaster getMaster() {
        return master;
    }

    public DaoSession getSession() {
        return session;
    }

    public DaoSession getNewSession() {
        session = master.newSession();
        return session;
    }
}
