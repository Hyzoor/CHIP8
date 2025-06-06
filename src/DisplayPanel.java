import javax.swing.*;
import java.awt.*;

public class DisplayPanel extends JPanel {

    private final Display display;
    private final int pixelSize;

    public DisplayPanel(Display display, int pixelSize) {
        this.display = display;
        this.pixelSize = pixelSize;
        setPreferredSize(new Dimension(64 * pixelSize, 32 * pixelSize));
        setBackground(Color.BLACK);

        display.addChangeListener(() -> {
            SwingUtilities.invokeLater(this::repaint);
        });

        setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int x = 0; x < 64; x++) {
            for(int y = 0; y < 32; y++){
                if(display.getPixel(x, y)){
                    g.setColor(Color.WHITE);
                }else{
                    g.setColor(Color.BLACK);
                }

                g.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
            }

        }
    }
}
