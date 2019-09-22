/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_1;

/**
 *
 * @author Heiner
 */
public class Interrupcion {
    
    static final int FINALIZAR_PROGRAMA = 0;
    static final int IMPRIMIR = 1;
    static final int ENTRADA_TECLADO = 2;
    static final int ERROR_PARAMETROS = 3;
    static final int ERROR_PILA = 4;
    private int numeroNucleo;
    private int numeroInterrupcion;
    private int valor;
    private Boolean listo;

    public Interrupcion(int numeroNucleo, int numeroInterrupcion) {
        this.numeroNucleo = numeroNucleo;
        this.numeroInterrupcion = numeroInterrupcion;
        this.listo = false;
    }
    
    public Interrupcion(int numeroNucleo, int numeroInterrupcion, int valor) {
        this.numeroNucleo = numeroNucleo;
        this.numeroInterrupcion = numeroInterrupcion;
        this.valor = valor;
        this.listo = false;
    }

    public int obtenerNumeroNucleo() {
        return numeroNucleo;
    }

    public int obtenerNumeroInterrupcion() {
        return numeroInterrupcion;
    }
    
    public int obtenerValor(){
        return valor;
    }
    
    public void establecerValor(int valor){
        this.valor = valor;
    }
    
    public Boolean obtenerEstado(){
        return listo;
    }
    
    public void establecerComoEjecutada(){
        listo = true;
    }
}
