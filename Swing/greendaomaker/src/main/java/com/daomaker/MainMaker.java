package com.daomaker;

import com.daomaker.maker.ActivityFromCloudMaker;
import com.daomaker.maker.EventMaker;
import com.daomaker.maker.RawActivityMaker;
import com.daomaker.maker.UserMaker;

import java.io.File;
import java.util.ArrayList;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Schema;

/**
 * db table generator
 * @since 2017-10-14 11:20:30.
 */
public class MainMaker {
    public static void main(String args[]){
        Schema schema = new Schema(1,"com.kidsdynamic.data.dao");
        schema.setDefaultJavaPackageTest("com.kidsdynamic.data.dao.test");
        schema.setDefaultJavaPackageDao("com.kidsdynamic.data.dao");

        ArrayList<Maker> makers = new ArrayList<>();
        makers.add(new UserMaker());
        makers.add(new ActivityFromCloudMaker());
        makers.add(new EventMaker());
        makers.add(new RawActivityMaker());


        for (Maker maker : makers) {
            maker.build(schema);
        }
        try {
            String path = MainMaker.class.getClassLoader().getResource(".").getFile().toString()+"../../../src/gen/";
            System.out.println(path);
            File file = new File(path);
            if (!file.exists()){
                file.mkdirs();
            }
            new DaoGenerator().generateAll(schema, path);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
