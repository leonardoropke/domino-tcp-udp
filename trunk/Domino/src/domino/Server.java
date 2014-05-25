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

class Server {

    public static void enviarMensagem(String msg) {
        JSONObject mensagem = new JSONObject();
        mensagem.put("mensagem", msg);
        String data = mensagem.toJSONString();
        try {
            ServerSocket srvr = new ServerSocket(1234);
            Socket skt = srvr.accept();
            System.out.print("Servidor conectado!\n");
            PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
            out.print(data);
            out.close();
            skt.close();
            srvr.close();
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    public static void main(String args[]) throws IOException {
        String mensagem = "";
        while (!mensagem.matches("sair")) {
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Digite a sua mensagem abaixo:");
            mensagem = bufferRead.readLine();
            enviarMensagem(mensagem);
        }

    }
}