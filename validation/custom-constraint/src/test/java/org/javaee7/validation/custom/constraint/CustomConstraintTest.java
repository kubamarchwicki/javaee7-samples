package org.javaee7.validation.custom.constraint;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/**
 * In this sample we're going to explore how to create custom constraints. 
 * 
 * First steps: create validation +annotation+ (constaint) and a corresponding +validator+.
 *
 * The +annotation+ is a constraint declaration
 *
 * include::ZipCode[]
 * 
 * Backed with +validation+ logic performing actual verification
 * 
 * include::ZipCodeValidator[]
 * 
 */
@RunWith(Arquillian.class)
public class CustomConstraintTest {

    @Inject
    MyBean bean;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    
    /**
     * Based on the definition of our +@Deployment+ method, we will be
     * creating and deploying a standard Java archite file (jar) with 
     * the following structure.
     * 
     * [source, file]
     * ----
     * /ValidationMessages.properties
     * /org/
     * /org/javaee7/
     * /org/javaee7/validation/
     * /org/javaee7/validation/custom/
     * /org/javaee7/validation/custom/constraint/
     * /org/javaee7/validation/custom/constraint/MyBean.class
     * /org/javaee7/validation/custom/constraint/ZipCodeValidator.class
     * /org/javaee7/validation/custom/constraint/ZipCode.class
     * /org/javaee7/validation/custom/constraint/ZipCode$Country.class
     * ----
     */
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(MyBean.class, ZipCode.class, ZipCodeValidator.class)
                .addAsResource("ValidationMessages.properties");
    }


    /**
     * In the following test we are invoking the saveZip() business method
     * 
     * include::MyBean#saveZip[]
     */
    @Test
    public void saveZipCodeforUs() {
        bean.saveZip("95051");                      //<1> When the method is called and the parameter is passed through, it is checked against validation logic, defined in +ZipCodeValidator+ class
    }


    /**
     * The following test invokes a different method with slightly adjusted constraint
     * 
     * include::MyBean#saveZipIndia[]
     * 
     */
    @Test
    public void saveZipCodeForIndia() {
//        thrown.equals(ConstraintViolationException.class);
//        thrown.expectMessage("javaee7.validation.custom.constraint.ZipCode");
        thrown.expectMessage("saveZipIndia.arg0");  //<1> The excetion message includes the method name _saveZipIndia_ and a generic name _arg0_ where the _0_ stands for a consecutive parameter numer.
        bean.saveZipIndia("95051");                 //<2> The passed parameter does not fulfill validation requirements hence a +ConstraintViolationException+ is thrown.
    }

}
