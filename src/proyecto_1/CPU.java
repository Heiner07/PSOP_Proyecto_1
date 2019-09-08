/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_1;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Heiner
 */
public class CPU {
    
    static String[] memoria = new String[128];
    static String[] disco = new String[1024];
    Nucleo nucleo1, nucleo2;
    List<Trabajo> colaTrabajoN1, colaTrabajoN2;
    List<BCP> procesos = new ArrayList<>();
    
    public CPU(){
        this.nucleo1 = new Nucleo();
        this.nucleo2 = new Nucleo();
        this.colaTrabajoN1 = new ArrayList<>();
        this.colaTrabajoN2 = new ArrayList<>();
    }
    
}
