package com.serveme.exception.util;

import java.util.ArrayList;
import java.util.List;

public class ExceptionUtil {

    public static List<Throwable> getThrowableList(Throwable throwable) {
        List<Throwable> list = new ArrayList<Throwable>();
        while (throwable != null && !list.contains(throwable)) {
            list.add(throwable);
            throwable = throwable.getCause();
        }
        return list;
    }
}
