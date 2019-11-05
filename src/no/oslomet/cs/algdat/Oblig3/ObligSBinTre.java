package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////

import no.oslomet.cs.algdat.Beholder;

import java.util.*;

public class ObligSBinTre<T> implements Beholder<T> {
    private static final class Node<T>   // en indre nodeklasse
    {
        private T verdi;                   // nodens verdi
        private Node<T> venstre, høyre;    // venstre og høyre barn
        private Node<T> forelder;          // forelder

        // konstruktør
        private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder) {
            this.verdi = verdi;
            venstre = v;
            høyre = h;
            this.forelder = forelder;
        }

        private Node(T verdi, Node<T> forelder)  // konstruktør
        {
            this(verdi, null, null, forelder);
        }

        @Override
        public String toString() {
            return "" + verdi;
        }

    } // class Node

    private Node<T> rot;                            // peker til rotnoden
    private int antall;                             // antall noder
    private int endringer;                          // antall endringer

    private final Comparator<? super T> comp;       // komparator

    public ObligSBinTre(Comparator<? super T> c)    // konstruktør
    {
        rot = null;
        antall = 0;
        comp = c;
    }

    @Override
    public boolean leggInn(T verdi) {
        Objects.requireNonNull(verdi, "Ulovlig med nullverdier!");

        Node<T> p = rot, q = null;               // p starter i roten
        int cmp = 0;                             // hjelpevariabel

        while (p != null)       // fortsetter til p er ute av treet
        {
            q = p;                                 // q er forelder til p
            cmp = comp.compare(verdi, p.verdi);     // bruker komparatoren
            p = cmp < 0 ? p.venstre : p.høyre;     // flytter p
        }

        // p er nå null, dvs. ute av treet, q er den siste vi passerte

        p = new Node<>(verdi, q);                   // oppretter en ny node

        if (q == null) rot = p;                  // p blir rotnode
        else if (cmp < 0) q.venstre = p;         // venstre barn til q
        else q.høyre = p;                        // høyre barn til q

        antall++;                                // én verdi mer i treet
        return true;                             // vellykket innlegging
    }

    @Override
    public boolean inneholder(T verdi) {
        if (verdi == null) return false;

        Node<T> p = rot;

        while (p != null) {
            int cmp = comp.compare(verdi, p.verdi);
            if (cmp < 0) p = p.venstre;
            else if (cmp > 0) p = p.høyre;
            else return true;
        }

        return false;
    }

    @Override
    public boolean fjern(T verdi) {

        if(verdi == null) return false;

        Node<T> p = rot;

        while(p!=null){
            int cmp = comp.compare(verdi,p.verdi);

            if(cmp < 0) p=p.venstre;
            else if(cmp > 0) p=p.høyre;
            else break;
        }

        if (p==null) return false;

        if (p.venstre==null || p.høyre==null) {

            Node<T> b = (p.venstre!=null) ? p.venstre : p.høyre;

            if (p == rot) {
                rot =  b;
                if(b!=null) b.forelder=null;
            }
            else if (p==p.forelder.venstre) {
                if(b!=null)b.forelder = p.forelder;
                p.forelder.venstre = b;
            } else {

                if(b!=null)b.forelder = p.forelder;
                p.forelder.høyre = b;
            }
        }
        else {

            Node<T> r = p.høyre;
            while (r.venstre != null) r = r.venstre;
            p.verdi = r.verdi;

            if(r.forelder!=p) {
                Node<T> q = r.forelder;
                q.venstre = r.høyre;
                if(q.venstre!=null)q.venstre.forelder = q;
            }
            else{
                p.høyre =  r.høyre;
                if(p.høyre !=null) p.høyre.forelder = p;

            }
        }

        antall--;
        return true;
    }

    public int fjernAlle(T verdi)
    {
        int i = 0;
        boolean fjernet = true;
        while(fjernet!=false){
            if(fjern(verdi))
                i++;
            else
                fjernet = false;
        }
        return i;
    }

    @Override
    public int antall() {
        return antall;
    }

    public int antall(T verdi) {
        Node<T> p = rot;
        int antallVerdi = 0;

        while (p != null) {
            int cmp = comp.compare(verdi, p.verdi);
            if (cmp < 0) {
                p = p.venstre;
            } else {
                if (cmp == 0) {
                    antallVerdi++;
                }
                p = p.høyre;
            }
        }
        return antallVerdi;
    }

    @Override
    public boolean tom() {
        return antall == 0;
    }

    public void nullstill()
    {
        if (!tom()) nullstill(rot);  // nullstiller
        rot = null; antall = 0;      // treet er nå tomt
    }

    private void nullstill(Node<T> p)
    {
        if (p.venstre != null)
        {
            nullstill(p.venstre);      // venstre subtre
            p.venstre = null;          // nuller peker
        }
        if (p.høyre != null)
        {
            nullstill(p.høyre);        // høyre subtre
            p.høyre = null;            // nuller peker
        }
        p.verdi = null;              // nuller verdien
    }


    private static <T> Node<T> førsteInorden(Node<T> p) {
        while (p.venstre != null) p = p.venstre;
        return p;
    }

    private static <T> Node<T> nesteInorden(Node<T> p) {
        if (p.høyre != null)  // p har høyre barn
        {
            return førsteInorden(p.høyre);
        } else  // må gå oppover i treet
        {
            while (p.forelder != null && p.forelder.høyre == p) {
                p = p.forelder;
            }
            return p.forelder;
        }
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[");
        if (!tom()) {
            Node<T> p = førsteInorden(rot);
            s.append(førsteInorden(rot).verdi);
            while (nesteInorden(p) != null) {
                s.append(", ");
                s.append(nesteInorden(p).verdi);
                p = nesteInorden(p);
            }
        }
        s.append("]");
        return s.toString();
    }



    public String omvendtString(){
        StringBuilder s = new StringBuilder();
        s.append("[");
        if (!tom()) {
            Stack stakk = new Stack();
            Node<T> p = førsteInorden(rot);
            stakk.push(""+førsteInorden(rot).verdi);
            while (nesteInorden(p) != null) {
                stakk.push(""+nesteInorden(p).verdi);
                p = nesteInorden(p);
            }
            for (int i = 0; i < stakk.size();i++) {
                s.append(stakk.pop());
                s.append(", ");
            }
            s.append(stakk.pop());
            s.append(", ");
            s.append(stakk.pop());
        }
        s.append("]");
        return s.toString();
    }

    public String høyreGren() {
        List<T> ut=new ArrayList<>();
        Queue<Node> queue=new LinkedList<>();
        if(rot==null){
            return ut.toString();
        }
        queue.offer(rot);
        while(queue.size()!=0){
            int str=queue.size();
            for(int i=0;i<str;i++){
                Node denne=queue.poll();
                if(i==0){
                    ut.add((T) denne.verdi);
                }
                if(denne.høyre!=null){
                    queue.offer(denne.høyre);
                }
                if(denne.venstre!=null){
                    queue.offer(denne.venstre);
                }
            }

        }
        return ut.toString();
    }

    public String lengstGren() {
        Stack<Node> stakk=new Stack<Node>();
        Node<T> p=rot;
        StringBuilder s=new StringBuilder();

        if(p==null){
            s.append("[]");
            return s.toString();
        }
        stakk.push(p);
        while(!stakk.isEmpty()){
            p=stakk.remove(0);


            if(p.høyre!=null){
                stakk.add(p.høyre);
            }
            if(p.venstre!=null){
                stakk.add(p.venstre);
            }
        }
        T verdi=p.verdi;
        s.append("[");
        Stack<Node> kø=new Stack<Node>();
        kø.add(p);
        while(p.forelder!=null){
            p=p.forelder;
            kø.add(p);
        }
        s.append(kø.pop());
        while(!kø.isEmpty()){
            s.append(", ").append(kø.pop());
        }
        s.append("]");
        return s.toString();
    }

    public String[] grener()
    {
        Liste<String> liste = new TabellListe<>();
        StringBuilder s = new StringBuilder("[");
        if (!tom()) grener(rot, liste, s);

        String[] grener = new String[liste.antall()];           // oppretter tabell

        int i = 0;
        for (String gren : liste)
            grener[i++] = gren;                   // fra liste til tabell

        return grener;                          // returnerer tabellen
    }
    private void grener(Node<T> p, Liste<String> liste, StringBuilder s)
    {
        T verdi = p.verdi;
        int k = verdi.toString().length(); // lengden på verdi

        if (p.høyre == null && p.venstre == null)  // bladnode
        {
            liste.leggInn(s.append(verdi).append(']').toString());

            // må fjerne det som ble lagt inn sist - dvs. k + 1 tegn
            s.delete(s.length() - k - 1, s.length());
        }
        else
        {
            s.append(p.verdi).append(',').append(' ');  // legger inn k + 2 tegn
            if (p.venstre != null) grener(p.venstre, liste, s);
            if (p.høyre != null) grener(p.høyre, liste, s);
            s.delete(s.length() - k - 2, s.length());   // fjerner k + 2 tegn
        }
    }


    public String bladnodeverdier()
    {
        Node<T> p=rot;
        StringBuilder s = new StringBuilder("[");
        if(p==null){
            s.append("]");
            return s.toString();
        }
        if (!tom()){
            bladnodeverdier(rot,s);
        }
        s.setLength(s.length()-2);
        s.append("]");
        return s.toString();
    }
    public String bladnodeverdier(Node<T> p,StringBuilder s){
        if(p==null){
            s.append("[]");
            return s.toString();
        }
        if (p.venstre == null && p.høyre == null) {
            s.append(p.verdi);
            s.append(", ");
        }
        if(p.venstre!=null){
            bladnodeverdier(p.venstre,s);
        }
        if(p.høyre!=null){
            bladnodeverdier(p.høyre,s);
        }

        return s.toString();
    }
    public String postString(){
        StringBuilder ut=new StringBuilder();
        Node<T> p=rot;
        if(p==null){
            ut.append("[]");
            return ut.toString();
        }
        Stack<Node<T>> stakk=new Stack<>();
        stakk.push(p);
        Stack<T>utStack=new Stack<>();
        while(!stakk.isEmpty()){
            Node denne=stakk.pop();
            utStack.push((T) denne.verdi);
            if (denne.venstre != null) {
                stakk.push(denne.venstre);
            }

            if (denne.høyre != null) {
                stakk.push(denne.høyre);
            }
        }
        ut.append("[");
        while (!utStack.empty()) {
            ut.append(utStack.pop());
            ut.append(", ");
        }

        ut.setLength(ut.length()-2);
        ut.append("]");
        return ut.toString();
    }


    @Override
    public Iterator<T> iterator()
    {
        return new BladnodeIterator();
    }

    private class BladnodeIterator implements Iterator<T>
    {
        private Node<T> p = rot, q = null;
        private boolean removeOK = false;
        private int iteratorendringer = endringer;
        private Stakk<Node<T>> s=new TabellStakk<>();


        private BladnodeIterator()  // konstruktør
        {
            if(rot==null){
                throw new NoSuchElementException();
            }
            p=forsteVenstre(rot);

        }
        private Node<T> forsteVenstre(Node <T> p){
            while(p!=null){
                if(p.venstre ==null &&p.høyre==null){
                    break;
                }
                if(p.venstre!=null){
                    if(p.høyre!=null){
                        s.leggInn(p.høyre);
                    }
                    p=p.venstre;
                }
                else{
                    p=p.høyre;
                }

            }
            return p;
        }

        @Override
        public boolean hasNext()
        {
            return p != null;  // Denne skal ikke endres!
        }


        @Override
        public T next()
        {
            T verdi=p.verdi;
            if(!s.tom()){
                p=forsteVenstre(s.taUt());
                return verdi;
            }
            else{
                p=null;
            }

            q=p;

            while(hasNext()){
                p=nesteInorden(p);

                if(p==null){
                    return verdi;
                }
                else if(p.venstre==null && p.høyre==null){
                    return verdi;
                }
                else if(p.venstre!=null &&p.høyre==null){
                    return p.venstre.verdi;
                }
                else if(p.høyre!=null &&p.venstre==null){
                    return p.høyre.verdi;
                }

            }
            removeOK=true;
            return verdi;
        }

        @Override
        public void remove()
        {

            if(q.forelder==null) {
                rot = null;
            }
            else{
                if(q.forelder.venstre==q){
                    q.forelder.venstre=null;
                }
                else{
                    q.forelder.høyre=null;
                }
            }
            antall--;
            endringer++;
            iteratorendringer++;

        }

    } // BladnodeIterator

    public static void main(String[] args) {
        no.oslomet.cs.algdat.Oblig3.ObligSBinTre<Integer> tre =
                new ObligSBinTre<>(Comparator.naturalOrder());


        int[] a = {5, 2, 8, 1, 4, 6, 9, 3, 7};
        for (int k : a) tre.leggInn(k);

        Iterator<Integer> i = tre.iterator();
        List<Integer> liste = new ArrayList<>();
        for (Integer verdi : tre) {
            liste.add(verdi);
        }
        String s = liste.toString();
        System.out.println(s.toString());
        tre.nullstill();
        tre.leggInn(1);
        i = tre.iterator();
        tre.leggInn(2);
        System.out.println(i.next());


    }
} // ObligSBinTre