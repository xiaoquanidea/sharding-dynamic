package com.github.hutiquan.sharding.core.annotation

import com.github.hutiquan.sharding.api.Sharding
import org.springframework.beans.factory.*
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.annotation.MergedAnnotation
import org.springframework.core.annotation.MergedAnnotations
import org.springframework.util.Assert
import org.springframework.util.StringUtils
import java.lang.reflect.AccessibleObject
import kotlin.streams.toList

open class ShardingAnnotationContainer(
    private val injectors: ObjectProvider<ShardingAnnotationInjector>
) : BeanFactoryAware, InitializingBean, BeanClassLoaderAware {

   private lateinit var beanFactory: ConfigurableListableBeanFactory
   private lateinit var classLoader: ClassLoader

    var shardingKeyParameterName = "value"
    val shardingAnnotationTypes: MutableSet<Class<out Annotation>> = mutableSetOf()

    init {
        shardingAnnotationTypes.add(Sharding::class.java)
    }

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
        val injectors = injectors.stream()
            .flatMap { it.inject().stream() }
            .filter { StringUtils.hasLength(it) }
            .toList()
        injectors.forEach { addShardingAnnotation(it) }
    }
}

/*
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
}*/
