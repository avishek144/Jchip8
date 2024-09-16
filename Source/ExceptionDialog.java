import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class ExceptionDialog {
    static void show(Frame parent, Exception e)
    {
        var exception_dialog = new JDialog(parent, "Exception");

        var button_quit = new JButton("Quit");
        var button_load_another_rom = new JButton("Load another ROM");
        exception_dialog.add(new JLabel(e.toString()), BorderLayout.NORTH);
        exception_dialog.add(button_quit, BorderLayout.WEST);
        exception_dialog.add(button_load_another_rom, BorderLayout.EAST);
    }
}
