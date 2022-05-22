//package com.mobileclass.flywithme;
//public class OpenClass {
//
//}
package com.mobileclass.flywithme;

public class OpenClass {
    public static int theme = 1;
    public static int character = 0;
    public static long pages = 1053;
    public static boolean home = false;
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

    public boolean go_home(){
        return home;
    }
    public void set_home(){
        home = true;
    }

}
