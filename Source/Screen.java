import java.awt.Color;
import javax.swing.JPanel;

public abstract class Screen
{
    private static int frame_height = 400, frame_width = 900;
    private static final int CHIP8_DISPLAY_HEIGHT = 32, CHIP8_DISPLAY_WIDTH = 64;
    private static char[][] buffer = new char[CHIP8_DISPLAY_HEIGHT][CHIP8_DISPLAY_WIDTH];

    static JPanel initialize()
    {
        var display = new Frame("jCHIP8");
        var menu_bar = new MenuBar();
        menu_bar.add(new Menu("File"));
        menu_bar.add(new Menu("Options"));
        menu_bar.add(new Menu("Help"));

        display.setMenuBar(menu_bar);
        display.setBackground(Color.black);
        display.setSize(frame_width, frame_height);
        display.setVisible(true);

        return display;
    }

    static int draw(int x, int y, int address, int size)
    {
        var collision = 0;

        update();
        return collision;
    }

    static void clear()
    {
        for (var row = 0; row < CHIP8_DISPLAY_HEIGHT; ++row)
            for (var column = 0; column < CHIP8_DISPLAY_WIDTH; ++column)
                buffer[row][column] = '0';
        update();
    }

    static void update()
    {

    }
}
