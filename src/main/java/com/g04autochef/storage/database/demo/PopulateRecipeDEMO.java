package com.g04autochef.storage.database.demo;
import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.model.DayOfTheWeek;
import com.g04autochef.model.TimeOfTheDay;
import com.g04autochef.model.storableDAO.*;
import com.g04autochef.storage.database.access.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/**
 * DEMO class to populate DB with example recipes.
 */

/*
compile with -Dfile.encoding=UTF-8 (Edit configuration -> Program arguments)
 */

public class PopulateRecipeDEMO {

    private final IngredientTypeDAODB ingredientTypeDao;
    private final UnitDAODB unitDAO;
    private final RecipeTypeDAODB recipeTypeDao;
    private final RecipeCookingStyleDAODB cookingStyleDao;
    private final ShopDAODB shopDao;
    private final IngredientDAODB ingredientDao;
    private final String PATISSERIES = "Patisserie";
    private final String FRAIS = "Produit frais";
    private final String SEL_EPICES_BOUILLON = "Epice" ;
    private final String VIANDE = "Viande";
    private final String LAITIER = "Produit laitiers";
    private final String LEGUME = "Légume";
    private final String AUTRE = "Autre";
    private final String GRAMME = "Gramme";
    private final String UNITE = "Unité";
    private final String CUILLERE_A_SOUPE = "Cuillère à soupe";
    private final String CUILLERE_A_CAFE = "Cuillère à café";
    private final String LITRE = "Litre";
    private final String MILLILITRE = "Millilitre";
    private final String BOITE = "Boite";


    private final ImportJson importJson;

    // DEMO MAIN --------------------------------------------------
    public static void main(String[] args) throws AccessException {
        DBManager.getInstance().dbConnect();
        DBManager.getInstance().dbCreateAllTables();
        PopulateRecipeDEMO demo = new PopulateRecipeDEMO();
        try {
            demo.insertTimeOfTheDay();
            demo.insertWeekDays();
        } catch (SQLException e) {
            throw new AccessException(e);
        }
        demo.addAll();
    }
    // ------------------------------------------------------------

    public PopulateRecipeDEMO() throws AccessException {
        unitDAO = new UnitDAODB();
        ingredientTypeDao = new IngredientTypeDAODB();
        recipeTypeDao = new RecipeTypeDAODB();
        cookingStyleDao = new RecipeCookingStyleDAODB();
        shopDao = new ShopDAODB();
        ingredientDao = new IngredientDAODB();
        importJson = new ImportJson();
    }

    public void insertWeekDays() throws SQLException{
        String sql = "INSERT INTO day_of_the_week(day_of_the_week_name) values(?)";
        try (PreparedStatement stmt = DBManager.getInstance().getDBConnection().prepareStatement(sql)) {
            for (DayOfTheWeek day : DayOfTheWeek.values()) {
                stmt.setString(1, day.name());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void insertTimeOfTheDay() throws SQLException{
        String sql = "INSERT INTO day_time(day_time_name) values(?)";
        try (PreparedStatement stmt = DBManager.getInstance().getDBConnection().prepareStatement(sql)) {
            for (TimeOfTheDay time : TimeOfTheDay.values()) {
                stmt.setString(1, time.name());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void addAll() throws AccessException {
        // Ingredients are add when they are made
        addIngredientType();
        addUnits();
        addRecipeType();
        addCookingStyle();
        addIngredients();
        addRecipe();
        addShop();

    }

    private void addIngredients() {
        try {
            String OEUF = "Oeuf";
            ingredientDao.insert(new Ingredient(OEUF, new IngredientType(FRAIS), new Vector<>(List.of(new Unit(UNITE)))));
            String LAIT = "Lait";
            ingredientDao.insert(new Ingredient(LAIT, new IngredientType(LAITIER), new Vector<>(List.of(new Unit(LITRE), new Unit(MILLILITRE)))));
            String SEL = "Sel";
            ingredientDao.insert(new Ingredient(SEL, new IngredientType(SEL_EPICES_BOUILLON), new Vector<>(List.of(new Unit(GRAMME), new Unit(CUILLERE_A_SOUPE), new Unit(CUILLERE_A_CAFE)))));
            String SUCRE = "Sucre";
            ingredientDao.insert(new Ingredient(SUCRE, new IngredientType(SEL_EPICES_BOUILLON), new Vector<>(List.of(new Unit(GRAMME), new Unit(CUILLERE_A_SOUPE), new Unit(CUILLERE_A_CAFE)))));
            String BEURRE = "Beurre";
            ingredientDao.insert(new Ingredient(BEURRE, new IngredientType(LAITIER), new Vector<>(List.of(new Unit(GRAMME)))));
            ingredientDao.insert(new Ingredient("Levure", new IngredientType(AUTRE), new Vector<>(List.of(new Unit(CUILLERE_A_CAFE), new Unit(CUILLERE_A_SOUPE), new Unit(GRAMME)))));
            ingredientDao.insert(new Ingredient("Chocolat pâtissier", new IngredientType(PATISSERIES), new Vector<>(List.of(new Unit(GRAMME)))));
            ingredientDao.insert(new Ingredient("Poudre d amandes", new IngredientType(AUTRE), new Vector<>(List.of(new Unit(GRAMME), new Unit(CUILLERE_A_SOUPE), new Unit(CUILLERE_A_CAFE)))));
            ingredientDao.insert(new Ingredient("Ail", new IngredientType(LEGUME), new Vector<>(List.of(new Unit(UNITE)))));
            ingredientDao.insert(new Ingredient("Moutarde", new IngredientType(AUTRE), new Vector<>(List.of(new Unit(GRAMME), new Unit(CUILLERE_A_CAFE), new Unit(CUILLERE_A_SOUPE), new Unit(BOITE)))));
            ingredientDao.insert(new Ingredient("Poivre", new IngredientType(SEL_EPICES_BOUILLON), new Vector<>(List.of(new Unit(GRAMME), new Unit(CUILLERE_A_SOUPE), new Unit(CUILLERE_A_CAFE)))));
            ingredientDao.insert(new Ingredient("Rôti de porc", new IngredientType(VIANDE), new Vector<>(List.of(new Unit(UNITE), new Unit(GRAMME)))));
            ingredientDao.insert(new Ingredient("Echalote", new IngredientType(LEGUME), new Vector<>(List.of(new Unit(UNITE), new Unit(GRAMME)))));
            ingredientDao.insert(new Ingredient("Crème fraiche", new IngredientType(LAITIER), new Vector<>(List.of(new Unit(MILLILITRE), new Unit(LITRE)))));

        } catch (Exception ignored) {}
    }

    private void addRecipe(){
        try {
            this.importJson.openJsonFile("populateDB.json");
            importJson.startImportRecipes();

        } catch (AccessException | FilterException | IOException e) {e.printStackTrace();}
    }

    private void addShop() throws AccessException {
        Shop shop1 = new Shop("lidl","Rue Delaunoy 14/16, 1080 Molenbeek-Saint-Jean") ;
        Shop shop2 =new Shop("Colruyt", "Rue Gray 102, 1040 Etterbeek");
        Shop shop3 = new Shop("aldi","Rue Goffart 40, Ixelles ");
        shopDao.insert(shop1);
        shopDao.insert(shop2);
        shopDao.insert(shop3);
    }

    private void addIngredientType(){
        try {
            ingredientTypeDao.insert(new IngredientType("Pate"));
            String FECULENT = "Féculent";
            ingredientTypeDao.insert(new IngredientType(FECULENT));
            String PRODUIT_MONDE = "Produit du monde";
            ingredientTypeDao.insert(new IngredientType(PRODUIT_MONDE));
            String FROMAGE = "Fromage";
            ingredientTypeDao.insert(new IngredientType(FROMAGE));
            String ALCOOL = "Alcool";
            ingredientTypeDao.insert(new IngredientType(ALCOOL));
            ingredientTypeDao.insert(new IngredientType(SEL_EPICES_BOUILLON));
            ingredientTypeDao.insert(new IngredientType(LEGUME));
            ingredientTypeDao.insert(new IngredientType(VIANDE));
            String FRUIT_SEC = "Fruit sec";
            ingredientTypeDao.insert(new IngredientType(FRUIT_SEC));
            String FRUIT = "Fruit";
            ingredientTypeDao.insert(new IngredientType(FRUIT));
            String HERBE = "Herbe";
            ingredientTypeDao.insert(new IngredientType(HERBE));
            String HUILE = "Huile";
            ingredientTypeDao.insert(new IngredientType(HUILE));
            String BOISSON = "Boisson";
            ingredientTypeDao.insert(new IngredientType(BOISSON));
            String PAIN = "Pain";
            ingredientTypeDao.insert(new IngredientType(PAIN));
            ingredientTypeDao.insert(new IngredientType(PATISSERIES));
            ingredientTypeDao.insert(new IngredientType(FRAIS));
            String SAUCE = "Sauce";
            ingredientTypeDao.insert(new IngredientType(SAUCE));
            String SURGELES = "Surgelé";
            ingredientTypeDao.insert(new IngredientType(SURGELES));
            ingredientTypeDao.insert(new IngredientType(AUTRE));
            ingredientTypeDao.insert(new IngredientType(LAITIER));


        } catch (AccessException e) {e.printStackTrace();}
    }
    private void addUnits() throws AccessException {
        unitDAO.insert(new Unit(GRAMME));
        unitDAO.insert(new Unit(UNITE));
        unitDAO.insert(new Unit(CUILLERE_A_SOUPE));
        unitDAO.insert(new Unit(CUILLERE_A_CAFE));
        unitDAO.insert(new Unit(LITRE));
        unitDAO.insert(new Unit(MILLILITRE));
        unitDAO.insert(new Unit(BOITE));
    }

    private void addCookingStyle() {
        try {
            cookingStyleDao.insert(new RecipeCookingStyle("Plat principal"));
            cookingStyleDao.insert(new RecipeCookingStyle("Mijoté"));
            cookingStyleDao.insert(new RecipeCookingStyle("Dessert"));
            cookingStyleDao.insert(new RecipeCookingStyle("Accompagnement"));
            cookingStyleDao.insert(new RecipeCookingStyle("Quiche"));
            cookingStyleDao.insert(new RecipeCookingStyle("Entrée"));
            cookingStyleDao.insert(new RecipeCookingStyle("Boisson"));
        } catch (AccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void addRecipeType() throws AccessException {
        String VEGE = "Végétarien";
        recipeTypeDao.insert(new RecipeType(VEGE));
        recipeTypeDao.insert(new RecipeType(VIANDE));
        String POISSON = "Poisson";
        recipeTypeDao.insert(new RecipeType(POISSON));
        recipeTypeDao.insert(new RecipeType(AUTRE));
    }
}


