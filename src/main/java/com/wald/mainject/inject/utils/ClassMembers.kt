package com.wald.mainject.inject.utils

import kotlin.reflect.KCallable
import kotlin.reflect.KVisibility


val KCallable<*>.isPublic
    get() = visibility == KVisibility.PUBLIC