import javax.swing.*;

public class Frame extends JFrame {

    public Frame(DisplayPanel displayPanel) {
        setTitle("CHIP8 Display");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        add(displayPanel);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
}
