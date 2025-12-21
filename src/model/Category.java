// Category enum: product categories used across the system

package model;

public enum Category {
    FRUIT, VEGETABLE, DAIRY, GROCERY, OTHER;

    public static Category fromString(String s) {
        if (s == null) return OTHER;
        try { return Category.valueOf(s.trim().toUpperCase()); }
        catch (Exception e) { return OTHER; }
    }
}
