package component.liba;

import component.libcommon.Utils;
import demo.lib.DemoLib;

/**
 * Created by ws on 17/9/8.
 */

public class ComponentA implements InterfaceA {
    @Override
    public String getName() {
        return DemoLib.getString() + "-" + Utils.getUtilName("Component-A");
    }
}
