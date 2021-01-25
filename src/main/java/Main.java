public class Main {

    public static void main(String[] args) {
        new Main();
    }

    private final FPGA fFpga;
    //    private final FPGASimple fFpga;
    private final WebServer fWebServer;

    private int pattern, setpoint, p, i, d;

    public Main() {
        fFpga = new FPGA();
        fWebServer = new WebServer() {
            @Override
            public int getValue(String key) {
                switch (key) {
                    case "input":
                        return (int) fFpga.getMemory1();
                    case "control":
                        return (int) fFpga.getMemory3();
                    default:
                        return 0;
                }
            }

            @Override
            public void setValue(String key, int value) {
                switch (key) {
                    case "led":
                        fFpga.setMemory0(value);
                        break;
                    case "p":
                        p = value;
                        updateMemory();
                        break;
                    case "setpoint":
                        setpoint = value;
                        updateMemory();
                        break;
                }
            }
        };

        pattern = 0;
        setpoint = 0;
        p = 0;
        i = 0;
        d = 0;

        blinkSequence();
    }

    public void blinkSequence() {
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                setLedPattern("01010101");
            } else {
                setLedPattern("10101010");
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setLedPattern("00000000");
    }

    public void setLedPattern(String ledPattern) {
        pattern = Integer.parseInt(ledPattern, 2);
        updateMemory();
    }

    public void updateMemory() {
        long setpoint = Math.max(Math.min(this.setpoint, 16384), 0);

        setpoint <<= 8;

        long memory0Value = pattern + setpoint;

        fFpga.setMemory0(memory0Value);

        int p = Math.max(Math.min(this.p, 1024), 0);
        int i = Math.max(Math.min(this.i, 1024), 0);
        int d = Math.max(Math.min(this.d, 1024), 0);

        i <<= 10;
        d <<= 20;

//        i = p << 10;
//        d = p << 20;

        long memory2Value = p + i + d;

        fFpga.setMemory2(memory2Value);

        if (memory2Value != fFpga.getMemory2()) {
            System.err.println("MATCH ERROR");
        }
    }
}
