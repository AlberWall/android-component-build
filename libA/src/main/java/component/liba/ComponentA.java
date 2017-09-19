package component.liba;

import component.libcommon.Utils;

/**
 * Created by ws on 17/9/8.
 */

public class ComponentA implements InterfaceA {
    @Override
    public String getName() {
        return Utils.getUtilName("Component-A");
    }
}
