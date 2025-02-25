package com.g04autochef.controller.utils;

import com.g04autochef.model.storableDAO.*;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class ShoppingListJSONExporter {


    private JSONObject exportShoppingList(ShoppingList list) {
        JSONObject object = new JSONObject();
        HashMap<String, HashMap<String, Vector<IngredientRecipe>>> sortedIngredients = list.getSortedIngredients();

        object.put("N", list.getName());
        JSONArray array = new JSONArray();
        JSONArray arrayType;
        JSONArray arrayUnit;
        for (String type : sortedIngredients.keySet()) {
            arrayType = new JSONArray();
            arrayType.put(type);
            for (String typeUnit : sortedIngredients.get(type).keySet()) {
                Vector<IngredientRecipe> ingredients = sortedIngredients.get(type).get(typeUnit);
                arrayUnit = new JSONArray();
                arrayUnit.put(typeUnit);
                for (IngredientRecipe ingr : ingredients) {
                    arrayUnit.put(ingr.getName());
                    arrayUnit.put(ingr.getQuantity());
                }
                arrayType.put(arrayUnit);
            }
            array.put(arrayType);
        }
        object.put("I", array);
        return object;
    }

    public String writeImg(ShoppingList list, String fileName) throws IOException, WriterException {
        JSONObject object = exportShoppingList(list);
        String content = object.toString();
        String file = "./img/qr/" + fileName + ".jpg";

        int width = 1000;
        int height = 1000;

        Map<EncodeHintType, Object> hint = new HashMap<>();
        hint.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hint);
        MatrixToImageWriter.writeToPath(bitMatrix,"jpg", Paths.get(file));
        return file;
    }
}
