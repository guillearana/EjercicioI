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

    /** Imagen de icono utilizada en la interfaz. */
    @FXML
    private ImageView imgIcon;

    /** Botón para cambiar el idioma de la interfaz. */
    @FXML
    private Button btnChangeLanguage;

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/guillearana/ejercicioi/ejerImodal.fxml"));
            Parent root = loader.load();
            ControllerModalEjerI controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Agregar Persona");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            Persona nuevaPersona = controller.getPersona();
            if (nuevaPersona != null) {
                PersonaDao personaDao = new PersonaDao();
                if (!personasData.contains(nuevaPersona)) {
                    try {
                        personaDao.aniadirPersona(nuevaPersona);
                        mostrarAlerta("Persona añadida con éxito.");
                        personasData.add(nuevaPersona);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        mostrarAlerta("Error al interactuar con la base de datos: " + e.getMessage());
                    }
                } else {
                    mostrarAlerta("La persona ya está en la tabla.");
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
        if (!personasData.isEmpty()) {
            Persona selected = tableInfo.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    PersonaDao personaDao = new PersonaDao();
                    personaDao.eliminarPersona(selected);
                    personasData.remove(selected);
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
                controller.setPersona(personaSeleccionada);

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Editar Persona");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                Persona personaModificada = controller.getPersona();
                if (personaModificada != null) {
                    PersonaDao personaDao = new PersonaDao();
                    personaDao.modificarPersona(personaModificada);

                    personaSeleccionada.setNombre(personaModificada.getNombre());
                    personaSeleccionada.setApellidos(personaModificada.getApellidos());
                    personaSeleccionada.setEdad(personaModificada.getEdad());
                    tableInfo.refresh();
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

        btnAgregar.setTooltip(new Tooltip("Agregar una nueva persona"));
        btnModificar.setTooltip(new Tooltip("Modificar la persona seleccionada"));
        btnEliminar.setTooltip(new Tooltip("Eliminar la persona seleccionada"));
        txtNombre.setTooltip(new Tooltip("Filtrar personas por nombre"));

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));

        colEdad.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : Integer.toString(item));
                setAlignment(Pos.CENTER_RIGHT);
            }
        });

        PersonaDao personaDao = new PersonaDao();
        ObservableList<Persona> personasCargadas = personaDao.cargarPersonas();
        personasData.addAll(personasCargadas);

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

        SortedList<Persona> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableInfo.comparatorProperty());
        tableInfo.setItems(sortedData);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem modifyItem = new MenuItem("Modificar");
        modifyItem.setOnAction(e -> {
            try {
                accionModificar(null);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        MenuItem deleteItem = new MenuItem("Eliminar");
        deleteItem.setOnAction(e -> accionEliminar(null));

        contextMenu.getItems().addAll(modifyItem, deleteItem);
        tableInfo.setContextMenu(contextMenu);
    }

    /**
     * Muestra una alerta con el mensaje proporcionado.
     *
     * @param mensaje el mensaje a mostrar en la alerta
     */
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setContentText(mensaje);
        alerta.show();
    }
}
