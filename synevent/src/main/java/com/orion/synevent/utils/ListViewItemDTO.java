package com.orion.synevent.utils;

public class ListViewItemDTO {

    private boolean checked = false;
    private String itemText = "";
    private Integer id;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public Integer getItemId(){
        return id;
    }

    public void setItemId(Integer id){
        this.id = id;
    }
}
