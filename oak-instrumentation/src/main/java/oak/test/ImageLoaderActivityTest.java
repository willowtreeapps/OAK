/*
 * Copyright (c) 2011. WillowTree Apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oak.test;

import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;

import oak.demo.ImageLoaderActivity;

/**
 * User: Michael Lake
 * Date: 9/20/11
 * Time: 10:58 AM
 */

public class ImageLoaderActivityTest extends ActivityInstrumentationTestCase2<ImageLoaderActivity> {

    public ImageLoaderActivityTest() {
        super("oak.demo", ImageLoaderActivity.class);
    }


    public void testUnderScoreIssue(){
        Uri uri = Uri.parse("http://sprint_center.s3.amazonaws.com/img/exterior_night2.jpg");

    }
}
