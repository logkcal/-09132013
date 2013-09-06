package com.henry4j;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.internal.util.collections.Sets.newMockSafeHashSet;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.val;

import org.junit.Test;
import org.mockito.internal.configuration.DefaultInjectionEngine;
import org.mockito.internal.configuration.injection.scanner.InjectMocksScanner;
import org.mockito.internal.configuration.injection.scanner.MockScanner;
import org.springframework.test.util.ReflectionTestUtils;

import com.henry4j.core.Bimock;
import com.henry4j.core.Bimock.Mode;

public class BimockTest {
    private Mode mode = Mode.Replay;
    private List<Long> list;

    @Test
    public void testRecordAndReplay() {
        val map = Bimock.of(new HashMap<String, Integer>(), mode, new File("src/test/resources/test-record-and-replay-map.json"));
        assertThat(map.put("abc", 3), equalTo(null));
        assertThat(map.size(), equalTo(1));
        assertThat(map.get("abc"), equalTo(3));

        val l = Bimock.of(new ArrayList<Long>(), mode, new File("src/test/resources/test-record-and-replay-list.json"));
        ReflectionTestUtils.setField(this, "list", l); // this can also be injected by ReflectionUtils.
        try {
            list.remove(-1);
            fail();
        } catch (ArrayIndexOutOfBoundsException e) {
            assertThat(e.getMessage(), equalTo("-1"));
        }
        assertThat(list.add(100L), equalTo(true));
        assertThat(list.toArray(new Long[1]), equalTo(new Long[] { 100L }));
    }

    public void injectMocks(final Object testClassInstance) {
        Class<?> clazz = testClassInstance.getClass();
        Set<Field> mockDependentFields = new HashSet<Field>();
        Set<Object> mocks = newMockSafeHashSet();
        while (clazz != Object.class) {
            new InjectMocksScanner(clazz).addTo(mockDependentFields);
            new MockScanner(testClassInstance, clazz).addPreparedMocks(mocks);
            clazz = clazz.getSuperclass();
        }
        new DefaultInjectionEngine().injectMocksOnFields(mockDependentFields, mocks, testClassInstance);
    }
}

//public static class Proxy implements InvocationHandler {
//@SuppressWarnings("unchecked")
//public static <T> T of(final T object, Mode mode, File resource) {
//  Enhancer enhancer = new Enhancer();
//  enhancer.setSuperclass(object.getClass());
//  enhancer.setCallback(new MethodInterceptor() {
//      @Override
//      public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
//              throws Throwable {
//          Object ret = proxy.invoke(object, args);
//          // doReturn(ret).when(ImmutableList.of()).size();
//          // doReturn(1).when(object).size();
//          return ret;
//      }
//  });
//
//  Object oo = ClassImposterizer.INSTANCE.imposterise(new MethodInterceptor() {
//      @Override
//      public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
//              throws Throwable {
//          return proxy.invoke(object, args)(object,  method);
//      }
//  }, object.getClass());
//  return (T)enhancer.create();
//}
//
//@Override
//public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//  System.out.println(args);
//  return null;
//}
//}
