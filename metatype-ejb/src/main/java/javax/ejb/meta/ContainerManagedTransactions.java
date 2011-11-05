package javax.ejb.meta;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@TransactionManagement(TransactionManagementType.CONTAINER)
@Metatype
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContainerManagedTransactions {
}
