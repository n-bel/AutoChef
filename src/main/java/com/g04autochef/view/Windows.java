package com.g04autochef.view;

public enum Windows {
    MainMenu("MainMenu.fxml"),
    ShoppingList("DisplayShoppingList.fxml"),
    RecipeList("DisplayRecipe.fxml"),
    MenuManager("DisplayMenu.fxml"),
    AddProduct("AddIngredient.fxml"),
    AddMenu("CreateMenu.fxml"),
    CreateRecipe("CreateRecipe.fxml"),
    ArchiveList("DisplayShoppingListArchive.fxml"),
    MultipleMenus("GenerateMultipleMenus.fxml"),
    ShopManager("MapShops.fxml"),
    AddShoppingList("AddShoppingListName.fxml"),
    DisplayRecipeOnly("DisplayRecipeViewOnly.fxml"),
    VisualizeShops("DisplayShoppingIngredient.fxml"),
    GenerateShoppingListFromMenu("GenerateShoppingListMenu.fxml"),
    AddHomeAddress("HomeAddress.fxml"),
    SendShoppingListEmail("SendShoppingListEmail.fxml"),
    DisplayQRCode("DisplayQRCode.fxml"),
    ;

    private final String pathToFXML;

    Windows(final String path) {
        this.pathToFXML = path;
    }

    public String getPathToFXML() {
        return this.pathToFXML;
    }
}
