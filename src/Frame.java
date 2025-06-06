import javax.swing.*;

public class Frame extends JFrame {

    public Frame(Display display, int pixelSize) {
        setTitle("CHIP8 Display");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DisplayPanel panel = new DisplayPanel(display, pixelSize);
        add(panel);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
}
