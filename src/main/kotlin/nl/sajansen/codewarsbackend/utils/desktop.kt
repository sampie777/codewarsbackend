import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.net.URI

private val logger = LoggerFactory.getLogger("utils.desktop")

fun copyString(string: String) {
    logger.info("Copying text to clipboard")
    val stringSelection = StringSelection(string)
    val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(stringSelection, stringSelection)
}

fun pasteText(sendPasteDelayMs: Int = 100) {
    logger.info("Pasting text from clipboard")
    val robot = Robot()
    robot.delay(sendPasteDelayMs)
    robot.keyPress(KeyEvent.VK_CONTROL)
    robot.keyPress(KeyEvent.VK_V)
    robot.keyRelease(KeyEvent.VK_V)
    robot.keyRelease(KeyEvent.VK_CONTROL)
    robot.delay(100)
    robot.keyPress(KeyEvent.VK_ENTER)
    robot.keyRelease(KeyEvent.VK_ENTER)
}

fun openWebURL(url: String): Boolean {
    if (!Desktop.isDesktopSupported()) {
        logger.warn("Cannot open link '$url': not supported by host")
        return false
    }
    try {
        Desktop.getDesktop().browse(URI(url))
        return true
    } catch (t: Throwable) {
        logger.error("Error during opening link '$url'")
        t.printStackTrace()
    }
    return false
}