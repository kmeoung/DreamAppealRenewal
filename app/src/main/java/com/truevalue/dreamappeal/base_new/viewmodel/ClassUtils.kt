package com.example.stackoverflowuser.base.viewmodel

object ClassUtils {

    /**
     * Get annotation of object
     *
     * @param obj             Object has annotation
     * @param annotationClass annotation to get instance
     * @return Annotation of object
     */
    fun <A : Annotation> getAnnotation(obj: Any, annotationClass: Class<A>): A? =
        obj.javaClass.getAnnotation(annotationClass)
}