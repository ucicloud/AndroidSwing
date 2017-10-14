package com.daomaker;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * @author hkb.
 * @since 2016/1/13/0013.
 */
public interface Maker {
     Entity build(Schema schema);
}
