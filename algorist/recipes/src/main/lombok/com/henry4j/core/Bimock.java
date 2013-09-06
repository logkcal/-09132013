package com.henry4j.core;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Bimock {
    private static String LINE = System.getProperty("line.separator");
    private static PojoMapper POJO_MAPPER = new PojoMapper(new MixIns());

    @SneakyThrows({ IOException.class })
    public static <T> T of(T object, Mode mode, final File resource) {
        if (Mode.Record == mode) {
            if (resource.exists()) {
                resource.delete();
            }
            @SuppressWarnings("unchecked")
            T mock = mock((Class<T>)object.getClass(), withSettings()
                    .spiedInstance(object)
                    .defaultAnswer(new Answer<T>() {
                @Override
                public T answer(InvocationOnMock iom) throws Throwable {
                    Object[] args = POJO_MAPPER.copyOf(iom.getArguments());
                    try {
                        T success = (T)iom.callRealMethod();
                        Files.append(Invocation.toJson(success, null, iom.getMethod(), args) + LINE, resource, Charsets.UTF_8);
                        return success;
                    } catch (Throwable failure) {
                        Files.append(Invocation.toJson(null, failure, iom.getMethod(), args) + LINE, resource, Charsets.UTF_8);
                        throw failure;
                    }
                }
            }));
            return mock;
        } else {
            @SuppressWarnings("unchecked")
            T mock = mock((Class<T>)object.getClass(), withSettings()
                    .spiedInstance(object)
                    .defaultAnswer(new Answer<T>() {
                        @Override
                        public T answer(InvocationOnMock invocation) throws Throwable {
                            throw new RuntimeException("UNCHECKED: this bug should go unhandled, as we come across unstubbed invocation(s).");
                        }
                    }));
            for (String line : Files.readLines(resource, Charsets.UTF_8)) {
                Invocation invocation = POJO_MAPPER.fromJson(line, Invocation.class);
                if (null != invocation.failure()) {
                    invocation.replay(doThrow(invocation.failure()).when(mock));
                } else {
                    if (Void.TYPE.equals(invocation.method().getReturnType())) {
                        invocation.replay(doNothing().when(mock));
                    } else {
                        invocation.replay(doReturn(invocation.success()).when(mock));
                    }
                }
            }
            return mock;
        }
    }

    public static enum Mode {
        Record, Replay
    }

    @Data
    @Accessors(fluent = true)
    @NoArgsConstructor @AllArgsConstructor(staticName = "of")
    public static class Invocation {
        private Object success;
        private Throwable failure;
        private Method method;
        private Object[] arguments;

        public static String toJson(Object success, Throwable failure, Method method, Object[] arguments) {
            return POJO_MAPPER.toJson(Invocation.of(success, null, method, arguments));
        }

        @SneakyThrows({ InvocationTargetException.class, IllegalAccessException.class })
        public Object replay(Object object) {
            return ReflectionUtils.findMethod(method.getDeclaringClass(), method.getName(), method.getParameterTypes()).invoke(object, arguments);
        }
    }

    @Component // this module needs to be Spring-auto-wired into PojoMapper's constructor.
    public static class MixIns extends SimpleModule {
        private static final long serialVersionUID = -2479398644334238459L;

        public MixIns() {
            super("Bimock.MixIns", new Version(1, 0, 0, "", "", ""));
            setMixInAnnotation(Method.class, MethodMixIn.class);
            setMixInAnnotation(Throwable.class, ThrowableMixIn.class);
        }

        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
        static abstract class ThrowableMixIn {
        }

        @JsonIgnoreProperties({ "override", "clazz", "annotations", "parameterAnnotations", "root", "synthetic", "typeParameters", "declaredAnnotations", "genericReturnType", "genericParameterTypes", "genericExceptionTypes", "bridge", "varArgs", "accessible" })
        static abstract class MethodMixIn {
            @JsonCreator
            MethodMixIn(
                    @JsonProperty("declaringClass") Class<?> declaringClass,
                    @JsonProperty("name") String name,
                    @JsonProperty("parameterTypes") Class<?>[] parameterTypes,
                    @JsonProperty("returnType") Class<?> returnType,
                    @JsonProperty("checkedExceptions") Class<?>[] checkedExceptions,
                    @JsonProperty("modifiers") int modifiers,
                    @JsonProperty("slot") int slot,
                    @JsonProperty("signature") String signature,
                    @JsonProperty("annotations") byte[] annotations,
                    @JsonProperty("parameterAnnotations") byte[] parameterAnnotations,
                    @JsonProperty("annotationDefault") byte[] annotationDefault) {
             }
        }
    }
}
