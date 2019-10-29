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

    public String høyreGren()
    {
        StringBuilder s = new StringBuilder();
        s.append("[");
        if(rot != null){
            Node p = rot;
            s.append(p);
            while(p.høyre != null || p.venstre!=null ){
                if(p.høyre != null)
                    p = p.høyre;
                else p = p.venstre;
                s.append(",").append(" ").append(p);
            }
        }
        s.append("]");
        return s.toString();
    }

    public String lengstGren()
    {
        Stack<Node> stakk=new Stack<Node>();
        Node<T> p=rot;
        stakk.push(p);
        while(!stakk.isEmpty()){
            System.out.println(stakk.toString());
            p=stakk.remove(0);


            if(p.høyre!=null){
                stakk.add(p.høyre);
            }
            if(p.venstre!=null){
                stakk.add(p.venstre);
            }
        }
        T verdi=p.verdi;
        StringBuilder s=new StringBuilder();
        Queue<Node> kø=new LinkedList<Node>();
        kø.add(p);
        while(p.forelder!=null){
            p=p.forelder;
            kø.add(p);
        }
        s.append(kø.remove());
        while(!kø.isEmpty()){
            s.append(",").append(kø.remove());
        }
        s.append("]");


        return s.toString();
    }

    public String[] grener()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public String bladnodeverdier()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public String postString()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
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

        private BladnodeIterator()  // konstruktør
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }

        @Override
        public boolean hasNext()
        {
            return p != null;  // Denne skal ikke endres!
        }

        @Override
        public T next()
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }




    } // BladnodeIterator







    public static void main(String[] args) {
        Integer[] a= {4,7,2,9,4,10,8,7,4,6,1};

        ObligSBinTre<Integer> tre = new ObligSBinTre<>(Comparator.naturalOrder());
        for (int verdi: a) tre.leggInn(verdi);


        System.out.println(tre.fjernAlle(4));
        System.out.println(tre.fjernAlle(7));
        System.out.println(tre.fjernAlle(8));

        System.out.println(tre.antall());



    }

} // ObligSBinTre
