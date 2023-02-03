package com.itmo.eva.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EnumUtils {
    /**
     * 枚举转 Map 结合code作为map的key，name作为map的value
     * @param enumT
     * @param methodNames
     * @return
     */
    public static <T> Map<Object,String> EnumToMap(Class<T> enumT, String... methodNames){
        Map<Object,String> enumMap = new HashMap<>();
        if(!enumT.isEnum()){
            return enumMap;
        }
        //获取枚举的所有属性
        T[] enums = enumT.getEnumConstants();
        if (enums == null || enums.length <= 0){
            return enumMap;
        }
        int count = methodNames.length;
        //默认接口code方法
        String codeMethod = "getCode";
        //默认接口name方法
        String nameMethod = "getName";
        if (count > 0 && !"".equals(methodNames[0])){
            codeMethod = methodNames[0];
        }
        if (count == 2 && !"".equals(methodNames[1])){
            nameMethod = methodNames[1];
        }
        for (T t:enums) {
            try {
                //获取code的值
                Object resultCode = getMethodValue(codeMethod,t);
                if ("".equals(resultCode)){
                    continue;
                }
                //获取name的值
                Object resultName= getMethodValue(nameMethod,t);
                if ("".equals(resultName)){
                    resultName = t;
                }
                enumMap.put(resultCode,resultName + "");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return enumMap;
    }
 
    /**
     * 根据反射，通过方法名获取方法值（忽略大小写）
     * @param methodName
     * @param t
     * @param args
     * @param <T>
     * @return
     */
    private static <T> Object getMethodValue(String methodName,T t,Object... args){
        Object result = "";
        try {
            //获取方法组，共有方法（public）
            Method[] methods = t.getClass().getMethods();
            if (methods.length <= 0){
                return result;
            }
            Method method = null;
            for (Method met : methods){
                //忽略大小写 取方法
                if (met.getName().equalsIgnoreCase(methodName)){
                    //如果存在，取出正确的方法名称
                    methodName = met.getName();
                    method = met;
                    break;
                }             }
            if (method == null){
                return result;
            }
            //方法执行
            result = method.invoke(t,args);
            //返回结果
            return result == null ? "" : result;
 
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}