import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.io.IOException;

public class GraalTest {
    public static void main(String[] args) throws IOException {
        Context context = Context.newBuilder()
                .allowIO(true)
                .allowPolyglotAccess(PolyglotAccess.ALL)
                .allowNativeAccess(true)
                .build();

        File file = new File("F:\\PREDAVANJA KARANTIN\\senzorski\\labs3\\src\\main\\java\\trilaterate3D.js");
        String language = Source.findLanguage(file);

        Source source = Source.newBuilder(language,file).build();

        context.eval(source);
        Value func = context.getBindings("js").getMember("trilaterate");

        Sensor sensor1 = new Sensor(
                new Position(0, 1, 2),
                50, true, true, 10.0);
        Sensor sensor2 = new Sensor(
                new Position(3, 4, 5),
                50, true, true, 10.0);

        Sensor sensor3 = new Sensor(
                new Position(6, 7, 8),
                50, true, true, 10.0);

        Sensor sensor = new Sensor(
                new Position(5, 7, 9),
                50, true, true, 10.0);

        Value result = func.execute(sensor1,sensor2,sensor3, sensor);

        System.out.println(result.asString());
    }

}
