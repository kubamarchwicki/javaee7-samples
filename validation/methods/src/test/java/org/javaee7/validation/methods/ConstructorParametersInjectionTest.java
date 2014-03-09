package org.javaee7.validation.methods;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

/**
 * In this test we show how to validate constuctor parameters, invoked upon bean creation.
 * 
 * include::MyBean2[]
 *
 * The class under test declares two constructor: default (without parameters - required by the
 * specification) and a custom, parametrized constructor, used for bean creation.
 * 
 * The +MyBean2+ class requires a valid parameter +MyParameter+ with a not null attribute _value_
 * 
 * include::MyParameter[]
 * 
 */
@RunWith(Arquillian.class)
public class ConstructorParametersInjectionTest {

	@Inject
	MyBean2 bean;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

	/**
         * A standard Java archive is created
         */
        @Deployment
	public static Archive<?> deployment() {
		return ShrinkWrap.create(JavaArchive.class)
                .addClasses(MyBean2.class, MyParameter.class)           //<1> The archive must contain the actual bean as well as all dependent classes
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	/**
         * In the test we try to invoke the +MyBean2.getValue()+ method on an 
         * injected instance of +MyBean2+
         *
         * Obviously, upon bean creation, the _value_ attriute is empty hence a
         * violation exception is raised
         */
        @Test
	public void constructorViolationsWhenNullParameters() {
        thrown.expect(ConstraintViolationException.class);              //<1> Standard +ConstraintViolationException+ exception in raised
        thrown.expectMessage("javax.validation.constraints.NotNull");  
        thrown.expectMessage("MyBean2.arg0.value");                     //<2> The exception referes to the attribute _value_ of the first parameter (_arg0_) of the +MyBean2+ constructor
        bean.getValue();                                                //<3> The exception is raised only when the actual method is called. Only then the containers resolved the custom constructor, passes the bean to the client and runs the method. In this case the first step fails (non-default constructor)
	}

}
