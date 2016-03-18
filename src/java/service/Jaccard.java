/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

/**
 *
 * @author Naoufal
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Jaccard {

    public Jaccard() {
    }

    public double jaccard_coeffecient(List l1, List l2) {

        double res = 0;
        //System.out.println("++++++++++++++++++++++++++++");
        //System.out.println("-->"+l1);
        //System.out.println("-->"+l2);
        for (int x = 0; x < l2.size(); x++) {            
            String s1 = (String) l1.get(x);
            String s2 = (String) l2.get(x);
            //System.out.println("++++++++++++++++++++++++++++++++++++++++" + s1 + "-----" + s2);

            double j_coeffecient;
            ArrayList<String> j1 = new ArrayList<String>();
            ArrayList<String> j2 = new ArrayList<String>();
            HashSet<String> set1 = new HashSet<String>();
            HashSet<String> set2 = new HashSet<String>();

            int j = 0;
            int i = 2;

            while (i <= s1.length()) {
                j1.add(s1.substring(j, i));
                j++;
                i++;
            }
            j = 0;
            i = 2;
            while (i <= s2.length()) {
                j2.add(s2.substring(j, i));
                j++;
                i++;
            }

//            Iterator<String> itr1 = j1.iterator();
//            while (itr1.hasNext()) {
//                String element = itr1.next();
//                System.out.print(element + " ");
//            }
//            System.out.println();
//            Iterator<String> itr2 = j2.iterator();
//            while (itr2.hasNext()) {
//                String element = itr2.next();
//                System.out.print(element + " ");
//            }
//            System.out.println();

            set2.addAll(j2);
            set2.addAll(j1);
            set1.addAll(j1);
            set1.retainAll(j2);

            //System.out.println("Union=" + set2.size());
            //System.out.println("Intersection=" + set1.size());
            j_coeffecient = ((double) set1.size()) / ((double) set2.size());
            if (!Double.isNaN(j_coeffecient)) {
                //System.out.println("---------------->Jaccard coeffecient=" + j_coeffecient);
                res += j_coeffecient;
            }
        }//for
//        if(res / l1.size()>0.4){
//        System.out.println(l1.get(0)+" >-"+res / l1.size()+"-< "+l2.get(0));
//        }
        return res / l1.size();
    }

}
