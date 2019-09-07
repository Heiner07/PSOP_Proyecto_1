/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author home
 */
public class Operaciones {
    
    //[0] = AC
    //[1] = AX
    //[2] = BX
    //[3] = CX
    //[4] = DX
    static int[] arregloRegistros = {0,0,0,0,0};
    static String[] memoria = new String[100];
    static int posicionMemoria=0;
    static int numeroInstrucciones=0;
    static boolean ejecutar = false;
    
    public void Operaciones(String instrucciones){
        String[] parts;
        parts = instrucciones.split(" ");
        String operacion = parts[0];
        String registro = parts[1];
        String numero = parts[2];        
        switch(operacion) {
            case "0001"://LOAD
                arregloRegistros[0] = arregloRegistros[registroPosicion(registro)];              
                break;    
                
            case "0010"://STORE
                arregloRegistros[registroPosicion(registro)] = arregloRegistros[0];
                break;
                
            case "0011"://MOV
               movimiento(registro,numero);
                break; 
                
            case "0100"://SUB
                restar(registro,numero);
                break;
                
            case "0101"://ADD
                sumar(registro, numero);
                break;

            default:             
                break;
        }
    }
     
    public static void muestraContenido(String archivo) throws FileNotFoundException, IOException {
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
        posicionMemoria=(int) (Math.random() * 80);
        //System.out.println("Posicion de memoria: "+posicionMemoria);
        int posicionMemoriaTemp=posicionMemoria;
        for(int i=0; i<numeroInstrucciones && posicionMemoriaTemp<memoria.length; posicionMemoriaTemp++,i++){
            memoria[posicionMemoriaTemp]=cadenaTemp[i];
        }
        memoriaToBits();
    }
    
    public static void memoriaToBits(){
        String[] parteOperacion,parteResto;
        String instruccionEnbits;
        int largo=posicionMemoria+numeroInstrucciones;
        for(int i=posicionMemoria;i<largo;i++){
            parteOperacion = memoria[i].split(" ");
            instruccionEnbits = parteOperacion[0];
            parteResto = parteOperacion[1].split(",");
            if(parteResto.length >=2){
                memoria[i] = toBinario(instruccionEnbits)+" "+toBinario(parteResto[0])+" "+decimalABinaro(Integer.parseInt(parteResto[1]));              
            }else{
               memoria[i] = toBinario(instruccionEnbits)+" "+toBinario(parteResto[0])+" 00000000";
            }
            //System.out.println(memoria[i]);
        }
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
    
    public static String toBinario(String registro){      
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
            default:
                return "0000";
        }    
    }
    
    
    public void movimiento(String registro, String numero){
        
        int numeroDecimal = Integer.parseInt(numero.substring(1, 8),2);
        if("1".equals(numero.substring(0,1))){
            numeroDecimal *= -1;           
        }
        arregloRegistros[registroPosicion(registro)] = numeroDecimal;
        
    }
    
    public void sumar(String registro, String numero){
        int numeroDecimal = Integer.parseInt(numero.substring(1, 8),2);
        if(numero.equals("00000000")){
            arregloRegistros[0] += arregloRegistros[registroPosicion(registro)];
        }else{            
            if("1".equals(numero.substring(0,1))){
                numeroDecimal *= -1;           
            }
            arregloRegistros[registroPosicion(registro)] += numeroDecimal;
        }       
    }
    
    
    public void restar(String registro, String numero){
        int numeroDecimal = Integer.parseInt(numero.substring(1, 8),2);
        if(numero.equals("00000000")){
            arregloRegistros[0] -= arregloRegistros[registroPosicion(registro)];
        }else{            
            if("1".equals(numero.substring(0,1))){
                numeroDecimal *= -1;           
            }
            arregloRegistros[registroPosicion(registro)] -= numeroDecimal;
        }
    }
    
    
    public static String decimalABinaro(int a) {
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
    
    public static void limpiarClase(){
        arregloRegistros = new int[]{0,0,0,0,0};
        memoria = new String[100];
        posicionMemoria=0;
        numeroInstrucciones=0;
        ejecutar = false;
    }
}