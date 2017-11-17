/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

public class SlowConnectionHandler implements Runnable {

    private final Socket socket;
    private static int win = 0, remaining;
    private static Character a;
    private static String str;
    char[] getC;
    private static final Random rand = new Random();
    readText rt = new readText();

    SlowConnectionHandler(Socket clientSocket) {
        this.socket = clientSocket;
    }

    @Override
    public void run() {
        try {
            String given = rt.getLine(rand.nextInt(25143));
            System.out.println(given);
            win = 0;
            boolean first = true;
            boolean charFound = false;

            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                getC = new char[given.length()];
                remaining = 8;
                System.out.println(given);
                for (int i = 0; i < given.length(); i++) {
                    getC[i] = '_';
                }
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                String view = new String(getC).concat(Integer.toString(remaining)).concat(getSign(win));
                if (first) {
                    pw.print(view);
                    pw.println();
                    pw.flush();
                }
                first = false;
//game already started
                while (remaining > 0) {
                    str = br.readLine();

                    if (str.length() < 2) {
                        a = str.charAt(0);
                        int index = 0;
                        while (index < given.length()) {
                            if (charCompare(a, given.charAt(index)) == true) {
                                if(getC[index]!=a)//deal with repeated input of the same character
                                    charFound = true;
                                     System.out.println("character found");
                                getC[index] = a;
                                index++;
                                
                                continue;
                            }
                            index++;
                        }
                        if (!charFound) {
                            remaining--;
                            System.out.println("character not found");
                        }
                        charFound = false;

                        if (arrayCompare(getC, given.toCharArray())) {//winMessage();
                             System.out.println("Guessed correctly using char");
                            win++;
                            remaining = 0;
                            given = generateWord();
                            view = new String(generateWord(given)).concat(Integer.toString(remaining)).concat(getSign(win));

                        } else if (remaining == 0) {//lossMessage();
                             System.out.println("Guessed failed using char");
                            win--;
                            given = generateWord();
                            view = new String(generateWord(given)).concat(Integer.toString(remaining)).concat(getSign(win));

                        } else {
                             System.out.println("continue geussing");
                            view = new String(getC).concat(Integer.toString(remaining)).concat(getSign(win));
                        }
                        pw.print(view);
                        pw.println();
                        pw.flush();

                    } else {//input is a word
                        if (str.compareToIgnoreCase(given) == 0) {//winMessage();
                            win++;
                            remaining = 0;
                            given = generateWord();
                            view = new String(generateWord(given)).concat(Integer.toString(remaining)).concat(getSign(win));
                            pw.print(view);
                            pw.println();
                            pw.flush();

                            break;
                        } else {//lossMessage();
                            remaining--;
                            if (remaining == 0) {
                                win--;
                            }
                            view = new String(getC).concat(Integer.toString(remaining)).concat(getSign(win));
                            pw.print(view);
                            pw.println();
                            pw.flush();
                        }
                    }
                }
            }
            //  System.out.println("Game exiting");
        } catch (IOException ex) {
            Logger.getLogger(SlowConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private char[] generateWord(String str) throws IOException {
        getC = new char[str.length()];
        for (int i = 0; i < str.length(); i++) {
            getC[i] = '_';
        }
        return getC;
    }

    private String generateWord() throws IOException {
        return rt.getLine(rand.nextInt(25143));
    }

    private static boolean charCompare(char c1, char c2) {
        return Character.toString(c1).compareToIgnoreCase(Character.toString(c2)) == 0;

    }

    private static boolean arrayCompare(char[] str1, char[] str2) {
        boolean equal = true;
        for (int i = 0; i < str1.length; i++) {
            if (str1[i] != str2[i]) {
                equal = false;
                break;
            }
        }
        return equal;
    }

    private String getSign(int a) {
        if (a == 0) {
            return "00";
        }
        if (a > 0) {
            return "+" + a;
        } else {
            return "-" + a * -1;
        }
    }
}
