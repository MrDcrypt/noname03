/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.gestiongym;
//import com.mycompany.gestiongym.PresentTableForm;
import com.mycompany.gestiongym.LoginGym.UserSession;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.sql.PreparedStatement;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import com.mycompany.gestiongym.frameRutina; // Asegúrate de importar la clase correctamente







/**
 *
 * @author daniel
 */

public class PresentFrameGym extends javax.swing.JFrame {
    private Connection connection;
    public class ClaseId {
    public static int idRequerido;  // Variable estática para guardar el ID del usuario
}

    private void connectToDatabase() {
        String url = "jdbc:mysql://127.0.0.1:3306/SistemaGym";  // Cambia "fitdb" por tu base de datos
        String user = "root";  // Usuario de la base de datos
        String password = "";  // Contraseña de la base de datos (si tienes una, agrégala aquí)

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }
    
    
   // Método para ajustar el ancho de las columnas
private void ajustarAnchoColumnas() {
    javax.swing.table.TableColumnModel columnModel = tableUsuarios.getColumnModel();

    // Establecer el ancho de las columnas manualmente
    columnModel.getColumn(0).setPreferredWidth(150);  // Ancho de la columna 'Nombre'
    columnModel.getColumn(1).setPreferredWidth(50);   // Ancho de la columna 'Edad'
    columnModel.getColumn(2).setPreferredWidth(50);   // Ancho de la columna 'Altura'
    columnModel.getColumn(3).setPreferredWidth(50);   // Ancho de la columna 'Peso'
    columnModel.getColumn(4).setPreferredWidth(100);  // Ancho de la columna 'Mes Actual'
    columnModel.getColumn(5).setPreferredWidth(100);  // Ancho de la columna 'Rutina'
}

// Método para cargar datos del usuario desde la base de datos
private void cargarDatosUsuario(int userId) {
    String query = "SELECT NyapAdmin, nMatricula FROM Admins WHERE Id = ?";

    try {
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, userId);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String nyappVar = resultSet.getString("NyapAdmin");
            String matriculaVar = resultSet.getString("nMatricula");

            labelRol.setText("COACH");
            labelNombre.setText("Nombre: " + nyappVar);
            labelLegajo.setText("Legajo: " + matriculaVar);
        } else {
            System.out.println("No se encontraron datos para el usuario con ID: " + userId);
        }

        ajustarAnchoColumnas();

        resultSet.close();
        statement.close();
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error al consultar los datos del usuario: " + e.getMessage());
    }
}

// Constructor del frame principal
private String query = "SELECT Nyap, edad, altura, peso, mesActual FROM Usuarios";

public PresentFrameGym() {
    initComponents();
    connectToDatabase();

    cargarIcono();
    cargarDatosUsuario(com.mycompany.gestiongym.LoginGym.UserSession.userId);
    cargarDatosTabla(false, query);
    configurarTabla();
    agregarEventosTabla();
}

// Renderizador de botón para la columna específica
class ButtonRenderer extends JButton implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if ("Ver".equals(value)) {
            setText("Ver");
            setBackground(isSelected ? table.getSelectionBackground() : Color.LIGHT_GRAY);
            return this;
        } else {
            return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}

// Editor de botón para manejar la acción al hacer clic
class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String nombreUsuario;
    private boolean isPushed;

    public ButtonEditor() {
        super(new JTextField());
        button = new JButton("Ver");
        button.setFocusable(false); // Evitar que el botón mantenga el foco
        button.addActionListener(e -> {
            if (isPushed) {
                System.out.println("Botón 'Ver' presionado para el usuario: " + nombreUsuario); // Depuración
                abrirRutina(nombreUsuario); // Acción personalizada al hacer clic
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if ("Ver".equals(value)) {
            nombreUsuario = table.getValueAt(row, 0).toString(); // Obtener el nombre del usuario
            isPushed = true;
            button.setText("Ver");
            return button;
        } else {
            return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, false, row, column);
        }
    }

    @Override
    public Object getCellEditorValue() {
        isPushed = false;
        return "Ver"; // Retornar el texto del botón
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        fireEditingStopped(); // Notificar el fin de la edición
        return super.stopCellEditing();
    }
}

// Renderizador personalizado para celdas
public class CustomCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (isSelected) {
            cell.setBackground(table.getSelectionBackground());
            cell.setForeground(table.getSelectionForeground());
        } else {
            cell.setBackground(new Color(102, 102, 102)); // Fondo gris oscuro
            cell.setForeground(new Color(57, 255, 20));  // Texto verde fosforescente
        }

        if (value != null && "No abonado".equals(value.toString())) {
            cell.setBackground(new Color(255, 102, 102)); // Fondo rojo suave
            cell.setForeground(Color.BLACK); // Texto negro
        }

        if (cell instanceof JLabel) {
            JLabel label = (JLabel) cell;
            label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
            label.setHorizontalAlignment(SwingConstants.CENTER); // Centrar contenido
        }

        return cell;
    }
}

// Configuración de la tabla
private void configurarTabla() {
    tableUsuarios.setRowHeight(25); // Altura de las filas
    tableUsuarios.setIntercellSpacing(new Dimension(1, 1)); // Espaciado entre celdas
    tableUsuarios.setGridColor(Color.BLACK); // Color de las líneas de la tabla

    for (int i = 0; i < tableUsuarios.getColumnCount(); i++) {
        if (i == 5) { // Columna de botones (índice 5)
            tableUsuarios.getColumnModel().getColumn(i).setCellRenderer(new ButtonRenderer());
            tableUsuarios.getColumnModel().getColumn(i).setCellEditor(new ButtonEditor());
        } else { // Otras columnas
            tableUsuarios.getColumnModel().getColumn(i).setCellRenderer(new CustomCellRenderer());
        }
    }
}

private void abrirRutina(String nombreUsuario) {
    try {
        System.out.println("Consultando ID para el usuario: " + nombreUsuario); // Depuración
        // Consulta para obtener el ID del usuario a partir del nombre
        String query = "SELECT id FROM Usuarios WHERE Nyap = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, nombreUsuario);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            int userIdSelec = resultSet.getInt("id"); // Obtener el ID del usuario
            System.out.println("Abriendo rutina para el usuario: " + nombreUsuario + " (ID: " + userIdSelec + ")");
            ClaseId.idRequerido = userIdSelec;

            // Crear y configurar el nuevo frame
            frameRutina frameRutinaV = new frameRutina();
            frameRutinaV.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cerrar este frame
            frameRutinaV.setLocationRelativeTo(this); // Relativo al frame principal
            frameRutinaV.setAlwaysOnTop(true); // Asegurar que quede al frente
            frameRutinaV.setVisible(true); // Mostrar el nuevo frame
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró al usuario: " + nombreUsuario, "Error", JOptionPane.ERROR_MESSAGE);
        }

        resultSet.close();
        statement.close();
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al obtener el ID del usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

  



private boolean enProcesoDeGuardado = false;

private void cargarDatosTabla(boolean mantenerFilaCarga, String query) {
    // Validar que la consulta no sea nula ni vacía
    if (query == null || query.trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Error: La consulta SQL está vacía.");
        return;
    }

    try {
        PreparedStatement statement = connection.prepareStatement(query); // Usa la consulta pasada como argumento
        ResultSet resultSet = statement.executeQuery();

        DefaultTableModel model = (DefaultTableModel) tableUsuarios.getModel();
        model.setRowCount(0); // Limpiar todas las filas del modelo

        // Agregar la fila de registro al inicio
        model.addRow(new Object[]{"", "", "", "", "", "Registrar"});

        // Recorrer el ResultSet y cargar los datos en la tabla
        while (resultSet.next()) {
            String nombre = resultSet.getString("Nyap");
            int edad = resultSet.getInt("edad");
            int altura = resultSet.getInt("altura");
            int peso = resultSet.getInt("peso");
            int mesActual = resultSet.getInt("mesActual");

            String estadoAbonado = (mesActual == 1) ? "Abonado" : "No abonado";
            model.addRow(new Object[]{nombre, edad, altura, peso, estadoAbonado, "Ver"});
        }

        configurarTabla();

        // Evitar duplicación de listeners
        for (TableModelListener listener : model.getTableModelListeners()) {
            model.removeTableModelListener(listener);
        }

        model.addTableModelListener(e -> {
            if (enProcesoDeGuardado) return; // Si ya está en proceso, no hacer nada
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (row >= 0 && column >= 0) {
                String newValue = model.getValueAt(row, column).toString();
                guardarCambios(row, column, newValue);
            }
        });

        resultSet.close();
        statement.close();

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al cargar los datos de la tabla: " + e.getMessage());
    }
}




private void guardarCambios(int row, int col, String newValue) {
    try {
        // Validar si la fila es la de registro (primera fila)
        if (row == 0) {
            return; // Salir sin realizar ninguna acción
        }

        // Validar si la columna es no editable (Nyap)
        if (col == 0) { // Columna 0 = Nyap
            JOptionPane.showMessageDialog(null, "La columna Nombre no es modificable.");
            return; // Salir sin realizar ninguna acción
        }

        // Obtener el Nyap (nombre) del usuario para identificarlo en la base de datos
        String nombreUsuario = (String) tableUsuarios.getValueAt(row, 0); // Primera columna es el Nyap

        // Validar que el Nyap sea válido
        if (nombreUsuario == null || nombreUsuario.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No se puede identificar el usuario a actualizar.");
            return; // Salir si no hay un Nyap válido
        }

        // Obtener el nombre real de la columna en la base de datos
        String columnName = getDatabaseColumnName(col);
        if (columnName == null) {
           // JOptionPane.showMessageDialog(null, "Columna no válida para modificar.");
            return; // Salir si la columna no es válida
        }

        // Caso especial para la columna mesActual
        if ("mesActual".equals(columnName) && "1".equals(newValue)) {
            // Mostrar un cuadro de diálogo para elegir entre plan Básico o Completo
            String[] options = {"Básico", "Completo"};
            int choice = JOptionPane.showOptionDialog(
                null,
                "¿Qué plan abonó el usuario?",
                "Seleccionar Plan",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );

            // Determinar el plan elegido
            String planElegido = null;
            if (choice == 0) {
                planElegido = "Básico";
            } else if (choice == 1) {
                planElegido = "Completo";
            }

            if (planElegido != null) {
                // Guardar el plan en la base de datos
                String planQuery = "UPDATE Usuarios SET plan = ? WHERE Nyap = ?";
                PreparedStatement planStatement = connection.prepareStatement(planQuery);
                planStatement.setString(1, planElegido);
                planStatement.setString(2, nombreUsuario);

                int planRowsAffected = planStatement.executeUpdate();
                if (planRowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "El plan fue actualizado correctamente a " + planElegido + ".");
                } else {
                    JOptionPane.showMessageDialog(null, "No se pudo actualizar el plan. Verifica los datos.");
                }
            } else {
                // Canceló la elección, revertimos el cambio en la tabla
                tableUsuarios.setValueAt(0, row, col); // Restablecemos mesActual a 0
                return; // Salir sin guardar cambios
            }
        }

        // Construir y ejecutar el query de actualización para las demás columnas
        String cambiosQuery = "UPDATE Usuarios SET " + columnName + " = ? WHERE Nyap = ?";
        PreparedStatement statement = connection.prepareStatement(cambiosQuery);

        // Asignar valores al query
        statement.setString(1, newValue); // Nuevo valor de la celda
        statement.setString(2, nombreUsuario); // Identificador único del usuario (Nyap)

        // Desactivar el listener para evitar la duplicación de eventos
        enProcesoDeGuardado = true;

        // Ejecutar la actualización y verificar las filas afectadas
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Cambio guardado correctamente.");
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "No se realizó ningún cambio. Verifica los datos.");
            });
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al guardar el cambio: " + e.getMessage());
    } finally {
        // Asegurar que el listener siempre se reactiva
        enProcesoDeGuardado = false;
    }
}





    
    
    private void registrarNuevoUsuario(DefaultTableModel model, int row) {
    try {
        // Validar si los datos en la fila son válidos
        String nombre = (String) model.getValueAt(row, 0);
        Object edadObj = model.getValueAt(row, 1);
        Object alturaObj = model.getValueAt(row, 2);
        Object pesoObj = model.getValueAt(row, 3);
        String mesActual = (String) model.getValueAt(row, 4);

        if (nombre == null || nombre.isEmpty() || 
            edadObj == null || alturaObj == null || pesoObj == null || 
            mesActual == null || mesActual.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, completa todos los campos antes de registrar.");
            return;
        }

        // Validar y convertir valores numéricos
        int edad = Integer.parseInt(edadObj.toString());
        int altura = Integer.parseInt(alturaObj.toString());
        int peso = Integer.parseInt(pesoObj.toString());

        // Insertar los datos en la base de datos
        String nuevoUserquery = "INSERT INTO Usuarios (Nyap, edad, altura, peso, mesActual, fechaRegistro) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement insertStatement = connection.prepareStatement(nuevoUserquery);
        insertStatement.setString(1, nombre);
        insertStatement.setInt(2, edad);
        insertStatement.setInt(3, altura);
        insertStatement.setInt(4, peso);
        insertStatement.setString(5, mesActual);
        insertStatement.setDate(6, new java.sql.Date(System.currentTimeMillis()));
        insertStatement.executeUpdate();

        // Recargar la tabla con la fila de registro al inicio
        cargarDatosTabla(true, query);

        // Notificar al usuario
        JOptionPane.showMessageDialog(null, "Usuario registrado correctamente.");
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Por favor, ingresa valores numéricos válidos para edad, altura y peso.");
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al registrar el usuario: " + e.getMessage());
    }
}

private void agregarEventosTabla() {
    tableUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            int row = tableUsuarios.rowAtPoint(evt.getPoint());
            int column = tableUsuarios.columnAtPoint(evt.getPoint());

            // Verifica que los índices sean válidos
            if (row == -1 || column == -1) {
                return; // Salir si no se hizo clic en una celda válida
            }

            // Verifica si el clic fue en la fila de registro (primera fila) y en la columna "Registrar"
            if (row == 0 && column == 5) { // Fila de registro es la primera (índice 0)
                DefaultTableModel model = (DefaultTableModel) tableUsuarios.getModel();
                registrarNuevoUsuario(model, row); // Registrar el usuario
            }
        }
    });
}


private String getDatabaseColumnName(int col) {
    switch (col) {
        case 1: return "edad";       // Columna 1 corresponde a "edad" en la BD
        case 2: return "altura";     // Columna 2 corresponde a "altura"
        case 3: return "peso";       // Columna 3 corresponde a "peso"
        case 4: return "mesActual";  // Columna 4 corresponde a "mesActual"
        default: return null;        // Cualquier otra columna no es válida para modificar
    }
}

    

// Método para registrar un nuevo usuario en la base de datos
 





    // Método para guardar los cambios en la base de datos



 



    @SuppressWarnings("unchecked")
    private void cargarIcono() {
        // Ruta relativa de la imagen en tu proyecto
        String ruta = "src/imagen/userPerfil.png"; // Ruta absoluta
        ImageIcon icono = new ImageIcon(ruta);
        //labelUserIcon.setIcon(icono);
       // ImageIcon icono = new ImageIcon(getClass().getResource(ruta));
        Image imagen = icono.getImage().getScaledInstance(labelUserIcon.getWidth(), labelUserIcon.getHeight(), Image.SCALE_SMOOTH);
        labelUserIcon.setIcon(new ImageIcon(imagen));

    }
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        buttonOkselect = new javax.swing.JButton();
        btnSalir1 = new javax.swing.JButton();
        labelUserIcon = new javax.swing.JLabel();
        labelRol = new javax.swing.JLabel();
        labelNombre = new javax.swing.JLabel();
        labelLegajo = new javax.swing.JLabel();
        btnSalir = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableUsuarios = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        checkAbonados = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setPreferredSize(new java.awt.Dimension(1366, 760));

        jLabel5.setFont(new java.awt.Font("Arial Black", 0, 36)); // NOI18N
        jLabel5.setText("Sistema gestión ");
        jLabel5.setAlignmentX(0.5F);

        buttonOkselect.setBackground(new java.awt.Color(70, 130, 180));
        buttonOkselect.setText("Actualizar");
        buttonOkselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkselectActionPerformed(evt);
            }
        });

        btnSalir1.setBackground(new java.awt.Color(255, 51, 51));
        btnSalir1.setText("Salir");
        btnSalir1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalir1ActionPerformed(evt);
            }
        });

        labelUserIcon.setBackground(new java.awt.Color(0, 102, 102));
        labelUserIcon.setText("jLabel1");
        labelUserIcon.setOpaque(true);

        labelRol.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        labelRol.setText("ROL");

        labelNombre.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        labelNombre.setText("Nombre:");

        labelLegajo.setText("Matricula n°:");

        btnSalir.setBackground(new java.awt.Color(70, 130, 180));
        btnSalir.setText("Configuración");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        jScrollPane1.setBackground(javax.swing.UIManager.getDefaults().getColor("ToolBar.foreground"));
        jScrollPane1.setForeground(new java.awt.Color(102, 102, 102));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tableUsuarios.setBackground(new java.awt.Color(102, 102, 102));
        tableUsuarios.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        tableUsuarios.setForeground(new java.awt.Color(51, 204, 0));
        tableUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Nombre", "Edad", "Altura", "Peso", "Mes Actual", "Rutina"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tableUsuarios.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setViewportView(tableUsuarios);

        jButton1.setBackground(new java.awt.Color(255, 204, 51));
        jButton1.setText("Pagos");

        jButton2.setText("Fitnes");

        jCheckBox1.setText("Ocultar");

        checkAbonados.setText("Solo abonados");
        checkAbonados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAbonadosActionPerformed(evt);
            }
        });

        jCheckBox3.setText("No pagados");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(labelUserIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelLegajo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 208, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(73, 73, 73)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelNombre)
                            .addComponent(labelRol, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(158, 158, 158))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonOkselect, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56)
                        .addComponent(btnSalir1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(562, 562, 562))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addGap(32, 32, 32)
                        .addComponent(checkAbonados)
                        .addGap(34, 34, 34)
                        .addComponent(jCheckBox3)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelRol)
                        .addGap(34, 34, 34)
                        .addComponent(labelNombre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelLegajo))
                    .addComponent(labelUserIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(checkAbonados)
                    .addComponent(jCheckBox3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonOkselect, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalir1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonOkselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkselectActionPerformed
        cargarDatosTabla(true,query);
        
    }//GEN-LAST:event_buttonOkselectActionPerformed

    private void btnSalir1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalir1ActionPerformed
       this.dispose();
    }//GEN-LAST:event_btnSalir1ActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        LoginGym loginForm = new LoginGym();
        loginForm.setVisible(true);

        // Cerrar el formulario actual
        
    }//GEN-LAST:event_btnSalirActionPerformed

    private void checkAbonadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkAbonadosActionPerformed
      // Verificar si el checkbox está seleccionado
    boolean abonadosSeleccionado = checkAbonados.isSelected();

    // Crear la consulta SQL basada en la selección
    String query;
    if (abonadosSeleccionado) {
        query = "SELECT Nyap, edad, altura, peso, mesActual FROM Usuarios WHERE mesActual = 1"; // Solo abonados
    } else {
        query = "SELECT Nyap, edad, altura, peso, mesActual FROM Usuarios"; // Todos los usuarios
    }

    // Asegurarse de que la consulta no sea nula ni vacía
    if (query == null || query.trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Error: No se pudo generar la consulta.");
        return;
    }

    // Llamar al método que carga los datos en la tabla
    cargarDatosTabla(true, query);
    }//GEN-LAST:event_checkAbonadosActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
          // Verificar si el checkbox está seleccionado
    boolean abonadosNoSeleccionado = jCheckBox3.isSelected();

    // Crear la consulta SQL basada en la selección
    String query;
    if (abonadosNoSeleccionado) {
        query = "SELECT Nyap, edad, altura, peso, mesActual FROM Usuarios WHERE mesActual != 1"; // Solo abonados
    } else {
        query = "SELECT Nyap, edad, altura, peso, mesActual FROM Usuarios"; // Todos los usuarios
    }

    // Asegurarse de que la consulta no sea nula ni vacía
    if (query == null || query.trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Error: No se pudo generar la consulta.");
        return;
    }

    // Llamar al método que carga los datos en la tabla
    cargarDatosTabla(true, query);
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
       
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PresentFrameGym.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PresentFrameGym.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PresentFrameGym.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PresentFrameGym.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PresentFrameGym().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnSalir1;
    private javax.swing.JButton buttonOkselect;
    private javax.swing.JCheckBox checkAbonados;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelLegajo;
    private javax.swing.JLabel labelNombre;
    private javax.swing.JLabel labelRol;
    private javax.swing.JLabel labelUserIcon;
    private javax.swing.JTable tableUsuarios;
    // End of variables declaration//GEN-END:variables
}
