package com.daomaker.maker;

import com.daomaker.Maker;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * @since 2016/1/14/0014.
 */
public class UserMaker implements Maker {
    @Override
    public Entity build(Schema schema) {
        Entity entity = schema.addEntity("DB_User");
        entity.setTableName("t_user");
        entity.setClassNameDao("UserDao");
        entity.addIdProperty().primaryKey().autoincrement().unique();
        entity.addStringProperty("email").columnName("email").notNull();
        entity.addStringProperty("firstName").columnName("first_name");
        entity.addStringProperty("lastName").columnName("last_name");
        entity.addLongProperty("lastUpdate").columnName("last_update");
        entity.addLongProperty("dataCreate").columnName("data_create");
        entity.addStringProperty("zipCode").columnName("zip_code");
        entity.addStringProperty("phoneNum").columnName("phone_number");
        entity.addStringProperty("profile").columnName("profile");
        entity.addIntProperty("focusID").columnName("focus_id");
        entity.addIntProperty("focusPID").columnName("focus_pid");

        return entity;
    }
}
