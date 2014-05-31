/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rede;

/**
 *
 * @author Raissa2
 */
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TCPServerSocket {

    private int portNumber;

    public TCPServerSocket(int portNumber) {
        this.portNumber = portNumber;
    }

    public boolean enviarMensagem(String msg) {
        try {
            ServerSocket srvr = new ServerSocket(portNumber);
            Socket skt = srvr.accept();
            PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
            out.print(msg);
            out.close();
            skt.close();
            srvr.close();
            return true;
        } catch (Exception e) {
            System.out.print(e);
        }
        return false;
    }

    public static void main(String args[]) throws IOException {
        TCPServerSocket server = new TCPServerSocket(1234);
        JSONObject o = new JSONObject();
        o.put("metodo", "teste");
        String mensagem = o.toJSONString();
        //while (true) {
           server.enviarMensagem(mensagem);
        //}

    }
}