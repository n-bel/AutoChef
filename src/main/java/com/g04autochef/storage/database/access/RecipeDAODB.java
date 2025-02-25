package com.g04autochef.storage.database.access;

import com.g04autochef.data_access.Picturable;
import com.g04autochef.data_access.Updatable;
import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.accessExceptions.ConnectionException;
import com.g04autochef.data_access.exceptions.accessExceptions.DoubleInsertException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.recipeFields.RecipeFieldName;
import com.g04autochef.model.storableDAO.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Vector;

public final class RecipeDAODB extends DAO_DB<Recipe> implements Updatable<Recipe>, Picturable<Recipe> {
    final IngredientDAODB ingredientDAO= new IngredientDAODB();
    final RecipeTypeDAODB recipeTypeDAO= new RecipeTypeDAODB();
    final RecipeCookingStyleDAODB recipeCookingStyleDAO = new RecipeCookingStyleDAODB();
    final UnitDAODB unitDAO = new UnitDAODB();
    public RecipeDAODB() throws AccessException {}

    @Override
    protected String getStringSelect() {
        return "select recipe_name,number_people,recipe_type_name,recipe_cooking_style_name from ((((recipe_ingredient natural join recipe natural join recipe_type) natural join" +
                " (ingredient natural join ingredient_type))natural join unit)natural join recipe_cooking_style) where ";
    }

    @Override
    protected String getStringSelectAll() {
        return "SELECT * FROM RECIPE natural join RECIPE_TYPE natural join recipe_cooking_style";
    }

    @Override
    protected String getStringGetID() {
        return "SELECT recipe_id FROM RECIPE where recipe_name=?";
    }

    @Override
    protected String getIDColumnName() {
        return "recipe_id";
    }

    @Override
    protected String getStringSelectGroupBy() {
        return "GROUP BY recipe_name";
    }

    @Override
    protected String getStringDelete() {
        return "DELETE FROM recipe where recipe_name=? ";
    }


    private Vector<String> getInstructions(String name) {
        Vector<String> instructions = new Vector<>();
        String request = "select instruction,step_number from ((select * from recipe r where r.recipe_name= ?) "+
                "natural join recipe_instruction) order by step_number";
        try (PreparedStatement stmt = conn.prepareStatement(request)){
            stmt.setString(1,name);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                instructions.add(rs.getString("instruction"));
            }
        }
        catch (SQLException ignored){}
        return instructions;

    }

    private Vector<IngredientRecipe> getIngredients(String name) {
        Vector<IngredientRecipe> ingredients = new Vector<>();
        String request = "select quantity,unit_name,ingredient_name,ingredient_type_name " +
                "from (((recipe_ingredient natural join recipe) natural join (ingredient natural join ingredient_type))natural join unit) " +
                "where recipe_name=?";
        try (PreparedStatement stmt = conn.prepareStatement(request)){
            stmt.setString(1,name);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String chosenUnit = rs.getString("unit_name");
                int quantity = rs.getInt("quantity");
                ingredients.add(new IngredientRecipe(ingredientDAO.buildModelObj(rs),new Unit(chosenUnit),quantity));
            }
        } catch (SQLException ignored) {}
        return ingredients;

    }


    @Override
    Recipe buildModelObj(final ResultSet rs) throws SQLException {
        String recipeName = rs.getString("recipe_name");
        int numberPeople = rs.getInt("number_people");
        String recipeTypeName = rs.getString("recipe_type_name");
        String style = rs.getString("recipe_cooking_style_name");
        Vector<String> instructions = getInstructions(recipeName);
        Vector<IngredientRecipe> ingredients = getIngredients(recipeName);
        return new Recipe(ingredients,instructions,numberPeople,recipeName, new RecipeType(recipeTypeName), new RecipeCookingStyle(style));
    }

    @Override
    protected Filter<Recipe> makeBasicFilter(Recipe obj){
        Filter<Recipe> filter=new Filter<>();
        try{filter.addField(new RecipeFieldName(obj.getName()));}
        catch(FilterException e){e.printStackTrace();}
        return filter;}


    @Override
    protected void insert(Recipe recipe,Integer id) throws AccessException {
        if (isPresent(recipe)) throw new DoubleInsertException();
        String insertRecipe = "INSERT into RECIPE(recipe_id,recipe_name,recipe_cooking_style_id,number_people,recipe_type_id) values(?,?,?,?,?)";

        try (PreparedStatement stmt = getPreparedStatement(insertRecipe)) {
            int typeId = recipeTypeDAO.getID(recipe.getType());
            int cookingStyleID = recipeCookingStyleDAO.getID(recipe.getCookingStyle());
            if(id==null) stmt.setNull(1,java.sql.Types.INTEGER);
            else stmt.setInt(1,id);
            stmt.setString(2,recipe.getName());
            stmt.setInt(3,cookingStyleID);
            stmt.setInt(4,recipe.getPeople());
            stmt.setInt(5,typeId);
            stmt.executeUpdate();

            insertRecipeComponents(recipe);
        }
        catch (SQLException e){
            throw new AccessException(e);}
    }

    private void insertRecipeComponents(Recipe recipe) throws AccessException {
        String insertRecipeIngredient = "INSERT into RECIPE_INGREDIENT(recipe_id,ingredient_id,quantity,unit_id) values(?,?,?,?)";
        String insertRecipeInstruction = "INSERT into RECIPE_INSTRUCTION(recipe_id,step_number,instruction) values (?,?,?)";
        int recipe_id= getID(recipe);
        int ingredient_id;
        int unit_id;
        try (PreparedStatement stmt2 = getPreparedStatement(insertRecipeIngredient) ;
             PreparedStatement stmt3 = getPreparedStatement(insertRecipeInstruction)) {
            for (IngredientRecipe ingredientRecipe: recipe.getIngredients()){
                ingredient_id = ingredientDAO.getID(ingredientRecipe.getIngredient().getIngredient());
                unit_id= unitDAO.getID(ingredientRecipe.getUnit());
                stmt2.setInt(1,recipe_id);
                stmt2.setInt(2,ingredient_id);
                stmt2.setInt(3,ingredientRecipe.getQuantity());
                stmt2.setInt(4,unit_id);
                stmt2.addBatch();
            }
            stmt2.executeBatch();

            int step_number = 1;
            for (String instruction: recipe.getInstructions()){
                stmt3.setInt(1,recipe_id);
                stmt3.setInt(2,step_number);
                stmt3.setString(3,instruction);
                stmt3.addBatch();
                ++step_number;
            }
            stmt3.executeBatch();
        } catch (SQLException e) {
            throw new AccessException(e);
        }

    }


    @Override
    public void update(Recipe recipe) throws AccessException {
        // remove old values
        int id = getID(recipe);
        String deleteOldRecipeIngr = "DELETE from RECIPE_INGREDIENT WHERE recipe_id=?";
        String deleteOldRecipeInstr = "DELETE from RECIPE_INSTRUCTION WHERE recipe_id=?";
        for (String s : new Vector<>(Arrays.asList(deleteOldRecipeIngr, deleteOldRecipeInstr))) {
            try (PreparedStatement stmt = getPreparedStatement(s)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new AccessException(e);
            }
        }

        // set new values
        insertRecipeComponents(recipe);
        String updateType = "UPDATE RECIPE SET recipe_type_id=?, number_people=?, recipe_cooking_style_id=? where recipe_id=?";
        int typeId = recipeTypeDAO.getID(recipe.getType());
        int styleId = recipeCookingStyleDAO.getID(recipe.getCookingStyle());
        try (PreparedStatement stmt = getPreparedStatement(updateType)){
            stmt.setInt(1, typeId);
            stmt.setInt(2, recipe.getPeople());
            stmt.setInt(3, styleId);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new AccessException(e);
        }


    }

    @Override
    public String getImagePath(Recipe recipe) {
        String path = null;
        int id = getID(recipe);
        String query = "SELECT picture_path FROM RECIPE_PICTURE WHERE recipe_id=? ";
        try (PreparedStatement stmt = getPreparedStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                path = rs.getString("picture_path");
            }
        } catch (SQLException | ConnectionException ignored) {}
        return path;
    }

    @Override
    public void setImage(Recipe recipe, String path) throws AccessException {
        int id = getID(recipe);
        String deleteQuery = "DELETE FROM RECIPE_PICTURE WHERE recipe_id=? ";
        String query = "INSERT INTO RECIPE_PICTURE(RECIPE_ID, PICTURE_PATH) values (?,?) ";
        try (PreparedStatement stmt1 = getPreparedStatement(deleteQuery);
             PreparedStatement stmt2 = getPreparedStatement(query)) {
            stmt1.setInt(1, id);
            stmt1.executeUpdate();
            stmt2.setInt(1, id);
            stmt2.setString(2, path);
            stmt2.executeUpdate();
        } catch (SQLException e) {
            throw new AccessException(e);
        }
    }
}
