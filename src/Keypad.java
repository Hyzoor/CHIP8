import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keypad implements KeyListener {

    private boolean[] keys = new boolean[16];

    public boolean isKeyPressed(int keyIndex){
        return keys[keyIndex];
    }

    public void reset(){
        keys = new boolean[16];
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = mapKey(e.getKeyCode());
        if(key != -1){
            keys[key] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = mapKey(e.getKeyCode());
        if(key != -1){
            keys[key] = false;
        }
    }

    private int mapKey(int keyCode){
        return switch (keyCode){
            case KeyEvent.VK_1 -> 0x1;
            case KeyEvent.VK_2 -> 0x2;
            case KeyEvent.VK_3 -> 0x3;
            case KeyEvent.VK_4 -> 0xC;
            case KeyEvent.VK_Q -> 0x4;
            case KeyEvent.VK_W -> 0x5;
            case KeyEvent.VK_E -> 0x6;
            case KeyEvent.VK_R -> 0xD;
            case KeyEvent.VK_A -> 0x7;
            case KeyEvent.VK_S -> 0x8;
            case KeyEvent.VK_D -> 0x9;
            case KeyEvent.VK_F -> 0xE;
            case KeyEvent.VK_Z -> 0xA;
            case KeyEvent.VK_X -> 0x0;
            case KeyEvent.VK_C -> 0xB;
            case KeyEvent.VK_V -> 0xF;
            default -> -1;
        };
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
