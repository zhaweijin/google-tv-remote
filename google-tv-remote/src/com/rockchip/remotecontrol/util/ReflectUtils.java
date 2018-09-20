package com.rockchip.remotecontrol.util;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtils
{
  public Object getProperty(Object obj, String fieldName)
    throws Exception
  {
    Object result = null;
    Class objClass = obj.getClass();
    Field field = objClass.getField(fieldName);
    result = field.get(obj);
    return result;
  }

  public Object getStaticProperty(String className, String fieldName)
    throws Exception
  {
    Class cls = Class.forName(className);
    Field field = cls.getField(fieldName);
    Object provalue = field.get(cls);
    return provalue;
  }

  public Object getPrivatePropertyValue(Object obj, String propertyName)
    throws Exception
  {
    Class cls = obj.getClass();
    Field field = cls.getDeclaredField(propertyName);
    field.setAccessible(true);
    Object retvalue = field.get(obj);
    return retvalue;
  }

  public static Method getMethod(String clsName, String methodName, Class<?>[] types)
  {
    try
    {
      Class cls = Class.forName(clsName);
      return cls.getMethod(methodName, types); } catch (Exception e) {
    }
    return null;
  }

  public static Object invokeMethod(Object obj, String methodName, Object[] arguments)
  {
    Class cls = obj.getClass();

    Object result = null;
    try {
      Class[] parameterTypes = (Class[])null;
      if (arguments != null) {
        parameterTypes = new Class[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
          Class temp = arguments[i].getClass();
          if (temp == Boolean.class) {
            temp = Boolean.TYPE;
          }
          parameterTypes[i] = temp;
        }
      }
      Method method = cls.getMethod(methodName, parameterTypes);
      result = method.invoke(obj, arguments);
    } catch (Exception e) {
      LogUtil.e("异常,", e);
    }
    return result;
  }
  public static Object invokeMethod(Object obj, String methodName, Class<?>[] types, Object[] arguments) {
    Class cls = obj.getClass();

    Object result = null;
    try {
      Method method = cls.getMethod(methodName, types);
      result = method.invoke(obj, arguments);
    } catch (Exception localException) {
    }
    return result;
  }
  public static Object invokeMethod(Class<?> cls, String methodName, Object[] arguments) {
    try {
      Object obj = cls.newInstance();
      return invokeMethod(obj, methodName, arguments);
    } catch (Exception e) {
    }
    return null;
  }

  public static Object invokeMethod(String className, String methodName, Object[] arguments) {
    try {
      Class cls = Class.forName(className);
      return invokeMethod(cls.newInstance(), methodName, arguments);
    } catch (Exception e) {
    }
    return null;
  }

  public static Object invokeStaticMethod(Class<?> cls, String methodName, Object[] arguments)
  {
    try
    {
      Class[] parameterTypes = (Class[])null;
      if (arguments != null) {
        parameterTypes = new Class[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
          Class temp = arguments[i].getClass();
          if (temp == Boolean.class) {
            temp = Boolean.TYPE;
          }
          parameterTypes[i] = temp;
        }
      }
      Method method = cls.getMethod(methodName, parameterTypes);
      return method.invoke(null, arguments);
    } catch (Exception e) {
    }
    return null;
  }

  public static Object invokeStaticMethod(String className, String methodName, Object[] arguments) {
    try {
      Class cls = Class.forName(className);
      return invokeStaticMethod(cls, methodName, arguments);
    } catch (Exception e) {
    }
    return null;
  }

  public static Object invokeStaticMethod(String className, String methodName, Class<?>[] types, Object[] arguments)
  {
    try {
      Class cls = Class.forName(className);
      return invokeStaticMethod(cls, methodName, types, arguments); } catch (Exception e) {
    }
    return null;
  }

  public static Object invokeStaticMethod(Class<?> cls, String methodName, Class<?>[] types, Object[] arguments) {
    try {
      Method method = cls.getMethod(methodName, types);
      return method.invoke(null, arguments); } catch (Exception e) {
    }
    return null;
  }

  public static void main(String[] args)
  {
    invokeMethod(TestClass.class, "test", new Object[] { "111111" });
  }

  static class TestClass {
    public void test(String params) {
      System.out.println(params);
    }
  }
}