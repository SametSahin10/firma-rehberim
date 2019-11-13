package net.dijitalbeyin.firma_rehberim;

public class Category {
    private int categoryId;
    private String categoryName;

    public Category(int i, String str) {
        this.categoryId = i;
        this.categoryName = str;
    }

    public int getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(int i) {
        this.categoryId = i;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String str) {
        this.categoryName = str;
    }
}
