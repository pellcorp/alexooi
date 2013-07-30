package au.com.alexooi.mojos.advent.generator.methods;

/**
 *
 */
public enum BuilderMethodType
{
    SETTER("setter"),
    ADD_TO_COLLECTION("add_to_collection"),
    EXTRA("extra");
    private final String template;

    BuilderMethodType(String template)
    {
        this.template = template;
    }

    public String getTemplate()
    {
        return template;
    }
}
