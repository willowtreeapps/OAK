/*
 * Copyright (c) 2014. WillowTree Apps
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

package oak;

/**
 * This class represents either a valid result or an exception. This is useful for asynchronous
 * calls where the exception cannot be thrown immediately.
 */
public class Result<T, E extends Exception> {
    private T mValue;
    private E mError;
    private boolean mIsSuccess;

    /**
     * Constructs a successful result.
     * @param value the successful value
     * @param <T> the value type
     * @param <E> the error type
     * @return the result
     */
    public static <T, E extends Exception> Result<T, E> success(T value) {
        return new Result<T, E>(value);
    }

    /**
     * Constructs an unsuccessful result.
     * @param error the error
     * @param <T> the value type
     * @param <E> the error type
     * @return the result
     */
    public static <T, E extends Exception> Result<T, E> error(E error) {
        return new Result<T, E>(error);
    }

    protected Result(T result) {
        mValue = result;
        mIsSuccess = true;
    }

    protected Result(E error) {
        mError = error;
        mIsSuccess = false;
    }

    /**
     * Returns if the result is successful
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return mIsSuccess;
    }

    /**
     * Returns if the result is an error
     * @return true if error, false otherwise
     */
    public boolean isError() {
        return !mIsSuccess;
    }

    /**
     * Returns the value if successful, throws the error is not.
     * @return the value if successful
     * @throws E the error if not
     */
    public T get() throws E {
        if (isSuccess()) {
            return mValue;
        } else {
            throw mError;
        }
    }

    /**
     * Returns the value if successful. Use @{link Result#isSuccess} to first check if the result
     * is successful.
     * @return the value if successful
     * @throws java.lang.IllegalStateException thrown if the result was not successful
     */
    public T getSuccess() {
        if (mIsSuccess) return mValue;
        else throw new IllegalStateException("Result was not a success");
    }

    /**
     * Returns the error if unsuccessful. Use @{link Result#isError} to first check if the result
     * is an error.
     * @return the error if unsuccessful
     * @throws java.lang.IllegalStateException thrown if the result was not an error
     */
    public E getError() {
        if (!mIsSuccess) return mError;
        else throw new IllegalStateException("Result was not an error");
    }
}