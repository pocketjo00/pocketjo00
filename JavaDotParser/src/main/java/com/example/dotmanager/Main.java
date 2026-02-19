package com.example.dotmanager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        GraphManager manager = new GraphManager();
        if (args.length > 0) {
            String filepath = args[0];
            System.out.println("Processing file: " + filepath);
            try {
                manager.parseGraph(filepath);
                System.out.println(manager.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please provide a DOT file path as argument.");
        }
    }
}
