package com.gg.common.route.imp;

import java.util.Map;

public interface RouterLoadGroup {

    Map<String,Class<? extends RouterLoadPath>> loadGroup();
}
