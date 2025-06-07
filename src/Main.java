import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws InterruptedException {


        Memory memory = new Memory();
        Display display = new Display();
        Keypad keypad = new Keypad();
        CPU cpu = new CPU(memory, display, keypad);

        DisplayPanel panel = new DisplayPanel(display);
        Frame frame = new Frame(panel);
        frame.addKeyListener(keypad);

//        memory.loadROM(Paths.get("src/tests/2-ibm-logo.ch8"));
//        memory.loadROM(Paths.get("src/tests/bc_test.ch8"));
//        memory.loadROM(Paths.get("src/roms/1-chip8-logo.ch8"));
//        memory.loadROM(Paths.get("src/roms/4-flags.ch8"));
//        memory.loadROM(Paths.get("src/roms/3-corax+.ch8"));
        memory.loadROM(Paths.get("src/tests/test_opcode.ch8"));


        while(true){
            cpu.cycle();
            Thread.sleep(20);
        }

    }
}
