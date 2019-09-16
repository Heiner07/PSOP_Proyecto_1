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
public class JPBCP extends javax.swing.JPanel {

    /**
     * Creates new form JPBCP
     * @param numeroProceso
     * @param estadoProceso
     * @param direccionPila
     * @param inicioMemoria
     * @param finMemoria
     * @param tiempoEjecucion
     * @param registros
     */
    public JPBCP(int numeroProceso, int estadoProceso, int direccionPila,
            int inicioMemoria, int finMemoria, int tiempoEjecucion, String IR, int[] registros) {
        initComponents();
        this.lbNumeroProceso.setText(String.valueOf(numeroProceso));
        this.lbEstadoProceso.setText(BCP.estadoProcesoCadena(estadoProceso));
        this.lbDireccionPila.setText(String.valueOf(direccionPila));
        this.lbInicioMemoria.setText(String.valueOf(inicioMemoria));
        this.lbFinMemoria.setText(String.valueOf(finMemoria));
        this.lbTiempoEjecucion.setText(String.valueOf(tiempoEjecucion));
        this.lbIR.setText(IR);
        establecerRegistros(registros);
    }
    
    public void establecerEstadoProceso(String estado){
        lbEstadoProceso.setText(estado);
    }
    
    public void establecerTiempo(String tiempo){
        lbTiempoEjecucion.setText(tiempo);
    }
    
    private void establecerRegistros(int[] registros){
        lbAX.setText(String.valueOf(registros[0]));
        lbBX.setText(String.valueOf(registros[1]));
        lbCX.setText(String.valueOf(registros[2]));
        lbDX.setText(String.valueOf(registros[3]));
        //registros[4] es el IR en int, en el constructor se asigna como cadena;
        lbAC.setText(String.valueOf(registros[5]));
        lbPC.setText(String.valueOf(registros[6]));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        lbNumeroProceso = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lbEstadoProceso = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lbDireccionPila = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lbInicioMemoria = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lbFinMemoria = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lbTiempoEjecucion = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lbPC = new javax.swing.JLabel();
        lbAC = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lbAX = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        lbBX = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        lbCX = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        lbDX = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        lbIR = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Número de Proceso:");
        jLabel1.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel1.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel1.setPreferredSize(new java.awt.Dimension(115, 15));

        lbNumeroProceso.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbNumeroProceso.setText("0");
        lbNumeroProceso.setMaximumSize(new java.awt.Dimension(115, 15));
        lbNumeroProceso.setMinimumSize(new java.awt.Dimension(115, 15));
        lbNumeroProceso.setPreferredSize(new java.awt.Dimension(115, 15));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Estado del Proceso:");
        jLabel3.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel3.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel3.setPreferredSize(new java.awt.Dimension(115, 15));

        lbEstadoProceso.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbEstadoProceso.setText("0");
        lbEstadoProceso.setMaximumSize(new java.awt.Dimension(115, 15));
        lbEstadoProceso.setMinimumSize(new java.awt.Dimension(115, 15));
        lbEstadoProceso.setPreferredSize(new java.awt.Dimension(115, 15));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Dirección de Pila:");
        jLabel5.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel5.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel5.setPreferredSize(new java.awt.Dimension(115, 15));

        lbDireccionPila.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbDireccionPila.setText("0");
        lbDireccionPila.setMaximumSize(new java.awt.Dimension(115, 15));
        lbDireccionPila.setMinimumSize(new java.awt.Dimension(115, 15));
        lbDireccionPila.setPreferredSize(new java.awt.Dimension(115, 15));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Inicio de Memoria:");
        jLabel7.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel7.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel7.setPreferredSize(new java.awt.Dimension(115, 15));

        lbInicioMemoria.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbInicioMemoria.setText("0");
        lbInicioMemoria.setMaximumSize(new java.awt.Dimension(115, 15));
        lbInicioMemoria.setMinimumSize(new java.awt.Dimension(115, 15));
        lbInicioMemoria.setPreferredSize(new java.awt.Dimension(115, 15));

        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Fin de Memoria:");
        jLabel9.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel9.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel9.setPreferredSize(new java.awt.Dimension(115, 15));

        lbFinMemoria.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbFinMemoria.setText("0");
        lbFinMemoria.setMaximumSize(new java.awt.Dimension(115, 15));
        lbFinMemoria.setMinimumSize(new java.awt.Dimension(115, 15));
        lbFinMemoria.setPreferredSize(new java.awt.Dimension(115, 15));

        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Tiempo ejecución:");
        jLabel11.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel11.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel11.setPreferredSize(new java.awt.Dimension(115, 15));

        lbTiempoEjecucion.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbTiempoEjecucion.setText("0");
        lbTiempoEjecucion.setMaximumSize(new java.awt.Dimension(115, 15));
        lbTiempoEjecucion.setMinimumSize(new java.awt.Dimension(115, 15));
        lbTiempoEjecucion.setPreferredSize(new java.awt.Dimension(115, 15));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Registros");
        jLabel13.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel13.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel13.setPreferredSize(new java.awt.Dimension(115, 15));

        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("PC:");
        jLabel15.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel15.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel15.setPreferredSize(new java.awt.Dimension(57, 15));

        lbPC.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbPC.setText("0");
        lbPC.setMaximumSize(new java.awt.Dimension(115, 15));
        lbPC.setMinimumSize(new java.awt.Dimension(115, 15));
        lbPC.setPreferredSize(new java.awt.Dimension(57, 15));

        lbAC.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbAC.setText("0");
        lbAC.setMaximumSize(new java.awt.Dimension(115, 15));
        lbAC.setMinimumSize(new java.awt.Dimension(115, 15));
        lbAC.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("AC:");
        jLabel18.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel18.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel18.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("AX:");
        jLabel19.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel19.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel19.setPreferredSize(new java.awt.Dimension(57, 15));

        lbAX.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbAX.setText("0");
        lbAX.setMaximumSize(new java.awt.Dimension(115, 15));
        lbAX.setMinimumSize(new java.awt.Dimension(115, 15));
        lbAX.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("BX:");
        jLabel21.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel21.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel21.setPreferredSize(new java.awt.Dimension(57, 15));

        lbBX.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbBX.setText("0");
        lbBX.setMaximumSize(new java.awt.Dimension(115, 15));
        lbBX.setMinimumSize(new java.awt.Dimension(115, 15));
        lbBX.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("CX:");
        jLabel23.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel23.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel23.setPreferredSize(new java.awt.Dimension(57, 15));

        lbCX.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbCX.setText("0");
        lbCX.setMaximumSize(new java.awt.Dimension(115, 15));
        lbCX.setMinimumSize(new java.awt.Dimension(115, 15));
        lbCX.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel25.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("DX:");
        jLabel25.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel25.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel25.setPreferredSize(new java.awt.Dimension(57, 15));

        lbDX.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbDX.setText("0");
        lbDX.setMaximumSize(new java.awt.Dimension(115, 15));
        lbDX.setMinimumSize(new java.awt.Dimension(115, 15));
        lbDX.setPreferredSize(new java.awt.Dimension(57, 15));

        jLabel27.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel27.setText("IR:");
        jLabel27.setMaximumSize(new java.awt.Dimension(115, 15));
        jLabel27.setMinimumSize(new java.awt.Dimension(115, 15));
        jLabel27.setPreferredSize(new java.awt.Dimension(57, 15));

        lbIR.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbIR.setText("0");
        lbIR.setMaximumSize(new java.awt.Dimension(115, 15));
        lbIR.setMinimumSize(new java.awt.Dimension(115, 15));
        lbIR.setPreferredSize(new java.awt.Dimension(57, 15));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbIR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbPC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbAC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbAX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbBX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbCX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbDX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbPC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbAC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbAX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbBX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbIR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbNumeroProceso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbEstadoProceso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbDireccionPila, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbInicioMemoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbFinMemoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbTiempoEjecucion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 14, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbNumeroProceso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbEstadoProceso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDireccionPila, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbInicioMemoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbFinMemoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTiempoEjecucion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbAC;
    private javax.swing.JLabel lbAX;
    private javax.swing.JLabel lbBX;
    private javax.swing.JLabel lbCX;
    private javax.swing.JLabel lbDX;
    private javax.swing.JLabel lbDireccionPila;
    private javax.swing.JLabel lbEstadoProceso;
    private javax.swing.JLabel lbFinMemoria;
    private javax.swing.JLabel lbIR;
    private javax.swing.JLabel lbInicioMemoria;
    private javax.swing.JLabel lbNumeroProceso;
    private javax.swing.JLabel lbPC;
    private javax.swing.JLabel lbTiempoEjecucion;
    // End of variables declaration//GEN-END:variables
}
