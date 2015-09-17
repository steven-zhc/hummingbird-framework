package com.hczhang.hummingbird.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by steven on 9/18/14.
 */
public class HBAssert extends org.apache.commons.lang3.Validate {

    /**
     * <p>HBAssert that the specified argument is not {@code null};
     * otherwise throwing an custom exception.
     *
     * <pre>HBAssert.notNull(myObject, "The object must not be null");</pre>
     *
     * <p>The message of the exception is &quot;The validated object is
     * null&quot;.
     *
     * @param <T>  the object type
     * @param object the object to check
     * @param exception A custom exception.
     * @param message the message
     * @param values the values
     * @return the validated object (never
     * for method chaining)
     * @throws NullPointerException if the object is
     *  #notNull(Object, String, Object...)
     */
    public static <T> T notNull(final T object, Class<? extends RuntimeException> exception, final String message, final Object... values) {
        if (object == null) {
            throwCustomException(exception, message, values);
        }
        return object;
    }

    /**
     * <p>HBAssert that the specified argument character sequence is
     * neither {@code null} nor a length of zero (no characters);
     * otherwise throwing an custom exception with the specified message.
     *
     * <pre>HBAssert.notEmpty(myString);</pre>
     *
     * <p>The message in the exception is &quot;The validated
     * character sequence is empty&quot;.
     *
     * @param <T>  the character sequence type
     * @param chars the character sequence to check, validated not null by this method
     * @param exception A custom exception.
     * @param message the message
     * @param values the values
     * @return the validated character sequence (never
     * method for chaining)
     * @throws NullPointerException if the character sequence is
     * @throws IllegalArgumentException if the character sequence is empty
     *  #notEmpty(CharSequence, String, Object...)
     */
    public static <T extends CharSequence> T notEmpty(final T chars, Class<? extends RuntimeException> exception, final String message, final Object... values) {
        if (chars == null || chars.length() == 0) {
            throwCustomException(exception, message, values);

        }
        return chars;
    }

    /**
     * <p>HBAssert that the specified argument character sequence is
     * neither {@code null}, a length of zero (no characters), empty
     * nor whitespace; otherwise throwing an custom exception with the specified
     * message.
     *
     * <pre>HBAssert.notBlank(myString, "The string must not be blank");</pre>
     *
     * @param <T>  the character sequence type
     * @param chars the character sequence to check, validated not null by this method
     * @param exception A custom exception.
     * @param message the
     * exception message if invalid, not null
     * @param values the optional values for the formatted exception message, null array not recommended
     * @return the validated character sequence (never
     * method for chaining)
     *  #notBlank(CharSequence)
     */
    public static <T extends CharSequence> T notBlank(final T chars, Class<? extends RuntimeException> exception, final String message, final Object... values) {
        if (chars == null || StringUtils.isBlank(chars)) {
            throwCustomException(exception, message, values);
        }
        return chars;
    }

    /**
     * <p>Validate that the argument condition is {@code true}; otherwise
     * throwing an custom exception with the specified message. This method is useful when
     * validating according to an arbitrary boolean expression, such as validating a
     * primitive number or using your own custom validation expression.
     *
     * <pre>
     * Validate.isTrue(i &gt;= min &amp;&amp; i &lt;= max, "The value must be between &#37;d and &#37;d", min, max);
     * Validate.isTrue(myObject.isOk(), "The object is not okay");</pre>
     *
     * @param expression the boolean expression to check
     * @param exception A custom exception.
     * @param message the
     * exception message if invalid, not null
     * @param values the optional values for the formatted exception message, null array not recommended
     * @throws IllegalArgumentException if expression is
     *  #isTrue(boolean)
     *  #isTrue(boolean, String, long)
     *  #isTrue(boolean, String, double)
     */
    public static void isTrue(final boolean expression, Class<? extends RuntimeException> exception, final String message, final Object... values) {
        if (expression == false) {
            throwCustomException(exception, message, values);
        }
    }

    /**
     * Is false.
     *
     * @param expression the expression
     * @param exception the exception
     * @param message the message
     * @param values the values
     */
    public static void isFalse(final boolean expression, Class<? extends RuntimeException> exception, final String message, final Object... values) {
        if (expression == true) {
            throwCustomException(exception, message, values);
        }
    }

    /**
     * Equals void.
     *
     * @param obj1 the obj 1
     * @param obj2 the obj 2
     * @param exception the exception
     * @param message the message
     * @param values the values
     */
    public static void equals(final Object obj1, final Object obj2, Class<? extends RuntimeException> exception, final String message, final Object... values) {
        if (! obj1.equals(obj2)) {
            throwCustomException(exception, message, values);
        }
    }

    private static void throwCustomException(Class<? extends RuntimeException> exception, final String message, final Object... values) {

        try {
            Constructor con = null;
            RuntimeException ex = null;
            if (values.length == 0) {
                con = exception.getDeclaredConstructor(String.class);
                ex = (RuntimeException) con.newInstance(message);
            } else {
                con = exception.getDeclaredConstructor(String.class, Object[].class);
                ex = (RuntimeException) con.newInstance(message, values);
            }

            throw ex;

        } catch (NoSuchMethodException e) {
            throw new DDDException("Cannot create new Exception [{}], error message: [{}]", exception.getName(), e.getMessage());
        } catch (InvocationTargetException e) {
            throw new DDDException("Cannot create new Exception [{}], error message: [{}]", exception.getName(), e.getMessage());
        } catch (InstantiationException e) {
            throw new DDDException("Cannot create new Exception [{}], error message: [{}]", exception.getName(), e.getMessage());
        } catch (IllegalAccessException e) {
            throw new DDDException("Cannot create new Exception [{}], error message: [{}]", exception.getName(), e.getMessage());
        }
    }
}
