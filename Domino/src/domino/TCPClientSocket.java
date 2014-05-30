/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domino;

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

    public void enviarRequisicao(String params) {
        try {
            Socket skt = new Socket(serverLocation, portNumber);
            ObjectOutputStream requisicao = new ObjectOutputStream(skt.getOutputStream());
            requisicao.writeObject(params);
            ObjectInputStream resposta = new ObjectInputStream(skt.getInputStream());
            JSONObject o = (JSONObject)resposta.readObject();
            System.out.println("Resposta do servidor:" + o);
            requisicao.close();
            resposta.close();
            skt.close();
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    public static void main(String args[]) {
        TCPClientSocket socket = new TCPClientSocket("localhost", 1234);
        socket.enviarRequisicao("olá!!!");
    }
}
