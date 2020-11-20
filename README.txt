Iordache Tiberiu-Mihai 322CD

Tema 1 POO 2020


Entitati:

    User
    Video -> Movie
          -> Serial
          -> SerialSeason
    Actor
    Action

Interfete:

    Pentru User:
        Command
        Query
        Suggestions

    Pentru Video:
        VideoActions

Modul de functionare al implementarii:

        Se creeaza un obiect de tip Action care are ca membrii date de baza pentru fiecare
    entitate necesara in cadrul scheletului. Aceasta entitate are o metoda doAction care primeste
    ca parametru un obiect de tipul ActionInputData din care copiaza toate informatiile actiunii
    si o executa. Prin executie, metoda va apela alte metode care la randul lor vor apela altele
    pana cand se va ajunge la case-ul dorit(Am implementat astfel pentru a nu avea o singura
    metoda de 500 de linii de cod).

        Pentru fiecare tip de actiune am creeat o interfata(prezente in cadrul pachetului actions),
    iar clasa User va implementa toate interfetele. De asemenea am creat si o interfata de actiune
    pentru clasa Video, deoarece din aceasta clasa voi deriva altele si am vrut sa evit folosire
    lui instance of.
        In cadrul interfetelor Command si Suggestion, inainte de apelul metodelor voi itera prin
    database-ul de useri pana fac match cu username-ul dat ca input de actiune, dupa care voi
    efectua actiunuile necesare asupra acelei referinte. Doar in cadrul metodelor din interfata
    Query am ales sa creez un dummy user deoarece se realizeaza doar o cautare, iar datele user-ului
    nu sunt necesare, doar cele din database. Astfel am incercat sa evit sa incarc aiurea programul
    cu inca un set de iterari. M-am gandit si la o implementare in care sa fac metodele din Query
    statice astfel incat sa le folosesc direct fara o instanta de user, insa din enuntul temei am
    inteles ca un user ar trebui sa efectueze cautarea, nu interfata.

        Legat de clase, avem clasa Video care implementeaza interfata VideoAction din care am
    derivat clasele Movie, Serial si SerialSeason. De asemenea, intre Serial si SerialSeason exista
    si o relatie de agregare, Serial avand o lista de obiecte de tip SerialSeason. Clasa User dupa
    cum am spus implementeaza cele 3 interfete de actiune. Ea mai contine si doua campuri:
    ratingMovieList si ratingSerialList. Acestea sunt 2 obiecte de tip Map care retin numele si
    rating-ul acordat unui film, pentru a implementa mai usor in continuare cautarile si
    recomandarile. In clasa Actor am mai introdus doua metode de calculare a numarului de premii
    castigate si a rating-ului dupa videoclipurile in care au jucat pentru a putea realiza cat
    mai usor sortarile.
        Am incercat pe cat de mult sa pastrez incapsularea prin a face membrii claselor privati si
    folosirea getterilor pentru a citi informatii.

        Pentru sortari am incercat sa folosesc referinte la metode, functii comparator sau expresii
    lambda in functie de situatie.

Probleme:

        In cadrul testelor de cautare a actorilor dupa filter description am tot intampinat
    probleme prin gasirea corecta a cuvantului (de ex sa nu gasesc war in cadrul cuvantului award),
    iar cea mai eficienta metoda pe care am gasit-o a fost folosirea lui pattern si matcher pentru
    a imi putea permite sa folosesc expresii regex, astfel incat cuvantul cautat sa inceapa cu orice
    caracter in afara de cele din intervalul a-z.

        In cadrul clasei Action am importat direct toate constantele din common.Constants. Cu toate
    ca inteleg ca nu este recomandat sa fac asta in general, in cazul de fata am preferat sa o fac,
    pentru a nu incarca inceputul fisierului cu toate importurile pentru constantele necesare.

        Legat de github, primele commit-uri sunt in mare doar teste deoarce am intampinat mici
    probleme pana am invatat sa folosesc intellij(ma refer prin a face commit si push direct din
    ide).

        La ultimele teste large, a durat destul de mult sa imi dau seama de ce nu primeam punctaj
    si sa gasesc cauzele problemelor deoarece cand puneam un breakpoint puteam sa nimeresc cu 20
    de actiuni inainte de actiunea pe care eu doream sa o testez si dura pana dadeam stepover(poate
    exista o metoda sa ajung direct la acea actiune, insa eu nu mi-am dat seama care era).

Feedback:

        Overall, mi-a placut chiar foarte mult tema! Am invatat multe, iar limbajul java in sine mi
    se pare mai usor de implementat si rezolvat bug-uri(de asemenea ide-ul a fost un mare ajutor in
    cazul depanarii problemelor).