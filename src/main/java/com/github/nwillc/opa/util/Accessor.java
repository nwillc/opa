/*
 * Copyright (c) 2016, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.opa.util;


import org.pmw.tinylog.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.WatchEvent;
import java.util.function.Function;

/**
 * A utility to convert Reflection access to a function.
 *
 */
public final class Accessor {
    private Accessor() {
    }

    /**
     * Create a Function from a instance variable name that returns it's value in a class, or super classes.
     * If a public getter for the field is available the function will utilize that, otherwise it will access
     * the field directly.
     *
     * @param fieldName the instance variable name
     * @param clz       the class
     * @param <T>       the instance type of the argument to the function
     * @throws NoSuchFieldException if the fieldName is not one present in the class
     * @return an accessor function
     */
    @SuppressWarnings("unchecked")
    public static <T> Function<T, String> getFunction(final String fieldName, final Class<T> clz)
            throws NoSuchFieldException {

        Class<?> classPtr = clz;
        String getter = "get" + fieldName;

        Method method = getDeclaredMethod(clz, getter);
        if (method != null) {
            return t -> {
                try {
                    method.setAccessible(true);
                    return method.invoke(t).toString();
                } catch (Exception e) {
                   Logger.error("Failed invoking " + method.getName() + " on " + clz.getName() + ": " + e);
                }
                return null;
            };
        }

        Field field = null;

        do {
            try {
                field = classPtr.getDeclaredField(fieldName);
                field.setAccessible(true);
                break;
            } catch (NoSuchFieldException e) {
                classPtr = classPtr.getSuperclass();
            }
        } while (classPtr != null);

        if (field == null) {
            throw new NoSuchFieldException("No field " + fieldName + " found in " + clz.getName());
        }

        final Field finalField = field;
        return t -> {
            try {
                return finalField.get(t).toString();
            } catch (IllegalAccessException e) {
                Logger.error("Can not access field: " + clz.getName() + '.' + fieldName, e);
            }
            return null;
        };
    }

    private static Method getDeclaredMethod(final Class<?> clz, String methodName) {
        Class<?> classPtr = clz;

        do {
            Method[] methods = classPtr.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equalsIgnoreCase(methodName) &&
                        (method.getModifiers() & Modifier.PUBLIC) != 0 &&
                         method.getParameterCount() == 0
                        ) {
                    return method;
                }
            }
            classPtr = classPtr.getSuperclass();
        } while (classPtr != null);
        return null;
    }
}
