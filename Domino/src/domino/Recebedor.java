/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domino;

import java.io.InputStream;
import java.util.Scanner;

/**
 *
 * @author Raissa2
 */
public class Recebedor implements Runnable {

    private InputStream servidor;

    public Recebedor(InputStream servidor) {
        this.servidor = servidor;
    }

    public void run() {
        // recebe msgs do servidor e imprime na tela
        Scanner s = new Scanner(this.servidor);
        while (s.hasNextLine()) {
            System.out.println(s.nextLine());
        }
    }
}