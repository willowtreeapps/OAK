package com.willowtreeapps.android.shared.lv;

import android.util.Pair;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

/**
 * User: mlake
 * Date: 5/17/11
 * Time: 11:13 AM
 */
public abstract class FilterAdapter<T extends Sectionable> extends AmazingAdapter
        implements Filterable {

    private CustomFilter mFilter;

    private List<Pair<String, List<T>>> mObjects;

    private ArrayList<Pair<String, List<T>>> mOriginalValues;

    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */

    private final Object mLock = new Object();


    public void setDataManually(List<Pair<String, List<T>>> data) {
        mObjects = data;
        notifyDataSetChanged();
    }

    public void setData(List<T> unSectionedList) {

        List<Pair<String, List<T>>> res = new ArrayList<Pair<String, List<T>>>();
        Pair<String, List<T>> pair;
        ArrayList<T> subArray = null;

        for (int i = 0; i < unSectionedList.size(); i++) {
            T currentSectionable = unSectionedList.get(i);
            String nextSection = "";

            if (subArray == null) {
                subArray = new ArrayList<T>();
            }

            if (i + 1 < unSectionedList.size()) {
                nextSection = unSectionedList.get(i + 1).getSection();
            }
            String currentSection = currentSectionable.getSection();

            subArray.add(currentSectionable);

            if (!currentSection.equals(nextSection)) {
                pair = new Pair<String, List<T>>(currentSection, subArray);
                res.add(pair);
                subArray = null;
            }

        }

        mObjects = res;
        notifyDataSetChanged();
    }

    public void clear() {
        mObjects = null;
    }

    public synchronized void replaceDataInSection(String section, List<T> dataForSection) {
        if (mObjects == null) {
            return;
        }

        for (int i = 0; i < mObjects.size(); i++) {
            Pair<String, List<T>> pair = mObjects.get(i);

            if (pair.first.equals(section)) {
                mObjects.remove(i);
                Pair<String, List<T>> newPair = new Pair<String, List<T>>(section, dataForSection);
                mObjects.add(i, newPair);
                break;
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (mObjects == null) {
            return 0;
        }
        int res = 0;
        for (int i = 0; i < mObjects.size(); i++) {
            res += mObjects.get(i).second.size();
        }
        return res;
    }

    @Override
    public T getItem(int position) {
        int c = 0;
        for (int i = 0; i < mObjects.size(); i++) {
            if (position >= c && position < c + mObjects.get(i).second.size()) {
                return mObjects.get(i).second.get(position - c);
            }
            c += mObjects.get(i).second.size();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getPositionForSection(int section) {
        if (section < 0) {
            section = 0;
        }
        if (section >= mObjects.size()) {
            section = mObjects.size() - 1;
        }
        int c = 0;
        for (int i = 0; i < mObjects.size(); i++) {
            if (section == i) {
                return c;
            }
            c += mObjects.get(i).second.size();
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        int c = 0;
        for (int i = 0; i < mObjects.size(); i++) {
            if (position >= c && position < c + mObjects.get(i).second.size()) {
                return i;
            }
            c += mObjects.get(i).second.size();
        }
        return -1;
    }


    // this is used by the fast search widget when scrolling through large lists, shows single letter in box
    @Override
    public String[] getSections() {
        String[] res = new String[mObjects.size()];
        for (int i = 0; i < mObjects.size(); i++) {
            res[i] = mObjects.get(i).first.substring(0, 1);
        }
        return res;
    }

    public String[] getSectionsWithFullName() {
        String[] res = new String[mObjects.size()];
        for (int i = 0; i < mObjects.size(); i++) {
            res[i] = mObjects.get(i).first;
        }
        return res;
    }


    public boolean isPositionTopOfSection(int position) {
        int c = 0;
        for (int i = 0; i < mObjects.size(); i++) {
            if (position >= c && position < c + mObjects.get(i).second.size()) {
                if (position == c) {
                    return true;
                }
            }
            c += mObjects.get(i).second.size();
        }
        return false;
    }

    public boolean isPositionBottomOfSection(int position) {
        int c = 0;
        for (int i = 0; i < mObjects.size(); i++) {
            if (position >= c && position < c + mObjects.get(i).second.size()) {
                if (position == c + mObjects.get(i).second.size() - 1) {
                    return true;
                }
            }
            c += mObjects.get(i).second.size();
        }
        return false;
    }


    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new CustomFilter();
        }
        return mFilter;
    }

    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<Pair<String, List<T>>>(mObjects);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (mLock) {
                    List<Pair<String, List<T>>> list = new ArrayList<Pair<String, List<T>>>(
                            mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                String prefixString = prefix.toString().toLowerCase();

                final ArrayList<Pair<String, List<T>>> values = mOriginalValues;
                final int count = values.size();

                final ArrayList<Pair<String, List<T>>>
                        newValues = new ArrayList<Pair<String, List<T>>>(count);

                for (int i = 0; i < count; i++) {
                    String sectionHeader = values.get(i).first;

                    final int sectionCount = values.get(i).second.size();

                    final List<T> newSectionItems = new ArrayList<T>(sectionCount);

                    for (int k = 0; k < sectionCount; k++) {
                        final T sectionValue = values.get(i).second.get(k);
                        final String sectionValueText = sectionValue.toString().toLowerCase();

                        if (sectionValueText.startsWith(prefixString)) {

                            newSectionItems.add(sectionValue);

                        } else {

                            final String[] words = sectionValueText.split(" ");
                            final int wordCount = words.length;

                            for (int j = 0; j < wordCount; j++) {
                                if (words[j].startsWith(prefixString)) {
                                    newSectionItems.add(sectionValue);
                                    break;
                                }
                            }
                        }

                    }
                    if (newSectionItems.size() > 0) {
                        newValues.add(new Pair<String, List<T>>(sectionHeader, newSectionItems));
                    }

                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            //noinspection unchecked
            mObjects = (List<Pair<String, List<T>>>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }
    }
}
