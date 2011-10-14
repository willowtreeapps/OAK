package com.willowtreeapps.android.shared.lv;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;


/**
 * User: mlake
 * Date: 5/19/11
 * Time: 11:19 AM
 */
public class SectionTool<T extends AbstractSectionable> {

    public List<Pair<String, List<T>>> getSectionedData(List<T> unSectionedList) {
        List<Pair<String, List<T>>> res = new ArrayList<Pair<String, List<T>>>();


        Pair<String, List<T>> pair;
        ArrayList<T> subArray = null;

        for (int i = 0; i < unSectionedList.size(); i++) {
            T currentSectionable = unSectionedList.get(i);
            String nextSection = "";

            if (subArray == null) subArray = new ArrayList<T>();

            if (i + 1 < unSectionedList.size()) nextSection = unSectionedList.get(i + 1).getSection();
            String currentSection = currentSectionable.getSection();

            subArray.add(currentSectionable);

            if (!currentSection.equals(nextSection)) {
                pair = new Pair<String, List<T>>(currentSection, subArray);
                res.add(pair);
                subArray = null;
            }

        }

        return res;
    }

}
