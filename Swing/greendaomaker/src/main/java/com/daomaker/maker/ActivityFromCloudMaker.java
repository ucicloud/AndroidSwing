package com.daomaker.maker;

import com.daomaker.Maker;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * 从服务端拉下来的数据
 * @since 2016/1/14/0014.
 */
public class ActivityFromCloudMaker implements Maker {
    @Override
    public Entity build(Schema schema) {
        Entity entity = schema.addEntity("DB_CloudActivity");
        entity.setTableName("t_activity_cloud");
        entity.setClassNameDao("CloudActivityDao");

        entity.addLongProperty("actvId").columnName("actv_id").primaryKey();
        entity.addStringProperty("macId").columnName("macId");
        entity.addStringProperty("kidId").columnName("kidId");
        entity.addStringProperty("type").columnName("type");
        entity.addLongProperty("steps").columnName("steps");
        entity.addLongProperty("distance").columnName("distance");
        entity.addLongProperty("receivedDate").columnName("receivedDate");

        return entity;
    }
}
