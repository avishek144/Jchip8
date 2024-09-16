import javax.swing.JFrame;
import javax.swing.JFileChooser;
import java.io.FileInputStream;

public class Start
{
    public static void main(String[] args)
    {
        var file_chooser = new JFileChooser();
        var start_frame = new JFrame("JCHIP8");

        Screen.setFrame(start_frame);

        file_chooser.setMultiSelectionEnabled(false);
        file_chooser.showOpenDialog(start_frame);
      
        try (var chip8_program = new FileInputStream(file_chooser.getSelectedFile())) {
            start_frame.setTitle("JCHIP8 - " + chip8_program.getName());
            int b = chip8_program.read();
            var address = 0x200;
            while (b > -1) {
                Memory.write(b, address);
                address = address + 1;
                b = chip8_program.read();
            }
        }
        catch (IOException chip8_program_reading_exception) {
            System.err.println(chip8_program_reading_exception);
        }

        try {
            Processor.run();
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }
}
