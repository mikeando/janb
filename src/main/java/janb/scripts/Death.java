package janb.scripts;

/**
 * Created by michaelanderson on 23/02/2015.
 */
public class Death extends Script {

    @Override
    public void action() {
        BoundChoice culture = getBoundChoice("culture");
        uidBuilder().add("death").add(culture).done();
        textBuilder().add("What does ").add(culture).add(" believe about death?").done();
    }

    @Override
    public String getTitle() {
        return "Death";
    }

}
