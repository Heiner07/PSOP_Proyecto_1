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

/**
 *
 * @author Heiner
 */
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
                if(interrupcion.obtenerNumeroInterrupcion()==Interrupcion.FINALIZAR_PROGRAMA){
                    taPantalla.setText(taPantalla.getText()+"Presione ENTER para finalizar el proceso...");
                    tfConsola.setEnabled(true);
                    tfConsola.requestFocus();
                }
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
            instruccion = CPU.memoria[i].split(" ");
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
        List<Trabajo> colaN1 = cpu.obtenerColaTrabajoN1();
        List<Trabajo> colaN2 = cpu.obtenerColaTrabajoN2();
        int numeroProcesosColaN1 = colaN1.size();
        int numeroProcesosColaN2 = colaN2.size();
        limpiarColas();
        for(int i=0;i<numeroProcesosColaN1;i++){
            modeloTablaColaN1.addRow(new Object[]{"BCP "+colaN1.get(i).obtenerNumeroBCP()});
        }
        for(int i=0;i<numeroProcesosColaN2;i++){
            modeloTablaColaN2.addRow(new Object[]{"BCP "+colaN2.get(i).obtenerNumeroBCP()});
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
                    proceso.obtenerTiempoEjecucion(), proceso.obtenerCadenaInstruccionIR(), proceso.obtenerRegistros());
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
        jPanel7 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        lbPCN1 = new javax.swing.JLabel();
        lbACN1 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        lbAXN1 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        lbBXN1 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        lbCXN1 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        lbDXN1 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        lbIRN1 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        lbPCN2 = new javax.swing.JLabel();
        lbACN2 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        lbAXN2 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        lbBXN2 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        lbCXN2 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        lbDXN2 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
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

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel37.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel37.setText("Núcleo 1");
        jLabel37.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel37.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel37.setPreferredSize(new java.awt.Dimension(115, 15));

        jLabel38.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel38.setText("PC:");
        jLabel38.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel38.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel38.setPreferredSize(new java.awt.Dimension(57, 15));

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

        jLabel39.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel39.setText("AC:");
        jLabel39.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel39.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel39.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel40.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel40.setText("AX:");
        jLabel40.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel40.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel40.setPreferredSize(new java.awt.Dimension(57, 15));

        lbAXN1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbAXN1.setText("0");
        lbAXN1.setMaximumSize(new java.awt.Dimension(115, 15));
        lbAXN1.setMinimumSize(new java.awt.Dimension(115, 15));
        lbAXN1.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel41.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel41.setText("BX:");
        jLabel41.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel41.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel41.setPreferredSize(new java.awt.Dimension(57, 15));

        lbBXN1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbBXN1.setText("0");
        lbBXN1.setMaximumSize(new java.awt.Dimension(115, 15));
        lbBXN1.setMinimumSize(new java.awt.Dimension(115, 15));
        lbBXN1.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel42.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel42.setText("CX:");
        jLabel42.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel42.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel42.setPreferredSize(new java.awt.Dimension(57, 15));

        lbCXN1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbCXN1.setText("0");
        lbCXN1.setMaximumSize(new java.awt.Dimension(115, 15));
        lbCXN1.setMinimumSize(new java.awt.Dimension(115, 15));
        lbCXN1.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel43.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel43.setText("DX:");
        jLabel43.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel43.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel43.setPreferredSize(new java.awt.Dimension(57, 15));

        lbDXN1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbDXN1.setText("0");
        lbDXN1.setMaximumSize(new java.awt.Dimension(115, 15));
        lbDXN1.setMinimumSize(new java.awt.Dimension(115, 15));
        lbDXN1.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel44.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel44.setText("IR:");
        jLabel44.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel44.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel44.setPreferredSize(new java.awt.Dimension(57, 15));

        lbIRN1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbIRN1.setText("0");
        lbIRN1.setMaximumSize(new java.awt.Dimension(115, 15));
        lbIRN1.setMinimumSize(new java.awt.Dimension(115, 15));
        lbIRN1.setPreferredSize(new java.awt.Dimension(57, 15));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbIRN1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbPCN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbACN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbAXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbBXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbCXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbDXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbPCN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbACN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbAXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbBXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDXN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbIRN1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel45.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel45.setText("Núcleo 2");
        jLabel45.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel45.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel45.setPreferredSize(new java.awt.Dimension(115, 15));

        jLabel46.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel46.setText("PC:");
        jLabel46.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel46.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel46.setPreferredSize(new java.awt.Dimension(57, 15));

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

        jLabel47.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel47.setText("AC:");
        jLabel47.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel47.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel47.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel48.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel48.setText("AX:");
        jLabel48.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel48.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel48.setPreferredSize(new java.awt.Dimension(57, 15));

        lbAXN2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbAXN2.setText("0");
        lbAXN2.setMaximumSize(new java.awt.Dimension(115, 15));
        lbAXN2.setMinimumSize(new java.awt.Dimension(115, 15));
        lbAXN2.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel49.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel49.setText("BX:");
        jLabel49.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel49.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel49.setPreferredSize(new java.awt.Dimension(57, 15));

        lbBXN2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbBXN2.setText("0");
        lbBXN2.setMaximumSize(new java.awt.Dimension(115, 15));
        lbBXN2.setMinimumSize(new java.awt.Dimension(115, 15));
        lbBXN2.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel50.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel50.setText("CX:");
        jLabel50.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel50.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel50.setPreferredSize(new java.awt.Dimension(57, 15));

        lbCXN2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbCXN2.setText("0");
        lbCXN2.setMaximumSize(new java.awt.Dimension(115, 15));
        lbCXN2.setMinimumSize(new java.awt.Dimension(115, 15));
        lbCXN2.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel51.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel51.setText("DX:");
        jLabel51.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel51.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel51.setPreferredSize(new java.awt.Dimension(57, 15));

        lbDXN2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbDXN2.setText("0");
        lbDXN2.setMaximumSize(new java.awt.Dimension(115, 15));
        lbDXN2.setMinimumSize(new java.awt.Dimension(115, 15));
        lbDXN2.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel52.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel52.setText("IR:");
        jLabel52.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel52.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel52.setPreferredSize(new java.awt.Dimension(57, 15));

        lbIRN2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbIRN2.setText("0");
        lbIRN2.setMaximumSize(new java.awt.Dimension(115, 15));
        lbIRN2.setMinimumSize(new java.awt.Dimension(115, 15));
        lbIRN2.setPreferredSize(new java.awt.Dimension(57, 15));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbIRN2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbPCN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbACN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbAXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbBXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbCXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbDXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbPCN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbACN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbAXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbBXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDXN2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            taPantalla.setText(taPantalla.getText()+"\n");
            tfConsola.setEnabled(false);
            interrupcion.establecerComoEjecutada();
            interrupcion=null;
        }
    }//GEN-LAST:event_tfConsolaKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAnalizarArchivo;
    private javax.swing.JButton btCargarArchivo;
    private javax.swing.JMenu btMenuConfiguracion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTable jtArchivos;
    private javax.swing.JTable jtColaN1;
    private javax.swing.JTable jtColaN2;
    private javax.swing.JTable jtDisco;
    private javax.swing.JTable jtMemoria;
    private javax.swing.JLabel lbACN1;
    private javax.swing.JLabel lbACN2;
    private javax.swing.JLabel lbAXN1;
    private javax.swing.JLabel lbAXN2;
    private javax.swing.JLabel lbBXN1;
    private javax.swing.JLabel lbBXN2;
    private javax.swing.JLabel lbCXN1;
    private javax.swing.JLabel lbCXN2;
    private javax.swing.JLabel lbDXN1;
    private javax.swing.JLabel lbDXN2;
    private javax.swing.JLabel lbIRN1;
    private javax.swing.JLabel lbIRN2;
    private javax.swing.JLabel lbPCN1;
    private javax.swing.JLabel lbPCN2;
    private javax.swing.JPanel panelBCPs;
    private javax.swing.JTextArea taPantalla;
    private javax.swing.JTextField tfConsola;
    // End of variables declaration//GEN-END:variables
}
