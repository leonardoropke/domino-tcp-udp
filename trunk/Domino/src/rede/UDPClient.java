package rede;


import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Regis
 */
public class UDPClient {
    //static String hostname = "192.168.1.2";
    public static void main(String args[]) {
        byte[] buffer = new byte[10];
        DataInputStream userInput = new DataInputStream(System.in);
        DatagramPacket inPacket = null;
        DatagramPacket outPacket = null;
        String line;
        try {
            DatagramSocket datagramSocket = new DatagramSocket(250);
            InetAddress addr = InetAddress.getLocalHost();
            while (true){
                System.out.print("> ");
                System.out.flush();
                line = userInput.readLine();
                if ((line==null) || line.equals("exit")) break;
                buffer = line.getBytes();
                outPacket = new DatagramPacket(buffer, line.length(), addr, 251);
                datagramSocket.send(outPacket);
                inPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(inPacket);
                buffer = inPacket.getData();
                line = new String(buffer, 0, inPacket.getLength());
                System.out.println("line");
            }
        } //end try
        catch(Exception e) 
        { 
            System.err.println(e); 
        }
    }
}
