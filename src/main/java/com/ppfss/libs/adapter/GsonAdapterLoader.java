// PPFSS_Libs Plugin
// Авторские права (c) 2026 PPFSS
// Лицензия: MIT

package com.ppfss.libs.adapter;

import com.google.gson.GsonBuilder;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Set;

public final class GsonAdapterLoader {
    private GsonAdapterLoader() {
    }

    public static void registerAll(GsonBuilder builder, String... basePackages) {
        if (builder == null || basePackages == null || basePackages.length == 0) {
            return;
        }

        ConfigurationBuilder config = new ConfigurationBuilder()
                .setScanners(Scanners.TypesAnnotated);

        for (String basePackage : basePackages) {
            if (basePackage == null || basePackage.isBlank()) {
                continue;
            }
            config.addUrls(ClasspathHelper.forPackage(basePackage));
        }

        Reflections reflections = new Reflections(config);
        Set<Class<?>> adapters = reflections.getTypesAnnotatedWith(GsonAdapter.class);
        for (Class<?> adapterClass : adapters) {
            if (adapterClass.isInterface() || adapterClass.isAnnotation() || adapterClass.isEnum()) {
                continue;
            }

            GsonAdapter annotation = adapterClass.getAnnotation(GsonAdapter.class);
            Object adapter = createAdapter(adapterClass);

            builder.registerTypeAdapter(annotation.value(), adapter);
        }
    }

    private static Object createAdapter(Class<?> adapterClass) {
        try {
            return adapterClass.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Cannot create adapter: " + adapterClass.getName(), ex);
        }
    }
}
