package es.guillearana.ejercicioi.controlador;

import es.guillearana.ejercicioi.model.Persona;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

/**
 * Controlador para la ventana modal de agregar o modificar una persona.
 * Esta clase permite al usuario ingresar o modificar los datos de una persona
 * y valida los datos antes de guardarlos.
 *
 * Se utiliza en conjunto con una interfaz gráfica de JavaFX para capturar los
 * datos de una persona, incluyendo nombre, apellidos y edad.
 */
public class ControllerModalEjerI {

    /** Botón que cancela la acción y cierra la ventana modal sin guardar cambios. */
    @FXML
    private Button btnCancelar;

    /** Botón que guarda la información ingresada en la ventana modal. */
    @FXML
    private Button btnGuardar;

    /** Campo de texto para ingresar o modificar los apellidos de la persona. */
    @FXML
    private TextField txtApellidos;

    /** Campo de texto para ingresar o modificar la edad de la persona. */
    @FXML
    private TextField txtEdad;

    /** Campo de texto para ingresar o modificar el nombre de la persona. */
    @FXML
    private TextField txtNombre;

    /** Objeto que almacena la persona creada o modificada en la ventana modal. */
    private Persona persona;

    /**
     * Cancela la operación de agregar o modificar y cierra la ventana modal.
     * Este método se ejecuta al hacer clic en el botón "Cancelar" y cierra
     * la ventana modal sin guardar cambios.
     *
     * @param event el evento de acción del botón "Cancelar"
     */
    @FXML
    void cancelarPersona(ActionEvent event) {
        cerrarVentana();
    }

    /**
     * Guarda los datos de la persona ingresada, validándolos previamente.
     * Este método se ejecuta al hacer clic en el botón "Guardar". Si los datos
     * son válidos, crea una nueva persona o modifica la existente, y luego
     * cierra la ventana modal.
     *
     * @param event el evento de acción del botón "Guardar"
     */
    @FXML
    void guardarPersona(ActionEvent event) {
        // Valida los datos antes de proceder
        if (datosValidos()) {
            String nombre = txtNombre.getText().trim();
            String apellidos = txtApellidos.getText().trim();
            int edad = Integer.parseInt(txtEdad.getText().trim());

            // Crea una nueva instancia de Persona si persona es null
            if (persona == null) {
                persona = new Persona(nombre, apellidos, edad);
                mostrarAlerta("Persona agregada con éxito.", Alert.AlertType.INFORMATION);
            } else {
                // Modifica la persona existente con los nuevos datos
                persona.setNombre(nombre);
                persona.setApellidos(apellidos);
                persona.setEdad(edad);
                mostrarAlerta("Persona guardada con éxito.", Alert.AlertType.INFORMATION);
            }
            cerrarVentana();
        }
    }

    /**
     * Cierra la ventana modal.
     * Este método se utiliza para cerrar la ventana de diálogo sin realizar
     * ninguna acción adicional.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    /**
     * Devuelve la persona creada o modificada en la ventana modal.
     * Se utiliza para recuperar los datos de la persona una vez que la ventana
     * ha sido cerrada.
     *
     * @return la persona creada/modificada o null si no se ha creado
     */
    public Persona getPersona() {
        return persona;
    }

    /**
     * Configura la persona seleccionada para su modificación.
     * Este método permite rellenar los campos de texto con los datos de una
     * persona previamente seleccionada para que puedan ser modificados.
     *
     * @param personaSeleccionada la persona seleccionada para modificar
     */
    public void setPersona(Persona personaSeleccionada) {
        this.persona = personaSeleccionada;
        if (personaSeleccionada != null) {
            txtNombre.setText(personaSeleccionada.getNombre());
            txtApellidos.setText(personaSeleccionada.getApellidos());
            txtEdad.setText(String.valueOf(personaSeleccionada.getEdad()));
        }
    }

    /**
     * Valida los datos ingresados en los campos de texto.
     * Comprueba que el nombre y los apellidos no estén vacíos y que la edad sea
     * un número positivo. Si se detectan errores, muestra una alerta con los
     * mensajes de error.
     *
     * @return true si los datos son válidos; false si hay errores
     */
    private boolean datosValidos() {
        StringBuilder errores = new StringBuilder();

        // Validar que el nombre no esté vacío
        if (txtNombre.getText().trim().isEmpty()) {
            errores.append("El nombre no puede estar vacío.\n");
        }

        // Validar que los apellidos no estén vacíos
        if (txtApellidos.getText().trim().isEmpty()) {
            errores.append("Los apellidos no pueden estar vacíos.\n");
        }

        // Validar que la edad esté presente y sea un número válido
        String edadTexto = txtEdad.getText().trim();
        if (edadTexto.isEmpty()) {
            errores.append("La edad no puede estar vacía.\n");
        } else {
            try {
                int edad = Integer.parseInt(edadTexto);
                if (edad < 0) {
                    errores.append("La edad no puede ser negativa.\n");
                }
            } catch (NumberFormatException e) {
                errores.append("La edad debe ser un número válido.\n");
            }
        }

        // Mostrar errores si existen
        if (errores.length() > 0) {
            mostrarAlerta(errores.toString(), Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    /**
     * Muestra una alerta informativa al usuario.
     * Este método se utiliza para notificar al usuario sobre errores o
     * confirmaciones de acciones realizadas.
     *
     * @param mensaje el mensaje a mostrar en la alerta
     * @param tipo el tipo de alerta (INFORMATION, ERROR, etc.)
     */
    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
