/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private int PC=0, IR=0;
    private BCP procesoEjecutando=null;
    private Boolean ejecutar = false;
    private Timer timerOperacion;
    private int tiempoRestante=0; // Variable que indicara cuantos segendos debe esperar hasta recibir otra instrucción
    
    public Nucleo(){
        timerOperacion = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                controlEjecucionNucleo();
            }
        });
        // Inicializo el timer.
        timerOperacion.start();
    }
    
    /**
     * Función llamada por el timerOperación que controla la ejecución de las operaciones en el núcleo.
     * Si el tiempo es cero entonces verifica que hay una instrucción a ejecutar y la realiza, sino resta el tiempo...
     * ...requerido de la operación anterior.
     */
    private void controlEjecucionNucleo(){
        try {
            if(tiempoRestante==0){
                if(ejecutar){
                    ejecutar=false;
                    Operaciones();
                }
            }else{
                tiempoRestante--;
            }
        } catch (InterruptedException ex) {
            // Modificar para mostrar mensaje correspondiente
            Logger.getLogger(Nucleo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void Operaciones() throws InterruptedException{
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
                tiempoRestante=TiempoInstrucciones.LOAD;
                break;    
                
            case "0010"://STORE
                registros[registroPosicion(registro)] = registros[0];
                tiempoRestante=TiempoInstrucciones.STORE;
                break;
                
            case "0011"://MOV
               movimiento(registro,numeroORegistro);
               tiempoRestante=TiempoInstrucciones.MOV;
               break; 
                
            case "0100"://SUB
                restar(registro,numeroORegistro);
                tiempoRestante=TiempoInstrucciones.SUB;
                break;
                
            case "0101"://ADD
                sumar(registro, numeroORegistro);
                tiempoRestante=TiempoInstrucciones.ADD;
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
        }
        guardarContexto(); // Guarda el estado en el proceso. (Para que sea reflejado en la interfaz).
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
        return (tiempoRestante==0);// Si es igual a cero entonces está listo.
    }
    
    /**
     * Recibe el proceso de la cola de trabajo. Y lo pone a ejecutar.
     * @param proceso
     * @throws InterruptedException 
     */
    public void recibirProceso(BCP proceso) throws InterruptedException{
        if(procesoEjecutando==null){
            // Si no hay proceso asignado, entonces solo se establece el entrante.
            establecerContexto(proceso);
            ejecutar=true;
        }else if(procesoEjecutando.obtenerNumeroProceso()==proceso.obtenerNumeroProceso()){
            // Si es el mismo proceso, solo indico que ejecute la siguiente instrucción
            ejecutar=true;
        }else{
            // Si es otro proceso diferente, entonces ejecuto un cambio de contexto.
            cambioContexto(proceso);
            ejecutar=true;
        }
    }
    
    /**
     * Guarda el contexto que tiene el núcleo en el procesos que está ejecutando.
     * Esta función se llama para el cambio de contexto y cada vez que termina Operaciones (Para reflejar los cambios en la interfaz)
     */
    private void guardarContexto(){
        if(PC>procesoEjecutando.obtenerFinMemoria()){
            // Si el pc supera al fin de memoria, entonces se llegó a la última instrucción
            procesoEjecutando.establecerEstado(BCP.TERMINADO);
        }// Esto comentado creo solo sería si se implementa multiples procesos al mismo tiempo para un núcleo
        /*else{
            // Si no es el último, entonces se establece en preparado
            procesoEjecutando.establecerEstado(BCP.PREPARADO);
        }*/
        procesoEjecutando.establecerRegistros(registros[1], registros[2], registros[3], registros[4], IR, registros[0],PC);
    }
    
    /**
     * Establece el contexto, del proceso entrante, en el núcleo.
     * Esta función se llama para el cambio de contexto.
     * @param procesoEntrante 
     */
    private void establecerContexto(BCP procesoEntrante){
        int[] registrosProceso=procesoEntrante.obtenerRegistros();
        registros[0]=registrosProceso[5];
        registros[1]=registrosProceso[0];
        registros[2]=registrosProceso[1];
        registros[3]=registrosProceso[2];
        registros[4]=registrosProceso[3];
        IR=registrosProceso[4];
        PC=registrosProceso[6];
        procesoEjecutando=procesoEntrante;
        procesoEjecutando.establecerEstado(BCP.EN_EJECUCION);
    }
    
    /**
     * Guarda el contexto (información de la ejecución) del procesos actual(ejeuctandose) y carga...
     * ...el contexto del proceso entrante para realizar las operaciones.
     * @param procesoEntrante 
     */
    private void cambioContexto(BCP procesoEntrante){
        guardarContexto();
        establecerContexto(procesoEntrante);
    }
    
}