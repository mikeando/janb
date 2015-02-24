package janb.scripts;

/**
 * Created by michaelanderson on 23/02/2015.
 */
public class Death extends Script {

    @Override void action() {
        BoundChoice culture = getBoundChoice("culture");
        uidBuilder().add("death").add(culture);
        textBuilder().add("What does ").add(culture).add(" believe about death?");
    }

    @Override
    public String getTitle() {
        return "Death";
    }

}
