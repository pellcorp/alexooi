/*
 *
 */
package au.com.alexooi.mojos.advent.api.extension;

public abstract class ExtraBuilderMethodsSupport<TheObject>
{
    private TheObject theObject;

    public TheObject getTheObject()
    {
        return theObject;
    }

    public void setTheObject(TheObject theObject)
    {
        this.theObject = theObject;
    }
}
