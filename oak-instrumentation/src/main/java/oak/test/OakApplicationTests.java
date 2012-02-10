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

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;

import android.graphics.Bitmap;
import android.os.Message;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.ImageView;

import java.net.URI;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.SSLException;

import oak.OAKImageLoader;
import oak.OAKImageLoaderHandler;
import oak.demo.OakApplication;
import oak.external.com.github.droidfu.http.CachedHttpResponse;

/**
 * User: Michael Lake
 * Date: 9/20/11
 * Time: 10:49 AM
 */
public class OakApplicationTests extends ApplicationTestCase<OakApplication> {

    public OakApplicationTests() {
        super(OakApplication.class);
      }

      @Override
      protected void setUp() throws Exception {
          super.setUp();
      }

      /**
       * The name 'test preconditions' is a convention to signal that if this
       * test doesn't pass, the test case was not set up properly and it might
       * explain any and all failures in other tests.  This is not guaranteed
       * to run before other tests, as junit uses reflection to find the tests.
       */
      @SmallTest
      public void testPreconditions() {
      }

      /**
       * Test basic startup/shutdown of Application
       */
      @MediumTest
      public void testSimpleCreate() {
          createApplication();

          assertEquals("testMessage",getApplication().getMessage());
      }

    public void testUnderScoreIssue() throws Exception {

        final String _URL =  "http://sprint_center.s3.amazonaws.com/img/exterior_night2.jpg";

        String unique = String.valueOf(new Date().getTime());
        final String URL =
                //"http://www.gravatar.com/avatar/2a1c55a7e1649a045761547925c4149e?s=512&blah="
                _URL + "?blah="
                        + unique;

        OAKImageLoader.initialize(getContext(),OAKImageLoader.PREFER_SD);

        ImageView imageView = new ImageView(getContext());

        final Holder<Boolean> loaded = new Holder<Boolean>();
        loaded.value = false;

        OAKImageLoaderHandler oakHandler = new OAKImageLoaderHandler(imageView, URL) {
            @Override
            public boolean handleImageLoaded(Bitmap bitmap, Message msg) {
                loaded.value = true;
                return super.handleImageLoaded(bitmap, msg);
            }
        };

        OAKImageLoader.start(URL, oakHandler);

        Thread.sleep(90000);

        assertTrue("Image from domain with underscore not loaded", loaded.value);

    }

    public class Holder<T> {

        public T value;
    }

}