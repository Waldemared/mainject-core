package com.wald.mainject.config.property

import com.wald.mainject.config.property.source.ResolvableProperties
import com.wald.mainject.config.property.source.ResolvableProperties.ResolvablePropertiesBuilder


fun PropertySource.asResolvable(constructor: ResolvablePropertiesBuilder<*>.() -> Any): ResolvableProperties<*> {
    return ResolvablePropertiesBuilder(this)
        .apply { constructor() }
        .build()
}