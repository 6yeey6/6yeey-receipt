package com.ibg.receipt.service.common;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractFactory<T extends NamedService> {
    private final Map<String, T> serviceMap;

    protected AbstractFactory(List<T> list) {
        this.serviceMap = list.stream().collect(Collectors.toMap(NamedService::name, Function.identity()));
    }

    public T getService(final String name) {
        T t = serviceMap.get(name);
        Objects.requireNonNull(t, name + "服务不存在");
        return t;
    }
}
