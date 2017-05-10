package SimServer;

/**
 * Created by arthur on 07.05.17.
 */
public class Models {

    public static String getBox(String modelID){
        return "<?xml version='1.0'?>\n" +
                "<sdf version ='1.6'>\n" +
                "  <model name ='box"+modelID+"'>\n" +
                "    <pose>1 2 0 0 0 0</pose>\n" +
                "    <link name ='link'>\n" +
                "      <pose>0 0 .5 0 0 0</pose>\n" +
                "      <collision name ='collision'>\n" +
                "        <geometry>\n" +
                "          <box><size>1 1 1</size></box>\n" +
                "        </geometry>\n" +
                "      </collision>\n" +
                "      <visual name ='visual'>\n" +
                "        <geometry>\n" +
                "          <box><size>1 1 1</size></box>\n" +
                "        </geometry>\n" +
                "      </visual>\n" +
                "    </link>\n" +
                "  </model>\n" +
                "</sdf>";
    }
}
