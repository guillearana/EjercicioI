package es.guillearana.ejercicioi.controlador;

import java.io.IOException;
import java.sql.SQLException;

import es.guillearana.ejercicioi.conexion.ConexionBD;
import es.guillearana.ejercicioi.dao.PersonaDao;
import es.guillearana.ejercicioi.model.Persona;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

/**
 * Controlador para la gestión de la vista principal de la aplicación de personas.
 * Este controlador maneja la lógica para agregar, modificar, eliminar, exportar e
 * importar personas en una lista, y actualiza la vista de la tabla.
 */
public class EjercicioIcontroller {

    /** Botón para agregar una nueva persona. */
    @FXML
    private Button btnAgregar;

    /** Botón para eliminar una persona seleccionada. */
    @FXML
    private Button btnEliminar;

    /** Botón para modificar una persona seleccionada. */
    @FXML
    private Button btnModificar;

    /** Columna de la tabla que muestra los apellidos de las personas. */
    @FXML
    private TableColumn<Persona, String> colApellidos;

    /** Columna de la tabla que muestra la edad de las personas. */
    @FXML
    private TableColumn<Persona, Integer> colEdad;

    /** Columna de la tabla que muestra el nombre de las personas. */
    @FXML
    private TableColumn<Persona, String> colNombre;

    /** Tabla para mostrar la información de las personas. */
    @FXML
    private TableView<Persona> tableInfo;

    /** Campo de texto para filtrar personas por nombre. */
    @FXML
    private TextField txtNombre;

    @FXML
    private ImageView imgIcon;

    @FXML
    private Button btnChangeLanguage; // Para referenciar el botón en el controlador

    // Crear tooltips
    Tooltip tooltipAgregar = new Tooltip("Agregar una nueva persona");
    Tooltip tooltipModificar = new Tooltip("Modificar la persona seleccionada");
    Tooltip tooltipEliminar = new Tooltip("Eliminar la persona seleccionada");

    /** Conexión a la base de datos. */
    private ConexionBD conexion = new ConexionBD();

    /** Lista observable que contiene las personas. */
    private ObservableList<Persona> personasData = FXCollections.observableArrayList();

    /**
     * Constructor del controlador. Se lanza una excepción SQLException.
     *
     * @throws SQLException si ocurre un error al establecer la conexión a la base de datos
     */
    public EjercicioIcontroller() throws SQLException {
    }

    /**
     * Acción para agregar una nueva persona.
     * Abre una ventana modal para ingresar los datos de la nueva persona
     * y la agrega a la lista si es válida.
     *
     * @param event el evento de acción del botón "Agregar"
     * @throws ClassNotFoundException si la clase no se encuentra durante la carga del FXML
     */
    @FXML
    void accionAgregar(ActionEvent event) throws ClassNotFoundException {
        // Abre la ventana modal para ingresar los detalles de la nueva persona
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/guillearana/ejercicioi/ejerImodal.fxml"));
            Parent root = loader.load();
            ControllerModalEjerI controller = loader.getController();

            // Configuración de la ventana modal
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false); // La ventana no es redimensionable
            stage.setTitle("Agregar Persona");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Bloquea la ventana principal hasta que la modal se cierre

            // Recuperamos la persona que hemos creado gracias al método getPersona situado
            // en el controlador modal
            Persona nuevaPersona = controller.getPersona();

            // Si la persona que recuperamos de la ventana modal no es null
            if (nuevaPersona != null) {
                PersonaDao personaDao = new PersonaDao(); // Crear una instancia de PersonaDao
                if (!personasData.contains(nuevaPersona)) { // Comprobamos que no exista ya
                    try {
                        personaDao.aniadirPersona(nuevaPersona); // Llamar al método para añadir la persona
                        mostrarAlerta("Persona añadida con éxito.");
                        personasData.add(nuevaPersona); // Añadir la persona a la lista
                    } catch (SQLException e) {
                        e.printStackTrace();
                        mostrarAlerta("Error al interactuar con la base de datos: " + e.getMessage());
                    }
                } else {
                    mostrarAlerta("La persona ya está en la tabla."); // Alerta si ya existe
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Acción para eliminar una persona seleccionada.
     * Muestra una alerta cuando se elimina una persona.
     *
     * @param event el evento de acción del botón "Eliminar"
     */
    @FXML
    void accionEliminar(ActionEvent event) {
        if (!personasData.isEmpty()) { // Asegúrate de que la lista no esté vacía.
            Persona selected = tableInfo.getSelectionModel().getSelectedItem(); // Obtener la persona seleccionada
            if (selected != null) {
                try {
                    PersonaDao personaDao = new PersonaDao(); // Crear una instancia de PersonaDao
                    personaDao.eliminarPersona(selected); // Llama al método eliminar de PersonaDao.

                    personasData.remove(selected); // Elimina de la lista local.
                    mostrarAlerta("Persona eliminada con éxito");
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Error al interactuar con la base de datos: " + e.getMessage());
                }
            } else {
                mostrarAlerta("No se ha seleccionado ninguna persona para eliminar.");
            }
        } else {
            mostrarAlerta("La lista de personas está vacía.");
        }
    }

    /**
     * Acción para modificar una persona seleccionada en la tabla.
     * Abre una ventana modal para editar los datos de la persona seleccionada.
     *
     * @param event el evento de acción del botón "Modificar"
     * @throws ClassNotFoundException si la clase no se encuentra durante la carga del FXML
     */
    @FXML
    void accionModificar(ActionEvent event) throws ClassNotFoundException {
        Persona personaSeleccionada = tableInfo.getSelectionModel().getSelectedItem();
        if (personaSeleccionada != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/guillearana/ejercicioi/ejerImodal.fxml"));
                Parent root = loader.load();
                ControllerModalEjerI controller = loader.getController();
                controller.setPersona(personaSeleccionada); // Rellenar campos con los datos de la persona seleccionada

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Editar Persona");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                Persona personaModificada = controller.getPersona();
                if (personaModificada != null) {
                    // Usar PersonaDao para modificar la persona en la base de datos
                    PersonaDao personaDao = new PersonaDao();
                    personaDao.modificarPersona(personaModificada); // Llamar al método modificar en PersonaDao

                    // Actualizar la lista local si la modificación fue exitosa
                    personaSeleccionada.setNombre(personaModificada.getNombre());
                    personaSeleccionada.setApellidos(personaModificada.getApellidos());
                    personaSeleccionada.setEdad(personaModificada.getEdad());
                    tableInfo.refresh(); // Refrescar la tabla para mostrar los cambios
                    mostrarAlerta("Persona modificada con éxito");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error al interactuar con la base de datos: " + e.getMessage());
            }
        } else {
            mostrarAlerta("No se ha seleccionado ninguna persona para modificar.");
        }
    }

    /**
     * Inicializa el controlador configurando las columnas de la tabla y el filtro de búsqueda.
     *
     * @throws SQLException si ocurre un error al cargar los datos de la base de datos
     */
    public void initialize() throws SQLException {
        Image image = new Image(getClass().getResourceAsStream("/es/guillearana/ejercicioi/imagenes/icono.png"));
        imgIcon.setImage(image);

        // Asignar Tooltips a los botones y al campo de texto
        btnAgregar.setTooltip(new Tooltip("Agregar una nueva persona"));
        btnModificar.setTooltip(new Tooltip("Modificar la persona seleccionada"));
        btnEliminar.setTooltip(new Tooltip("Eliminar la persona seleccionada"));
        txtNombre.setTooltip(new Tooltip("Filtrar personas por nombre"));

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));

        // Centrar el texto de la columna de edad
        colEdad.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : Integer.toString(item));
                setAlignment(Pos.CENTER_RIGHT); // Alineación a la derecha
            }
        });

        // Cargar personas desde la base de datos
        PersonaDao personaDao = new PersonaDao();
        ObservableList<Persona> personasCargadas = personaDao.cargarPersonas();
        personasData.addAll(personasCargadas);

        // Filtrado de la lista de personas por nombre
        FilteredList<Persona> filteredData = new FilteredList<>(personasData, p -> true);
        txtNombre.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(persona -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return persona.getNombre().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Vincular la tabla con la lista filtrada
        SortedList<Persona> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableInfo.comparatorProperty());
        tableInfo.setItems(sortedData);

        // Crear el menú contextual con opciones de "Modificar" y "Eliminar"
        ContextMenu contextMenu = new ContextMenu();

        MenuItem modifyItem = new MenuItem("Modificar");
        modifyItem.setOnAction(event -> {
            try {
                accionModificar(new ActionEvent());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        MenuItem deleteItem = new MenuItem("Eliminar");
        deleteItem.setOnAction(event -> accionEliminar(new ActionEvent()));

        contextMenu.getItems().addAll(modifyItem, deleteItem);

        // Asignar el menú contextual a cada fila de la tabla
        tableInfo.setRowFactory(tv -> {
            TableRow<Persona> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    tableInfo.getSelectionModel().select(row.getItem()); // Seleccionar la fila
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }


    /**
     * Muestra un mensaje de alerta.
     *
     * @param mensaje el mensaje a mostrar en la alerta
     */
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
