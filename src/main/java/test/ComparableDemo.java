package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ComparableDemo{

    public static void main(String[] args) {

        List ts1 = new ArrayList();
        ts1.add(new Employee ("Tom",40000.00));
        ts1.add(new Employee ("Harry",20000.00));
        ts1.add(new Employee ("Maggie",50000.00));
        ts1.add(new Employee ("Chris",70000.00));
        Collections.sort(ts1);
        Iterator itr = ts1.iterator();

        while(itr.hasNext()){
            Object element = itr.next();
            System.out.println(element + "\n");
        }

    }
}