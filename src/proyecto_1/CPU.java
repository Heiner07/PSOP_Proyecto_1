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
    private Nucleo nucleo1, nucleo2;
    private List<Trabajo> colaTrabajoN1, colaTrabajoN2;
    private List<BCP> procesos;
    private int idProceso;
    
    public CPU(){
        this.nucleo1 = new Nucleo();
        this.nucleo2 = new Nucleo();
        this.colaTrabajoN1 = new ArrayList<>();
        this.colaTrabajoN2 = new ArrayList<>();
        this.procesos = new ArrayList<>();
        this.idProceso = 0;
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
    
    public List<String> cargarProgramas(List<String> archivos){
        int cantidadArchivos=archivos.size();
        List<String> erroresLectura = new ArrayList<>(); // Almacena los archivos donde ocurrió un error;
        List<String> instrucciones;
        for(int i=0;i<cantidadArchivos;i++){
            try {
                instrucciones=obtenerIntruccionesArchivo(archivos.get(i));
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
        int[] finInicioMemoria=determinarPosicionesMemoria(numeroInstrucciones);
        int estadoProceso;
        if(finInicioMemoria[0]==-1){
            // Si no hay espacio, entonces no se cargan las instrucciones en memoria.
            estadoProceso=BCP.EN_ESPERA;
        }else{
            // Si hay espacio, entonces sí se cargar las instrucciones en memoria.
            // Funcion cargarInstrucciones en memoria (Pendiente).
            estadoProceso=BCP.NUEVO;
        }
        BCP proceso=new BCP(estadoProceso, idProceso++, 0, finInicioMemoria[0], finInicioMemoria[1]);
        procesos.add(proceso);
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
