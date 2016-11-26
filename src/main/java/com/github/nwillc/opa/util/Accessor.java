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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ObjDoubleConsumer;

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

        // Check for public getter
        Optional<Method> methodOptional = getMethod(clz, "get" + fieldName);
        if (methodOptional.isPresent()) {
            final Method method = methodOptional.get();
            return t -> {
                try {
                    return valueOf(method.invoke(t));
                } catch (Exception e) {
                   Logger.error("Failed invoking " + method.getName() + " of " + clz.getName(), e);
                }
                return null;
            };
        }

        // Try field itself next
        Optional<Field> fieldOptional = getField(clz, fieldName);
        if (!fieldOptional.isPresent()) {
            throw new NoSuchFieldException("No field " + fieldName + " found in " + clz.getName());
        }

        final Field field = fieldOptional.get();
        return t -> {
            try {
                return valueOf(field.get(t));
            } catch (Exception e) {
                Logger.error("Can not access field " + fieldName + " of " + clz.getName(), e);
            }
            return null;
        };
    }

    private static String valueOf(Object o) {
        if (o == null) {
            return null;
        }

        return o.toString();
    }

    private static Optional<Method> getMethod(final Class<?> clz, String methodName) {
        Class<?> classPtr = clz;
        do {
            Method[] methods = classPtr.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equalsIgnoreCase(methodName) &&
                        (method.getModifiers() & Modifier.PUBLIC) != 0 &&
                         method.getParameterCount() == 0
                        ) {
                    method.setAccessible(true);
                    return Optional.of(method);
                }
            }
            classPtr = classPtr.getSuperclass();
        } while (classPtr != null);
        return Optional.empty();
    }

    private static Optional<Field> getField(final Class<?> clz, String fieldName) {
        Class<?> classPtr = clz;
        do {
            try {
                Field field = classPtr.getDeclaredField(fieldName);
                field.setAccessible(true);
                return Optional.of(field);
            } catch (NoSuchFieldException e) {
                classPtr = classPtr.getSuperclass();
            }
        } while (classPtr != null);
        return Optional.empty();
    }
}
