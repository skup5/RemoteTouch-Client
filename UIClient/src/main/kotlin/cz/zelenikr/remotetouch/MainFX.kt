package cz.zelenikr.remotetouch


import cz.zelenikr.remotetouch.controller.AppController
import cz.zelenikr.remotetouch.controller.settings.PairDeviceController
import cz.zelenikr.remotetouch.data.dto.UserInfo
import cz.zelenikr.remotetouch.dialog.ErrorDialog
import cz.zelenikr.remotetouch.dialog.LocalizedWizardPane
import cz.zelenikr.remotetouch.dialog.LoginDialog
import cz.zelenikr.remotetouch.dialog.RegisterDialog
import cz.zelenikr.remotetouch.manager.SecurityManager
import cz.zelenikr.remotetouch.manager.SettingsManager
import impl.org.controlsfx.skin.DecorationPane
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import org.controlsfx.dialog.Wizard
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * JavaFX application class. This is the entry point of the program.
 *
 * @author Roman Zelenik
 */
class MainFX : Application() {

    private val securityManager = SecurityManager.getInstance()

    private lateinit var settings: SettingsManager
    private lateinit var stage: Stage

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        var startMain = true

        stage = primaryStage
        stage.scene = Scene(Group(), 1.0, 1.0)
        stage.scene.stylesheets.addAll(*Resources.styleSheets)
        stage.icons.add(Resources.Icons.applicationIcon)

        val user = showLogin()
        if (user == null || user.password == null) {
            close()
            return
        } else {
            settings = SettingsManager.unlockInstance(user.password)
        }

        if (shouldShowWizard()) {
            startMain = showWizard()
        }

        if (startMain) {
            showMainWindow()
        } else {
            close()
        }
    }

    /**
     * Closes application.
     */
    private fun close() {
        stage.close()
    }

    /**
     * Checks if user has to enter initialization values like phone name or pair key.
     *
     * @return true if some init value is missing
     */
    private fun shouldShowWizard(): Boolean {
        return !settings.containsDeviceName() || !settings.containsPairKey()
    }

    /**
     * Initializes and makes the app main window visible to the user.
     *
     * @throws IOException if an error occurs during loading layout
     */
    @Throws(IOException::class)
    private fun showMainWindow() {
        val pair = Resources.loadView("view/app.fxml", STRINGS)
        val controller = pair.value as AppController

        stage.setOnHidden {
            controller.onClose()
            Platform.exit()
        }
        stage.title = STRINGS.getString(Resources.Strings.APPLICATION_TITLE)
        stage.scene = Scene(pair.key as Parent, MIN_WIDTH, MIN_HEIGHT)
        stage.minHeight = MIN_HEIGHT
        stage.minWidth = MIN_WIDTH
        stage.show()
    }

    /**
     * Shows to the user an app initialization wizard.
     *
     * @return true if wizard was successfully completed
     */
    @Throws(IOException::class)
    private fun showWizard(): Boolean {
        val success = AtomicBoolean(false)

        val owner = stage

        // createUrlValidator wizard
        val wizard = Wizard(owner, STRINGS.getString(Resources.Strings.APPLICATION_TITLE))

        // createUrlValidator pages
        val pairDeviceView = Resources.loadView("view/settings/pair_device.fxml", STRINGS)
        val pairDeviceController = pairDeviceView.value as PairDeviceController

        val content = DecorationPane()
        content.children.add(pairDeviceView.key)

        val page1 = object : LocalizedWizardPane() {
            override fun onEnteringPage(wizard: Wizard?) {
                super.onEnteringPage(wizard)
                val buttons = buttonTypes.filtered { buttonType -> buttonType.buttonData.compareTo(ButtonBar.ButtonData.BACK_PREVIOUS) == 0 }
                if (!buttons.isEmpty()) {
                    val backButton = lookupButton(buttons[0]) as Button
                    backButton.disabledProperty().addListener { _, _, newValue -> backButton.isVisible = !newValue }
                }
                wizard!!.invalidProperty().unbind()
                wizard.invalidProperty().bind(pairDeviceController.validationSupport.invalidProperty())
            }
        }
        page1.headerText = STRINGS.getString(Resources.Strings.WIZARD_PAIR_DEVICE_HEADER)
        page1.content = content

        wizard.flow = Wizard.LinearFlow(page1)

        //        System.out.println("page1: " + page1);

        // show wizard and wait for response
        wizard.showAndWait().ifPresent { result ->
            if (result == ButtonType.FINISH) {
                //System.out.println("Wizard finished, settings: " + wizard.getSettings());
                success.set(true)
            }
        }

        return success.get()
    }

    /**
     * Shows login dialog to unlock application. The dialog will show repeatedly if authentication failed.
     *
     * @return [UserInfo] with user input values or `null` if the dialog was canceled
     */
    private fun showLogin(): UserInfo? {
        var user: UserInfo? = null

        if (securityManager.existOwner()) {
            val loginDialog = LoginDialog(STRINGS.getString(Resources.Strings.APPLICATION_TITLE))
            loginDialog.dialogPane.stylesheets.addAll(*Resources.styleSheets)
            loginDialog.setOnAuthenticateCallback { userInfo -> userInfo != null && securityManager.authenticateOwner(userInfo) }

            var showAgain: Boolean
            do {
                val result = loginDialog.showAndWait()
                if (result.isPresent && result.get() == LoginDialog.BUTTON_LOGIN) {
                    if (loginDialog.isAuthenticated) {
                        user = loginDialog.user
                        showAgain = false
                    } else {
                        val errorDialog = ErrorDialog(
                                STRINGS.getString(Resources.Strings.APPLICATION_TITLE),
                                STRINGS.getString(Resources.Strings.LOGIN_ERROR_CONTENT))
                        errorDialog.setHeaderText(STRINGS.getString(Resources.Strings.LOGIN_ERROR_HEADER))
                        errorDialog.showAndWait()
                        showAgain = true
                    }

                    continue
                } else if (result.isPresent && result.get() == LoginDialog.BUTTON_RESET) {
                    securityManager.resetOwner()
                    user = showRegister()
                }

                showAgain = false

            } while (showAgain)

        } else {
            user = showRegister()
        }

        return user
    }

    /**
     * Shows register dialog to initialize application [owner][UserInfo].
     *
     * @return new registered [UserInfo] or `null` if the dialog was canceled
     */
    private fun showRegister(): UserInfo? {
        var user: UserInfo? = null
        val registerDialog = RegisterDialog(STRINGS.getString(Resources.Strings.APPLICATION_TITLE))
        registerDialog.dialogPane.stylesheets.addAll(*Resources.styleSheets)

        val result = registerDialog.showAndWait()
        if (result.isPresent && result.get() == RegisterDialog.BUTTON_CREATE) {
            user = registerDialog.user
            securityManager.createOwner(user!!)
        }
        return user
    }

    companion object {

        private const val MIN_WIDTH = 500.0
        private const val MIN_HEIGHT = 500.0

        private val STRINGS = Resources.loadStrings(SettingsManager.getLocale())

        @JvmStatic
        fun main(args: Array<String>) {
            Locale.setDefault(SettingsManager.getLocale())
            launch(MainFX::class.java, *args)
        }
    }
}