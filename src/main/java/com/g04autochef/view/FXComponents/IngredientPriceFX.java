package com.g04autochef.view.FXComponents;

import com.g04autochef.model.storableDAO.IngredientPrice;
import com.g04autochef.view.map.DisplayShoppingIngredientViewController;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.util.Vector;

public final class IngredientPriceFX extends IngredientWithQuantityFX {

    private Spinner<Double> price;
    private final Double INIT_VALUE = 0.0;

    private void initializeSpinnerPrice(Double price) {
        this.price = new Spinner<>();
        final double min = 0.00, max = 1000.00, amountToStepBy = 0.5;
        this.price.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, price, amountToStepBy));
        this.price.setEditable(true);
    }

    public IngredientPriceFX(IngredientPrice ingredientPrice, Vector<String> possibleSuggestionsName, IngredientWithQuantityFXListener controller) {
        super(ingredientPrice.getIngredient(), possibleSuggestionsName, controller);
        initializeSpinnerPrice(ingredientPrice.getPrice());
    }

    /**
     * Constructor for creating a new ingredient price fx
     * @param possibleSuggestionsName names of products for the dynamic autocomplete
     * @param displayShoppingIngredientViewController the reference of the listener
     */
    public IngredientPriceFX(Vector<String> possibleSuggestionsName, DisplayShoppingIngredientViewController displayShoppingIngredientViewController) {
        super(possibleSuggestionsName, displayShoppingIngredientViewController);
        initializeSpinnerPrice(INIT_VALUE);
    }

    /**
     * Delete the ingredient by calling the parent method and also resetting the price to
     * the initial value
     */
    @Override
    protected void deleteIngredient(){
        super.deleteIngredient();
        price.getValueFactory().setValue(INIT_VALUE);
    }

    public Spinner<Double> getPrice() {
        return price;
    }
}
