import org.yaml.snakeyaml.Yaml;

/**
 * Created by michaelanderson on 30/01/2015.
 */
public class test {

    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        String document =
                "  a: 1\n"+
                "  b:\n" +
                "    c: 3\n" +
                "    d: 4";

        final Object load = yaml.load(document);
        System.err.printf("Result %s %s\n", load, load.getClass());


        System.err.printf("This is a message");
    }
}
