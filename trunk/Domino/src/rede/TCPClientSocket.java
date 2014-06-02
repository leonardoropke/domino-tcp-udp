/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rede;

/**
 *
 * @author Raissa2
 */
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TCPClientSocket {

    String serverLocation;
    int portNumber;

    public TCPClientSocket(String serverLocation, int portNumber) {
        this.serverLocation = serverLocation;
        this.portNumber = portNumber;
    }

    public boolean enviarMensagem(String msg) throws ParseException {
        Socket skt;
        try {
            skt = new Socket(serverLocation, portNumber);
            PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
            JSONObject mensagem = new JSONObject();
            mensagem.put("mensagem", msg);
            out.print(mensagem.toJSONString());
            out.close();
            JSONParser parser = new JSONParser();       
            BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            mensagem = (JSONObject) parser.parse(in);
            System.out.println(mensagem);
            skt.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(TCPClientSocket.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean receberMensagem() {
        Socket skt;
        BufferedReader in;
        JSONObject mensagem;
        JSONParser parser = new JSONParser();
        try {
            skt = new Socket(serverLocation, portNumber);
            skt.wait();
            in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            mensagem = (JSONObject) parser.parse(in);
            System.out.println(mensagem);
            in.close();
            skt.close();
            return true;
        } catch (ConnectException e) {
        } catch (Exception e) {
            System.out.print(e);
            System.out.print("Erro no cliente!");
        }
        return false;
    }

    public static void main(String args[]) throws ParseException {
        TCPClientSocket socket = new TCPClientSocket("localhost", 1234);
        socket.enviarMensagem("teste");
        socket.receberMensagem();
    }
}
