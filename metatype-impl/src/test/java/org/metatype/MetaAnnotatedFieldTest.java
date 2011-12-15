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

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author David Blevins
 */
public class MetaAnnotatedFieldTest extends TestCase {

    public void test() throws Exception {

        final Class<?>[] classes = new Class[]{Square.class, Circle.class, Triangle.class, Oval.class, Store.class, Farm.class, None.class};

        final Map<String, Annotated<Field>> map = new HashMap<String, Annotated<Field>>();

        for (Class<?> clazz : classes) {
            final MetaAnnotatedClass<?> annotatedClass = new MetaAnnotatedClass(clazz);

            for (MetaAnnotatedField field : annotatedClass.getDeclaredFields()) {
                map.put(field.getName(), field);
            }
        }

        // Check the positive scenarios
        {
            final java.lang.reflect.AnnotatedElement annotated = map.get("circle");
            assertNotNull(annotated);

            assertTrue(annotated.isAnnotationPresent(Color.class));
            assertTrue(annotated.getAnnotation(Color.class) != null);
            assertTrue(contains(Color.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(Color.class, annotated.getAnnotations()));
            assertEquals("white", annotated.getAnnotation(Color.class).value());

            assertTrue(annotated.isAnnotationPresent(Red.class));
            assertTrue(annotated.getAnnotation(Red.class) != null);
            assertTrue(contains(Red.class, annotated.getDeclaredAnnotations()));
            assertTrue(contains(Red.class, annotated.getAnnotations()));

            assertEquals(2, annotated.getDeclaredAnnotations().length);
            assertEquals(2, annotated.getAnnotations().length);
        }

        {
            final java.lang.reflect.AnnotatedElement annotated = map.get("square");
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

            assertEquals(1, annotated.getDeclaredAnnotations().length);
            assertEquals(2, annotated.getAnnotations().length);
        }

        {
            final java.lang.reflect.AnnotatedElement annotated = map.get("triangle");
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

            assertEquals(1, annotated.getDeclaredAnnotations().length);
            assertEquals(3, annotated.getAnnotations().length);
        }

        { // Circular - Egg wins
            final java.lang.reflect.AnnotatedElement annotated = map.get("store");
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

            assertEquals(1, annotated.getDeclaredAnnotations().length);
            assertEquals(3, annotated.getAnnotations().length);
        }

        { // Circular - Chicken wins
            final java.lang.reflect.AnnotatedElement annotated = map.get("farm");
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

            assertEquals(1, annotated.getDeclaredAnnotations().length);
            assertEquals(3, annotated.getAnnotations().length);
        }

    }

    private boolean contains(Class<? extends Annotation> type, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (type.isAssignableFrom(annotation.annotationType())) return true;
        }
        return false;
    }

    // 100% your own annotations, even the @Metatype annotation
    // Any annotation called @Metatype and annotated with itself works
    //@Metatype
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ANNOTATION_TYPE)
    public @interface Metatype {
    }

    @Target({FIELD})
    @Retention(RUNTIME)
    public static @interface Color {
        String value() default "";
    }

    @Metatype
    @Target({FIELD})
    @Retention(RUNTIME)
    public static @interface Red {
        public class $ {

            @Red
            @Color("red")  // one level deep
            private Object field;
        }
    }

    @Metatype
    @Target({FIELD})
    @Retention(RUNTIME)
    public static @interface Crimson {
        public class $ {

            @Crimson
            @Red  // two levels deep
            private Object field;
        }
    }

    // Green is intentionally not used in the classes
    // passed directly to the finder to ensure that
    // the finder is capable of following the path to
    // the root annotation even when some of the
    // annotations in the path are not strictly part
    // of the archive
    @Metatype
    @Target({FIELD})
    @Retention(RUNTIME)
    public static @interface Green {
        public class $ {

            @Green
            @Color("green")  // two levels deep
            private Object field;
        }
    }

    @Metatype
    @Target({FIELD})
    @Retention(RUNTIME)
    public static @interface DarkGreen {
        public class $ {

            @DarkGreen
            @Green
            private Object field;
        }
    }


    @Metatype
    @Target({FIELD})
    @Retention(RUNTIME)
    public static @interface Forrest {
        public class $ {

            @Forrest
            @DarkGreen
            private Object field;
        }
    }

    @Metatype
    @Target({FIELD})
    @Retention(RUNTIME)
    public static @interface Chicken {
        public class $ {

            @Chicken
            @Color("chicken")
            @Egg
            private Object field;
        }
    }

    @Metatype
    @Target({FIELD})
    @Retention(RUNTIME)
    public static @interface Egg {
        public class $ {

            @Egg
            @Color("egg")
            @Chicken
            private Object field;
        }
    }

    public static class Square {

        @Red // -> @Color
        private Object square;
    }

    public static class Circle {

        @Red // will be covered up by @Color
        @Color("white")
        private Object circle;
    }

    public static class Triangle {

        @Crimson // -> @Red -> @Color
        private Object triangle;
    }

    public static class Oval {

        @Forrest // -> @Green -> @Color
        private Object oval;
    }

    // always good to have a fake in there
    public static class None {

        private Object none;
    }

    public static class Store {

        @Egg
        private Object store;

    }

    public static class Farm {

        @Chicken
        private Object farm;

    }

}