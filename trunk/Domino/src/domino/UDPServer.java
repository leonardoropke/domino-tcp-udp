package domino;


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
public class UDPServer {
    public static void main(String[] args) {
    byte[] buffer = new byte[1000];
    String sumString = "";
    DatagramSocket datagramSocket = null;
    DatagramPacket inPacket = null;
    DatagramPacket outPacket = null;
    InetAddress addr = null;
    try {
        datagramSocket = new DatagramSocket(251);
        while(true) {
            inPacket = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(inPacket);
            buffer = inPacket.getData();
            addr = inPacket.getAddress();
            String userInput = new String(buffer, 0, inPacket.getLength());
            buffer = userInput.getBytes();
            outPacket = new DatagramPacket(buffer, sumString.length(), addr, 250);
            datagramSocket.send(outPacket);
        }
    }
     catch (Exception e)
     { 
        System.err.println("Fechando DatagramSocket");
        if (datagramSocket != null)
            datagramSocket.close();
     }
 }

}
