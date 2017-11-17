/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

public class readText {

   static File file = new File("words.txt");
    
    public readText() {
    }

    public String getLine(int lineNum) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        for (int i = 0; i < lineNum; i++) {
            line = br.readLine();
        }
        return line;
    }

    public static void main(String[] args) throws IOException {
        readText rt = new readText();
        System.out.println(rt.getLine(3));
        
    }
}