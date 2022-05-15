package com.mobileclass.flywithme.multiple;

public class Singleton {
    // Static variable reference of single_instance
    // of type Singleton
    private static Singleton single_instance = null;

    // Declaring a variable of type String
    public String username;
    public String leftName;
    public String rightName;
    public String right;
    public String left;

    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself
    private Singleton()
    {
        username = "Hello I am a string part of Singleton class";
//        role = "set role";
//        right = "";
//        left = "";
    }

    // Static method
    // Static method to create instance of Singleton class
    public static Singleton getInstance()
    {
        if (single_instance == null)
            single_instance = new Singleton();

        return single_instance;
    }
}
