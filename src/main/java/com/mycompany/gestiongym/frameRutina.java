/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.gestiongym;


import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.Statement;
import com.mycompany.gestiongym.PresentFrameGym.ClaseId;






/**
 *
 * @author daniel
 */
public class frameRutina extends javax.swing.JFrame {
    
    
    private Connection connection;
    public  int userIdSelec = com.mycompany.gestiongym.PresentFrameGym.ClaseId.idRequerido;
    private int idProveniente;
    private boolean enProcesoDeEdicion = false;

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
    private void cargarTiposRutina() {
    String query = "SELECT nombre FROM tiposRutina";

    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(query)) {

        // Limpiar el comboBox antes de llenarlo
        comboTipoRutina.removeAllItems();

        // Llenar el comboBox con los nombres
        while (resultSet.next()) {
            comboTipoRutina.addItem(resultSet.getString("nombre"));
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al cargar tipos de rutina: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
private void guardarTipoRutina() {
    if (connection == null) {
        JOptionPane.showMessageDialog(null, "No hay conexión a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String tipoRutinaSeleccionado = (String) comboTipoRutina.getSelectedItem();

    if (tipoRutinaSeleccionado == null || tipoRutinaSeleccionado.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Selecciona un tipo de rutina.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String queryGetId = "SELECT id FROM tiposRutina WHERE nombre = ?";
    String queryUpdateUsuario = "UPDATE Usuarios SET tipoRutinaId = ? WHERE id = ?";

    try (
        PreparedStatement statementGetId = connection.prepareStatement(queryGetId);
        PreparedStatement statementUpdateUsuario = connection.prepareStatement(queryUpdateUsuario)
    ) {
        // Obtener el ID del tipo de rutina seleccionado
        statementGetId.setString(1, tipoRutinaSeleccionado);
        ResultSet resultSet = statementGetId.executeQuery();

        if (resultSet.next()) {
            int idTipoRutina = resultSet.getInt("id");

            // Aquí debes obtener el ID del usuario al que deseas asignar el tipo de rutina
            int idUsuario = userIdSelec; // Cambia este valor dinámicamente según el usuario actual

            // Actualizar el campo tipoRutinaId en la tabla Usuarios
            statementUpdateUsuario.setInt(1, idTipoRutina);
            statementUpdateUsuario.setInt(2, idUsuario);

            int rowsUpdated = statementUpdateUsuario.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "El tipo de rutina '" + tipoRutinaSeleccionado + "' fue asignado correctamente al usuario.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el usuario especificado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró el tipo de rutina seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al asignar el tipo de rutina: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}



    
 private void mostrarDatosUsuario() {
    // Consulta para obtener edad, altura y peso por el ID del usuario
    String query = "SELECT Nyap, edad, altura, peso, tipoRutinaId FROM Usuarios WHERE id = ?";

    try (PreparedStatement statement = connection.prepareStatement(query)) {
        // Asignar el ID del usuario al parámetro de la consulta
        System.out.println("el ID USADO ES: "+ userIdSelec);
        statement.setInt(1, userIdSelec);

        // Ejecutar la consulta
        ResultSet resultSet = statement.executeQuery();

        // Verificar si hay datos
        if (resultSet.next()) {
            // Obtener datos de la base de datos
            String nombre = resultSet.getString("Nyap");
            int edad = resultSet.getInt("edad");
            int alturaCm = resultSet.getInt("altura"); // Altura en cm
            int peso = resultSet.getInt("peso");
            int tipoRutina = resultSet.getInt("tipoRutinaId");// Peso en kg

            // Convertir altura de cm a metros
            double alturaM = alturaCm / 100.0;
            String nombreRutina = "";

            // Calcular IMC
            double imc = (alturaM > 0) ? (peso / (alturaM * alturaM)) : 0;
            double imcRedondeado = Math.round(imc * 10.0) / 10.0;
            if (tipoRutina ==1){
                nombreRutina = "Musculación";
            }else if (tipoRutina ==2){nombreRutina = "Definición";}

            // Mostrar los datos en los labels
            labelUsuario.setText("Usuario: "+ nombre);
            labelEdadFR.setText("Edad: " + edad );
            labelAlturaFR.setText("Altura: " + alturaCm + "cm");
            labelPesoFR.setText("Peso: " + peso + "kg");
            labelImcFR.setText(String.format("IMC: " + imcRedondeado));
            labelTipoRutina.setText(String.format("Tipo rutina: "+  nombreRutina));
        } else {
            // Si no se encuentra el usuario
            JOptionPane.showMessageDialog(null, "Usuario no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al recuperar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


private void cargarIcono() {
    // Ruta relativa de la imagen en tu proyecto
    String ruta = "src/imagen/pdf.png"; // Ajusta esta ruta según la ubicación real de tu imagen

    // Crear el ImageIcon desde la ruta
    ImageIcon icono = new ImageIcon(ruta);

    // Redimensionar la imagen al tamaño del botón
    Image imagen = icono.getImage().getScaledInstance(jButton2.getWidth(), jButton2.getHeight(), Image.SCALE_SMOOTH);

    // Establecer la imagen redimensionada como ícono del botón
    jButton2.setIcon(new ImageIcon(imagen));
}
private void cargarIconoW() {
    // Ruta relativa de la imagen en tu proyecto
    String ruta = "src/imagen/social.png"; // Ajusta esta ruta según la ubicación real de tu imagen

    // Crear el ImageIcon desde la ruta
    ImageIcon icono = new ImageIcon(ruta);

    // Redimensionar la imagen al tamaño del botón
    Image imagen = icono.getImage().getScaledInstance(btnWha.getWidth(), btnWha.getHeight(), Image.SCALE_SMOOTH);

    // Establecer la imagen redimensionada como ícono del botón
    btnWha.setIcon(new ImageIcon(imagen));
}




public class CustomCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Llamar al renderizador predeterminado
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Restablecer el color de fondo predeterminado
        if (isSelected) {
            cell.setBackground(table.getSelectionBackground()); // Fondo de selección predeterminado
            cell.setForeground(table.getSelectionForeground()); // Texto de selección predeterminado
        } else {
            cell.setBackground(new Color(102, 102, 102)); // Fondo gris oscuro
            cell.setForeground(new Color(0,204,204));  // Texto verde fosforescente
        }

       

        // Dibujar bordes personalizados
        if (cell instanceof JLabel) {
            JLabel label = (JLabel) cell;
            label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK)); // Bordes en todos los lados
            label.setHorizontalAlignment(SwingConstants.CENTER); // Centrar el contenido
        }

        return cell;
    }
}



private void configurarModeloEditable() {
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

    model.addTableModelListener(e -> {
        if (enProcesoDeEdicion) {
            return; // Ignorar eventos mientras se procesa otro
        }

        int row = e.getFirstRow();
        int col = e.getColumn();

        if (row < 0 || col < 0) {
            return; // Índice inválido
        }

        String nuevoValor = (String) model.getValueAt(row, col);

        // Deshabilitar el listener temporalmente
        enProcesoDeEdicion = true;

        try {
            int key = row * 7 + col;

            if (nuevoValor == null || nuevoValor.trim().isEmpty()) {
                // Si la celda está vacía, eliminar el contenido
                if (mapIdRutinas.containsKey(key)) {
                    eliminarEjercicio(row, col);
                }
            } else {
                // Actualizar o registrar el ejercicio
                if (mapIdRutinas.containsKey(key)) {
                    modificarEjercicio(row, col, nuevoValor);
                } else {
                    registrarEjercicio(col + 1, nuevoValor);
                }
            }
        } finally {
            enProcesoDeEdicion = false;
        }
    });
}
private void eliminarEjercicio(int row, int col) {
    int key = row * 7 + col;
    Integer idRutina = mapIdRutinas.get(key);

    if (idRutina == null) {
        JOptionPane.showMessageDialog(null, "No se encontró el ejercicio para eliminar.");
        return;
    }

    String deleteQuery = "DELETE FROM Rutinas WHERE id = ?";
    try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
        statement.setInt(1, idRutina);

        int rowsDeleted = statement.executeUpdate();
        if (rowsDeleted > 0) {
            JOptionPane.showMessageDialog(null, "Ejercicio eliminado correctamente.");

            // Reflejar el cambio en la tabla gráfica
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setValueAt("", row, col);

            // Remover el ID del mapa
            mapIdRutinas.remove(key);
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo eliminar el ejercicio. Verifique los datos.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al eliminar el ejercicio: " + e.getMessage());
    }
}
private void agregarMenuContextual() {
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem eliminarItem = new JMenuItem("Eliminar contenido");

    eliminarItem.addActionListener(e -> {
        int row = jTable1.getSelectedRow();
        int col = jTable1.getSelectedColumn();

        if (row < 0 || col < 0) {
            JOptionPane.showMessageDialog(null, "Selecciona una celda primero.");
            return;
        }

        int key = row * 7 + col;
        if (mapIdRutinas.containsKey(key)) {
            eliminarEjercicio(row, col);
        } else {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setValueAt("", row, col);
        }
    });

    popupMenu.add(eliminarItem);
    jTable1.setComponentPopupMenu(popupMenu);
}

private void limpiarCeldaRegistro(int diaSemana) {
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    int col = diaSemana - 1; // Determinar la columna correspondiente al día (1-7 → 0-6)

    // Buscar la primera celda no vacía en la columna y vaciarla
    for (int i = 0; i < model.getRowCount(); i++) {
        Object valor = model.getValueAt(i, col);
        if (valor != null && !valor.toString().trim().isEmpty()) {
            model.setValueAt("", i, col); // Vaciar la celda
            return;
        }
    }
}

private void registrarEjercicio(int diaSemana, String nombreEjercicio) {
    String query = "INSERT INTO Rutinas (idUsuario, idDiaSemana, nombreEjercicio) VALUES (?, ?, ?)";

    try (PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, userIdSelec); // Reemplaza con el ID del usuario actual
        statement.setInt(2, diaSemana);
        statement.setString(3, nombreEjercicio.trim());

        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "Ejercicio registrado correctamente.");

            // Actualizar la tabla gráfica
            actualizarTabla(diaSemana, nombreEjercicio);

            // Limpiar la celda utilizada para el registro
            limpiarCeldaRegistro(diaSemana);

            // Asegurar que exista una sola fila vacía al final
            gestionarFilaVacía(diaSemana);
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo registrar el ejercicio. Intenta de nuevo.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al registrar el ejercicio: " + e.getMessage());
    }
}

private void verificarYAgregarFilaVacía() {
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

    // Verificar si ya existe una fila completamente vacía al final de la tabla
    int lastRow = model.getRowCount() - 1;
    boolean filaVacía = true;

    if (lastRow >= 0) {
        for (int i = 0; i < model.getColumnCount(); i++) {
            Object valor = model.getValueAt(lastRow, i);
            if (valor != null && !valor.toString().trim().isEmpty()) {
                filaVacía = false;
                break;
            }
        }
    }

    // Si no hay una fila vacía al final, agrega una nueva
    if (!filaVacía || lastRow < 0) {
        Object[] nuevaFila = new Object[7]; // Fila vacía con 7 columnas
        model.addRow(nuevaFila);
    }
}



private boolean validarUsuario(int idUsuario) {
    String query = "SELECT COUNT(*) FROM Usuarios WHERE id = ?";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, idUsuario);
        ResultSet rs = statement.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            return true; // Usuario válido
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al validar el usuario: " + e.getMessage());
    }
    return false; // Usuario no válido
}


private void actualizarTabla(int diaSemana, String nombreEjercicio) {
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

    // Determinar la columna correspondiente al día
    int col = diaSemana - 1; // Convertir el día de la semana (1-7) a índice de columna (0-6)

    // Buscar la primera fila vacía en esa columna
    for (int i = 0; i < model.getRowCount(); i++) {
        Object valor = model.getValueAt(i, col);
        if (valor == null || valor.toString().trim().isEmpty()) {
            // Actualizar la celda vacía con el nuevo ejercicio
            model.setValueAt(nombreEjercicio, i, col);
            return;
        }
    }
}



private void modificarEjercicio(int row, int col, String nuevoEjercicio) {
    // Obtener el ID de la rutina basado en la fila y la columna
    int key = row * 7 + col;
    Integer idRutina = mapIdRutinas.get(key);

    if (idRutina == null) {
        JOptionPane.showMessageDialog(null, "No se pudo encontrar la rutina para modificar.");
        return;
    }

    String updateQuery = "UPDATE Rutinas SET nombreEjercicio = ? WHERE id = ?";
    try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
        statement.setString(1, nuevoEjercicio.trim());
        statement.setInt(2, idRutina);

        int rowsUpdated = statement.executeUpdate();
        if (rowsUpdated > 0) {
            JOptionPane.showMessageDialog(null, "Ejercicio modificado correctamente.");

            // Reflejar el cambio en la tabla gráfica
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setValueAt(nuevoEjercicio, row, col);
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo modificar el ejercicio. Verifique los datos.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al modificar el ejercicio: " + e.getMessage());
    }
}

private void gestionarFilaVacía(int diaSemana) {
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

    // Verificar si ya existe una fila completamente vacía
    int lastRow = model.getRowCount() - 1;
    boolean filaCompletamenteVacía = true;

    if (lastRow >= 0) {
        for (int i = 0; i < model.getColumnCount(); i++) {
            Object valor = model.getValueAt(lastRow, i);
            if (valor != null && !valor.toString().trim().isEmpty()) {
                filaCompletamenteVacía = false;
                break;
            }
        }
    }

    // Si la última fila está llena o no existe, agregar una nueva fila vacía
    if (!filaCompletamenteVacía || lastRow < 0) {
        Object[] nuevaFila = new Object[7]; // Fila vacía con 7 columnas
        model.addRow(nuevaFila);
    }
}



private void agregarNuevaFilaVacía(int diaSemana) {
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

    // Determinar la columna correspondiente al día (0-6)
    int columnIndex = diaSemana - 1; // Día (1-7) a índice de columna (0-6)

    // Buscar si ya hay una fila completamente vacía en esa columna
    boolean existeFilaVacía = false;
    for (int i = 0; i < model.getRowCount(); i++) {
        if (model.getValueAt(i, columnIndex) == null || model.getValueAt(i, columnIndex).toString().trim().isEmpty()) {
            existeFilaVacía = true;
            break;
        }
    }

    // Si no hay fila vacía para este día, agrega una nueva
    if (!existeFilaVacía) {
        Object[] nuevaFila = new Object[7]; // Fila vacía con 7 columnas
        model.addRow(nuevaFila);
    }
}


// Mapa para almacenar los IDs de los ejercicios
private Map<Integer, Integer> mapIdRutinas = new HashMap<>();

private void cargarRutinas(int userIdSelec) {
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Lunes");
    model.addColumn("Martes");
    model.addColumn("Miércoles");
    model.addColumn("Jueves");
    model.addColumn("Viernes");
    model.addColumn("Sábado");
    model.addColumn("Domingo");

    // Limpiar el mapa
    mapIdRutinas.clear();

    String query = "SELECT id, idDiaSemana, nombreEjercicio FROM Rutinas WHERE idUsuario = ? ORDER BY idDiaSemana";

    try (PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, userIdSelec);

        ResultSet resultSet = statement.executeQuery();

        List<List<String>> ejerciciosPorDia = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            ejerciciosPorDia.add(new ArrayList<>());
        }

        // Procesar los resultados
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            int diaSemana = resultSet.getInt("idDiaSemana") - 1; // Índice basado en 0
            String ejercicio = resultSet.getString("nombreEjercicio");

            // Validar día de la semana antes de agregar
            if (diaSemana >= 0 && diaSemana < 7) {
                ejerciciosPorDia.get(diaSemana).add(ejercicio);

                // Asociar el ID de la rutina con la fila actual
                int fila = ejerciciosPorDia.get(diaSemana).size() - 1;
                mapIdRutinas.put(fila * 7 + diaSemana, id);
            } else {
                System.err.println("Día de la semana inválido: " + diaSemana);
            }
        }

        // Determinar el número máximo de filas necesarias
        int maxFilas = ejerciciosPorDia.stream().mapToInt(List::size).max().orElse(0);

        // Llenar el modelo de la tabla
        for (int i = 0; i < maxFilas; i++) {
            Object[] fila = new Object[7];
            for (int j = 0; j < 7; j++) {
                fila[j] = i < ejerciciosPorDia.get(j).size() ? ejerciciosPorDia.get(j).get(i) : null;
            }
            model.addRow(fila);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al cargar las rutinas: " + e.getMessage());
    }

    // Asignar el modelo a la tabla
    jTable1.setModel(model);
}



private String getNombreDia(int dia) {
    switch (dia) {
        case 1: return "Lunes";
        case 2: return "Martes";
        case 3: return "Miércoles";
        case 4: return "Jueves";
        case 5: return "Viernes";
        case 6: return "Sábado";
        case 7: return "Domingo";
        default: return "Día desconocido";
    }
}


    /**
     * Creates new form frameRutina
     */
    public frameRutina() {
        initComponents();
    connectToDatabase();
    
    cargarRutinas(userIdSelec); // Cargar rutinas del usuario actual
    configurarModeloEditable();
    gestionarFilaVacía(1);
    CustomCellRenderer customRenderer = new CustomCellRenderer();
    for (int i = 0; i < jTable1.getColumnCount(); i++) {
        jTable1.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
    }
     mostrarDatosUsuario();
     cargarTiposRutina();
    
    cargarIcono();
    cargarIconoW(); }
    
    // Aplicar el renderizador personalizado después de initComponents


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelUsuario = new javax.swing.JLabel();
        labelEdadFR = new javax.swing.JLabel();
        labelAlturaFR = new javax.swing.JLabel();
        labelPesoFR = new javax.swing.JLabel();
        labelImcFR = new javax.swing.JLabel();
        labelTipoRutina = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnUpdateFR = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        btnWha = new javax.swing.JButton();
        comboTipoRutina = new javax.swing.JComboBox<>();
        btnGuardarTipoRutina = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 51, 51));

        labelUsuario.setFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
        labelUsuario.setText("Usuario:");

        labelEdadFR.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        labelEdadFR.setText("Edad:");

        labelAlturaFR.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        labelAlturaFR.setText("Altura:");

        labelPesoFR.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        labelPesoFR.setText("Peso:");

        labelImcFR.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        labelImcFR.setText("IMC:");

        labelTipoRutina.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        labelTipoRutina.setText("Tipo Rutina: ");

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setBackground(new java.awt.Color(102, 102, 102));
        jTable1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jTable1.setForeground(new java.awt.Color(0, 204, 204));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        btnUpdateFR.setBackground(new java.awt.Color(70, 130, 180));
        btnUpdateFR.setText("Actualizar");
        btnUpdateFR.setAlignmentX(0.5F);
        btnUpdateFR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateFRActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(255, 51, 51));
        jButton1.setText("Cerrar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        comboTipoRutina.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnGuardarTipoRutina.setText("Guardar");
        btnGuardarTipoRutina.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnGuardarTipoRutina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarTipoRutinaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnUpdateFR, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(btnWha)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(31, 31, 31)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelTipoRutina)
                    .addComponent(labelUsuario)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelEdadFR, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(114, 114, 114)
                        .addComponent(labelAlturaFR)
                        .addGap(223, 223, 223)
                        .addComponent(labelPesoFR)
                        .addGap(208, 208, 208)
                        .addComponent(labelImcFR))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(comboTipoRutina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(btnGuardarTipoRutina)))
                .addContainerGap(267, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(labelUsuario)
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelEdadFR)
                    .addComponent(labelAlturaFR)
                    .addComponent(labelPesoFR)
                    .addComponent(labelImcFR))
                .addGap(29, 29, 29)
                .addComponent(labelTipoRutina)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboTipoRutina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardarTipoRutina))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 104, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnUpdateFR, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .addComponent(btnWha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateFRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateFRActionPerformed
            cargarRutinas(userIdSelec);
            configurarModeloEditable();
            gestionarFilaVacía(1);
            CustomCellRenderer customRenderer = new CustomCellRenderer();
    for (int i = 0; i < jTable1.getColumnCount(); i++) {
        jTable1.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
    }
    }//GEN-LAST:event_btnUpdateFRActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnGuardarTipoRutinaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarTipoRutinaActionPerformed
        guardarTipoRutina();
    }//GEN-LAST:event_btnGuardarTipoRutinaActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
              this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(frameRutina.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frameRutina.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frameRutina.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frameRutina.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frameRutina().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardarTipoRutina;
    private javax.swing.JButton btnUpdateFR;
    private javax.swing.JButton btnWha;
    private javax.swing.JComboBox<String> comboTipoRutina;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel labelAlturaFR;
    private javax.swing.JLabel labelEdadFR;
    private javax.swing.JLabel labelImcFR;
    private javax.swing.JLabel labelPesoFR;
    private javax.swing.JLabel labelTipoRutina;
    private javax.swing.JLabel labelUsuario;
    // End of variables declaration//GEN-END:variables
}
