package com.daomaker.maker;

import com.daomaker.Maker;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * 从服务端拉下来的activity数据，经过转换后保存到该表
 * @since 2016/1/14/0014.
 */
public class ActivityFormatMaker implements Maker {
    @Override
    public Entity build(Schema schema) {
        Entity entity = schema.addEntity("DB_FormatActivity");
        entity.setTableName("t_activity_format");
        entity.setClassNameDao("FormatActivityDao");

        entity.addIdProperty().primaryKey().autoincrement().unique();
        entity.addLongProperty("actvId").columnName("actv_id");

        entity.addLongProperty("indoorId").columnName("indoor_id");
        entity.addLongProperty("indoorSteps").columnName("indoor_steps");
        entity.addLongProperty("outdoorId").columnName("outdoor_id");
        entity.addLongProperty("outdoorSteps").columnName("outdoor_steps");
        entity.addStringProperty("macId").columnName("macId");
        entity.addStringProperty("kidId").columnName("kidId");
        entity.addLongProperty("distance").columnName("distance");
        entity.addLongProperty("time").columnName("time");

        return entity;
    }
}
