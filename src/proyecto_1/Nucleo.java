/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 *
 * @author home
 */
public class Nucleo {
    
    //[0] = AC
    //[1] = AX
    //[2] = BX
    //[3] = CX
    //[4] = DX
    private int[] registros = {0,0,0,0,0};
    static String[] memoria = new String[100];
    private int PC=0, IR=0;
    private BCP procesoEjecutando=null;
    static int numeroInstrucciones=0;
    static boolean ejecutar = false;
    private boolean bandera= false;
    private Boolean listo = true; // Indica si está listo para ejecutar otra instrucción.
    private Boolean ejecutar1 = false;
    private Timer timerOperacion;
    private String[] parametros2;
    public Nucleo(){
        timerOperacion = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    // Función que repetirá según el intervalo asignado (1 segundo).
                    if(ejecutar1){
                        ejecutar1=false;
                        Operaciones("");
                        
                    }
                } catch (InterruptedException ex) {
                    // Modificar para mostrar mensaje correspondiente
                    Logger.getLogger(Nucleo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        // Inicializo el timer.
        timerOperacion.start();
    }
    
    public void Operaciones(String instrucciones2) throws InterruptedException{
        listo=false;
        // Asigno el IR
        IR=PC;
        // Traigo la instrucción de memoria y aumento el PC
       
        String instrucciones = CPU.memoria[PC++];
        String[] parts;
        parts = instrucciones.split(" ");
        String operacion = parts[0];
        String registro = parts[1];
        String numeroORegistro = parts[2];    
        
        switch(operacion) {
            case "0001"://LOAD
                registros[0] = registros[registroPosicion(registro)]; 
                Thread.sleep(TiempoInstrucciones.LOAD*1000);
                break;    
                
            case "0010"://STORE
                registros[registroPosicion(registro)] = registros[0];
                Thread.sleep(TiempoInstrucciones.STORE*1000);
                break;
                
            case "0011"://MOV
               movimiento(registro,numeroORegistro);
               Thread.sleep(TiempoInstrucciones.MOV*1000);
               break; 
                
            case "0100"://SUB
                restar(registro,numeroORegistro);
                Thread.sleep(TiempoInstrucciones.SUB*1000);
                break;
                
            case "0101"://ADD
                sumar(registro, numeroORegistro);
                Thread.sleep(TiempoInstrucciones.ADD*1000);
                break;
            case "0110"://INC  
                incrementar(registro);
                Thread.sleep(TiempoInstrucciones.INC * 1000);
                break;
            case "0111"://DEC
                decrementar(registro);
                Thread.sleep(TiempoInstrucciones.DEC * 1000);
                break;
            case "1000"://INT 20H
                
                break;
            case "1001"://JUMP [+/-Desplazamiento]
                int decimal = Integer.parseInt(numeroORegistro.substring(1, 8),2);
                if("1".equals(numeroORegistro.substring(0,1))){
                    decimal *= -1;           
                }              
                PC += decimal;
                Thread.sleep(TiempoInstrucciones.JUMP * 1000);
                break;
            case "1010"://CMP Val1,Val2
                compararValores(registro,numeroORegistro);
                Thread.sleep(TiempoInstrucciones.CMP * 1000);
                break;
            case "1011"://JE [ +/-Desplazamiento]
                if(bandera){
                    int decimal2 = Integer.parseInt(numeroORegistro.substring(1, 8),2);
                    if("1".equals(numeroORegistro.substring(0,1))){
                        decimal2 *= -1;           
                    }              
                    PC += decimal2;                   
                }
                Thread.sleep(TiempoInstrucciones.JEJNE * 1000);
                break;
            case "1100"://JNE [ +/-Desplazamiento]
                if(!bandera){
                    int decimal3 = Integer.parseInt(numeroORegistro.substring(1, 8),2);
                    if("1".equals(numeroORegistro.substring(0,1))){
                        decimal3 *= -1;           
                    }              
                    PC += decimal3;                   
                }
                Thread.sleep(TiempoInstrucciones.JEJNE * 1000);
                break;
            case "1101"://POP AX
                
                
                break;          
            default:             
                break;
        }listo=true;
    }
    public static int registroPosicion(String registro){       
        switch(registro) {
            case "0001"://AX                    
                 return 1;   
            case "0010"://BX
                return 2;                                  
            case "0011"://CX
                return 3;
            case "0100"://DX
               return 4;
            default:
                return 0;  
        }
    }
    
    
    
    public void movimiento(String registro, String numero){
        int numeroDecimal;
        if(numero.length() == 8){
            numeroDecimal = Integer.parseInt(numero.substring(1, 8),2);
        }else{
            //Es un registro entonces accedo a la dirección del registro para obtener el número
            numeroDecimal = registros[registroPosicion(numero.substring(5, 9))];       
        }
        if("1".equals(numero.substring(0,1))){
            numeroDecimal *= -1;           
        }
        registros[registroPosicion(registro)] = numeroDecimal;
        
    }
    
    public void sumar(String registro, String numero){     
        int numeroDecimal;
        if(numero.length() == 8){
            numeroDecimal = Integer.parseInt(numero.substring(1, 8),2);
        }else{
            //Es un registro entonces accedo a la dirección del registro para obtener el número
            numeroDecimal = registros[registroPosicion(numero.substring(5, 9))];       
        }
        if(numero.equals("00000000")){
            registros[0] += registros[registroPosicion(registro)];
        }else{            
            if("1".equals(numero.substring(0,1))){
                numeroDecimal *= -1;           
            }
            registros[registroPosicion(registro)] += numeroDecimal;
        }       
    }
    
    
    public void restar(String registro, String numero){
        int numeroDecimal;
        if(numero.length() == 8){
            numeroDecimal = Integer.parseInt(numero.substring(1, 8),2);
        }else{
            //Es un registro entonces accedo a la dirección del registro para obtener el número
            numeroDecimal = registros[registroPosicion(numero.substring(5, 9))];       
        }
        if(numero.equals("00000000")){
            registros[0] -= registros[registroPosicion(registro)];
        }else{            
            if("1".equals(numero.substring(0,1))){
                numeroDecimal *= -1;           
            }
            registros[registroPosicion(registro)] -= numeroDecimal;
        }
    }
    
    public void incrementar(String registro){
        if(registro.equals("0000")){
            registros[0] += 1;
        }else{
            registros[registroPosicion(registro)] += 1;
            
        }       
    }
    
    public void decrementar(String registro){
        if(registro.equals("0000")){
            registros[0] -= 1;
        }else{
            registros[registroPosicion(registro)] -= 1;
            
        }       
    }
    
    public void compararValores(String val1,String val2){
        int numeroDecimal1 = registros[(registroPosicion(val1))];      
        int numeroDecimal2 = registros[registroPosicion(val2.substring(5, 9))];
        bandera = numeroDecimal1 == numeroDecimal2;
    }
    
    
    public String decimalABinaro(int a) {
        boolean negativo = false;
        if(a<0){
            a *=-1;
            negativo = true;
        }       
        String temp = Integer.toBinaryString(a);
        
        while(temp.length() !=7){
            temp = "0"+temp;
        }
        if(negativo){temp="1"+temp;}else{temp="0"+temp;}
        return temp;
    }
    
    public int obtenerPC(){
        return PC;
    }
    
    public int obtenerIR(){
        return IR;
    }
    
    public int[] obtenerRegistros(){
        return registros;
    }
    
    public Boolean obtenerEstado(){
        return listo;
    }
    
    public void recibirProceso(BCP proceso) throws InterruptedException{
        if(procesoEjecutando==null){
            // Cargar proceso
            establecerContexto(proceso);
            //Operaciones("");
            ejecutar1=true;
        }else if(procesoEjecutando.obtenerNumeroProceso()==proceso.obtenerNumeroProceso()){
            //Operaciones("");
            ejecutar1=true;
        }else{
            cambioContexto(proceso);
            ejecutar1=true;
        }
    }
    
    private void guardarContexto(){
        procesoEjecutando.establecerRegistros(registros[1], registros[2], registros[3], registros[4], IR, registros[0],PC);
    }
    
    private void establecerContexto(BCP procesoEntrante){
        int[] registrosProceso=procesoEntrante.obtenerRegistros();
        registros[0]=registrosProceso[5];
        registros[1]=registrosProceso[0];
        registros[2]=registrosProceso[1];
        registros[3]=registrosProceso[2];
        registros[4]=registrosProceso[3];
        IR=registrosProceso[4];
        PC=registrosProceso[5];
        procesoEjecutando=procesoEntrante;
    }
    
    private void cambioContexto(BCP procesoEntrante){
        guardarContexto();
        establecerContexto(procesoEntrante);
    }
    
    
    /*public static void limpiarClase(){
        arregloRegistros = new int[]{0,0,0,0,0};
        memoria = new String[100];
        posicionMemoria=0;
        numeroInstrucciones=0;
        ejecutar = false;
    }*/
}