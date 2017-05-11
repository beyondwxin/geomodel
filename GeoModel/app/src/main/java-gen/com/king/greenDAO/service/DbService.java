package com.king.greenDAO.service;

import android.content.Context;

import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.tinker.SampleApplicationLike;
import com.king.greenDAO.bean.GeoModel;
import com.king.greenDAO.bean.Position;
import com.king.greenDAO.dao.DaoSession;
import com.king.greenDAO.dao.GeoModelDao;
import com.king.greenDAO.dao.PositionDao;
import com.king.greenDAO.dao.UserDao;

import java.util.List;

/**
 * Created by king on 2016/11/4.
 */

public class DbService {

    private static DbService instance;
    private static Context appContext;
    private DaoSession mDaoSession;
    private UserDao userDao;
    private GeoModelDao geoModelDao;
    private PositionDao positionDao;


    private DbService() {
    }

    /**
     * 采用单例模式
     *
     * @param context 上下文
     * @return dbservice
     */
    public static DbService getInstance(Context context) {
        if (instance == null) {
            instance = new DbService();
            if (appContext == null) {
                appContext = context.getApplicationContext();
            }
            instance.mDaoSession = SampleApplicationLike.getInstance().getDaoSession();
            instance.userDao = instance.mDaoSession.getUserDao();
            instance.geoModelDao = instance.mDaoSession.getGeoModelDao();
            instance.positionDao = instance.mDaoSession.getPositionDao();
        }
        return instance;
    }

    public void delete(long id) {
        mDaoSession.getUserDao().deleteByKey(id);
    }

    /**
     * 根据userId,取出其类别下所有的信息
     *
     * @param userId 类别id
     * @return 信息列表
     */
    public List<GeoModel> getModelByUserId(long userId) {
        return userDao.load(userId).getModels();
    }

    /**
     * 添加或修改
     *
     * @param model 信息
     * @return 添加或修改的id
     */
    public long saveModel(GeoModel model) {
        return geoModelDao.insertOrReplace(model);
    }

    /**
     * 获取添加的一条数据
     *
     * @param userId
     * @return 信息列表
     */
    public List<GeoModel> getGaoModelByMaxId(long userId) {
        return geoModelDao.queryBuilder().where(GeoModelDao.Properties.UserId.eq(userId)).offset(0).limit(1).orderDesc(GeoModelDao.Properties.Id).list();
    }

    /**
     * 获取相关的位置列表
     *
     * @param modelId
     */
    public List<Position> getPositionByModelId(long modelId) {
        return positionDao.queryBuilder().where(PositionDao.Properties.ModelId.eq(modelId)).orderAsc(PositionDao.Properties.Id).list();
    }


    /**
     * 分页显示信息
     *
     * @param userId
     * @param pageNum  当前页数
     * @param pageSize 每页显示数
     * @return 信息列表
     */
    public List<GeoModel> getGaoModelByPageSize(long userId, int pageNum, int pageSize) {
        return geoModelDao.queryBuilder().where(GeoModelDao.Properties.UserId.eq(userId)).offset((pageNum - 1) * pageSize).limit(pageSize).orderDesc(GeoModelDao.Properties.Id).list();
    }

}
