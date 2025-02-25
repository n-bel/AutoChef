package com.g04autochef.controller;


import com.g04autochef.data_access.DAO;
import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.archiveFields.ArchiveArchivedField;
import com.g04autochef.data_access.filters.archiveFields.ArchiveNotArchivedField;
import com.g04autochef.data_access.filters.ingredientFields.IngredientFieldName;
import com.g04autochef.data_access.filters.menuWeeklyFields.MenuWeeklyFieldName;
import com.g04autochef.data_access.filters.shoppingListFields.ShoppingListFieldName;
import com.g04autochef.data_access.Updatable;
import com.g04autochef.controller.utils.ShoppingListEmailSender;
import com.g04autochef.controller.utils.ShoppingListJSONExporter;
import com.g04autochef.controller.utils.ShoppingListPDFExporter;
import com.g04autochef.model.storableDAO.Ingredient;
import com.g04autochef.model.storableDAO.IngredientRecipe;
import com.g04autochef.model.storableDAO.MenuWeekly;
import com.g04autochef.model.storableDAO.ShoppingList;
import com.g04autochef.storage.database.access.MenuWeeklyDAODB;
import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.Windows;
import com.g04autochef.view.shoppingList.*;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import javax.mail.*;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Vector;

/**
 * Controller for all the shopping lists views
 */
public class ShoppingListController<ShoppingListDAO extends DAO<ShoppingList> & Updatable<ShoppingList>, IngredientDAO extends DAO<Ingredient>, MenuWeeklyDAO extends DAO<MenuWeekly>> extends Controller implements DisplayShoppingListViewController.DisplayShoppingListListener, ArchivedShoppingListViewController.ArchivedShoppingListListener, GenerateShoppingListFromMenuViewController.GenerateShoppingListFromMenuListenerSLC, SendShoppingListMail.SendShoppingListMailListener, DisplayQrCodeViewController.DisplayQRCodeListener {

    private final Stage stage;
    private Stage addNameShoppingListStage;
    private Stage generateShoppingListFromMenuStage;
    private Stage sendShoppingListEmailStage;
    private Stage displayQrCodeStage;
    private final ShoppingListDAO shoppingListDAO;
    private Vector<ShoppingList> shoppingLists;
    private final IngredientDAO ingredientDAO;
    private final MenuWeeklyDAO menuWeeklyDAO;

    /**
     * @param stage Window that will contain the stage
     * @param mainController the reference to the mainController
     * @param shoppingListDAO the shopping list DAO for the communication with the database
     * @param ingredientDAO the ingredient DAO for the communication with the database
     */
    public ShoppingListController(Stage stage, ControllerListener mainController, ShoppingListDAO shoppingListDAO, IngredientDAO ingredientDAO, MenuWeeklyDAO menuWeeklyDAO) {
        super(mainController);
        this.stage = stage;
        this.shoppingListDAO = shoppingListDAO;
        this.ingredientDAO = ingredientDAO;
        this.menuWeeklyDAO = menuWeeklyDAO;
    }

    /**
     * Method overriden from Controller
     * will load the View and show it
     */
    @Override
    public void show() {
        try{
            FXMLLoader loader = getFxmlLoader(stage, DisplayShoppingListViewController.class.getResource(Windows.ShoppingList.getPathToFXML()));
            DisplayShoppingListViewController displayShoppingListViewController = loader.getController();
            displayShoppingListViewController.setListener(this);
            displayShoppingListViewController.definitionTableViewProduct();
            displayShoppingListViewController.definitionTableViewShoppingList();
            loadShoppingList(displayShoppingListViewController);
        } catch (IOException | AccessException | FilterException e) {
            AlertBoxGenerator.showError("Erreur de chargement de la liste de course", e);
        }
    }

    @Override
    public void closeAddShoppingList() {
        addNameShoppingListStage.hide();
    }

    @Override
    public void addNewShoppingList(String newShoppingListName, DisplayShoppingListViewController displayShoppingListViewController) throws AccessException, FilterException {
        closeAddShoppingList();
        verifyNameValidity(newShoppingListName, displayShoppingListViewController);
    }

    private void verifyNameValidity(String newShoppingListName, DisplayShoppingListViewController displayShoppingListViewController) throws FilterException, AccessException {
        Filter<ShoppingList> shoppingListFilter = new Filter<>();
        shoppingListFilter.addField(new ShoppingListFieldName(newShoppingListName));
        Vector<ShoppingList> shoppingListName = shoppingListDAO.select(shoppingListFilter);
        if (shoppingListName.size() == 0){
            displayShoppingListViewController.addShoppingListToTableView(newShoppingListName);

        }else{
            AlertBoxGenerator.showWarning("Ce nom est déjà utilisé", "");
        }
    }

    @Override
    public void showArchivedList() throws IOException, AccessException, FilterException {
        FXMLLoader loader = getFxmlLoader(stage, ArchivedShoppingListViewController.class.getResource(Windows.ArchiveList.getPathToFXML()));
        ArchivedShoppingListViewController archivedShoppingListViewController = loader.getController();
        archivedShoppingListViewController.setListener(this);

        archivedShoppingListViewController.definitionTableViewProduct();
        archivedShoppingListViewController.definitionTableViewShoppingList();

        loadShoppingList(archivedShoppingListViewController);
    }

    /**
     * @param shoppingList the shopping list that we want to modify
     * delete the shoppinglist from the database
     */
    @Override
    public void deleteShoppingList(ShoppingList shoppingList) {
        try{
            shoppingListDAO.delete(shoppingList);
        }
        catch (AccessException e){
            AlertBoxGenerator.showError("Erreur lors de la suppression de la liste", e);
        }
    }


    /**
     * @param shoppingList the current shopping list that we want to update
     */
    @Override
    public void updateShoppingListController(ShoppingList shoppingList) {
        try {
            shoppingListDAO.insert(shoppingList);

        }catch (AccessException e){
            try {
                shoppingListDAO.update(shoppingList);
            } catch (AccessException ex) {
                AlertBoxGenerator.showWarning("Une erreur s'est produite lors de la mise à jour !", "Suppression de la liste de courses");
            }
        }
    }

    @Override
    public void showAddShoppingListView(DisplayShoppingListViewController displayShoppingListViewController) throws IOException {
        FXMLLoader loader = new FXMLLoader(AddNameShoppingListViewController.class.getResource(Windows.AddShoppingList.getPathToFXML()));
        Parent root = loader.load();
        AddNameShoppingListViewController addNameShoppingListViewController = loader.getController();
        addNameShoppingListViewController.setListener(displayShoppingListViewController);

        addNameShoppingListStage = new Stage();
        addNameShoppingListStage.setScene(new Scene(root, 600, 400));

        addNameShoppingListStage.setTitle("Ajout Liste de Courses");
        addNameShoppingListStage.setResizable(false);
        addNameShoppingListStage.initModality(Modality.APPLICATION_MODAL);    // block primary stage while opened
        addNameShoppingListStage.showAndWait();
    }

    @Override
    public void showGenerateShoppingListFromMenuView(DisplayShoppingListViewController displayShoppingListViewController) throws IOException, AccessException, FilterException {
        FXMLLoader loader = new FXMLLoader(AddNameShoppingListViewController.class.getResource(Windows.GenerateShoppingListFromMenu.getPathToFXML()));
        Parent root = loader.load();
        GenerateShoppingListFromMenuViewController generateShoppingListFromMenuViewController = loader.getController();
        generateShoppingListFromMenuViewController.setListener(this);
        generateShoppingListFromMenuViewController.initializeMenuComboBox();
        final ShoppingList currentShoppingList = displayShoppingListViewController.getCurrentlySelectedShoppingList();
        generateShoppingListFromMenuViewController.setCurrentShoppingList(currentShoppingList);

        generateShoppingListFromMenuStage = new Stage();
        generateShoppingListFromMenuStage.setScene(new Scene(root, 600, 400));

        generateShoppingListFromMenuStage.setTitle("Génération Liste de Courses");
        generateShoppingListFromMenuStage.setResizable(false);
        generateShoppingListFromMenuStage.initModality(Modality.APPLICATION_MODAL);    // block primary stage while opened
        generateShoppingListFromMenuStage.showAndWait();
        loadShoppingList(displayShoppingListViewController);
    }

    /**
     * Overriden from all the listeners of sub controllers
     */
    @Override
    public void returnToShoppingList() {
        this.show();
    }

    /**
     * @param displayShoppingListViewController the controller of the view for calling methods
     * load the shopping list from the database for the DisplayShoppingList
     */
    public void loadShoppingList(DisplayShoppingListViewController displayShoppingListViewController) throws AccessException, FilterException {
        Filter<ShoppingList> shoppingListFilter = new Filter<>();
        shoppingListFilter.addField(new ArchiveNotArchivedField<>());
        shoppingLists = shoppingListDAO.select(shoppingListFilter);

        displayShoppingListViewController.setShoppingList(shoppingLists);
    }

    /**
     * @param archivedShoppingListListener the controller of the view for calling methods
     * load the shopping list from the database for the DisplayShoppingList
     */
    public void loadShoppingList(ArchivedShoppingListViewController archivedShoppingListListener) throws AccessException, FilterException {
        Filter<ShoppingList> shoppingListFilter = new Filter<>();
        shoppingListFilter.addField(new ArchiveArchivedField<>());
        shoppingLists = shoppingListDAO.select(shoppingListFilter);

        archivedShoppingListListener.setShoppingList(shoppingLists);
    }

    @Override
    public Vector<Ingredient> requestIngredients(String ingredientName) throws FilterException, AccessException {
        Filter<Ingredient> ingredientFilter = new Filter<>();
        ingredientFilter.addField(new IngredientFieldName(ingredientName));
        return ingredientDAO.select(ingredientFilter);
    }

    /**
     *{@return Vector of all the possible ingredients names for the dynamic completion}
     */
    @Override
    public Vector<String> getPossibleSuggestionsName() throws AccessException {
        Vector<Ingredient> ingredients = ingredientDAO.selectAll();
        Vector<String> ingredientsName = new Vector<>();
        for (Ingredient ingredient : ingredients) {
            ingredientsName.add(ingredient.getName());
        }
        return ingredientsName;
    }

    /**
     * @param ingredient the ingredient that we want to extract all of its units
     * {@return Vector of all the possible units names for the dynamic completion}
     */
    @Override
    public Vector<String> getUnitNames(IngredientRecipe ingredient) throws AccessException, FilterException {
        Filter<Ingredient> ingredientFilter = new Filter<>();
        ingredientFilter.addField(new IngredientFieldName(ingredient.getName()));
        Vector<Ingredient> currentIngredient = ingredientDAO.select(ingredientFilter);
        return currentIngredient.get(0).getUnitNames();
    }

    @Override
    public void exportToPDF(String name) {
        try {
            ShoppingList shoppingList = getShoppingList(name);
            ShoppingListPDFExporter exporter = new ShoppingListPDFExporter(shoppingList);
            String fileName = exporter.exportPDF();

            AlertBoxGenerator.showInfo("Le pdf a bien été créé", fileName);
        } catch (IOException | AccessException | FilterException e) {
            AlertBoxGenerator.showError("Erreur lors de la création du pdf",e);
        }
    }

    @Override
    public void displaySendEmail(String shoppingListName) throws IOException {
        FXMLLoader loader = new FXMLLoader(AddNameShoppingListViewController.class.getResource(Windows.SendShoppingListEmail.getPathToFXML()));
        Parent root = loader.load();
        SendShoppingListMail sendShoppingListMail = loader.getController();
        sendShoppingListMail.setListener(this);
        Vector<String> pdfNamesVector = searchPDF();
        sendShoppingListMail.initializePDFComboBox(pdfNamesVector);
        sendShoppingListMail.initializeComboBoxListener();
        sendShoppingListMail.setCurrentShoppingListName(shoppingListName);

        sendShoppingListEmailStage = new Stage();
        sendShoppingListEmailStage.setScene(new Scene(root, 600, 400));

        sendShoppingListEmailStage.setTitle("Envoie Liste de Courses par mail");
        sendShoppingListEmailStage.setResizable(false);
        sendShoppingListEmailStage.initModality(Modality.APPLICATION_MODAL);    // block primary stage while opened
        sendShoppingListEmailStage.show();
    }

    private ShoppingList getShoppingList(String name) throws FilterException, AccessException {
        Filter<ShoppingList> filter = new Filter<>();
        filter.addField(new ShoppingListFieldName(name));
        return shoppingListDAO.select(filter).get(0);
    }

    private String generateQrCode(ShoppingList shoppingList) throws IOException, WriterException {
        ShoppingListJSONExporter jsonExporter = new ShoppingListJSONExporter();
        String fileName = jsonExporter.writeImg(shoppingList, shoppingList.getName());
        return fileName;
    }
    @Override
    public void displayQrCode(String name) {
        try {
            ShoppingList shoppingList = getShoppingList(name);
            String fileName = generateQrCode(shoppingList);


            FXMLLoader loader = new FXMLLoader(AddNameShoppingListViewController.class.getResource(Windows.DisplayQRCode.getPathToFXML()));
            Parent root = loader.load();
            DisplayQrCodeViewController qrCodeViewController = loader.getController();
            qrCodeViewController.setListener(this);

            InputStream stream = new FileInputStream(fileName);
            Image image = new Image(stream);
            qrCodeViewController.setImage(image);
            qrCodeViewController.setTitle(shoppingList.getName());

            displayQrCodeStage = new Stage();
            displayQrCodeStage.setScene(new Scene(root, 525, 560));
            displayQrCodeStage.setTitle("Votre liste de course");
            displayQrCodeStage.setResizable(false);
            displayQrCodeStage.initModality(Modality.APPLICATION_MODAL);    // block primary stage while opened
            displayQrCodeStage.show();


        } catch (Exception e) {
            AlertBoxGenerator.showError("Erreur lors de l'affichage du QR Code",e);
        }
    }

    private Vector<String> searchPDF() {
        Vector<String> pdfNamesVector = new Vector<>();

        String userDirectory = System.getProperty("user.dir");
        Path dir = Paths.get(userDirectory);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.pdf")) {
            for (Path file : stream) {
                pdfNamesVector.add(String.valueOf(file.getFileName()));
            }
        } catch (IOException e) {
            AlertBoxGenerator.showError("Erreur: pas de pdf trouve !", e);
        }
        return pdfNamesVector;
    }

    @Override
    public Vector<MenuWeekly> requestMenu(String menuName) throws AccessException, FilterException {
        Filter<MenuWeekly> menuWeeklyFilter = new Filter<>();
        menuWeeklyFilter.addField(new MenuWeeklyFieldName(menuName));
        return menuWeeklyDAO.select(menuWeeklyFilter);
    }

    @Override
    public void confirmSelectedMenu(ShoppingList shoppingList) {
        try{
            shoppingListDAO.update(shoppingList);
        } catch (AccessException e) {
            e.printStackTrace();
        }
        generateShoppingListFromMenuStage.hide();
    }

    @Override
    public void cancelAndReturn() {
        generateShoppingListFromMenuStage.hide();
    }

    @Override
    public Vector<MenuWeekly> requestMenus() throws AccessException {
        MenuWeeklyDAODB menuWeeklyDAODB = new MenuWeeklyDAODB();
        return menuWeeklyDAODB.selectAll();
    }

    public void sendEmail(String emailDestination, String pdfName) throws GeneralSecurityException {
        AlertBoxGenerator.showInfo("L'email sera transferé", "Ceci peut prendre quelque instants");
        try {
            ShoppingListEmailSender.getInstance().sendEmail(emailDestination, pdfName);
            AlertBoxGenerator.showInfo("La liste de courses à bien été envoyé à l'adresse : "+emailDestination, "");
        } catch (MessagingException e) {
            AlertBoxGenerator.showError("Une erreur est survenue lors de l'envoie du mail", e);
        }
    }

    @Override
    public void confirmSending(String pdfName, String emailDestination) throws GeneralSecurityException {
        sendEmail(emailDestination, pdfName);
        sendShoppingListEmailStage.hide();
    }

    @Override
    public void cancelSending() {
        sendShoppingListEmailStage.hide();
    }

    @Override
    public String updateShoppingListNameEmail(String pdfName) {
        return pdfName.replace(".pdf", "");
    }


    @Override
    public void closeWindow() {
        displayQrCodeStage.hide();
    }
}
