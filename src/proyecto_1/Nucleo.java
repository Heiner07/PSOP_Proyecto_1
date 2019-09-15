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
    private Boolean listo = true; // Indica si está listo para ejecutar otra instrucción.
    private Boolean ejecutar1 = false;
    private Timer timerOperacion;
    
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
                break;
            case "0111"://DEC
                break;
            case "1000"://INT 20H
                break;
            case "1001"://JUM [+/-Desplazamiento]
                break;
            case "1010"://CMP Val1,Val2
                break;
            case "1011"://JE [ +/-Desplazamiento]
                break;
            case "1100"://JNE [ +/-Desplazamiento]
                break;
            case "1101"://POP AX
                break;          
            default:             
                break;
        }listo=true;
    }
     
    public void muestraContenido(String archivo) throws FileNotFoundException, IOException {
        String cadena;
        String cadena2 ="";
        
        FileReader f = new FileReader(archivo);
        try (BufferedReader b = new BufferedReader(f)) {
            while((cadena = b.readLine())!=null) {
                cadena2 += cadena +"\n";               
            }
        }
        String[] cadenaTemp=cadena2.split("\n");
        numeroInstrucciones=cadenaTemp.length;
        PC=(int) (Math.random() * 80);
        //System.out.println("Posicion de memoria: "+posicionMemoria);
        int posicionMemoriaTemp=PC;
        for(int i=0; i<numeroInstrucciones && posicionMemoriaTemp<memoria.length; posicionMemoriaTemp++,i++){
            memoria[posicionMemoriaTemp]=cadenaTemp[i];
        }
        memoriaToBits();
    }
    
    public void memoriaToBits(){
        String[] parteOperacion,parteResto;
        String instruccionEnbits;
        int largo=PC+numeroInstrucciones;
        for(int i=PC;i<largo;i++){
            parteOperacion = memoria[i].split(" ");
            instruccionEnbits = parteOperacion[0];
            if("INC".equals(instruccionEnbits) || "DEC".equals(instruccionEnbits)){                
                 if(parteOperacion.length == 2){
                     
                     memoria[i] = toBinario(instruccionEnbits)+" "+toBinario(parteOperacion[1])+" 00000000";
                 
                 }else{
                    memoria[i] = toBinario(instruccionEnbits)+" "+"0000"+" 00000000";                  
                 }
                                 
            }else{
                parteResto = parteOperacion[1].split(",");
                if(parteResto.length >=2){
                    try{
                        memoria[i] = toBinario(instruccionEnbits)+" "+toBinario(parteResto[0])+" "+decimalABinaro(Integer.parseInt(parteResto[1]));                   
                    }catch(Exception e){
                        memoria[i] = toBinario(instruccionEnbits)+" "+toBinario(parteResto[0])+" 00000"+toBinario(parteResto[1]);    
                    }

                }else{
                    //Verifico si es jum, je, jne
                    if("JUM".equals(instruccionEnbits) || "JE".equals(instruccionEnbits) || "JNE".equals(instruccionEnbits)){
                        memoria[i] = toBinario(instruccionEnbits)+ " 0000 " +decimalABinaro(Integer.parseInt(parteResto[0]));
                    
                    }else{
                        memoria[i] = toBinario(instruccionEnbits)+" "+toBinario(parteResto[0])+" 00000000";                  
                    }
                    
                }//SALE ELSE DENTRO              
            }//SALE ELSE
            System.out.println(memoria[i]);
        }//FOR
    }//FUNCION
    
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
    
    public String toBinario(String registro){      
        switch(registro) {
            case "AX"://AX                    
                 return "0001";
            case "BX"://BX
                return "0010";                                  
            case "CX"://CX
                return "0011";
            case "DX"://DX
               return "0100";
               
               
            case "LOAD"://LOAD
               return "0001";
            case "STORE"://STORE
               return "0010";
            case "MOV"://MOV
               return "0011";
            case "SUB"://SUB
               return "0100";                
            case "ADD"://ADD
               return "0101"; 
            case "INC"://INC    
                return "0110";
            case "DEC"://DEC
                return "0111";
            case "INT"://INT
                return "1000";
            case "JUM"://JUM [+/-Desplazamiento]
                return "1001";
            case "CMP"://CMP Val1,Val2
                return "1010";
            case "JE"://JE [ +/-Desplazamiento]
                return "1011";
            case "JNE"://JNE [ +/-Desplazamiento]
                return "1100";
            case "POP"://POP AX
                return "1101";                
            case "20H":
                return "0101";
            case "16H":
                return "0110";
            case "05H":
                return "0111";               
            default:
                return "0000";
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