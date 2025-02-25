package com.g04autochef.controller.utils;

import com.g04autochef.data_access.DAO;
import com.g04autochef.data_access.Picturable;
import com.g04autochef.model.storableDAO.StorableDAO;
import javafx.scene.image.Image;

import java.io.File;

public class ImageController<Type extends StorableDAO, ObjectDAO extends DAO<Type> & Picturable<Type>> {

    private final ObjectDAO dao;

    public ImageController(ObjectDAO dao) {
        this.dao = dao;
    }

    public Image getImage(Type t) {
        String path = dao.getImagePath(t);
        try {
            return new Image(new File(path).toURI().toString());
        } catch (Exception e) {
            return null;
        }
    }
}