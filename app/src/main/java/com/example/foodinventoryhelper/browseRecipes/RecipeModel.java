package com.example.foodinventoryhelper.browseRecipes;

public class RecipeModel {
    private String recipeName;
    private String imgid;
    private int recipeID;

    public RecipeModel(String recipeName, String imgID, int recipeID){
        this.recipeName = recipeName;
        this.imgid = imgID;
        this.recipeID = recipeID;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getImgid() {
        return imgid;
    }

    public void setImgid(String imgid) {
        this.imgid = imgid;
    }

    public int getRecipeID() { return recipeID; }

    public void setRecipeID(int recipeID) { this.recipeID = recipeID; }
}
