package com.daomaker.maker;

import com.daomaker.Maker;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * 从手表同步过来的原始数据
 * @since 2016/1/14/0014.
 */
public class RawActivityMaker implements Maker {
    @Override
    public Entity build(Schema schema) {
        Entity entity = schema.addEntity("DB_RawActivity");
        entity.setTableName("t_raw_activity");
        entity.setClassNameDao("RawActivityDao");
        entity.addIdProperty().primaryKey().autoincrement().unique();
        entity.addStringProperty("indoorActivity").columnName("indoor_activity").notNull();
        entity.addStringProperty("outdoorActivity").columnName("outdoor_activity");
        entity.addIntProperty("timeZoneOffset").columnName("timeZoneOffset");
        entity.addLongProperty("time").columnName("time");
        entity.addStringProperty("macId").columnName("macId");

        return entity;
    }
}
