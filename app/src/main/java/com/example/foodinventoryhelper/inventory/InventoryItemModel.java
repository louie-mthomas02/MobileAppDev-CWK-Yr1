package com.example.foodinventoryhelper.inventory;

public class InventoryItemModel {

    private int id;
    private String name;
    private float quantity;
    private String units;
    private String img;
    private boolean needToGetImg;

    public InventoryItemModel(int id, String name, float quantity, String units) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.units = units;
        this.img = null;
        this.needToGetImg = false;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public float getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }

    public String getUnits() {
        return units;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getImg(){return img;}

    public void setImg(String img) { this.img = img; }

    public boolean isNeedToGetImg() { return needToGetImg; }

    public void setNeedToGetImg(boolean needToGetImg) { this.needToGetImg = needToGetImg; }
}
