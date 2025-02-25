package com.g04autochef.view.map;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.esri.arcgisruntime.tasks.networkanalysis.*;
import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.model.storableDAO.Shop;
import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.ViewController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class MapViewController extends ViewController {

    private MapViewListener mapController;
    @FXML private ComboBox<String> search_store;
    @FXML private Button getRoute;

    @FXML private TextField search_address_street;
    @FXML private TextField search_number;
    @FXML private TextField search_postalcode;
    @FXML private TextField search_name;


    @FXML private MapView mapview;
    @FXML private AnchorPane anchorPane;

    private LocatorTask locatorTask;
    private GraphicsOverlay graphicsOverlay;
    private PictureMarkerSymbol pinSymbol;
    private PictureMarkerSymbol houseSymbol;

    private String currentAddress;
    
    private RouteTask routeTask;
    private RouteParameters routeParameters;
    private final ObservableList<Stop> routeList = FXCollections.observableArrayList();
    private final ComboBox<String> travelmodes = new ComboBox<>();
    private final TitledPane routeInformationTitledPane = new TitledPane();

    private final HashMap<String, TravelMode> travelModesFR = new HashMap<>();


    public void setListener(final MapViewListener mapController) {
        this.mapController = mapController;
    }

    public void setupMap(){
        String API_KEY = "AAPKab7ba16628a34ebf8c5509ddace5d01bq_NdibYBuGKTPAbUwSO42U26cXQiF8LNx6Vf7Gtv4IUFbq31d48CXp5r_YGwxj7s";
        ArcGISRuntimeEnvironment.setApiKey(API_KEY);
        initialiseMapView();
        String apiGeoServerUrl = "https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer";
        locatorTask = new LocatorTask(apiGeoServerUrl);

        String apiRouteServerUrl = "https://route-api.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World";
        routeTask = new RouteTask(apiRouteServerUrl);
        routeTask.loadAsync();


    }

    private void initialiseMapView() {
        final ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS);
        mapview = new MapView();
        mapview.setMap(map);
        mapview.setViewpoint(new Viewpoint(50.850340, 4.351710, 144447.638572));
        graphicsOverlay = new GraphicsOverlay();
        mapview.getGraphicsOverlays().add(graphicsOverlay);

        final Callout callout = mapview.getCallout();
        callout.setLeaderPosition(Callout.LeaderPosition.BOTTOM);
    }

    /**
     * Sets up map view in FXML.
     * Map is only loaded in FXML and scene builder doesn't support editing it,
     * hence values must be given manually.
     */
    public void initializeAttributes(){

        routeInformationTitledPane.setText("Pas de route affiché ");
        travelmodes.setLayoutX(0);
        travelmodes.setLayoutY(30);
        anchorPane.getChildren().addAll(mapview,travelmodes,routeInformationTitledPane);
        AnchorPane.setBottomAnchor(mapview, 0.0);
        AnchorPane.setLeftAnchor(mapview, 0.0);
        AnchorPane.setRightAnchor(mapview, 0.0);
        AnchorPane.setTopAnchor(mapview, 0.0);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources){
        setupMap();
        initializeAttributes();
        setSymbolImage();
        final GeocodeParameters geocodeParameters = new GeocodeParameters();
        setupGeocoder(geocodeParameters);


        setGeocodeQueryEventHandler(geocodeParameters);
        setRouteQuery();
        travelmodes.getSelectionModel().selectedIndexProperty().addListener( e -> routeParameters.setTravelMode(travelModesFR.get(travelmodes.getSelectionModel().getSelectedItem())));
    }

    /**
     * {@return Setup the object image and return it}
     */
    private Image setImage(final String image){
        final String image_name = image;
        final InputStream ressourceInputStream = getClass().getResourceAsStream(image_name);
        final Image img = new Image(ressourceInputStream, 0, 30, true, true);
        return img;
    }

    /**
     * Load all the symbol
     */
    public void setSymbolImage(){
        String image_name = "/com/g04autochef/img/magasin_symbol.png";
        pinSymbol = new PictureMarkerSymbol(setImage(image_name));
        pinSymbol.loadAsync();

        String imageNameHome = "/com/g04autochef/img/house.png";
        houseSymbol = new PictureMarkerSymbol(setImage(imageNameHome));
        houseSymbol.loadAsync();

    }

    @FXML
    public void getRoute()  {

        String initPosition = mapController.showRoute();
        if (initPosition != null) {
            final GeocodeParameters geocodeParameters = new GeocodeParameters();
            setupGeocoder(geocodeParameters);
            setGeocodeQueryEventButton(geocodeParameters, initPosition);
        }
    }

    public void setShops(Vector<String> shopAdress)  {
        search_store.getItems().addAll(FXCollections.observableList(shopAdress));
        search_store.getSelectionModel().selectFirst();
    }

    /**
     * Allows to visualize the ingredients for the user's display.
     * If the store is empty the user will have to add ingredients
     * otherwise all ingredients and prices will be displayed
     */
    @FXML
    private void visualizeShop() throws IOException, AccessException, FilterException {

        if (search_store.getValue() != null) {
            Vector<Shop> shops = mapController.currentShopAddress(search_store.getValue());
            if(shops.isEmpty()){
                Vector<Shop> shopempty = mapController.currentShopAddressEmpty(search_store.getValue());

                mapController.visualizeShop(shopempty.get(0));
            }
            else{
                Shop currentShop = shops.get(0);
                mapController.visualizeShop(currentShop);
            }
        }
        else
            AlertBoxGenerator.showWarning("Aucun magasin selectionné", "Veuillez selectionner un magasin pour le visualiser");

    }

    private void hideCallout() {
        mapview.getCallout().dismiss();
    }

    /**
     * We get the selected store from the comboBox
     * @return the address selected
     */
    private String getUserQuery() {
        final String query;
        final boolean userSuppliedOwnQuery = search_store.getSelectionModel().getSelectedIndex() == -1;
        if (userSuppliedOwnQuery) {
            query = search_store.getEditor().getText();
        } else { // User chose a suggested query
            query = search_store.getSelectionModel().getSelectedItem();
        }

        return query;
    }

    public void setupGeocoder(final GeocodeParameters geocodeParameters){
        geocodeParameters.getResultAttributeNames().add("*");
        geocodeParameters.setMaxResults(1); // Get closest match
        geocodeParameters.setOutputSpatialReference(mapview.getSpatialReference());
    }



    @FXML private void addStore() throws IOException {
        if (aFieldIsEmpty()) {
            alertEmptyField();
        }
        else{
            final String address = getAdress();
            Shop shop = new Shop(search_name.getText(),address);
            mapController.addStore(shop);
        }
    }

    /**
     * retrieves the address that the user has written
     * @return the address
     */
    private String getAdress(){
        String address = search_address_street.getText() + " " + search_number.getText() + " " + search_postalcode.getText();
        return address;
    }


    /**
     * {@return True if any of the fields are empty and allows you to add or to delete }
     */
    private boolean aFieldIsEmpty(){
        final boolean fieldAddressIsEmpty = search_address_street.getText().trim().isEmpty();
        final boolean fieldPostalCodeIsEmpty = search_postalcode.getText().trim().isEmpty();
        final boolean fieldNumberIsEmpty = search_number.getText().trim().isEmpty();
        final boolean fieldNameIsEmpty = search_name.getText().trim().isEmpty();

        final boolean isAFieldEmpty = fieldAddressIsEmpty || fieldPostalCodeIsEmpty || fieldNumberIsEmpty || fieldNameIsEmpty;
        return isAFieldEmpty;
    }

    private static void alertEmptyField(){
        AlertBoxGenerator.showWarning("Un champ est vide !", "Veuillez remplir tout les champs");
    }

    /**
     * Button deleteStore is used to delete a store that is selected
     * or that has been written
     */
    @FXML private void deleteStore() throws IOException, AccessException, FilterException {
        if(noShopIsSelected()){
            alertNoShopSelected();
        }
        else{
            Vector<Shop> shops = mapController.currentShopAddress(search_store.getValue());
            if (shops.isEmpty()){
                Vector<Shop> shopempty = mapController.currentShopAddressEmpty(search_store.getValue());
                mapController.deleteStore(shopempty.get(0));
            }else{
                mapController.deleteStore(shops.get(0));

            }

        }
    }

    private boolean noShopIsSelected(){
        final boolean isNoShopSelected = search_store.getSelectionModel().isEmpty();
        return isNoShopSelected;
    }

    private static void alertNoShopSelected(){
        AlertBoxGenerator.showWarning("Pas de magasin sélectionné !", "Veuillez sélectionner un magasin");
    }

    @FXML private void returnToMainMenu() {
        mapController.returnToMainMenu();
    }

    @FXML private void addHomeAddress() {
        mapController.showAddHomeAddressPopup();
    }


    /**
     * creates all parameters for route, directions, travel mode and best route sequence
     */
    private void setRouteQuery(){
        routeTask.addDoneLoadingListener(() ->{
            if(routeTask.getLoadStatus() == LoadStatus.LOADED){
                final ListenableFuture<RouteParameters> routeParametersFuture = routeTask.createDefaultParametersAsync();
                routeParametersFuture.addDoneListener(() -> {
                    try{
                        routeParameters = routeParametersFuture.get();
                        routeParameters.setReturnStops(true);
                        routeParameters.setReturnDirections(true);
                        travelModesFR.put("Voiture", routeTask.getRouteTaskInfo().getTravelModes().get(1));
                        travelModesFR.put("Pied", routeTask.getRouteTaskInfo().getTravelModes().get(5));
                        travelModesFR.put("Vélo", routeTask.getRouteTaskInfo().getTravelModes().get(7));
                        travelmodes.getItems().addAll(travelModesFR.keySet());
                        travelmodes.getSelectionModel().selectFirst();
                        //routeParameters.setFindBestSequence(true);
                    } catch ( InterruptedException | ExecutionException e){
                        new Alert(Alert.AlertType.ERROR, "Pas de possibiliter de route avec les paramètres donner " + e.getMessage()).show();
                    }
                });
            } else {
                new Alert(Alert.AlertType.ERROR, "Impossible a charger la route " + routeTask.getLoadStatus().toString()).show();
            }
        });
    }

    private void getPointGeocode(GeocodeResult geocodeInit,GeocodeResult geocodeDest){
        if(routeList.size() == 0 ) {
            Stop destPoint = new Stop(new Point(geocodeInit.getDisplayLocation().getX(), geocodeInit.getDisplayLocation().getY(), geocodeDest.getDisplayLocation().getSpatialReference()));

            Stop initPoint = new Stop(new Point(geocodeDest.getDisplayLocation().getX(), geocodeDest.getDisplayLocation().getY(), geocodeDest.getDisplayLocation().getSpatialReference()));

            routeList.add(initPoint);
            routeList.add(destPoint);
            routeParameters.setStops(routeList);
        }
    }

    /**
     * Find the route and calculate the time and the km with the init pos and dest pos
     */
    private void getRouteOnMap(GeocodeResult geocodeInit,GeocodeResult geocodeDest){

        getPointGeocode(geocodeInit,geocodeDest);

        ListenableFuture<RouteResult> routeResultFuture =  routeTask.solveRouteAsync(routeParameters);

        routeResultFuture.addDoneListener(() -> {
           try{
               RouteResult routeResult = routeResultFuture.get();

               if(!routeResult.getRoutes().isEmpty()){
                   Route firstRoute = routeResult.getRoutes().get(0);
                   String output = String.format("Temps de deplacement : %d min (%.2f km)",Math.round(firstRoute.getTravelTime()),firstRoute.getTotalLength() / 1000);
                   routeInformationTitledPane.setText(output);
                   showDestinationOnMap(geocodeInit,geocodeDest,firstRoute);

               }
           } catch (InterruptedException | ExecutionException e){
               new Alert(Alert.AlertType.ERROR,"Echec résolution de la route").show();
               e.printStackTrace();
           }
        });
    }

    /**
     * Retrieves the geolocation of the two points
     */
    private void getGeocode(ListenableFuture<List<GeocodeResult>> locationInit,ListenableFuture<List<GeocodeResult>> locationDest){
        locationInit.addDoneListener(() -> {
            try{
                List<GeocodeResult> geocodesInit = locationInit.get();
                List<GeocodeResult> geocodesDest = locationDest.get();
                if (geocodesInit.size() > 0 && geocodesDest.size() > 0) {
                    GeocodeResult resultInit = geocodesInit.get(0);
                    GeocodeResult resultDest = geocodesDest.get(0);

                    getRouteOnMap(resultInit, resultDest);
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "Pas de résolution trouvé pour la localisation").show();
                }
            }catch (InterruptedException | ExecutionException e){
                new Alert(Alert.AlertType.ERROR,"Erreur pour obtenir le resultat.").show();
                e.printStackTrace();
            }
        });
    }

    /**
     * When the button is pressed, both addresses are retrieved and get their geocode
     */
    private void setGeocodeQueryEventButton(GeocodeParameters geocodeParammeters,String initPosition){
        getRoute.setOnAction((final ActionEvent evt) -> {

            final String initPos = initPosition;
            final String destPos = getUserQuery();
            final boolean destPosIsEmpty = destPos.equals("");
            final boolean initPosIsEmpty = initPos.equals("");
            if( !destPosIsEmpty && !initPosIsEmpty) {
                hideCallout();
                final ListenableFuture<List<GeocodeResult>> locationInit = locatorTask.geocodeAsync(initPos, geocodeParammeters);
                final ListenableFuture<List<GeocodeResult>> locationDest = locatorTask.geocodeAsync(destPos, geocodeParammeters);
                getGeocode(locationInit, locationDest);
            }
            else{
                new Alert(Alert.AlertType.WARNING, "VEUILLEZ RENTRER UNE POSITION DE DEPART ET DE DESTINATION").show();
            }
        });

    }
    /**
     * We get the selected store from the comboBox and
     * we call the API to read the address
     */
    private void setGeocodeQueryEventHandler(GeocodeParameters geocodeParameters) {
        search_store.setOnAction((final ActionEvent evt) -> {
            final String query = getUserQuery();
            currentAddress = query;
            final boolean queryIsEmpty = query.equals("");
            if (!queryIsEmpty) {
                hideCallout();
                final ListenableFuture<List<GeocodeResult>> results = locatorTask.geocodeAsync(query,geocodeParameters);
                results.addDoneListener(() ->{
                    try {
                        List<GeocodeResult> geocodes = results.get();
                        if (geocodes.size() > 0) {
                            GeocodeResult result = geocodes.get(0);
                            showShopOnlyOnMap(result);
                        } else {
                            AlertBoxGenerator.showInfo("Recherche","Pas de resultat");
                        }
                    }catch(InterruptedException | ExecutionException e){
                        AlertBoxGenerator.showError("Erreur lors de la recherche", e);
                    }
                });
                try {
                    mapController.currentShopAddress(query);
                } catch (FilterException  | AccessException e) {
                    AlertBoxGenerator.showError("Erreur lors de la recherche de l'adresse", e);
                }
            }
        });
    }


    /**
     * Update the pin where it is selected
     */
    private void updateMarker(Graphic markerShop,GeocodeResult geocode) {
        Platform.runLater(() -> {
            // clear out previous results
            mapview.getGraphicsOverlays().forEach(graphicsOverlay -> graphicsOverlay.getGraphics().clear());

            // add the marker to the graphics overlay
            graphicsOverlay.getGraphics().add(markerShop);
            // display the callout
            Callout callout = mapview.getCallout();
            callout.setTitle(markerShop.getAttributes().get("title").toString());
            callout.setDetail(markerShop.getAttributes().get("detail").toString());

            callout.showCalloutAt(geocode.getDisplayLocation(), new Point2D(0, -24), Duration.ZERO);

        });
    }

    /**
     * Update the pin where it is selected
     */
    private void updateMarkerDouble(Graphic home,Graphic markerShop,Graphic route, GeocodeResult geocode) {
        Platform.runLater(() -> {
            // add the marker to the graphics overlay
            mapview.getGraphicsOverlays().forEach(graphicsOverlay -> graphicsOverlay.getGraphics().clear());
            graphicsOverlay.getGraphics().add(home);
            graphicsOverlay.getGraphics().add(markerShop);
            graphicsOverlay.getGraphics().add(route);
            // display the callout
            Callout callout = mapview.getCallout();


            callout.showCalloutAt(geocode.getDisplayLocation(), new Point2D(0, -24), Duration.ZERO);
        });
    }

    /**
     * A zoom into the center of the geolocation
     */
    private void settingViewPoint(GeocodeResult geocode) {
        mapview.setViewpointCenterAsync(geocode.getDisplayLocation(), 100000);
    }

    /**
     * With the geolocation of the search points, it will put the image and the markers for the shop only
     */
    private void showShopOnlyOnMap(GeocodeResult geocode) throws ExecutionException, InterruptedException {
        final Vector<String> allInfoPoint;
        final HashMap<String, Object> listInfo;
        allInfoPoint = mapController.markerInfoPoint(geocode);
        listInfo = mapController.calloutInfoPoint(allInfoPoint);
        Graphic home = new Graphic(geocode.getDisplayLocation(), listInfo, pinSymbol);
        updateMarker(home, geocode);
        settingViewPoint(geocode);
    }

    /**
     * With the geolocation of the search points, it will put the image and the markers for the route and create a
     * route depending the travelmode
     */
    private void showDestinationOnMap(GeocodeResult geocodeInit,GeocodeResult geocodeDest,Route route) throws ExecutionException, InterruptedException {
        Geometry routeGeometry = route.getRouteGeometry();
        Graphic routeGraphic = new Graphic(routeGeometry,new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,0xff0000ff,2));

        Graphic home = new Graphic(geocodeInit.getDisplayLocation() ,houseSymbol);
        Graphic destination = new Graphic(geocodeDest.getDisplayLocation(),pinSymbol);
        updateMarkerDouble(home,destination,routeGraphic,geocodeDest);
        settingViewPoint(geocodeInit);
        routeList.clear();



    }

    public interface MapViewListener{
        Vector<String> initializeAttributesGeocode(GeocodeResult geocode);

        void returnToMainMenu();
        void addStore(Shop shop) throws IOException;
        void deleteStore(Shop shop) throws IOException;
        Vector<String> getSuggestionAddress() throws IOException, AccessException;
        Vector<String> formatCallout(String addrType, String placeName,
                                     String placeAddr, String matchAddr, String locType);
        void visualizeShop(Shop selectedShop) throws IOException, AccessException;
        Vector<Shop> currentShopAddress(String currentAddress) throws FilterException, AccessException;
        Vector<Shop> currentShopAddressEmpty(String currentAddress) throws FilterException, AccessException;
        void showAddHomeAddressPopup();
        String showRoute();
        Vector<String> markerInfoPoint(GeocodeResult geocode);
        HashMap<String, Object> calloutInfoPoint(Vector<String> listInfoGeocode);
    }
}
