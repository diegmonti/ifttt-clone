package iftttclone.channels;

public class Scheduler {
	// Quando inizializzato lo scheduler analizza le annotazioni e popola o aggiorna il database
	// Lo scheduler, ogni X minuti, cerca le ricette attive ed esegue il trigger (con la reflection)
	// Se non ritorna null prende il testo dei field dell'action, lo processa sostituendo
	// tutti gli {{ingrendient}} con il valore che recupera dalla mappa e poi chiama la relativa action
	// Le classi dei canali devono in qualche modo sapere l'utente corrente e il relativo token
}
