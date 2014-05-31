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

public class TCPClientSocket {

    String serverLocation;
    int portNumber;

    public TCPClientSocket(String serverLocation, int portNumber) {
        this.serverLocation = serverLocation;
        this.portNumber = portNumber;
    }

    public boolean receberMensagem() {
        Socket skt;
        BufferedReader in;
        JSONObject mensagem;
        JSONParser parser = new JSONParser();
        try {
            skt = new Socket(serverLocation, portNumber);
            in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            mensagem = (JSONObject) parser.parse(in);
            System.out.println(mensagem);
            in.close();
            return true;
        } catch (ConnectException e) {
        } catch (Exception e) {
            System.out.print(e);
            System.out.print("Erro no cliente!");
        }
        return false;
    }

    public static void main(String args[]) {
        TCPClientSocket socket = new TCPClientSocket("localhost", 1234);
                socket.receberMensagem();
    }
}
