package com.github.hutiquan.sharding.core.annotation

import com.github.hutiquan.sharding.api.Database
import com.github.hutiquan.sharding.api.Sharding
import com.github.hutiquan.sharding.api.Source
import org.springframework.beans.factory.BeanClassLoaderAware
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.BeanFactoryUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.annotation.MergedAnnotation
import org.springframework.core.annotation.MergedAnnotations
import org.springframework.util.Assert
import java.lang.reflect.AccessibleObject
import java.lang.reflect.AnnotatedElement

class ShardingAnnotationContainer: BeanFactoryAware, InitializingBean, BeanClassLoaderAware {

   private lateinit var beanFactory: ConfigurableListableBeanFactory
   private lateinit var classLoader: ClassLoader

    var shardingKeyParameterName = "value"
    val shardingAnnotationTypes: MutableSet<Class<out Annotation>> = mutableSetOf()

    fun addShardingAnnotation(annoClassFullName: String) {
        Assert.notNull(annoClassFullName, "sharding annotation class 全限定路径不能为空")
        val shardingAnno = Class.forName(annoClassFullName)
        Assert.isTrue(shardingAnno.isAnnotation, "sharding annotation class 必须是注解类型")

        shardingAnnotationTypes.add(shardingAnno as Class<out Annotation>)
    }

    fun addShardingAnnotation(anno: Annotation) {
        Assert.notNull(anno, "sharding annotation type 不能为空")
        shardingAnnotationTypes.add(anno.javaClass)
    }


    fun findShardingAnnotation(ao: AccessibleObject): MergedAnnotation<out Annotation>? {
        val annotations: MergedAnnotations = MergedAnnotations.from(ao)

        for (type in shardingAnnotationTypes) {
            val annotation: MergedAnnotation<out Annotation> = annotations.get(type)
            if (annotation.isPresent) {
                return annotation
            }
        }
        return null
    }


    fun getShardingAnnoValue(ann: MergedAnnotation<*>): String? {
        val annotationAttributes = ann.asMap({ mergeAnnotation: MergedAnnotation<*> ->
            AnnotationAttributes(mergeAnnotation.type)
        })

        return getShardingAnnoValue(annotationAttributes)
    }

    fun getShardingAnnoValue(annotationAttributes: AnnotationAttributes): String? {
        return annotationAttributes[this.shardingKeyParameterName] as? String
    }

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory as ConfigurableListableBeanFactory
    }

    override fun setBeanClassLoader(classLoader: ClassLoader) {
        this.classLoader = classLoader
    }

    override fun afterPropertiesSet() {
        val beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
            beanFactory,
            IShardingAnnotation::class.java
        )
        beans.values.forEach {
            addShardingAnnotation(it.provideAnno())
        }
    }
}

//@Sharding("test")
//@Source("test")
//@Database("test")
class A {

//    @Sharding("Sharding")
    @Source("Source")
    @Database("Database")
    fun printA() {
        println("plint AAA")
    }

}

fun main() {
    val aClass = A::class
    val annotations = aClass.annotations

    val get = annotations.get(0)
    val from = MergedAnnotation.from(get)

    MergedAnnotation.of(annotations.get(0).javaClass)
    println("from = ${from}")
}