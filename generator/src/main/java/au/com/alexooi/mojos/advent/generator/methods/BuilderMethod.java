/*
 *
 */
package au.com.alexooi.mojos.advent.generator.methods;

public interface BuilderMethod
{
    BuilderMethodType getType();

    String getMethodExceptionsSignature();
}
