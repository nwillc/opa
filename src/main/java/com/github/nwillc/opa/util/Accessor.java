/*
 * Copyright 2018 nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without
 * fee is hereby granted, provided that the above copyright notice and this permission notice appear
 * in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT,
 * OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE,
 * DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.opa.util;


import net.openhft.compiler.CompilerUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A utility to convert member value access to a function.  This is not reflection based,
 * an specific class is created to provide the access. Creating the function is slower,
 * but then it's used over time it is full speed as opposed to that of reflection.
 */
public final class Accessor {
    private static final String GETTER_TEMPLATE =
            "package %s; \n" +
            "public class %s implements Accessor.StringAccessor {\n" +
            "  public Object getter(Object target) throws Exception {\n" +
            "    return ((%s)target).%s;\n" +
            "  }\n" +
            "}\n";

    private static final Map<String, Function> FUNCTIONS = new ConcurrentHashMap<>();

    private Accessor() {
    }

    /**
     * Create a Function from a instance variable name that returns it's value as a String in a class, or super classes.
     * If a public getter for the field is available the function will utilize that, otherwise it will access
     * the field directly if it is public.
     *
     * @param fieldName the instance variable name
     * @param clz       the class
     * @param <T>       the instance type of the argument to the function
     * @return an accessor function
     * @throws NoSuchFieldException if the fieldName is not one accecible in the class
     */
    @SuppressWarnings("unchecked")
    public static <T> Function<T, String> getFunction(final String fieldName, final Class<T> clz)
            throws NoSuchFieldException {

        final String className = accessorClassName(clz, fieldName);

        if (FUNCTIONS.containsKey(className)) {
            return FUNCTIONS.get(className);
        }

        // Check for public getter
        final Optional<Method> methodOptional = getMethod(clz, "get" + fieldName);
        if (methodOptional.isPresent()) {
            final Method method = methodOptional.get();

            final String javaCode = String.format(GETTER_TEMPLATE,
                    Accessor.class.getPackage().getName(),
                    className,
                    clz.getCanonicalName(),
                    method.getName() + "()");
            try {
                Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(Accessor.class.getPackage().getName() + "." + className, javaCode);
                final StringAccessor accessor = (StringAccessor) aClass.newInstance();
                Function f = accessor::access;
                FUNCTIONS.put(className, f);
                return f;
            } catch (Exception e) {
                throw new RuntimeException("Failed creating accessor", e);
            }
        }

        // Check for public field
        final Optional<Field> fieldOptional = getField(clz, fieldName);
        if (!fieldOptional.isPresent()) {
            throw new NoSuchFieldException("No getter for, or public field '" + fieldName + "' found in " + clz.getName());
        }

        final Field field = fieldOptional.get();
        final String javaCode = String.format(GETTER_TEMPLATE,
                Accessor.class.getPackage().getName(),
                className,
                clz.getCanonicalName(),
                field.getName());

        try {
            Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(Accessor.class.getPackage().getName() + "." + className, javaCode);
            final StringAccessor accessor = (StringAccessor) aClass.newInstance();
            Function f = accessor::access;
            FUNCTIONS.put(className, f);
            return f;
        } catch (Exception e) {
            throw new RuntimeException("Failed creating accessor", e);
        }
    }

    private static String accessorClassName(Class clz, String field) {
        return clz.getSimpleName() + "_get_" + field;
    }

    private static Optional<Method> getMethod(final Class<?> clz, final String methodName) {
        Class<?> classPtr = clz;
        do {
            final Method[] methods = classPtr.getDeclaredMethods();
            for (final Method method : methods) {
                if (method.getName().equalsIgnoreCase(methodName) &&
                        isPublic(method) &&
                        method.getParameterCount() == 0) {
                    return Optional.of(method);
                }
            }
            classPtr = classPtr.getSuperclass();
        } while (classPtr != null);
        return Optional.empty();
    }

    private static Optional<Field> getField(final Class<?> clz, final String fieldName) {
        Class<?> classPtr = clz;
        do {
            try {
                final Field field = classPtr.getDeclaredField(fieldName);
                if (isPublic(field))
                    return Optional.of(field);
            } catch (Exception e) {     //NOSONAR

            }
            classPtr = classPtr.getSuperclass();
        } while (classPtr != null);
        return Optional.empty();
    }

    private static boolean isPublic(Member member) {
        return (member.getModifiers() & Modifier.PUBLIC) != 0;
    }

    public interface StringAccessor {
        Object getter(Object target) throws Exception;

        default String access(Object target) {
            if (target == null) {
                return null;
            }
            try {
                Object val = getter(target);
                if (val == null) {
                    return null;
                }

                if (val instanceof String) {
                    return (String) val;
                }

                return String.valueOf(val);
            } catch (Exception e) {
                return null;
            }
        }

    }
}
