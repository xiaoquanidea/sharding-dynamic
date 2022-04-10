/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-06 6:39 PM
 */
package com.github.hutiquan.sharding.core.aspectj

import org.aopalliance.intercept.MethodInvocation
import java.lang.reflect.Field
import java.lang.reflect.Proxy

object ShardingClassResolver {
    private var mpEnabled = false

    //mapperInterfaceField中存放的是mybatis mapper接口的Class对象
    private var mapperInterfaceField: Field? = null

    /**
     * {@link org.apache.ibatis.binding.MapperProxy}
     */
    init {
        var proxyClass: Class<*>? = null
        try {
            proxyClass = Class.forName("com.baomidou.mybatisplus.core.override.MybatisMapperProxy")
        } catch (e1: ClassNotFoundException) {
            try {
                proxyClass = Class.forName("com.baomidou.mybatisplus.core.override.PageMapperProxy")
            } catch (e2: ClassNotFoundException) {
                try {
                    proxyClass = Class.forName("org.apache.ibatis.binding.MapperProxy")
                } catch (e3: ClassNotFoundException) {
                }
            }
        }
        if (proxyClass != null) {
            try {
                mapperInterfaceField = proxyClass.getDeclaredField("mapperInterface")
                mapperInterfaceField?.isAccessible = true
                mpEnabled = true
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IllegalAccessException::class)
    fun targetClass(invocation: MethodInvocation): Class<*> {
        //maEnabled 只有获取到mapperInterface属性字段的时候,才为true
        if (mpEnabled) {
            /**
             * 获取原对象 如果该对象是mybatisMapper jdk动态代理对象,获取该对象的 mapperInterface字段(保存了Mapper的Class对象),
             * Proxy.getInvocationHandler(target) 获取的是MapperProxy代理对象的执行处理器
             * 所以下方(Class) mapperInterfaceField.get(Proxy.getInvocationHandler(target)) 强转为class
             */
            val target = invocation.getThis() //代理的目标对象(原对象)
            val targetClass: Class<*> = target!!.javaClass
            return if (Proxy.isProxyClass(targetClass)) mapperInterfaceField!![Proxy.getInvocationHandler(
                target
            )] as Class<*> else targetClass
        }
        return invocation.method.declaringClass
    }
}