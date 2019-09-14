/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_1;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Heiner
 */
public class JFVentanaPrincipal extends javax.swing.JFrame {
    
    int paso;
    int posicionMemoria;
    int instruccion;
    String rutaArchivo;
    Boolean archivoCargado;
    DefaultTableModel modeloTablaArchivos, modeloTablaMemoria, modeloTablaDisco;
    List<String> archivos;
    List<JPBCP> BCPs; // Lista de control de BCPs
    CPU cpu;
    
    /* Hilos de control */
    Timer timerControlBCPs;
    
    /**
     * Creates new form JFVentanaPrincipal
     */
    public JFVentanaPrincipal() {
        initComponents();
        establecerValores();
        configurarTablaMemoria();
        configurarTablaDisco();
        //configurarTabla();
        configurarHilosDeControl();
        this.archivos=new ArrayList<>();
        this.BCPs=new ArrayList<>();
        this.modeloTablaArchivos = (DefaultTableModel) jtArchivos.getModel();
        this.cpu=new CPU();
        this.archivoCargado=false;
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
    
    // No estoy utilizando este metodo, pero lo mantengo por si requiere de algunas funciones que hay aqui.
    private void configurarTabla(){
        String[] columnas = new String[]{"Archivo", "", ""};
        Object[][] datos = new Object[][]{};
        this.jtArchivos.setModel(new DefaultTableModel(datos, columnas){
            Class[] tipos = new Class[]{
                java.lang.String.class,
                JButton.class,
                JButton.class
            };
            
            @Override
            public Class getColumnClass(int columnIndex) {
                // Este método es invocado por el CellRenderer para saber que dibujar en la celda,
                // observen que estamos retornando la clase que definimos de antemano.
                return tipos[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Sobrescribimos este método para evitar que la columna que contiene los botones sea editada.
                return false;//!(this.getColumnClass(column).equals(JButton.class));
            }
        });
        
        this.jtArchivos.setDefaultRenderer(JButton.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                /**
                 * Retorna el objeto que se va a dibujar en la celda.
                 * Se dibujará en la celda el objeto que devuelva el TableModel. También 
                 * Este renderer retorna el objeto tal y como lo recibe (dibuja cualquier componente).
                 */
                return (Component) o;
            }
        });
        this.jtArchivos.getColumnModel().getColumn(0).setPreferredWidth(80);
        this.jtArchivos.getColumnModel().getColumn(1).setPreferredWidth(40);
        this.jtArchivos.getColumnModel().getColumn(2).setPreferredWidth(40);
    }
    
    private void establecerValores(){
        this.paso=1;
        this.posicionMemoria=0;
        this.instruccion=1;
    }
    
    /**
     * Establece la funcion para el timer timerControlBCPs que se encargará de actualizar...
     * ...los valores de los BCPs en la interfaz gráfica.
     */
    private void configurarHilosDeControl(){
        timerControlBCPs = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // Funcion que repetirá segun el intervalo asignado (1 segundo).
                controlGraficoBCPs();
            }
        });
        // Inicializo el timer.
        timerControlBCPs.start();
    }
    
    private void reiniciarValores(){
        panelInstrucciones.removeAll();
        establecerValores();
        
       // Nucleo.limpiarClase();
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
                    proceso.obtenerTiempoEjecucion(), proceso.obtenerRegistros());
            panelBCPs.add(procesoInterfaz); // Agrego el nuevo BCP gráfico al panel de BCPs
            BCPs.add(procesoInterfaz); // Agrego el nuevo BCP gráfico a la lista de control
        }// Actualizo el panel de BCPs
        panelBCPs.updateUI();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btSiguiente = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        panelInstrucciones = new javax.swing.JPanel();
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
        jMenuBar1 = new javax.swing.JMenuBar();
        btMenuConfiguracion = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Proyecto_1");

        btSiguiente.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btSiguiente.setText("Siguiente");
        btSiguiente.setEnabled(false);
        btSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSiguienteActionPerformed(evt);
            }
        });

        panelInstrucciones.setLayout(new java.awt.GridLayout(0, 2));
        jScrollPane2.setViewportView(panelInstrucciones);

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
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 695, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        taPantalla.setRows(5);
        jScrollPane6.setViewportView(taPantalla);

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Pantalla");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Consola");

        tfConsola.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btSiguiente)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btSiguiente)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btAnalizarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAnalizarArchivoActionPerformed
        if(!archivos.isEmpty()){
            List<String> archivosAnalizar=obtenerArchivosAnalizar();
            if(!archivosAnalizar.isEmpty()){
                try {// Modificar el siguiente bloque, solo envia un archivo no todos
                    reiniciarValores();
                    Nucleo op = new Nucleo();     
                    op.muestraContenido(rutaArchivo);
                    posicionMemoria=Nucleo.posicionMemoria;
                    btSiguienteActionPerformed(evt); // Funcion del boton Siguiente.
                    btSiguiente.setEnabled(true);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error al leer el archivo", "Error de archivo",JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(this, "Marque al menos un archivo para ejecutar", "Ejecutar archivo",JOptionPane.WARNING_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(this, "Cargue un archivo para ejecutar", "Carga de archivo",JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btAnalizarArchivoActionPerformed

    private void btSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSiguienteActionPerformed
        if(paso<= (Nucleo.numeroInstrucciones *2) && posicionMemoria<99){
            if(Nucleo.ejecutar){
                JPPaso ejecutarInstruccion=new JPPaso(paso,posicionMemoria-1,instruccion,true);   
                panelInstrucciones.add(ejecutarInstruccion);
                Nucleo.ejecutar = false;
                instruccion++;
            }else{
                JPPaso ejecucion=new JPPaso(paso,posicionMemoria,instruccion,false);
                panelInstrucciones.add(ejecucion);
                Nucleo.ejecutar = true;
                posicionMemoria++;
            }
            // PROBANDO
            //Creo un proceso en la cpu
            cpu.crearProceso();
            // PROBANDO
            paso++;
            jScrollPane2.getVerticalScrollBar().setValue(jScrollPane2.getVerticalScrollBar().getMaximum());
            panelInstrucciones.updateUI();
        }else{
            JOptionPane.showMessageDialog(this, "No hay más instrucciones o se llegó al final de la memoria.", "No hay más instrucciones",JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btSiguienteActionPerformed

    private void btCargarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCargarArchivoActionPerformed
        JFileChooser cargador=new JFileChooser();
        cargador.setFileFilter(new FileNameExtensionFilter("ASM", "asm"));
        cargador.showOpenDialog(this);
        File archivo=cargador.getSelectedFile();
        if(archivo!=null){
            rutaArchivo=archivo.getPath();
            archivoCargado=true;
            // Agrega la ruta del archivo a la lista de archivos.
            archivos.add(archivo.getPath());
            // Agrega el nombre del archivo a la tabla de archivos.
            modeloTablaArchivos.addRow(new Object[]{archivo.getName(),false});
        }
    }//GEN-LAST:event_btCargarArchivoActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        Configuracion configuracion = new Configuracion(this, true);
        configuracion.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAnalizarArchivo;
    private javax.swing.JButton btCargarArchivo;
    private javax.swing.JMenu btMenuConfiguracion;
    private javax.swing.JButton btSiguiente;
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
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTable jtArchivos;
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
    private javax.swing.JPanel panelInstrucciones;
    private javax.swing.JTextArea taPantalla;
    private javax.swing.JTextField tfConsola;
    // End of variables declaration//GEN-END:variables
}
