/**
* @Title:			High
* @Package:			com.foo.test.annotation
* @Description:		Customized annotation which is used to mark High priority test methods.  
* @author:			Alex LI
* @date:			2014/03/19
* @COPYRIGHT:		2014 foo Labs. All rights reserved
*/

package com.foo.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test that should run as part of the high tests, to comply the Testlink convention
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface High {

}
