/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;


public class JFVentanaPrincipal extends javax.swing.JFrame {
    
    int paso;
    int posicionMemoria;
    String rutaArchivo;
    Boolean archivoCargado;
    Interrupcion interrupcion;
    DefaultTableModel modeloTablaArchivos, modeloTablaMemoria, modeloTablaDisco;
    DefaultTableModel modeloTablaColaN1, modeloTablaColaN2;
    List<String> archivos;
    List<JPBCP> BCPs; // Lista de control de BCPs
    CPU cpu;
    
    /* Hilos de control */
    Timer timerControlBCPs, timerControlNucleos, timerControlColaNucleos;
    Timer timerControlMemoria, timerControlDisco, timerControlInterrupciones;
    
    /**
     * Creates new form JFVentanaPrincipal
     */
    public JFVentanaPrincipal() {
        initComponents();
        configurarColores();
        establecerValores();
        configurarTablaMemoria();
        configurarTablaDisco();
        this.modeloTablaColaN1 = (DefaultTableModel) jtColaN1.getModel();
        this.modeloTablaColaN2 = (DefaultTableModel) jtColaN2.getModel();
        this.modeloTablaArchivos = (DefaultTableModel) jtArchivos.getModel();
        this.archivos=new ArrayList<>();
        this.BCPs=new ArrayList<>();
        this.cpu=new CPU();
        this.interrupcion=null;
        this.archivoCargado=false;
        configuararHilos();
        this.setLocationRelativeTo(null);
    }
    
    private void configurarColores(){
        // Configurando colores del núcleo 1
        jpNucleo1.setBackground(Colores.nucleo1);
        lbNucleo1Titulo.setForeground(Colores.fuenteNucleo1);
        lbPCN1.setForeground(Colores.fuenteNucleo1);
        lbPCN1Titulo.setForeground(Colores.fuenteNucleo1);
        lbAXN1.setForeground(Colores.fuenteNucleo1);
        lbAXN1Titulo.setForeground(Colores.fuenteNucleo1);
        lbBXN1.setForeground(Colores.fuenteNucleo1);
        lbBXN1Titulo.setForeground(Colores.fuenteNucleo1);
        lbCXN1.setForeground(Colores.fuenteNucleo1);
        lbCXN1Titulo.setForeground(Colores.fuenteNucleo1);
        lbDXN1.setForeground(Colores.fuenteNucleo1);
        lbDXN1Titulo.setForeground(Colores.fuenteNucleo1);
        lbACN1.setForeground(Colores.fuenteNucleo1);
        lbACN1Titulo.setForeground(Colores.fuenteNucleo1);
        lbIRN1.setForeground(Colores.fuenteNucleo1);
        lbIRN1Titulo.setForeground(Colores.fuenteNucleo1);
        
        //Configurando colores del núcleo 2
        jpNucleo2.setBackground(Colores.nucleo2);
    }
    
    private void configurarTablaMemoria(){
        this.modeloTablaMemoria = (DefaultTableModel) jtMemoria.getModel();
        for(int i=0;i<CPU.LARGOMEMORIA;i++){
            modeloTablaMemoria.addRow(new Object[]{i,"0000","0000","00000000"});
        }
    }
    
    private void configurarTablaDisco(){
        this.modeloTablaDisco = (DefaultTableModel) jtDisco.getModel();
        for(int i=0;i<CPU.LARGODISCO;i++){
            modeloTablaDisco.addRow(new Object[]{i,"0000","0000","00000000"});
        }
    }
    
    private void establecerValores(){
        this.paso=1;
        this.posicionMemoria=0;
    }
    
    /**
     * Se encarga de llamar a las distintas funciones que inicializan los hilos que actualizan...
     * ...los valores en la interfaz gráfica.
     */
    private void configuararHilos(){
        configurarHiloBCPs();
        configurarHiloNucleos();
        configurarHiloColaNucleos();
        configurarHiloMemoria();
        configurarHiloDisco();
        configurarHiloInterrupciones();
    }
    
    /**
     * Establece la función para el timer timerControlInterrupciones que se encargará de reflejar...
     * ...la ejecución de la interrupción en la interfaz.
     */
    private void configurarHiloInterrupciones(){
        timerControlInterrupciones = new Timer(1000, (ActionEvent ae) -> {
            // Función que repetirá segun el intervalo asignado (1 segundo).
            controlGraficoInterrupciones();
        });
        // Inicializo el timer.
        timerControlInterrupciones.start();
    }
    
    /**
     * Se encarga de mostrar las interrupciones del sistema (en consola y pantalla).
     * Este método es invocado por el timer timerControlInterrupciones.
     */
    private void controlGraficoInterrupciones(){
        if(interrupcion==null){
            if(cpu.obtenerInterrupcion()!=null && !cpu.obtenerInterrupcion().obtenerEstado()){
                interrupcion=cpu.obtenerInterrupcion();
                int numeroInterrupcion = interrupcion.obtenerNumeroInterrupcion();
                switch (numeroInterrupcion) {
                    case Interrupcion.FINALIZAR_PROGRAMA:
                        taPantalla.setText(taPantalla.getText()+"Presione ENTER para finalizar el proceso...");
                        break;
                    case Interrupcion.IMPRIMIR:
                        taPantalla.setText(taPantalla.getText()+interrupcion.obtenerValor()+"\nPresione ENTER para continuar...");
                        break;
                    case Interrupcion.ENTRADA_TECLADO:
                        taPantalla.setText(taPantalla.getText()+"Ingrese el valor: ");
                        break;
                    case Interrupcion.ERROR_PARAMETROS:
                        taPantalla.setText(taPantalla.getText()+"ERROR DE PARÁMETROS, más parámetros...\n...de los permitidos en pila.\n");
                        interrupcion.establecerComoEjecutada();
                        interrupcion=null;
                        return;// Para que no active el tfConsola;
                    case Interrupcion.ERROR_PILA:
                        taPantalla.setText(taPantalla.getText()+"ERROR DE PILA, fuera de rango.\n");
                        interrupcion.establecerComoEjecutada();
                        interrupcion=null;
                        return;// Para que no active el tfConsola;
                    default:
                        break;
                }
                tfConsola.setEnabled(true);
                tfConsola.requestFocus();
            }
        }
    }
    
    /**
     * Establece la función para el timer timerControlDisco que se encargará de actualizar...
     * ...los valores del disco en la interfaz gráfica.
     */
    private void configurarHiloDisco(){
        timerControlDisco = new Timer(1000, (ActionEvent ae) -> {
            // Función que repetirá segun el intervalo asignado (1 segundo).
            controlGraficoDisco();
        });
        // Inicializo el timer.
        timerControlDisco.start();
    }
    
    /**
     * Controla los valores del disco que se muestran en la interfaz gráfica.
     * Carga los valores del disco y actualiza los valores en la interfaz gráfica.
     * Este método es invocado por el timer timerControlDisco.
     */
    private void controlGraficoDisco(){
        String[] instruccion;
        for(int i=0;i<CPU.LARGODISCO;i++){
            instruccion = CPU.disco[i].split(" ");
            modeloTablaDisco.setValueAt(instruccion[0], i, 1);
            modeloTablaDisco.setValueAt(instruccion[1], i, 2);
            modeloTablaDisco.setValueAt(instruccion[2], i, 3);
        }
    }
    
    /**
     * Establece la función para el timer timerControlMemoria que se encargará de actualizar...
     * ...los valores de la memoria en la interfaz gráfica.
     */
    private void configurarHiloMemoria(){
        timerControlMemoria = new Timer(1000, (ActionEvent ae) -> {
            // Función que repetirá segun el intervalo asignado (1 segundo).
            controlGraficoMemoria();
        });
        // Inicializo el timer.
        timerControlMemoria.start();
    }
    
    /**
     * Controla los valores de la memoria que se muestran en la interfaz gráfica.
     * Carga los valores de memoria y actualiza los valores en la interfaz gráfica.
     * Este método es invocado por el timer timerControlMemoria.
     */
    private void controlGraficoMemoria(){
        String[] instruccion;
        for(int i=0;i<CPU.LARGOMEMORIA;i++){
            instruccion = CPU.memoriaVirtual[i].split(" ");
            modeloTablaMemoria.setValueAt(instruccion[0], i, 1);
            modeloTablaMemoria.setValueAt(instruccion[1], i, 2);
            modeloTablaMemoria.setValueAt(instruccion[2], i, 3);
        }
    }
    
    /**
     * Establece la función para el timer timerControlColaNucleos que se encargará de actualizar...
     * ...los valores en la interfaz gráfica.
     */
    private void configurarHiloColaNucleos(){
        timerControlColaNucleos = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // Función que repetirá segun el intervalo asignado (1 segundo).
                controlGraficoColaNucleos();
            }
        });
        // Inicializo el timer.
        timerControlColaNucleos.start();
    }
    
    /**
     * Controla las colas de los Núcleos que se muestran en la interfaz gráfica.
     * Carga las Colas de los Núcleos del cpu, actualiza los valores en la interfaz gráfica.
     * Este método es invocado por el timer timerControlColaNucleos.
     */
    private void controlGraficoColaNucleos(){
        // Obtengo todos los procesos del CPU
        /*List<Trabajo> colaN1 = cpu.obtenerColaTrabajoN1();
        List<Trabajo> colaN2 = cpu.obtenerColaTrabajoN2();
        int numeroProcesosColaN1 = colaN1.size();
        int numeroProcesosColaN2 = colaN2.size();
        limpiarColas();
        for(int i=0;i<numeroProcesosColaN1;i++){
            modeloTablaColaN1.addRow(new Object[]{"BCP "+colaN1.get(i).obtenerNumeroBCP()});
        }
        for(int i=0;i<numeroProcesosColaN2;i++){
            modeloTablaColaN2.addRow(new Object[]{"BCP "+colaN2.get(i).obtenerNumeroBCP()});
        }*/
        
        List<Trabajo> colaN1 = CPU.colaImprimir1;
        List<Trabajo> colaN2 = CPU.colaImprimir2;
        
        int numeroProcesosColaN1 = colaN1.size();
        int numeroProcesosColaN2 = colaN2.size();
        limpiarColas();
        for(int i=0;i<numeroProcesosColaN1;i++){
            modeloTablaColaN1.addRow(new Object[]{"BCP"+colaN1.get(i).obtenerNumeroBCP()+"   "+colaN1.get(i).obtenerInstruccion()});
        }
        for(int i=0;i<numeroProcesosColaN2;i++){
            modeloTablaColaN2.addRow(new Object[]{"BCP"+colaN2.get(i).obtenerNumeroBCP()+"   "+colaN2.get(i).obtenerInstruccion()});
            
        }
        
       
    }
    
    
    private void limpiarColas(){
        int numeroFilasColaN1=modeloTablaColaN1.getRowCount();
        int numeroFilasColaN2=modeloTablaColaN2.getRowCount();
        for(int i=numeroFilasColaN1-1;0<=i;i--){
            modeloTablaColaN1.removeRow(i);
        }
        for(int i=numeroFilasColaN2-1;0<=i;i--){
            modeloTablaColaN2.removeRow(i);
        }
    }
    
    /**
     * Establece la función para el timer timerControlNucleos que se encargará de actualizar...
     * ...los valores de los Núcleos en la interfaz gráfica.
     */
    private void configurarHiloNucleos(){
        timerControlNucleos = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // Función que repetirá según el intervalo asignado (1 segundo).
                controlGraficoNucleos();
            }
        });
        // Inicializo el timer.
        timerControlNucleos.start();
    }
    
    /**
     * Controla los Núcleos que se muestran en la interfaz gráfica.
     * Carga los Núcleos del cpu, actualiza los valores de los núcleos en la interfaz gráfica.
     * Este método es invocado por el timer timerControlNucleos.
     */
    private void controlGraficoNucleos(){
        // Obtengo los núcleos del CPU
        Nucleo n1 = cpu.obtenerNucleo1();
        Nucleo n2 = cpu.obtenerNucleo2();
        
        // Establezco los valores del núcleo 1
        int[] registrosN1=n1.obtenerRegistros(); // Obtengo los registros del núcleo 1.
        lbPCN1.setText(String.valueOf(n1.obtenerPC()));
        lbIRN1.setText(String.valueOf(n1.obtenerCadenaInstruccionIR()));
        lbACN1.setText(String.valueOf(registrosN1[0]));
        lbAXN1.setText(String.valueOf(registrosN1[1]));
        lbBXN1.setText(String.valueOf(registrosN1[2]));
        lbCXN1.setText(String.valueOf(registrosN1[3]));
        lbDXN1.setText(String.valueOf(registrosN1[4]));
        
        // Establezco los valores del núcleo 2
        int[] registrosN2=n2.obtenerRegistros(); // Obtengo los registros del núcleo 2.
        lbPCN2.setText(String.valueOf(n2.obtenerPC()));
        lbIRN2.setText(String.valueOf(n2.obtenerCadenaInstruccionIR()));
        lbACN2.setText(String.valueOf(registrosN2[0]));
        lbAXN2.setText(String.valueOf(registrosN2[1]));
        lbBXN2.setText(String.valueOf(registrosN2[2]));
        lbCXN2.setText(String.valueOf(registrosN2[3]));
        lbDXN2.setText(String.valueOf(registrosN2[4]));
        
        // Actualizo el panel de BCPs
        //panelBCPs.updateUI();
    }
    
    /**
     * Establece la función para el timer timerControlBCPs que se encargará de actualizar...
     * ...los valores de los BCPs en la interfaz gráfica.
     */
    private void configurarHiloBCPs(){
        timerControlBCPs = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // Función que repetirá según el intervalo asignado (1 segundo).
                controlGraficoBCPs();
            }
        });
        // Inicializo el timer.
        timerControlBCPs.start();
    }
    
    /**
     * Controla los BCPs que se muestran en la interfaz gráfica.
     * Carga los BCPs del cpu, limpia los elementos gráficos y carga unos nuevos...
     * ...con la información actualizada.
     * Este método es invocado por el timer timerControlBCPs.
     */
    private void controlGraficoBCPs(){
        // Obtengo todos los procesos del CPU
        List<BCP> procesos = cpu.obtenerProcesos();
        int numeroProcesos = procesos.size();
        JPBCP procesoInterfaz;
        BCP proceso;
        // Limpio los BCPs de la interfaz gráfica
        limpiarProcesosInterfaz();
        for(int i=0;i<numeroProcesos;i++){
            proceso=procesos.get(i);
            // Creo el nuevo BCP gráfico.
            procesoInterfaz=new JPBCP(proceso.obtenerNumeroProceso(), proceso.obtenerEstadoProceso(),
                    proceso.obtenerDireccionpila(), proceso.obtenerInicioMemoria(), proceso.obtenerFinMemoria(),
                    proceso.obtenerTiempoEjecucion(), proceso.obtenerCadenaInstruccionIR(), proceso.obtenerNucleo(),
                    proceso.obtenerRegistros());
            panelBCPs.add(procesoInterfaz); // Agrego el nuevo BCP gráfico al panel de BCPs
            BCPs.add(procesoInterfaz); // Agrego el nuevo BCP gráfico a la lista de control
        }// Actualizo el panel de BCPs
        panelBCPs.updateUI();
    }
    
    private List<String> obtenerArchivosAnalizar(){
        List<String> archivosAnalizar=new ArrayList<>();
        int cantidadArchivos=modeloTablaArchivos.getRowCount();
        for(int i=0;i<cantidadArchivos;i++){
            Boolean ejecutar=(Boolean) modeloTablaArchivos.getValueAt(i, 1);
            if(ejecutar){
                archivosAnalizar.add(archivos.get(i));
            }
        }return archivosAnalizar;
    }
    
    /**
     * Limpia los BCPs (o procesos) que se muestran en la interfaz gráfica
     * Y la lista de control.
     */
    private void limpiarProcesosInterfaz(){
        panelBCPs.removeAll();
        BCPs.clear();
    }
    
    private Boolean esNumero(String valor){
        if(!valor.equals("")){
            int largo = valor.length();
            for(int i=0; i<largo; i++){
                char caracter = valor.charAt(i);
                if(caracter<'0' || caracter>'9'){
                    return false;
                }
            }
        }else{
            return false;
        }return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btCargarArchivo = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtArchivos = new javax.swing.JTable();
        btAnalizarArchivo = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtMemoria = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtDisco = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        panelBCPs = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jpNucleo1 = new javax.swing.JPanel();
        lbNucleo1Titulo = new javax.swing.JLabel();
        lbPCN1Titulo = new javax.swing.JLabel();
        lbPCN1 = new javax.swing.JLabel();
        lbACN1 = new javax.swing.JLabel();
        lbACN1Titulo = new javax.swing.JLabel();
        lbAXN1Titulo = new javax.swing.JLabel();
        lbAXN1 = new javax.swing.JLabel();
        lbBXN1Titulo = new javax.swing.JLabel();
        lbBXN1 = new javax.swing.JLabel();
        lbCXN1Titulo = new javax.swing.JLabel();
        lbCXN1 = new javax.swing.JLabel();
        lbDXN1Titulo = new javax.swing.JLabel();
        lbDXN1 = new javax.swing.JLabel();
        lbIRN1Titulo = new javax.swing.JLabel();
        lbIRN1 = new javax.swing.JLabel();
        jpNucleo2 = new javax.swing.JPanel();
        lbNucleo2Titulo = new javax.swing.JLabel();
        lbPCN2Titulo = new javax.swing.JLabel();
        lbPCN2 = new javax.swing.JLabel();
        lbACN2 = new javax.swing.JLabel();
        lbACN2Titulo = new javax.swing.JLabel();
        lbAXN2Titulo = new javax.swing.JLabel();
        lbAXN2 = new javax.swing.JLabel();
        lbBXN2Titulo = new javax.swing.JLabel();
        lbBXN2 = new javax.swing.JLabel();
        lbCXN2Titulo = new javax.swing.JLabel();
        lbCXN2 = new javax.swing.JLabel();
        lbDXN2Titulo = new javax.swing.JLabel();
        lbDXN2 = new javax.swing.JLabel();
        lbIRN2Titulo = new javax.swing.JLabel();
        lbIRN2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        taPantalla = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tfConsola = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtColaN1 = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        jtColaN2 = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        btMenuConfiguracion = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Proyecto_1");

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btCargarArchivo.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btCargarArchivo.setText("Cargar Archivo");
        btCargarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCargarArchivoActionPerformed(evt);
            }
        });

        jtArchivos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Archivo", "Ejecutar"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtArchivos.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jtArchivos);
        if (jtArchivos.getColumnModel().getColumnCount() > 0) {
            jtArchivos.getColumnModel().getColumn(0).setPreferredWidth(100);
            jtArchivos.getColumnModel().getColumn(1).setPreferredWidth(30);
        }

        btAnalizarArchivo.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btAnalizarArchivo.setText("Analizar");
        btAnalizarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAnalizarArchivoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btAnalizarArchivo)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btCargarArchivo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btCargarArchivo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btAnalizarArchivo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText("Memoria");

        jtMemoria.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Posición", "Operación", "Registro", "Valor"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtMemoria.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(jtMemoria);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("Disco");

        jtDisco.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Posición", "Operación", "Registro", "Valor"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtDisco.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(jtDisco);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 231, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4)
                .addContainerGap())
        );

        panelBCPs.setLayout(new java.awt.GridLayout(0, 1));
        jScrollPane5.setViewportView(panelBCPs);

        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jpNucleo1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbNucleo1Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbNucleo1Titulo.setText("Núcleo 1");
        lbNucleo1Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbNucleo1Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbNucleo1Titulo.setPreferredSize(new java.awt.Dimension(115, 15));

        lbPCN1Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbPCN1Titulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbPCN1Titulo.setText("PC:");
        lbPCN1Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbPCN1Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbPCN1Titulo.setPreferredSize(new java.awt.Dimension(57, 15));

        lbPCN1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbPCN1.setText("0");
        lbPCN1.setMaximumSize(new java.awt.Dimension(115, 15));
        lbPCN1.setMinimumSize(new java.awt.Dimension(115, 15));
        lbPCN1.setPreferredSize(new java.awt.Dimension(57, 15));

        lbACN1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbACN1.setText("0");
        lbACN1.setMaximumSize(new java.awt.Dimension(115, 15));
        lbACN1.setMinimumSize(new java.awt.Dimension(115, 15));
        lbACN1.setPreferredSize(new java.awt.Dimension(57, 15));

        lbACN1Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbACN1Titulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbACN1Titulo.setText("AC:");
        lbACN1Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbACN1Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbACN1Titulo.setPreferredSize(new java.awt.Dimension(57, 15));

        lbAXN1Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbAXN1Titulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbAXN1Titulo.setText("AX:");
        lbAXN1Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbAXN1Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbAXN1Titulo.setPreferredSize(new java.awt.Dimension(57, 15));

        lbAXN1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbAXN1.setText("0");
        lbAXN1.setMaximumSize(new java.awt.Dimension(115, 15));
        lbAXN1.setMinimumSize(new java.awt.Dimension(115, 15));
        lbAXN1.setPreferredSize(new java.awt.Dimension(57, 15));

        lbBXN1Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbBXN1Titulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbBXN1Titulo.setText("BX:");
        lbBXN1Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbBXN1Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbBXN1Titulo.setPreferredSize(new java.awt.Dimension(57, 15));

        lbBXN1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbBXN1.setText("0");
        lbBXN1.setMaximumSize(new java.awt.Dimension(115, 15));
        lbBXN1.setMinimumSize(new java.awt.Dimension(115, 15));
        lbBXN1.setPreferredSize(new java.awt.Dimension(57, 15));

        lbCXN1Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbCXN1Titulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbCXN1Titulo.setText("CX:");
        lbCXN1Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbCXN1Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbCXN1Titulo.setPreferredSize(new java.awt.Dimension(57, 15));

        lbCXN1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbCXN1.setText("0");
        lbCXN1.setMaximumSize(new java.awt.Dimension(115, 15));
        lbCXN1.setMinimumSize(new java.awt.Dimension(115, 15));
        lbCXN1.setPreferredSize(new java.awt.Dimension(57, 15));

        lbDXN1Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbDXN1Titulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbDXN1Titulo.setText("DX:");
        lbDXN1Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbDXN1Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbDXN1Titulo.setPreferredSize(new java.awt.Dimension(57, 15));

        lbDXN1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbDXN1.setText("0");
        lbDXN1.setMaximumSize(new java.awt.Dimension(115, 15));
        lbDXN1.setMinimumSize(new java.awt.Dimension(115, 15));
        lbDXN1.setPreferredSize(new java.awt.Dimension(57, 15));

        lbIRN1Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbIRN1Titulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbIRN1Titulo.setText("IR:");
        lbIRN1Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbIRN1Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbIRN1Titulo.setPreferredSize(new java.awt.Dimension(57, 15));

        lbIRN1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbIRN1.setText("0");
        lbIRN1.setMaximumSize(new java.awt.Dimension(115, 15));
        lbIRN1.setMinimumSize(new java.awt.Dimension(115, 15));
        lbIRN1.setPreferredSize(new java.awt.Dimension(57, 15));

        javax.swing.GroupLayout jpNucleo1Layout = new javax.swing.GroupLayout(jpNucleo1);
        jpNucleo1.setLayout(jpNucleo1Layout);
        jpNucleo1Layout.setHorizontalGroup(
            jpNucleo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpNucleo1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpNucleo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpNucleo1Layout.createSequentialGroup()
                        .addComponent(lbIRN1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbIRN1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jpNucleo1Layout.createSequentialGroup()
                        .addGroup(jpNucleo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbNucleo1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpNucleo1Layout.createSequentialGroup()
                                .addComponent(lbPCN1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbPCN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbACN1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbACN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpNucleo1Layout.createSequentialGroup()
                                .addComponent(lbAXN1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbAXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbBXN1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbBXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpNucleo1Layout.createSequentialGroup()
                                .addComponent(lbCXN1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbCXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbDXN1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbDXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpNucleo1Layout.setVerticalGroup(
            jpNucleo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpNucleo1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbNucleo1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpNucleo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbPCN1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbPCN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbACN1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbACN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpNucleo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbAXN1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbAXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbBXN1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbBXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpNucleo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbCXN1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDXN1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpNucleo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbIRN1Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbIRN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpNucleo2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbNucleo2Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbNucleo2Titulo.setText("Núcleo 2");
        lbNucleo2Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbNucleo2Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbNucleo2Titulo.setPreferredSize(new java.awt.Dimension(115, 15));

        lbPCN2Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbPCN2Titulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbPCN2Titulo.setText("PC:");
        lbPCN2Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbPCN2Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbPCN2Titulo.setPreferredSize(new java.awt.Dimension(57, 15));

        lbPCN2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbPCN2.setText("0");
        lbPCN2.setMaximumSize(new java.awt.Dimension(115, 15));
        lbPCN2.setMinimumSize(new java.awt.Dimension(115, 15));
        lbPCN2.setPreferredSize(new java.awt.Dimension(57, 15));

        lbACN2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbACN2.setText("0");
        lbACN2.setMaximumSize(new java.awt.Dimension(115, 15));
        lbACN2.setMinimumSize(new java.awt.Dimension(115, 15));
        lbACN2.setPreferredSize(new java.awt.Dimension(57, 15));

        lbACN2Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbACN2Titulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbACN2Titulo.setText("AC:");
        lbACN2Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbACN2Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbACN2Titulo.setPreferredSize(new java.awt.Dimension(57, 15));

        lbAXN2Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbAXN2Titulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbAXN2Titulo.setText("AX:");
        lbAXN2Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbAXN2Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbAXN2Titulo.setPreferredSize(new java.awt.Dimension(57, 15));

        lbAXN2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbAXN2.setText("0");
        lbAXN2.setMaximumSize(new java.awt.Dimension(115, 15));
        lbAXN2.setMinimumSize(new java.awt.Dimension(115, 15));
        lbAXN2.setPreferredSize(new java.awt.Dimension(57, 15));

        lbBXN2Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbBXN2Titulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbBXN2Titulo.setText("BX:");
        lbBXN2Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbBXN2Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbBXN2Titulo.setPreferredSize(new java.awt.Dimension(57, 15));

        lbBXN2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbBXN2.setText("0");
        lbBXN2.setMaximumSize(new java.awt.Dimension(115, 15));
        lbBXN2.setMinimumSize(new java.awt.Dimension(115, 15));
        lbBXN2.setPreferredSize(new java.awt.Dimension(57, 15));

        lbCXN2Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbCXN2Titulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbCXN2Titulo.setText("CX:");
        lbCXN2Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbCXN2Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbCXN2Titulo.setPreferredSize(new java.awt.Dimension(57, 15));

        lbCXN2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbCXN2.setText("0");
        lbCXN2.setMaximumSize(new java.awt.Dimension(115, 15));
        lbCXN2.setMinimumSize(new java.awt.Dimension(115, 15));
        lbCXN2.setPreferredSize(new java.awt.Dimension(57, 15));

        lbDXN2Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbDXN2Titulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbDXN2Titulo.setText("DX:");
        lbDXN2Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbDXN2Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbDXN2Titulo.setPreferredSize(new java.awt.Dimension(57, 15));

        lbDXN2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbDXN2.setText("0");
        lbDXN2.setMaximumSize(new java.awt.Dimension(115, 15));
        lbDXN2.setMinimumSize(new java.awt.Dimension(115, 15));
        lbDXN2.setPreferredSize(new java.awt.Dimension(57, 15));

        lbIRN2Titulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbIRN2Titulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbIRN2Titulo.setText("IR:");
        lbIRN2Titulo.setMaximumSize(new java.awt.Dimension(115, 15));
        lbIRN2Titulo.setMinimumSize(new java.awt.Dimension(115, 15));
        lbIRN2Titulo.setPreferredSize(new java.awt.Dimension(57, 15));

        lbIRN2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbIRN2.setText("0");
        lbIRN2.setMaximumSize(new java.awt.Dimension(115, 15));
        lbIRN2.setMinimumSize(new java.awt.Dimension(115, 15));
        lbIRN2.setPreferredSize(new java.awt.Dimension(57, 15));

        javax.swing.GroupLayout jpNucleo2Layout = new javax.swing.GroupLayout(jpNucleo2);
        jpNucleo2.setLayout(jpNucleo2Layout);
        jpNucleo2Layout.setHorizontalGroup(
            jpNucleo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpNucleo2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpNucleo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpNucleo2Layout.createSequentialGroup()
                        .addComponent(lbIRN2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbIRN2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jpNucleo2Layout.createSequentialGroup()
                        .addGroup(jpNucleo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbNucleo2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpNucleo2Layout.createSequentialGroup()
                                .addComponent(lbPCN2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbPCN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbACN2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbACN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpNucleo2Layout.createSequentialGroup()
                                .addComponent(lbAXN2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbAXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbBXN2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbBXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpNucleo2Layout.createSequentialGroup()
                                .addComponent(lbCXN2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbCXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbDXN2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbDXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpNucleo2Layout.setVerticalGroup(
            jpNucleo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpNucleo2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbNucleo2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpNucleo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbPCN2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbPCN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbACN2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbACN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpNucleo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbAXN2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbAXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbBXN2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbBXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpNucleo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbCXN2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDXN2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpNucleo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbIRN2Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbIRN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("CPU");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3)
                    .addComponent(jpNucleo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpNucleo2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpNucleo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpNucleo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        taPantalla.setEditable(false);
        taPantalla.setColumns(20);
        taPantalla.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        taPantalla.setRows(5);
        jScrollPane6.setViewportView(taPantalla);

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Pantalla");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Consola");

        tfConsola.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tfConsola.setEnabled(false);
        tfConsola.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tfConsolaKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addComponent(tfConsola)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfConsola, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Colas de trabajo");

        jtColaN1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Procesos en la cola del Núcleo 1"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtColaN1.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jtColaN1);
        if (jtColaN1.getColumnModel().getColumnCount() > 0) {
            jtColaN1.getColumnModel().getColumn(0).setResizable(false);
        }

        jtColaN2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Procesos en la cola del Núcleo 2"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtColaN2.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(jtColaN2);
        if (jtColaN2.getColumnModel().getColumnCount() > 0) {
            jtColaN2.getColumnModel().getColumn(0).setResizable(false);
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btMenuConfiguracion.setText("Opciones");

        jMenuItem1.setText("Configuración");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        btMenuConfiguracion.add(jMenuItem1);

        jMenuItem2.setText("Salir");
        btMenuConfiguracion.add(jMenuItem2);

        jMenuBar1.add(btMenuConfiguracion);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 764, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btAnalizarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAnalizarArchivoActionPerformed
        if(!archivos.isEmpty()){
            List<String> archivosAnalizar=obtenerArchivosAnalizar();
            if(!archivosAnalizar.isEmpty()){
                // Carga los programas (archivos) al CPU.
                List<String> errores = cpu.cargarProgramas(archivosAnalizar); // Esta funcion retorna los archivos que fallaron.
                int cantidadErrores = errores.size();
                String archivosConError="";
                // Se crea la cadena con los archivo que presentaron errores.
                for(int i=0;i<cantidadErrores;i++){
                    archivosConError+="\""+errores.get(i)+"\" ";
                }
                if(errores.size()>0){
                    // Se muestra el mensaje con los archivos con errores.
                    JOptionPane.showMessageDialog(this, "Ocurrió un error leyendo el(los) archivo(s): "+archivosConError,
                            "Error leyendo archivo",JOptionPane.WARNING_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(this, "Marque al menos un archivo para ejecutar", "Ejecutar archivo",JOptionPane.WARNING_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(this, "Cargue un archivo para ejecutar", "Carga de archivo",JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btAnalizarArchivoActionPerformed

    private void btCargarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCargarArchivoActionPerformed
        JFileChooser cargador=new JFileChooser();
        cargador.setFileFilter(new FileNameExtensionFilter("ASM", "asm"));
        cargador.setMultiSelectionEnabled(true);
        cargador.showOpenDialog(this);
        File[] archivosSeleccionados=cargador.getSelectedFiles();
        File archivo;
        if(archivosSeleccionados!=null){
            int numeroArchivos=archivosSeleccionados.length;
            for(int i=0;i<numeroArchivos;i++){
                archivo=archivosSeleccionados[i];
                rutaArchivo=archivo.getPath();
                archivoCargado=true;
                // Agrega la ruta del archivo a la lista de archivos.
                archivos.add(archivo.getPath());
                // Agrega el nombre del archivo a la tabla de archivos.
                modeloTablaArchivos.addRow(new Object[]{archivo.getName(),false});
            }
        }
    }//GEN-LAST:event_btCargarArchivoActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        Configuracion configuracion = new Configuracion(this, true);
        configuracion.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void tfConsolaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfConsolaKeyTyped
        /* Se encarga de detectar el enter en el campo de texto consola y establece la interrupción como completa*/
        if(evt.getKeyChar()=='\n'){
            Boolean bloquear=true;
            String mensaje="\nNo es un Número.\n";
            if(interrupcion.obtenerNumeroInterrupcion()==Interrupcion.ENTRADA_TECLADO){
                String valor = tfConsola.getText();
                Boolean esNumero = esNumero(valor);
                if(esNumero){
                    if(valor.length()<10){
                        interrupcion.establecerValor(Integer.valueOf(valor));
                    }else{
                        mensaje="\nNúmero muy grande.\n";
                        esNumero=false;
                    }
                }
                bloquear=esNumero;
            }
            if(bloquear){
                taPantalla.setText(taPantalla.getText()+tfConsola.getText()+"\n");
                tfConsola.setText("");
                tfConsola.setEnabled(false);
                interrupcion.establecerComoEjecutada();
                interrupcion=null;
            }else{
                taPantalla.setText(taPantalla.getText()+tfConsola.getText()+mensaje);
            }
        }
    }//GEN-LAST:event_tfConsolaKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAnalizarArchivo;
    private javax.swing.JButton btCargarArchivo;
    private javax.swing.JMenu btMenuConfiguracion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JPanel jpNucleo1;
    private javax.swing.JPanel jpNucleo2;
    private javax.swing.JTable jtArchivos;
    private javax.swing.JTable jtColaN1;
    private javax.swing.JTable jtColaN2;
    private javax.swing.JTable jtDisco;
    private javax.swing.JTable jtMemoria;
    private javax.swing.JLabel lbACN1;
    private javax.swing.JLabel lbACN1Titulo;
    private javax.swing.JLabel lbACN2;
    private javax.swing.JLabel lbACN2Titulo;
    private javax.swing.JLabel lbAXN1;
    private javax.swing.JLabel lbAXN1Titulo;
    private javax.swing.JLabel lbAXN2;
    private javax.swing.JLabel lbAXN2Titulo;
    private javax.swing.JLabel lbBXN1;
    private javax.swing.JLabel lbBXN1Titulo;
    private javax.swing.JLabel lbBXN2;
    private javax.swing.JLabel lbBXN2Titulo;
    private javax.swing.JLabel lbCXN1;
    private javax.swing.JLabel lbCXN1Titulo;
    private javax.swing.JLabel lbCXN2;
    private javax.swing.JLabel lbCXN2Titulo;
    private javax.swing.JLabel lbDXN1;
    private javax.swing.JLabel lbDXN1Titulo;
    private javax.swing.JLabel lbDXN2;
    private javax.swing.JLabel lbDXN2Titulo;
    private javax.swing.JLabel lbIRN1;
    private javax.swing.JLabel lbIRN1Titulo;
    private javax.swing.JLabel lbIRN2;
    private javax.swing.JLabel lbIRN2Titulo;
    private javax.swing.JLabel lbNucleo1Titulo;
    private javax.swing.JLabel lbNucleo2Titulo;
    private javax.swing.JLabel lbPCN1;
    private javax.swing.JLabel lbPCN1Titulo;
    private javax.swing.JLabel lbPCN2;
    private javax.swing.JLabel lbPCN2Titulo;
    private javax.swing.JPanel panelBCPs;
    private javax.swing.JTextArea taPantalla;
    private javax.swing.JTextField tfConsola;
    // End of variables declaration//GEN-END:variables
}
