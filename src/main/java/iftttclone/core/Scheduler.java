package iftttclone.core;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
	// Lo scheduler, ogni X minuti, cerca le ricette attive ed esegue il trigger (con la reflection)
	// Se non ritorna null prende il testo dei field dell'action, lo processa sostituendo
	// tutti gli {{ingrendient}} con il valore che recupera dalla mappa e poi chiama la relativa action
	// Le classi dei canali devono in qualche modo sapere l'utente corrente e il relativo token

	@Scheduled(fixedRate = 30000)
	public void run() {
		// System.err.println("Hello!");
		// TODO
	}
}
