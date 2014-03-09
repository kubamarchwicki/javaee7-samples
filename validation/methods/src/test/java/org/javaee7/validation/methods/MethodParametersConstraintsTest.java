package org.javaee7.validation.methods;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

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

/**
 * In this sample we are showing method validation capabilities for JEE7
 */
@RunWith(Arquillian.class)
public class MethodParametersConstraintsTest {

	@Inject
	MyBean bean;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

        /** 
         * Only bean in requierd for deployment. All constraints and validators
         * are included in the server runtime
         */ 
	@Deployment
	public static Archive<?> deployment() {
		return ShrinkWrap.create(JavaArchive.class).addClasses(MyBean.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

        /** 
         * At first +@Size+ constraint is checked for 
         * 
         * include::MyBean#sayHello[]
         */ 
	@Test
	public void methodSizeTooLong() {
		thrown.expect(ConstraintViolationException.class);                      //<1> +ConstraintViolationException+ is thrown
		thrown.expectMessage("javax.validation.constraints.Size");              //<2> Parameter size is exceeded
		thrown.expectMessage("org.javaee7.validation.methods.MyBean.sayHello"); //<3> For a method, which a fully qualified name is included in the exception message
		bean.sayHello("Duke");
	}

        /**
         * Testing the same method with a shorter parameter works fine
         */
	@Test
	public void methodSizeOk() {
		bean.sayHello("Duk");
	}

        /**
         * Another constraint checked is +@Future+ which requires that a +Date+ returned 
         * by method is a future date
         *
         * include::MyBean#showDate[]
         */
	@Test
	public void showDateFromPast() {
		thrown.expect(ConstraintViolationException.class);
		thrown.expectMessage("javax.validation.constraints.Future");
		thrown.expectMessage("org.javaee7.validation.methods.MyBean.showDate");
		bean.showDate(false);
	}

        /**
         * Similarly, forcing a return of a future date works smoothly.
         */
	@Test
	public void showDateFromFuture() {
		bean.showDate(true);
	}

        /**
         * Finally, a methods parameter can be annotated with more than one constraint
         * and each parameter can be constrained individually
         *
         * include::MyBean#showList[]
         * 
         * An empty list is passed 
         */
	@Test
	public void multipleParametersWithEmptyList() {
		thrown.expect(ConstraintViolationException.class);
		thrown.expectMessage("javax.validation.constraints.Size");      //<1> The Size constraint is not valid
		thrown.expectMessage("showList.arg0");                          //<2> For the first argument of +showList+ method
		bean.showList(new ArrayList<String>(), "foo");
	}

	/**
         * In a similar fashion a second parameter can be checked.
         * 
         */
        @Test
	public void multipleParametersNullSecondParameter() {
		thrown.expect(ConstraintViolationException.class);
		thrown.expectMessage("javax.validation.constraints.NotNull");
		thrown.expectMessage("showList.arg1");                          //<1> Assertion failed for the method's second argument

		List<String> list = new ArrayList<>();
		list.add("bar");
		bean.showList(list, null);                                      //<2> When a _null_ value has been passed 
	}

        /**
         * For a non-empty list and not null second parameter, test successfully passed
         */
	@Test
	public void multipleParametersWithCorrectValues() {
		List<String> list = new ArrayList<>();
		list.add("bar");
		list.add("woof");
		String string = bean.showList(list, "foo");
		assertThat(string, is(equalTo("foobar foowoof ")));
	}
}
