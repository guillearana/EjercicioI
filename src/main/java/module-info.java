
/**
 * Módulo principal para la aplicación EjercicioF, que gestiona y controla la información de personas.
 */
module es.guillearana.ejercicioh {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens es.guillearana.ejercicioi to javafx.fxml;
    exports es.guillearana.ejercicioi;
    exports es.guillearana.ejercicioi.controlador;
    opens es.guillearana.ejercicioi.controlador to javafx.fxml;
    opens es.guillearana.ejercicioi.model to javafx.fxml, javafx.base; // Permite acceso a clases del paquete model desde javafx.fxml y javafx.base.
}