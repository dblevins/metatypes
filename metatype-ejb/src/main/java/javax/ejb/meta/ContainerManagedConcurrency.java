package javax.ejb.meta;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Metatype;

@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Metatype
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContainerManagedConcurrency {
}
