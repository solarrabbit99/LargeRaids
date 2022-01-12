package com.solarrabbit.largeraids.nms;

import java.util.function.Predicate;

public interface AbstractPoiTypeWrapper {
    Predicate<? super AbstractPoiTypeWrapper> getPredicate();
}
