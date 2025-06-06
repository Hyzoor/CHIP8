import java.util.ArrayList;
import java.util.List;

public class Display {

    private boolean[][] screen = new boolean[64][32];
    private final List<Runnable> listeners = new ArrayList<>();


    public void repaint(){
        notifyListeners();
    }

    public boolean getPixel(int x, int y){
        return screen[x][y];
    }

    public void setPixel(int x, int y, boolean value){
        screen[x][y] = value;
    }

    public void clear(){
        screen = new boolean[64][32];
    }

    public void addChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}
