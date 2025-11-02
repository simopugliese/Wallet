package com.simonepugliese;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * Gestisce il meccanismo di timeout di sicurezza per la sessione utente.
 */
public class TimeoutController {

    // Tempo di inattività prima del lock (5 minuti)
    private static final Duration TIMEOUT_DURATION = Duration.seconds(300);
    private final PauseTransition securityTimeout;
    private final Runnable lockAction;

    public TimeoutController(Runnable lockAction) {
        this.lockAction = lockAction;
        this.securityTimeout = new PauseTransition(TIMEOUT_DURATION);
        this.securityTimeout.setOnFinished(event -> lockAction.run());
    }

    /**
     * Avvia il monitoraggio del timeout.
     */
    public void startTimeout() {
        securityTimeout.playFromStart();
    }

    /**
     * Resetta il timer al verificarsi di un'interazione utente.
     */
    public void resetTimeout() {
        if (securityTimeout.getStatus() == PauseTransition.Status.RUNNING) {
            securityTimeout.playFromStart();
        }
    }

    /**
     * Ferma il monitoraggio del timeout.
     */
    public void stopTimeout() {
        securityTimeout.stop();
    }
}
