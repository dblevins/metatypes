/*
 * Copyright 2011 David Blevins
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.metatype;

import junit.framework.TestCase;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Basic assertions:
 * <p/>
 * - getDeclaredAnnotations should not include meta-annotations
 * - meta-annotations can be recursive
 * - the most top-level value is the one returned from getAnnotation()
 *
 * @author David Blevins
 */
public class RootMetaAnnotatedClassTest extends TestCase {

    public void test() throws Exception {

        { // Circle
            final java.lang.reflect.AnnotatedElement annotated = new MetaAnnotatedClass(Circle.class);
            assertNotNull(annotated);

            assertTrue(annotated.isAnnotationPresent(Color.class));
            assertTrue(annotated.getAnnotation(Color.class) != null);
            assertTrue(contains(Color.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(Color.class, annotated.getAnnotations()));
            assertEquals("white", annotated.getAnnotation(Color.class).value());
        }

        { // Square
            final java.lang.reflect.AnnotatedElement annotated  = new MetaAnnotatedClass(Square.class);
            assertNotNull(annotated);

            assertTrue(annotated.isAnnotationPresent(Color.class));
            assertTrue(annotated.getAnnotation(Color.class) != null);
            assertTrue(!contains(Color.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(Color.class, annotated.getAnnotations()));
            assertEquals("red", annotated.getAnnotation(Color.class).value());

            assertTrue(annotated.isAnnotationPresent(Red.class));
            assertTrue(annotated.getAnnotation(Red.class) != null);
            assertTrue(contains(Red.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(Red.class, annotated.getAnnotations()));
        }

        { // Triangle
            final java.lang.reflect.AnnotatedElement annotated = new MetaAnnotatedClass(Triangle.class);
            assertNotNull(annotated);

            assertTrue(annotated.isAnnotationPresent(Color.class));
            assertTrue(annotated.getAnnotation(Color.class) != null);
            assertTrue(!contains(Color.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(Color.class, annotated.getAnnotations()));
            assertEquals("red", annotated.getAnnotation(Color.class).value());

            assertTrue(annotated.isAnnotationPresent(Red.class));
            assertTrue(annotated.getAnnotation(Red.class) != null);
            assertTrue(!contains(Red.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(Red.class, annotated.getAnnotations()));

            assertTrue(annotated.isAnnotationPresent(Crimson.class));
            assertTrue(annotated.getAnnotation(Crimson.class) != null);
            assertTrue(contains(Crimson.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(Crimson.class, annotated.getAnnotations()));
        }

        { // Circular - Egg wins
            final java.lang.reflect.AnnotatedElement annotated  = new MetaAnnotatedClass(Store.class);
            assertNotNull(annotated);

            assertTrue(annotated.isAnnotationPresent(Color.class));
            assertTrue(annotated.getAnnotation(Color.class) != null);
            assertTrue(!contains(Color.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(Color.class, annotated.getAnnotations()));
            assertEquals("egg", annotated.getAnnotation(Color.class).value());

            assertTrue(annotated.isAnnotationPresent(Egg.class));
            assertTrue(annotated.getAnnotation(Egg.class) != null);
            assertTrue(contains(Egg.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(Egg.class, annotated.getAnnotations()));

            assertTrue(annotated.isAnnotationPresent(Chicken.class));
            assertTrue(annotated.getAnnotation(Chicken.class) != null);
            assertTrue(!contains(Chicken.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(Chicken.class, annotated.getAnnotations()));
        }

        { // Circular - Chicken wins
            final java.lang.reflect.AnnotatedElement annotated  = new MetaAnnotatedClass(Farm.class);
            assertNotNull(annotated);

            assertTrue(annotated.isAnnotationPresent(Color.class));
            assertTrue(annotated.getAnnotation(Color.class) != null);
            assertTrue(!contains(Color.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(Color.class, annotated.getAnnotations()));
            assertEquals("chicken", annotated.getAnnotation(Color.class).value());

            assertTrue(annotated.isAnnotationPresent(Egg.class));
            assertTrue(annotated.getAnnotation(Egg.class) != null);
            assertTrue(!contains(Egg.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(Egg.class, annotated.getAnnotations()));

            assertTrue(annotated.isAnnotationPresent(Chicken.class));
            assertTrue(annotated.getAnnotation(Chicken.class) != null);
            assertTrue(contains(Chicken.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(Chicken.class, annotated.getAnnotations()));
        }

        { // Fake - annotated, but not meta-annotated
            final java.lang.reflect.AnnotatedElement annotated  = new MetaAnnotatedClass(Fake.class);
            assertNotNull(annotated);

            assertTrue(annotated.isAnnotationPresent(NotMeta.class));
            assertTrue(annotated.getAnnotation(NotMeta.class) != null);
            assertTrue(contains(NotMeta.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(NotMeta.class, annotated.getAnnotations()));

            assertFalse(annotated.isAnnotationPresent(Color.class));
            assertTrue(annotated.getAnnotation(Color.class) == null);
            assertFalse(contains(Color.class, annotated.getDeclaredAnnotations()));
            assertFalse(contains(Color.class, annotated.getAnnotations()));
        }

        { // None
            final java.lang.reflect.AnnotatedElement annotated  = new MetaAnnotatedClass(None.class);
            assertNotNull(annotated);

            assertFalse(annotated.isAnnotationPresent(NotMeta.class));
            assertFalse(annotated.isAnnotationPresent(Stereotype.class));
            assertTrue(annotated.getAnnotation(NotMeta.class) == null);
            assertTrue(annotated.getAnnotation(Stereotype.class) == null);

            assertEquals(0, annotated.getDeclaredAnnotations().length);
            assertEquals(0, annotated.getAnnotations().length);
        }

    }

    private boolean contains(Class<? extends Annotation> type, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (type.isAssignableFrom(annotation.annotationType())) return true;
        }
        return false;
    }



    @Metaroot
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ANNOTATION_TYPE)
    public @interface Metaroot {
    }

    @Metaroot
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ANNOTATION_TYPE)
    public @interface Stereotype {
    }

    @Target(value = {TYPE})
    @Retention(value = RUNTIME)
    public static @interface Color {
        String value() default "";
    }

    @Stereotype
    @Color("red")
    // one level deep
    @Target(value = {TYPE})
    @Retention(value = RUNTIME)
    public static @interface Red {
    }

    @Stereotype
    @Red
    // two levels deep
    @Target(value = {TYPE})
    @Retention(value = RUNTIME)
    public static @interface Crimson {
    }

    @Red
    @Target(value = {TYPE})
    @Retention(value = RUNTIME)
    public static @interface NotMeta {
    }

    @Stereotype
    @Color("egg")
    @Chicken
    // Circular
    @Target(value = {TYPE})
    @Retention(value = RUNTIME)
    public static @interface Egg {
    }


    @Stereotype
    @Color("chicken")
    @Egg
    // Circular
    @Target(value = {TYPE})
    @Retention(value = RUNTIME)
    public static @interface Chicken {
    }


    @Red
    // -> @Color
    public static class Square {
    }

    @Red
    // will be covered up by @Color
    @Color("white")
    public static class Circle {
    }

    @Crimson
    // -> @Red -> @Color
    public static class Triangle {

    }

    // always good to have a fake in there

    public static class None {

    }

    @NotMeta
    public static class Fake {

    }

    @Egg
    public static class Store {

    }

    @Chicken
    public static class Farm {
    }
}
