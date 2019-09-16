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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;


/**
 *
 * @author Heiner
 */
public class CPU {
    
    static final int LARGOMEMORIA = 128;
    static final int LARGODISCO = 1024;
    static String[] memoria = new String[LARGOMEMORIA];
    static String[] disco = new String[LARGODISCO];
    private Nucleo nucleo1, nucleo2;
    private List<Trabajo> colaTrabajoN1, colaTrabajoN2;
    private List<BCP> procesos;
    private int idProceso;
    private String[] parametros;
    private boolean tieneParametros = false;
    /* Hilos de Control */
    private Timer timerControlColasNucleos;
    
    public CPU(){
        this.nucleo1 = new Nucleo();
        this.nucleo2 = new Nucleo();
        this.colaTrabajoN1 = new ArrayList<>();
        this.colaTrabajoN2 = new ArrayList<>();
        this.procesos = new ArrayList<>();
        this.idProceso = 0;
        inicializaMemoria();
        configuararHilos();
    }
    
    private void inicializaMemoria(){
        for(int i=0;i<CPU.LARGOMEMORIA;i++){
            CPU.memoria[i] = "0000 0000 00000000";
        }
    }
    
    /**
     * Se encarga de llamar a las distintas funciones que controlan la ejecucion de la instrucciones.
     */
    private void configuararHilos(){
        configurarHiloColasNucleos();
    }
    
    /**
     * Establece la funcion para el timer timerControlColasNucleos que se encargará de enviar...
     * ...las instrucciones al procesador correspondiente.
     */
    private void configurarHiloColasNucleos(){
        timerControlColasNucleos = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    // Función que repetirá según el intervalo asignado (1 segundo).
                    verificarColas();
                } catch (InterruptedException ex) {
                    // Modificar para mostrar mensaje correspondiente.
                    Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        // Inicializo el timer.
        timerControlColasNucleos.start();
    }
    
    private void verificarColas() throws InterruptedException{
        if(nucleo1.obtenerEstado()){
            if(colaTrabajoN1.size()>0){
                nucleo1.recibirProceso(obtenerBCP(colaTrabajoN1.remove(0).numeroBCP));
            }
        }
        if(nucleo2.obtenerEstado()){
            if(colaTrabajoN2.size()>0){
                nucleo2.recibirProceso(obtenerBCP(colaTrabajoN2.remove(0).numeroBCP));
            }
        }
    }
    
    public String[] obtenerParametros(){
        return parametros;
    
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
    
    private BCP obtenerBCP(int numeroBCP){
        int numeroProcesos = procesos.size();
        BCP proceso;
        for(int i=0;i<numeroProcesos;i++){
            proceso=procesos.get(i);
            if(proceso.obtenerNumeroProceso()==numeroBCP){
                return proceso;
            }
        }return null;
    }
    
    public List<String> cargarProgramas(List<String> archivos){
        int cantidadArchivos=archivos.size();
        List<String> erroresLectura = new ArrayList<>(); // Almacena los archivos donde ocurrió un error;
        List<String> instrucciones;
        String[] obtenerParametros;
        for(int i=0;i<cantidadArchivos;i++){
            try {
                instrucciones=obtenerIntruccionesArchivo(archivos.get(i));
                
                obtenerParametros = instrucciones.get(i).split(" ");
                if(obtenerParametros[0].equals("PARAM")){              
                    parametros = obtenerParametros[1].split(",");  
                    tieneParametros = true;
                    instrucciones.remove(i);
                }
                System.out.println(i + " -> "+Arrays.toString(parametros));
                crearProceso(instrucciones);
            } catch (IOException ex) {
                erroresLectura.add(archivos.get(i));
            }
        }return erroresLectura;
    }
    
    /**
     * Crea un proceso para la lista de instrucciones indicadas.
     * Si hay espacio para crear un bloque se agregan a memoria, sino se pone a la espera.
     * @param instrucciones 
     */
    public void crearProceso(List<String> instrucciones){
        int numeroInstrucciones=instrucciones.size();
        //if(tieneParametros)numeroInstrucciones-=1;
        
        int[] finInicioMemoria=determinarPosicionesMemoria(numeroInstrucciones);
        int estadoProceso;
        int nucleo = (int) (Math.random() * 2); // Se determina el núcleo donde se ejecutará el proceso.
        if(finInicioMemoria[0]==-1){
            // Si no hay espacio, entonces no se cargan las instrucciones en memoria.
            estadoProceso=BCP.EN_ESPERA;
        }else{
            // Si hay espacio, entonces sí se cargar las instrucciones en memoria.
            cargarInstrucciones(finInicioMemoria[0],finInicioMemoria[1],instrucciones);
            estadoProceso=BCP.PREPARADO;
            agregarProcesoCola(nucleo, numeroInstrucciones);
        }
        BCP proceso=new BCP(estadoProceso, idProceso++, 0, finInicioMemoria[0], finInicioMemoria[1], nucleo);
        procesos.add(proceso);
    }
    
    private void cargarInstrucciones(int inicioMemoria, int finMemoria, List<String> instrucciones){
        String[] parteOperacion,parteResto;
        String instruccionEnbits;


        // Utilizo inicio de memoria como mi indice para moverme en la memoria, por eso lo aumento

        for(int i=0; inicioMemoria<=finMemoria; inicioMemoria++,i++){
            parteOperacion = instrucciones.get(i).split(" ");
            instruccionEnbits = parteOperacion[0];
            
            if("INC".equals(instruccionEnbits) || "DEC".equals(instruccionEnbits)){                
                 if(parteOperacion.length == 2){ 

                    CPU.memoria[i] = toBinario(instruccionEnbits)+" "+toBinario(parteOperacion[1])+" 00000000";
                    

                    CPU.memoria[inicioMemoria] = toBinario(instruccionEnbits)+" "+toBinario(parteOperacion[1])+" 00000000";

                 }else{
                    CPU.memoria[inicioMemoria] = toBinario(instruccionEnbits)+" "+"0000"+" 00000000";
                 }
                                 
            }else{
                parteResto = parteOperacion[1].split(",");
                if(parteResto.length >=2){
                    try{
                        CPU.memoria[i] = toBinario(instruccionEnbits)+" "+toBinario(parteResto[0])+" "+decimalABinaro(Integer.parseInt(parteResto[1]));  
                       
                        CPU.memoria[inicioMemoria] = toBinario(instruccionEnbits)+" "+toBinario(parteResto[0])+" "+decimalABinaro(Integer.parseInt(parteResto[1]));                   
                    }catch(Exception e){
                        CPU.memoria[i] = toBinario(instruccionEnbits)+" "+toBinario(parteResto[0])+" 00000"+toBinario(parteResto[1]);
                       
                        CPU.memoria[inicioMemoria] = toBinario(instruccionEnbits)+" "+toBinario(parteResto[0])+" 00000"+toBinario(parteResto[1]);    
                    }

                }else{
                    //Verifico si es jum, je, jne
                    if("JUM".equals(instruccionEnbits) || "JE".equals(instruccionEnbits) || "JNE".equals(instruccionEnbits)){
                        CPU.memoria[i] = toBinario(instruccionEnbits)+ " 0000 " +decimalABinaro(Integer.parseInt(parteResto[0]));
                        CPU.memoria[inicioMemoria] = toBinario(instruccionEnbits)+ " 0000 " +decimalABinaro(Integer.parseInt(parteResto[0]));
                    
                    }else{
                        CPU.memoria[i] = toBinario(instruccionEnbits)+" "+toBinario(parteResto[0])+" 00000000";     

                        CPU.memoria[inicioMemoria] = toBinario(instruccionEnbits)+" "+toBinario(parteResto[0])+" 00000000";                  
                    }
                    
                }//SALE ELSE DENTRO              
            }
        }
    }
    
    private String toBinario(String registro){      
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
    
    private String decimalABinaro(int a) {
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
    
    private void agregarProcesoCola(int nucleo, int numeroInstrucciones){
        Trabajo trabajo;
        if(nucleo==0){
            for(int i=0;i<numeroInstrucciones;i++){
                trabajo=new Trabajo(0, idProceso);
                colaTrabajoN1.add(trabajo);
            }
        }else{
            for(int i=0;i<numeroInstrucciones;i++){
                trabajo=new Trabajo(1, idProceso);
                colaTrabajoN2.add(trabajo);
            }
        }
    }
    
    /**
     * Determina si hay espacio suficiente según la memoria indicada (memoriaRequerida). Se utiliza...
     * ...para determinar las posiciones de memoria que tendra el bloque (proceso).
     * Se retorna un arreglo de enteros de dos elementos. La posicion 0 es el inicio de memoria...
     * ... y la posicion 1 es el fin de memoria.
     * Si no hay espacio para el bloque, se retorna -1 en ambas posiciones [-1, -1].
     * @param memoriaRequerida
     * @return int[]
     */
    // Falta considerar que puede haber un solo bloque, pero no empieza en la posicion cero.
    @SuppressWarnings("empty-statement")
    public int[] determinarPosicionesMemoria(int memoriaRequerida){
        int numeroProcesos = procesos.size();
        int inicioMemoria = -1, finMemoria = -1;
        int finMemoriaTemp = -1; // Almacena el fin de memoria del proceso anterior
        Boolean hayEspacio = false;
        BCP proceso;
        /* Recorro todos los procesos, bajo el supuesto de que están ordenados de acuerdo a la memoria.
        *  Entiendase por ordenado como: el inicio de memoria del bloque siguiente es el más cercano...
        *  ...con respecto al fin de memoria del bloque anterior.
        */
        for(int i=0;i<numeroProcesos;i++){
            proceso=procesos.get(i);
            if(finMemoria!=-1){
                if(proceso.obtenerFinMemoria()-finMemoriaTemp>=memoriaRequerida+1){
                    inicioMemoria=finMemoriaTemp+1;
                    finMemoria=proceso.obtenerFinMemoria()-1;
                   
                    hayEspacio=true;
                    break;
                }
            }finMemoriaTemp=proceso.obtenerFinMemoria();
        }
        
        if(hayEspacio){
            // Si hay espacio, retorno las posiciones encontradas.
            return new int[]{inicioMemoria, finMemoria};
        }else{
            // Si no hay espacio según lo anterior, pueden pasar tres cosas:
            if(numeroProcesos==0 && memoriaRequerida<CPU.LARGOMEMORIA){
                // No hay bloques y el programa cabe en memoria
                return new int[]{0, memoriaRequerida-1};
            }else if(CPU.LARGOMEMORIA-finMemoriaTemp>=memoriaRequerida+1){
                // Hay espacio después del ultimo bloque.
                return new int[]{finMemoriaTemp+1, finMemoriaTemp+memoriaRequerida};
            }else{
                // No hay espacio para el bloque
                return new int[]{ -1,-1};
            }
        }
    }
    
    /**
     * Obtiene las instrucciones (líneas) del archivo indicado. Las restorna en una lista de string.
     * @param archivo
     * @return List<String>
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public List<String> obtenerIntruccionesArchivo(String archivo) throws FileNotFoundException, IOException{
        String cadena;
        List<String> instrucciones = new ArrayList<>();
        
        try (FileReader f = new FileReader(archivo)) {
            BufferedReader b = new BufferedReader(f);
            while((cadena = b.readLine())!=null) {
                instrucciones.add(cadena);
            }
        }
        
        return instrucciones;
    }
}
