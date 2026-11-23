package gestion;

import java.util.Timer;
import java.util.TimerTask;

public class GestorTemporizador {
    Timer timer;
    public void iniciar(int segundos, Runnable accionAlFinalizar) {
        timer = new Timer();
        TimerTask timerTask = new TimerTask(){
            @Override
            public void run() {
                detener();
                accionAlFinalizar.run();
            }
        };

        timer.schedule(timerTask, segundos * 1000L);
    }
    public void detener() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
