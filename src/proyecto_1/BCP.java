/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author Heiner
 */
public class BCP {
    
    // Estados del proceso
    static int NUEVO=0;
    static int EN_EJECUCION=1;
    static int EN_ESPERA=2;
    static int PREPARADO=3;
    static int TERMINADO=4;
    
    private int estadoProceso;
    private int numeroProceso;
    private int PC;
    private int AX, BX, CX, DX, IR, AC, direccionPila;
    Timer timer;
    int segundos;
    
    //Limites de memoria
    private int inicioMemoria, finMemoria;
    
    public BCP(int estadoProceso, int numeroProceso, int direccionPila, int inicioMemoria, int finMemoria){
        this.AX=0;
        this.BX=0;
        this.CX=0;
        this.DX=0;
        this.IR=0;
        this.AC=0;
        this.estadoProceso=estadoProceso;
        this.numeroProceso=numeroProceso;
        this.direccionPila=0;
        this.inicioMemoria=inicioMemoria;
        this.finMemoria=finMemoria;
        this.PC=inicioMemoria;
        this.segundos=0;
        this.timer=new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                segundos++;
            }
        });
        this.timer.start();
    }
    
    public void establecerRegistros(int AX, int BX, int CX, int DX, int IR, int AC){
        this.AX=AX;
        this.BX=BX;
        this.CX=CX;
        this.DX=DX;
        this.IR=IR;
        this.AC=AC;
    }
    
    public int obtenerNumeroProceso(){
        return numeroProceso;
    }
    
    public int obtenerEstadoProceso(){
        return estadoProceso;
    }
    
    public int obtenerDireccionpila(){
        return direccionPila;
    }
    
    public int obtenerInicioMemoria(){
        return inicioMemoria;
    }
    
    public int obtenerFinMemoria(){
        return finMemoria;
    }
    
    public int obtenerPC(){
        return PC;
    }
    
    public int[] obtenerRegistros(){
        return new int[]{AX, BX, CX, DX, IR, AC, PC};
    }
    
    public int obtenerTiempoEjecucion(){
        return segundos;
    }
    
    public static String estadoProcesoCadena(int estadoProceso){
        String estadoProcesoCadena;
        if(estadoProceso==BCP.EN_EJECUCION){
            estadoProcesoCadena="En Ejecución";
        }else if(estadoProceso==BCP.EN_ESPERA){
            estadoProcesoCadena="En Espera";
        }else if(estadoProceso==BCP.NUEVO){
            estadoProcesoCadena="Nuevo";
        }else if(estadoProceso==BCP.PREPARADO){
            estadoProcesoCadena="Preparado";
        }else{
            estadoProcesoCadena="Terminado";
        }return estadoProcesoCadena;
    }
}
