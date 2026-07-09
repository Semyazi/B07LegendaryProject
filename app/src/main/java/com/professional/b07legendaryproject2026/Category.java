package com.professional.b07legendaryproject2026;

public class Category(){
    private final static String[] categories;
    private int categoryNum;
    private String categoryName;

    public Category(int categoryNum){
        if(ID<0||ID>=categories.length){
            throw new Exception("Invalid ID");
        } else{
            this.categoryNum=categoryNum;
            categoryName = categories[categoryNum];
        }
    }

    public Category(String s){
        boolean found = false;

        for (int i = 0; i<categories.length; i++){
            if(s.equals(categories[i])){
                
            }
        }

    }
}