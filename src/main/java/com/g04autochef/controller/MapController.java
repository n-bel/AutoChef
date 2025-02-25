package com.g04autochef.controller;

import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.g04autochef.data_access.DAO;
import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.ShopFields.ShopFieldAddress;
import com.g04autochef.data_access.filters.ingredientFields.IngredientFieldName;
import com.g04autochef.model.storableDAO.Ingredient;
import com.g04autochef.model.storableDAO.Shop;
import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.map.DisplayShoppingIngredientViewController;
import com.g04autochef.view.map.MapViewController;
import com.g04autochef.view.Windows;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 * Controller for the Map to communicate with MapViewController
 */
public class MapController<ShopDAO extends DAO<Shop>,ShopIngredientPriceDAO extends DAO<Shop>, IngredientDAO extends DAO<Ingredient>> extends Controller implements MapViewController.MapViewListener, DisplayShoppingIngredientViewController.DisplayShoppingIngredientListener, AddHomeController.MapControllerListener {

    private final AddHomeController addHomeController = new AddHomeController(this);
    private final Stage stage;

    private String homeAddress="";

    private final ShopDAO shopDAO;
    private final IngredientDAO ingredientDAO;
    private final ShopIngredientPriceDAO shopIngredientPriceDAO;

    /**
     * Create a new object from the address selected from comboBox
     * and retrieve all the information from the address shop
     * @return return all the information from the shop with the ingredient and the prices selected with the Filter
     */
    public Vector<Shop> currentShopAddress(String currentAddress) throws FilterException, AccessException {
        final Filter<Shop> shopFilter = getShopFilter(currentAddress);
        final Vector<Shop> shopInit = shopIngredientPriceDAO.select(shopFilter);
        return shopInit;
    }

    /**
     * Create a new object from the address selected from comboBox
     * and retrieve all the information from the address shop
     * @return return all the information from the shop selected with the Filter
     *
     */
    public Vector<Shop> currentShopAddressEmpty(String currentAddress) throws FilterException, AccessException {
        final Filter<Shop> shopFilter = getShopFilter(currentAddress);
        final Vector<Shop> shopInit = shopDAO.select(shopFilter);
        return shopInit;
    }


    /**
     * {@return shop filter for the current shop select}
     */
    private Filter<Shop> getShopFilter(String currentAddress) throws FilterException {
        Filter<Shop> shopFilter = new Filter<>();
        shopFilter.addField(new ShopFieldAddress(currentAddress));
        return shopFilter;
    }


    public MapController(Stage stage, ControllerListener mainController, ShopDAO shopDAO, IngredientDAO ingredientDAO,ShopIngredientPriceDAO shopIngredientPriceDAO){
        super(mainController);
        this.stage = stage;
        this.shopDAO = shopDAO;
        this.ingredientDAO = ingredientDAO;
        this.shopIngredientPriceDAO = shopIngredientPriceDAO;
    }

    /**
     * Selects all available stores from the DAO
     * @return a vector with all shop addresses
     */
    @Override
    public Vector<String> getSuggestionAddress() throws AccessException {
        Vector<Shop> shopDTO= shopDAO.selectAll();
        Vector<String> shopDTOAddress = new Vector<>();
        for(Shop shop : shopDTO){
            shopDTOAddress.add(shop.getAddress());
        }
        return shopDTOAddress;
    }

    /**
     * Get all the ways of writing an address from the geocode
     * from the API that the user selected
     * @return a vector with all the addresses
     */
    @Override
    public Vector<String> initializeAttributesGeocode(GeocodeResult geocode) {
        Vector<String> listAttributes = new Vector<>();
        listAttributes.add(geocode.getAttributes().get("Addr_type").toString());
        listAttributes.add(geocode.getAttributes().get("PlaceName").toString());
        listAttributes.add(geocode.getAttributes().get("Place_addr").toString());
        listAttributes.add(geocode.getAttributes().get("Match_addr").toString());
        listAttributes.add(geocode.getAttributes().get("Type").toString());
        return listAttributes;
    }

    /**
     * Get the store that the user has written
     * and add it to the database with the DAO if an error
     * occur an exception will be shown
     */
    @Override
    public void addStore(Shop shop) {
        try{
            shopDAO.insert(shop);
            this.show();
        } catch (AccessException e){
            AlertBoxGenerator.showError("Ce shop n'a pas été ajouté",e);
        }
    }

    /**
     * Get the store that the user has written or selected
     * and delete it to the database with the DAO if an error
     * occur an exception will be shown
     */
    @Override
    public void deleteStore(Shop shop) {
        try{
            shopDAO.delete(shop);
            this.show();
        } catch (AccessException e){
            AlertBoxGenerator.showError("Ce shop n'a pas été supprimé",e);
        }
    }

    /**
     * Format according to what the user enters in case
     * the user enter a wrongly writte address
     * @return a list with all attributes of an address format
     */
    @Override
    public Vector<String> formatCallout(String addrType, String placeName,
                                        String placeAddr, String matchAddr, String locType) {
        String title;
        String detail;
        switch (addrType) {
            case "POI":
                title = placeName.equals("") ? "" : placeName;
                if (!placeAddr.equals("")) {
                    detail = placeAddr;
                } else if (!matchAddr.equals("") && !locType.equals("")) {
                    detail = !matchAddr.contains(",") ? locType : matchAddr.substring(matchAddr.indexOf(", ") + 2);
                } else {
                    detail = "";
                }
                break;
            case "StreetName":
            case "PointAddress":
            case "Postal":
                if (matchAddr.contains(",")) {
                    title = matchAddr.isBlank() ? "" : matchAddr.split(",")[0];
                    detail = matchAddr.isBlank() ? "" : matchAddr.substring(matchAddr.indexOf(", ") + 2);
                    break;
                }
            default:
                title = "";
                detail = matchAddr.equals("") ? "" : matchAddr;
        }
        Vector<String> listAttributes = new Vector<>(Arrays.asList(title, detail));
        return listAttributes;
    }

    @Override
    public void visualizeShop(Shop selectedShop) throws IOException, AccessException {
        FXMLLoader loader = getFxmlLoader(stage, MapViewController.class.getResource(Windows.VisualizeShops.getPathToFXML()));
        DisplayShoppingIngredientViewController displayShoppingIngredientViewController = loader.getController();
        displayShoppingIngredientViewController.setListener(this);

        displayShoppingIngredientViewController.setShop(selectedShop);
        displayShoppingIngredientViewController.initializeShopView();
        displayShoppingIngredientViewController.definitionTableViewIngredientPrice();
        displayShoppingIngredientViewController.displayIngredientPrices();
    }

    @Override
    protected void show() {
        try{
            FXMLLoader loader = getFxmlLoader(stage, MapViewController.class.getResource(Windows.ShopManager.getPathToFXML()));
            MapViewController mapViewController = loader.getController();
            mapViewController.setListener(this);
            mapViewController.setShops(getSuggestionAddress());
        } catch (IOException | AccessException e) {
            AlertBoxGenerator.showError("Erreur de chargement de la carte", e);
        }
    }

    @Override
    public void returnToMap() {
        this.show();
    }

    /**
     * With the name of the ingredient we get all the information related to the ingredient
     * through the dao
     * @return vector of requested ingredients
     */
    @Override
    public Vector<Ingredient> requestIngredients(String ingredientName) throws FilterException, AccessException {
        final Filter<Ingredient> ingredientFilter = new Filter<>();
        ingredientFilter.addField(new IngredientFieldName(ingredientName));
        final Vector<Ingredient> vectorIngredient = ingredientDAO.select(ingredientFilter);
        return vectorIngredient;
    }

    /**
     * Get all the names of each ingredient through the dao
     * @return a vector of ingredient name
     */
    @Override
    public Vector<String> getPossibleSuggestionsName() throws AccessException {
        Vector<Ingredient> ingredientsDTO = ingredientDAO.selectAll();
        Vector<String> ingredientsDTOName = new Vector<>();
        for (Ingredient ingredient : ingredientsDTO) {
            ingredientsDTOName.add(ingredient.getName());
        }
        return ingredientsDTOName;
    }

    /**
     * Save the user's changes to the ingredient and price for each store
     */
    @Override
    public void saveShopChanges(final Shop shop){
        try{
            shopIngredientPriceDAO.delete(shop);
        } catch (AccessException ignored) {}
        try{
            shopIngredientPriceDAO.insert(shop);
        }catch (AccessException ignored) {}
    }

    @Override
    public void showAddHomeAddressPopup() {
        addHomeController.show();
    }

    @Override
    public void setHomeAddress(final String homeAddress){
        this.homeAddress = homeAddress;
    }

    @Override
    public String showRoute() {
        return homeAddress;
    }

    @Override
    public Vector<String> markerInfoPoint(GeocodeResult geocode) {
        final String addrType, placeName, placeAddr, matchAddr, locType;
        Vector<String> listAttributes = initializeAttributesGeocode(geocode);

        addrType = listAttributes.get(0);
        placeName = listAttributes.get(1);
        placeAddr = listAttributes.get(2);
        matchAddr = listAttributes.get(3);
        locType = listAttributes.get(4);

        listAttributes = formatCallout(addrType, placeName, placeAddr, matchAddr, locType);
        return listAttributes;
    }

    @Override
    public HashMap<String, Object> calloutInfoPoint(Vector<String> listInfoGeocode) {
        HashMap<String, Object> listInfo = new HashMap<>();
        final String title, detail;

        title = listInfoGeocode.get(0);
        detail = listInfoGeocode.get(1);

        listInfo.put("title", title);
        listInfo.put("detail", detail);
        return listInfo;
    }
}
