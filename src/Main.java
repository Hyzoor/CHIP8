import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws InterruptedException {


        Memory memory = new Memory();
        Display display = new Display();
        CPU cpu = new CPU(memory, display);

        memory.loadROM(Paths.get("src/tests/2-ibm-logo.ch8"));
//        memory.loadROM(Paths.get("src/tests/test_opcode.ch8"));



        int pixelSize = 18;
        DisplayPanel panel = new DisplayPanel(display, pixelSize);
        Frame frame = new Frame(panel);



        while(true){
            cpu.cycle();
            Thread.sleep(200);
        }

    }
}
