package application

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType

object UIHandler {

    var application: JavaFXApplication? = null

    fun confirm(message: String): Boolean = application?.confirmation(message) ?: false

    fun message(message: String) {
        application?.alert(Alert.AlertType.INFORMATION, message, ButtonType.OK)
    }

    fun error(message: String) {
        application?.alert(Alert.AlertType.ERROR, message, ButtonType.OK)
    }
}