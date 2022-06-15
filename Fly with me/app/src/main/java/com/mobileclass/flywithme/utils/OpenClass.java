//package com.mobileclass.flywithme;
//public class OpenClass {
//
//}
package com.mobileclass.flywithme.utils;

public class OpenClass {
    public static int theme = 1;
    public static int character = 0;
    public static int rocketAmount = 0;
    public static int healthAmount = 0;
    public static long pages = 1053;
    public static boolean home = false;
    public static String backTo = "theme";
    public void setTheme(int num)
    {

        theme = num;
    }
    public int getTheme()
    {

        return theme;
    }
    public void setCharacter(int num)
    {
        character = num;
    }
    public int getCharacter()
    {

        return character;
    }

    public String get_place(){
        return backTo;
    }
    public void set_place(String place){
        backTo = place;
    }

    public boolean go_home(){
        return home;
    }
    public void set_home(){
        home = true;
    }

    public void addHealth(int num) { healthAmount += num;}
    public int getHealthAmount() {return healthAmount;}

    public void addRocket(int num) { rocketAmount += num;}
    public int getRocketAmount() {return rocketAmount;}
}
