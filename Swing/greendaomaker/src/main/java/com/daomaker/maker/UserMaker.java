package com.daomaker.maker;

import com.daomaker.Maker;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
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

//        entity.addIdProperty().primaryKey().autoincrement().unique();
        entity.addLongProperty("userId").columnName("users_id").notNull().primaryKey();
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
        entity.addStringProperty("registrationId").columnName("registrationId");

        //kids info table
        Entity kids = schema.addEntity("DB_Kids");
        kids.setTableName("t_kids");
        kids.setClassNameDao("KidsDao");
        kids.addLongProperty("kidsId").columnName("kids_id").notNull().primaryKey();
        kids.addStringProperty("name").columnName("name");
        kids.addLongProperty("dateCreated").columnName("dateCreated");
        kids.addStringProperty("macId").columnName("macId");
        kids.addStringProperty("currentVersion").columnName("currentVersion");
        kids.addStringProperty("firmwareVersion").columnName("firmwareVersion");
        kids.addStringProperty("profile").columnName("profile");
        kids.addStringProperty("state").columnName("state");
        kids.addIntProperty("battery").columnName("battery");
        kids.addLongProperty("subHostId").columnName("subHostId");
        kids.addIntProperty("shareType").columnName("shareType");//分享类型：0 无；1 自己分享给别人；2 别人分享给自己



        //建立user表与kids表一对多关系
        Property property = kids.addLongProperty("parentId").columnName("parent_id").getProperty();
        kids.addToOne(entity,property);

        entity.addToMany(kids,property).setName("kidsList");

        return entity;
    }
}
