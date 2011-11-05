# Meta-Annotations

Meta-Annotations are an experiment in annotation inheritance, abstraction and encapsulation with a Java SE mindset

A meta-annotation is any annotation class annotated with @Metatype.  The other annotations used on the meta-annotation become part of its definition.
If any of those annotations happen to also be meta-annotations, they are unrolled as well and their annotations become part of the definition.


## The root @Metatype

The recursion that is the meta-annotation concept only happens when an annotation is marked as a `@Metatype`.  While at some point in the future
there may be a standard `@Metatype` annotation you can import and use, currently this is designed to be something you supply for yourself.

To create your `@Metatype`, simply:

  - Define an annotation called `Metatype` in any package
  - Annotated that `@Metatype` annotation with itself
  - Mark `@Target(ElementType.ANNOTATION_TYPE)` and `@Retention(RetentionPolicy.RUNTIME)`
  - Apply no other annotations

In short, a valid `@Metatype` root is any annotation named `@Metatype` which is annotated with itself and no other annotations of significance.

    package org.superbiz.accesstimeout.api;

    import java.lang.annotation.ElementType;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;

    @Metatype
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    public @interface Metatype {
    }


## Creating Meta-Annotations

If the annotation in question can be applied to `ElementType.ANNOTATION_TYPE` or `ElementType.TYPE`, creating
a meta-annotation version of it is quite easy.

    @TransactionManagement(TransactionManagementType.CONTAINER)
    @Metatype
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ContainerManagedTransactions {
    }

When the annotation in question cannot be applied to `ElementType.ANNOTATION_TYPE` or `ElementType.TYPE`, things get interesting.
This is where meta-annotations depart from things like `@Stereotype`.  The goal of meta-annotations is to be completely generic
and not specific to any one domain or API.  A such, you cannot really require all existing APIs change to allow for meta-annotations.
The goal is that meta-annotations can be used generically and do not need to be "designed" into an API.

To allow annotations that apply to `FIELD`, `METHOD`, `PARAMETER`, `CONSTRUCTOR`, `LOCAL_VARIABLE`, or `PACKAGE`, as well as any other
location where annotations may be applied in the future a compromise is made.

    import javax.ejb.Schedule;
    import java.lang.annotation.ElementType;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;

    @Metatype
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)

    public @interface Daily {
        public static class $ {

            @Daily
            @Schedule(second = "0", minute = "0", hour = "0", month = "*", dayOfWeek = "*", year = "*")
            public void method() {
            }
        }
    }

An inner class named `$`.  This is enough to bind together the `@Daily` and `@Schedule` in the context to which they both apply.

Ugly but effective.  Alternate proposals welcome.

The above is considered the public API portion of the meta-annotation concept.

The concept itself is born out of standards based systems like EJB and CDI where annotation
processing is invisible to the application itself.  In those settings the above is enough and
no additional APIs would be needed to support meta-annotations in standard APIs.

# Under the covers

The "guts" of this particular implementation is designed to look and feel as much like the reflection API as possible.
Obviously, with VM level control, you could do much better.  A clean Java SE API might be just what is needed and its very
possible that meta-annotations should really be a Java SE concept.

Here's a glimpse as to how things can look under the covers:

    final java.lang.reflect.AnnotatedElement annotated = new org.metatype.MetaAnnotatedClass(Triangle.class);
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


The application classes would look like so:

    @Crimson
    // -> @Red -> @Color
    public static class Triangle {

    }

    @Metatype
    @Color("red")
    // one level deep
    @Target(value = {TYPE})
    @Retention(value = RUNTIME)
    public static @interface Red {
    }

    @Metatype
    @Red
    // two levels deep
    @Target(value = {TYPE})
    @Retention(value = RUNTIME)
    public static @interface Crimson {
    }


See these test cases for more examples like the above:

 - [MetaAnnotatedClassTest](https://github.com/dblevins/metatypes/blob/master/metatype-impl/src/test/java/org/metatype/MetaAnnotatedClassTest.java)
 - [MetaAnnotatedMethodTest](https://github.com/dblevins/metatypes/blob/master/metatype-impl/src/test/java/org/metatype/MetaAnnotatedMethodTest.java)
 - [MetaAnnotatedFieldTest](https://github.com/dblevins/metatypes/blob/master/metatype-impl/src/test/java/org/metatype/MetaAnnotatedFieldTest.java)


# Best Practices

It is recommended to have an `api` package or some other package where "approved' annotations are defined and to prohibit usage of the non-meta versions of those annotations.
All the real configuration will then be centralized in the `api` package and changes to the values of those annotations will be localized to that package and automatically be
reflected throughout the application.

An interesting side-effect of this approach is that if the `api` package where the meta-annotation definitions exist is kept in a separate jar as well, then one can effectively
change the configuration of an entire application by simply replacing the `api` jar.

# Future concepts

## XML Overriding

The unrolling of meta-annotations happens under the covers.  In that same vein, so could the concept of overriding.

The above `@Red` annotation might theoretically be overridden via xml as follows:

    <org.superbiz.api.Red>
      <org.superbiz.api.Color value="dark red"/>
    </org.superbiz.api.Red>

Or take more complex meta-annotation definition like the following:

    package org.superbiz.corn.meta.api;

    import javax.ejb.Schedule;
    import javax.ejb.Schedules;
    import java.lang.annotation.ElementType;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;

    @Metatype
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)

    public @interface PlantingTime {
        public static interface $ {

            @PlantingTime
            @Schedules({
                    @Schedule(month = "5", dayOfMonth = "20-Last", minute = "0", hour = "8"),
                    @Schedule(month = "6", dayOfMonth = "1-10", minute = "0", hour = "8")
            })
            public void method();
        }
    }

This might theoretically be overridden as:

    <org.superbiz.corn.meta.api.PlantingTime>
      <javax.ejb.Schedules>
        <value>
          <javax.ejb.Schedule month="5" dayOfMonth="15-Last" minute="30" hour="5"/>
          <javax.ejb.Schedule month="6" dayOfMonth="1-15" minute="30" hour="5"/>
        </value>
      </javax.ejb.Schedules>
    </org.superbiz.corn.meta.api.PlantingTime>


## Merging or Aggregating definitions

Certain annotations take lists and are designed to be multiples.  In the current definition of meta-annotations, the following is illegal.

    @RolesAllowed({"Administrator", "SuperUser"})
    @Metatype
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Admins {
    }

    @RolesAllowed({"Employee", "User"})
    @Metatype
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Users {
    }


    public static class MyBean {

        @Admin
        @User
        public void doSomething() {
            // ...
        }
    }

Here the @Admin and @User annotation each resolve to @RolesAllowed.  Since only one @RolesAllowed annotation is allowed on the method per
the Java language specification, this results in an error.

The intention is clear however and aggregating metadata together in this way is natural.

A theoretical way to support something like this is with an annotation to describe that this aggregation is intended and desired.  Note the addition of the theoretical `@Merge` annotation.

    @RolesAllowed({"Administrator", "SuperUser"})
    @Metatype
    @Merge
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Admins {
    }

    @RolesAllowed({"Employee", "User"})
    @Metatype
    @Merge
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Users {
    }


    public static class MyBean {

        @Admin
        @User
        public void doSomething() {
            // ...
        }
    }


A new `@RolesAllowed` annotation would be created containing the list `{"Administrator", "SuperUser", "Employee", "User"}` and that would represent the final `@RolesAllowed`
usage for the `doSomething()` method.

## Parameters

Currently, meta-annotations to not allow parameters.  It could, however, be desirable to allow this and provide some way for parameters of the encapsulated annotations be
overridden in the meta-annotation.

Ideas on how this could be well designed are welcome.

# Case Study: EJB

A view of how Meta-Annotations might be applied to a technology such as EJB is here:

 - [metatype-ejb/src/main/java/javax/ejb/meta/](https://github.com/dblevins/metatypes/tree/master/metatype-ejb/src/main/java/javax/ejb/meta/)

# Presentations

 - [2011 JavaOne EJB with Meta Annotations](http://www.slideshare.net/dblevins1/2011-java-oneejbwithmetaannotations)
