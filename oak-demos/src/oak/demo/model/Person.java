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

package oak.demo.model;

import oak.Sectionable;

/**
 * User: Michael Lake Date: 10/13/11 Time: 3:31 PM
 */

// START SNIPPET: sectionable

public class Person implements Sectionable {

    private String name;

    private String city;

    public Person(String name, String city) {
        this.name = name;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    //
    @Override
    public String getSection() {
        return city;
    }

    /*
     * when filtering occurs, it is doing a case-insensitive search on toString()
     * for more complex matching, implement Queryable on your model
     *
     */
    @Override
    public String toString() {
        return name + " " + city;
    }


}
// END SNIPPET: sectionable
