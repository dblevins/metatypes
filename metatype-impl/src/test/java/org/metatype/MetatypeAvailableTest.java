/*
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
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import junit.framework.TestCase;

import javax.annotation.Metaroot;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class MetatypeAvailableTest extends TestCase {
    private boolean contains(Class<? extends Annotation> type, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (type.isAssignableFrom(annotation.annotationType())) return true;
        }
        return false;
    }

    public void testSquare() {
        // Square
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

        assertTrue(contains(Metatype.class, annotated.getAnnotations()));
    }

    @Metatype // we want to carry this one forward
    @Metaroot
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ANNOTATION_TYPE)
    public @interface Metatype {
    }

    @Target(value = {TYPE})
    @Retention(value = RUNTIME)
    public static @interface Color {
        String value() default "";
    }

    @Metatype
    @Color("red")
    // one level deep
    @Target(value = {TYPE})
    @Retention(value = RUNTIME)
    public static @interface Red {
    }

    @Red
    // -> @Color
    public static class Square {
    }

}
