package janb.ui;

import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Created by michaelanderson on 18/02/2015.
 */
public class ANBStyle {
    public static ANBStyle defaultStyle = new ANBStyle("");
    public static ANBStyle bold  = new ANBStyle("-fx-font-weight: bold;");

    String css;

    public ANBStyle(String css) {

        this.css = css;
    }

    public String toCss() {
        return css;
    }

    public static ANBStyle randomBold() {

        final Random random = new Random();
        final Color color = Color.hsb(360 * random.nextDouble(), 0.5 + 0.5*random.nextDouble(), 1.0);

        int red = (int) Math.round(color.getRed()*255);
        int green = (int) Math.round(color.getGreen()*255);
        int blue = (int) Math.round(color.getBlue()*255);


        String css = "-fx-font-weight: bold;\n" +
                "-fx-font-size: 12px;\n" +
                "-fx-text-fill: #880000;\n" +
                "-fx-effect: dropshadow( gaussian , rgba("+red+","+green+","+blue+",0.5) , 0,0,0,1 );";
        return new ANBStyle(css);
    }
}
