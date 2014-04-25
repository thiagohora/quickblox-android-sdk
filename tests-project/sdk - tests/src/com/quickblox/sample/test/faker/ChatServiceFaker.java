package com.quickblox.sample.test.faker;

import com.quickblox.module.chat.QBChatService;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPTCPConnection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ChatServiceFaker {

    public static QBChatService newInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Constructor<QBChatService> constructor = QBChatService.class.getDeclaredConstructor((Class[]) null);
        constructor.setAccessible(true);
        return constructor.newInstance((Object[]) null);
    }

    public static void notifyConnectionError(QBChatService service,
            Exception e) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field connectionField = service.getClass().getDeclaredField("connection");
        connectionField.setAccessible(true);
        XMPPTCPConnection connection = (XMPPTCPConnection) connectionField.get(service);

        Method notifyConnectionError = connection.getClass().getDeclaredMethod("notifyConnectionError", Exception.class);
        notifyConnectionError.setAccessible(true);
        notifyConnectionError.invoke(connection, e);
    }
}
