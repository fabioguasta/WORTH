# WORTH
Network Laboratory project (course of University of Pisa) 

WORTH: WORkTogetHer

### Descrizione del problema
Negli ultimi anni sono state create numerose applicazioni collaborative, per la condivisione di contenuti,
messaggistica, videoconferenza, gestione di progetti, ecc. In questo progetto didattico, __WORTH__
(WORkTogetHer), ci focalizzeremo sull’organizzazione e la gestione di progetti in modo collaborativo. Le
applicazioni di collaborazione e project management (es. [Trello](https://trello.com/), [Asana](https://asana.com/)) aiutano le persone a organizzarsi e
coordinarsi nello svolgimento di progetti comuni. Questi possono essere progetti professionali, o in
generale qualsiasi attività possa essere organizzata in una serie di compiti (es. to do list) che sono svolti da
membri di un gruppo: le applicazioni di interesse sono di diverso tipo, si pensi alla organizzazione di un
progetto di sviluppo software con i colleghi del team di sviluppo, ma anche all’organizzazione di una festa
con un gruppo di amici.
Alcuni di questi tool (es. Trello) implementano il metodo Kanban (cartello o cartellone pubblicitario, in
giapponese), un metodo di gestione “agile”. La lavagna Kanban fornisce una vista di insieme delle attività e
ne visualizza l’evoluzione, ad esempio dalla creazione e il successivo progresso fino al completamento,
dopo che è stata superata con successo la fase di revisione. Una persona del gruppo di lavoro può prendere
in carico un’attività quando ne ha la possibilità, spostando l’attività sulla lavagna.
Il progetto consiste nell’implementazione di WORkTogetHer (WORTH): uno strumento per la gestione di
progetti collaborativi che si ispira ad alcuni principi della metodologia Kanban.

### Specifica delle operazioni
Gli utenti possono accedere a WORTH dopo registrazione e login.
In WORTH, un progetto, identificato da un nome univoco, è costituito da una serie di “card” (“carte”), che
rappresentano i compiti da svolgere per portarlo a termine, e fornisce una serie di servizi. Ad ogni progetto
è associata una lista di membri, ovvero utenti che hanno i permessi per modificare le card e accedere ai
servizi associati al progetto (es. chat).
Una card è composta da un nome e una descrizione testuale. Il nome assegnato alla card deve essere
univoco nell’ambito di un progetto. Ogni progetto ha associate quattro liste che definiscono il flusso di
lavoro come passaggio delle card da una lista alla successiva: TODO, INPROGRESS, TOBEREVISED, DONE.
Qualsiasi membro del progetto può spostare la card da una lista all’altro, rispettando i vincoli illustrati nel
diagramma in figura.
Le card appena create sono automaticamente inserite nella lista TODO. Qualsiasi membro può spostare una
card da una lista all’altra. Quando tutte le card sono nella lista DONE il progetto può essere cancellato, da
un qualsiasi membro partecipante al progetto.
Ad ogni progetto è associata una chat di gruppo, e tutti i membri di quel progetto, se online (dopo aver
effettuato il login), possono ricevere e inviare i messaggi sulla chat. Sulla chat il sistema invia inoltre
automaticamente le notifiche di eventi legati allo spostamento di una card del progetto da una lista
all’altra.  
![fig1](/img/fig1.png)

Un utente registrato e dopo login eseguita con successo ha i permessi per:
* recuperare la lista di tutti gli utenti registrati al servizio;
* recuperare la lista di tutti gli utenti registrati al servizio e colleggati al servizio (in stato online);
* creare un progetto;
* recuperare la lista dei progetti di cui è membro.

Un utente che ha creato un progetto ne diventa automaticamente un membro. Può aggiungere altri utenti registrati come membri del progetto.
Tutti i membri del progetto hanno gli stessi diritti (il creatore stesso è un membro come tutti gli altri), in particolare:
* aggiungere altri utenti registrati come membri del progetto;
* recuperare la lista dei membri del progetto;
* creare Card nel progetto;
* recuperare la lista di card associate ad un progetto;
* recuperare le informazioni di una specifica Card del progetto;
* recuperare la "storia" di una specifica card del progetto (_vedi seguito per dettagli_);
* spostare qualsiasi Card del progetto rispettando i vincoli della figura sopra;
* inviare un messaggio sulla chat di progetto;
* leggere i messaggi dalla chat di progetto;
* cancellare il progetto.

Di seguito sono specificate le __operazioni offerte dal servizio__. In sede di implementazione è possibile
aggiungere ulteriori parametri, se necessario.

_register(nickUtente, password)_: per inserire un nuovo utente, il server mette a disposizione una operazione
di registrazione di un utente. Il server risponde con un codice che può indicare l’avvenuta registrazione,
oppure, se il nickname è già presente, o se la password è vuota, restituisce un messaggio d’errore. Come
specificato in seguito, le registrazioni sono tra le informazioni da persistere.

_login(nickUtente, password)_: login di un utente già registrato per accedere al servizio. Il server risponde con
un codice che può indicare l’avvenuto login, oppure, se l’utente ha già effettuato la login o la password è
errata, restituisce un messaggio d’errore.

_logout(nickUtente)_: effettua il logout dell’utente dal servizio.

_listUsers()_: utilizzata da un utente per visualizzare la lista dei nickUtente registrati al servizio e il loro stato
(online o offline).

_listOnlineusers()_: utilizzata da un utente per visualizzare la lista dei nickUtente registrati al servizio e online
in quel momento.

_listProjects()_: operazione per recuperare la lista dei progetti di cui l’utente è membro.

_createProject(projectName)_: operazione per richiedere la creazione di un nuovo progetto. Se l’operazione
va a buon fine, il progetto è creato e ha come membro l’utente che ne ha richiesto la creazione.

_addMember(projectName, nickUtente)_: operazione per aggiungere l’utente nickUtente al progetto
projectname. Se l’utente è registrato l’aggiunta come membro è eseguita senza chiedere il consenso a
nickUtente, se l’utente non è registrato l’operazione non può essere completata e il servizio restituisce un
messaggio di errore.

_showMembers(projectName)_: operazione per recuperare la lista dei membri del progetto.

_showCards(projectName)_: operazione per recuperare la lista di card associate ad un progetto projectName.

_showCard(projectName, cardName)_: operazione per recuperare le informazioni (nome, descrizione
testuale, lista in cui si trova in quel momento) della card cardName associata ad un progetto projectName.

_addCard(projectName, cardName, descrizione)_: operazione per richiedere l’aggiunta della card di nome
cardName al progetto projectname. La card deve essere accompagnata da una breve testo descrittivo. La
card viene automaticamente inserita nella lista TODO.

_moveCard(projectName, cardName, listaPartenza, listaDestinazione)_: operazione per richiedere lo
spostamento della card di nome cardName al progetto projectname dalla lista listaPartenza alla lista
listaDestinazione.

_getCardHistory(projectName, cardName)_: operazione per richiedere la “storia” della card, ovvero la
sequenza di eventi di spostamento della card, dalla creazione allo spostamento più recente.

_readChat(projectName)_: operazione per visualizzare i messaggi della chat del progetto projectName 

_sendChatMsg(projectName, messaggio)_: l’utente invia un messaggio alla chat del progetto projectName 

_cancelProject(projectName)_: un membro di progetto chiede di cancellare un progetto. L’operazione può
essere completata con successo solo se tutte le card sono nella lista DONE.

Permessi | Utente | Membro di progetto
------------ | ------------ | ------------
_listUsers_ | <div><textalign=center>x</div> | 

