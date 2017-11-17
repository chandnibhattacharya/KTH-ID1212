/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnection implements Runnable {

    private final String host;
    private final int port;
    private final SimpleClient gui;
    private final LinkedBlockingQueue<String> strings = new LinkedBlockingQueue<>();
    private boolean wordCompleted;

    public ServerConnection(SimpleClient gui, String host, int port) {
        this.host = host;
        this.port = port;
        this.gui = gui;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket("localhost", 1111);
            int i = 0;
            wordCompleted = false;
            while (!wordCompleted) {
                i++;
                PrintWriter pw = new PrintWriter(socket.getOutputStream());

                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String guess = br.readLine();
                gui.connected();
                gui.showResult(guess);
                byte[] toServer = strings.take().getBytes();
                String file = new String(toServer, "UTF-8");
                //System.out.println(file);
                pw.println(file);
                pw.flush();
            }
            socket.close();
        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//Used to submit a string for reversal.@param text The string to reverse.

    void reverse(String text) {
        strings.add(text);
    }
}
