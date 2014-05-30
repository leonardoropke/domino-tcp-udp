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

public class TCPServerSocket {

    private int portNumber;

    public TCPServerSocket(int portNumber) {
        this.portNumber = portNumber;
    }

    public void enviarResposta() {
        try {
            ServerSocket srvr = new ServerSocket(portNumber);
            Socket skt = srvr.accept();
            ObjectInputStream requisicao = new ObjectInputStream(skt.getInputStream());
            System.out.println("Servidor leu requisicao:" + requisicao.readObject());
            ObjectOutputStream resposta = new ObjectOutputStream(skt.getOutputStream());
            resposta.writeObject("tchau");
            requisicao.close();
            resposta.close();
            srvr.close();
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    public static void main(String args[]) throws IOException {
        TCPServerSocket server = new TCPServerSocket(1234);
        server.enviarResposta();
    }
}
