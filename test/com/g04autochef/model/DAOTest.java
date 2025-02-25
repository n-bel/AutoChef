package com.g04autochef.model;
import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.ingredientFields.IngredientFieldName;
import com.g04autochef.data_access.filters.ingredientFields.IngredientFieldType;
import com.g04autochef.data_access.filters.ingredientFields.IngredientFieldUnit;
import com.g04autochef.model.storableDAO.Ingredient;
import com.g04autochef.storage.database.access.DBManager;
import com.g04autochef.storage.database.access.IngredientDAODB;
import com.g04autochef.storage.database.demo.PopulateRecipeDEMO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;

public class DAOTest {
    private final IngredientDAODB ingredientDAODB= new IngredientDAODB();

    public DAOTest() throws AccessException {
    }

    @BeforeAll
    private static void mockDBconstructor() throws AccessException, SQLException {
        DBManager.getInstance("mockDB").dbCreateAllTables();
        PopulateRecipeDEMO populate = new PopulateRecipeDEMO();
        try{
        populate.insertTimeOfTheDay();
        populate.insertWeekDays();
        populate.addAll();}
        catch(SQLException ignored){}
    }

    @Test
    public void selectAllIngredients() throws AccessException {
        Assertions.assertEquals(42,ingredientDAODB.selectAll().size());
    }

    @Test
    public void selectIngredientTypeFilter() throws AccessException, FilterException {
        Filter<Ingredient> filter= new Filter<>();
        filter.addField(new IngredientFieldType("Viande"));
        Assertions.assertEquals(3,ingredientDAODB.select(filter).size());
    }

    @Test
    public void selectIngredientNameFilter() throws AccessException,FilterException{
        Filter<Ingredient> filter= new Filter<>();
        filter.addField(new IngredientFieldName("Courgette"));
        Assertions.assertEquals(1,ingredientDAODB.select(filter).size());
    }

    @Test
    public void selectIngredientUnitFilter() throws AccessException,FilterException{
        Filter<Ingredient> filter= new Filter<>();
        filter.addField(new IngredientFieldUnit("Gramme"));
        Assertions.assertEquals(23,ingredientDAODB.select(filter).size());
    }

    @Test
    public void selectIngredientUnitTypeFilter() throws AccessException,FilterException{
        Filter<Ingredient> filter= new Filter<>();
        filter.addField(new IngredientFieldUnit("Gramme"));
        filter.addField(new IngredientFieldType("Epice"));
        Assertions.assertEquals(3,ingredientDAODB.select(filter).size());
    }

    @SuppressWarnings("unused")
    @AfterAll
    private static void cleanMockDB(){
        DBManager.getInstance().dbDisconnect();
        File dbFile = new File(DBManager.getInstance().getName());
        try{dbFile.delete();}
        catch (Exception ignored){}

    }

}
