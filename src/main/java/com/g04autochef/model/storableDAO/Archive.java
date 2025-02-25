package com.g04autochef.model.storableDAO;

/**
 * Base class for objects that can be archived.
 */
public abstract class Archive implements StorableDAO {
    private Boolean archived = false;

    final public void archive() {archived = true;}
    final public void unArchive() {archived = false;}
    final public Boolean isArchived() {return archived;}
}