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

class Client {

    public Client() {
    }

    public static void receberMensagem() {
        Socket skt;
        BufferedReader in;
        JSONObject mensagem;
        JSONParser parser = new JSONParser();
        try {
            skt = new Socket("localhost", 1234);
            in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            mensagem = (JSONObject) parser.parse(in);
            System.out.println("Mensagem do servidor");
            System.out.println(mensagem);
            in.close();
        } catch (ConnectException e) {
            System.out.println("Aguardando dados do servidor...");
        } catch (Exception e) {
            System.out.print("Erro no cliente!");
        }

    }

    public static void main(String args[]) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                receberMensagem();
            }
        }, 1000, 1000);
    }
}
