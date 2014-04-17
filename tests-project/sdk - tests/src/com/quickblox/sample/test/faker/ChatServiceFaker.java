package com.quickblox.sample.test.faker;

import com.quickblox.module.chat.QBChatService;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ChatServiceFaker {

    public static QBChatService newInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<QBChatService> clazz = QBChatService.class;

        Constructor<QBChatService> c = clazz.getDeclaredConstructor((Class[]) null);
        c.setAccessible(true); //hack
        return c.newInstance((Object[]) null);
    }
}
