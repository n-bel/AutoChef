package com.g04autochef.model.storableDAO;

import java.util.ArrayList;


/**
 * Interface for model objects.
 */
public interface StorableDAO {

    /**
     * @return Return the set of parameters that make the object unique.
     */
    ArrayList<String> getUniqueID();
}
