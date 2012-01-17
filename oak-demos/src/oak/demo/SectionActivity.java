/*
 * Copyright (c) 2011. WillowTree Apps, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oak.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import oak.SectionListView;
import oak.SectionAdapter;

/**
 * User: Michael Lake Date: 10/13/11 Time: 3:22 PM
 */
public class SectionActivity extends Activity {

    SectionListView mSectionListView;

    private PersonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.filter);

        mSectionListView = (SectionListView) findViewById(R.id.amazing_lv);
        mSectionListView.setPinnedHeaderView(
                LayoutInflater.from(this).inflate(R.layout.filter_header, mSectionListView, false));

        mSectionListView.setAdapter(adapter = new PersonAdapter());

        populateAdapter();

        mSectionListView.setOnItemClickListener(adapter);

        ((TextView) findViewById(R.id.filter_et)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void populateAdapter() {
        // START SNIPPET: sectiontool
        List<Person> persons = new ArrayList<Person>();

        persons.add(new Person("Kermit the Frog", "Chicago"));
        persons.add(new Person("Miss Piggy", "Chicago"));
        persons.add(new Person("Gonzo", "Chicago"));

        persons.add(new Person("Leonardo", "New York"));
        persons.add(new Person("Michelangelo", "New York"));
        persons.add(new Person("Donatello", "New York"));
        persons.add(new Person("Raphael", "New York"));

        persons.add(new Person("Mister Rogers", "Pittsburgh"));

        persons.add(new Person("Captain Crunch", "Orlando"));
        persons.add(new Person("Soggies", "Orlando"));

        persons.add(new Person("Pooh Bear", "Hundred Acre Wood"));
        persons.add(new Person("Eor", "Hundred Acre Wood"));
        persons.add(new Person("Owl", "Hundred Acre Wood"));
        persons.add(new Person("Tigger", "Hundred Acre Wood"));
        persons.add(new Person("Piglet", "Hundred Acre Wood"));

        adapter.setData(persons);
        adapter.notifyDataSetChanged();
        // END SNIPPET: sectiontool

    }

    class PersonAdapter extends SectionAdapter<Person> implements AdapterView.OnItemClickListener {

        @Override
        public View getAmazingView(int position, View convertView, ViewGroup parent) {
            View res = convertView;
            if (res == null) {
                res = getLayoutInflater().inflate(R.layout.filter_list_item, null);
            }

            TextView lName = (TextView) res.findViewById(R.id.lName);
            Person person = getItem(position);
            String text = person.getName();

            LinearLayout view = (LinearLayout) res.findViewById(R.id.cell_bg);

            final ViewGroup.MarginLayoutParams lpt = (ViewGroup.MarginLayoutParams) view
                    .getLayoutParams();

            int topMargin = 10;
            int bottomMargin = 10;

            if (isPositionTopOfSection(position)) {
                if (isPositionBottomOfSection(position)) {
                    view.setBackgroundResource(R.drawable.cell);
                } else {
                    view.setBackgroundResource(R.drawable.cell_top);
                    bottomMargin = 1;
                }
            } else if (isPositionBottomOfSection(position)) {
                view.setBackgroundResource(R.drawable.cell_bottom);
                topMargin = 1;
            } else {
                view.setBackgroundResource(R.drawable.cell_mid);
                topMargin = 1;
                bottomMargin = 1;
            }

            lpt.setMargins(lpt.leftMargin, topMargin, lpt.rightMargin, bottomMargin);

            view.setLayoutParams(lpt);

            lName.setText(text);

            return res;
        }

        @Override
        public void configurePinnedHeader(View header, int position, int alpha) {
            TextView lSectionHeader = (TextView) header;
            lSectionHeader.setText(getSectionsWithFullName()[getSectionForPosition(position)]);
        }

        @Override
        protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
            if (displaySectionHeader) {
                view.findViewById(R.id.header).setVisibility(View.VISIBLE);
                TextView lSectionTitle = (TextView) view.findViewById(R.id.header);
                lSectionTitle.setText(getSectionsWithFullName()[getSectionForPosition(position)]);
            } else {
                view.findViewById(R.id.header).setVisibility(View.GONE);
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Toast.makeText(SectionActivity.this, "You tapped on: " + getItem(position).getName(),
                    1000);
        }
    }
}
