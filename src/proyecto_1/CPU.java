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
    
    static final int LARGOMEMORIA = 128;
    static final int LARGODISCO = 1024;
    static String[] memoria = new String[LARGOMEMORIA];
    static String[] disco = new String[LARGODISCO];
    Nucleo nucleo1, nucleo2;
    List<Trabajo> colaTrabajoN1, colaTrabajoN2;
    List<BCP> procesos;
    
    public CPU(){
        this.nucleo1 = new Nucleo();
        this.nucleo2 = new Nucleo();
        this.colaTrabajoN1 = new ArrayList<>();
        this.colaTrabajoN2 = new ArrayList<>();
        this.procesos = new ArrayList<>();
    }
    
    public Nucleo obtenerNucleo1(){
        return nucleo1;
    }
    
    public Nucleo obtenerNucleo2(){
        return nucleo2;
    }
    
    public List<Trabajo> obtenerColaTrabajoN1(){
        return colaTrabajoN1;
    }
    
    public List<Trabajo> obtenerColaTrabajoN2(){
        return colaTrabajoN2;
    }
    
    public List<BCP> obtenerProcesos(){
        return procesos;
    }
    
    private void verificaProcesos(){
        int numeroProcesos=procesos.size();
        BCP proceso;
        for(int i=0;i<numeroProcesos;i++){
            proceso=procesos.get(i);
            if(proceso.obtenerEstadoProceso()==BCP.EN_ESPERA){
                
            }
        }
    }
    
    public void crearProceso(){
        BCP proceso=new BCP(BCP.NUEVO, procesos.size(), 0, 0, 30);
        procesos.add(proceso);
    }
}
