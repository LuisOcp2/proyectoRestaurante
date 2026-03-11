// COMPARTIDO
module com.mosqueteros.proyecto_restaurante {

    // JavaFX 21
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // MaterialFX
    requires MaterialFX;

    // Ikonli (Iconos)
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.material2;

    // JDBC (MySQL)
    requires java.sql;

    // BCrypt
    requires jbcrypt;

    // ── Exports ──────────────────────────────────────────────────
    exports com.mosqueteros.proyecto_restaurante;
    exports com.mosqueteros.proyecto_restaurante.model;
    exports com.mosqueteros.proyecto_restaurante.dao;
    exports com.mosqueteros.proyecto_restaurante.controller;
    exports com.mosqueteros.proyecto_restaurante.util;

    // ── Abrir paquetes a JavaFX (reflexión para FXML) ─────────────
    opens com.mosqueteros.proyecto_restaurante to javafx.fxml;
    opens com.mosqueteros.proyecto_restaurante.controller to javafx.fxml;
    opens com.mosqueteros.proyecto_restaurante.model to javafx.base;
}

