import com.wald.mainject.config.dsl.module
import com.wald.mainject.inject.definition.ImplClassBeanDefinition
import com.wald.mainject.inject.definition.InstanceBeanDefinition
import com.wald.mainject.integrate.HttpClientArchetype
import com.wald.mainject.integrate.HttpDocumentService
import com.wald.mainject.integrate.JPADocumentRepository
import com.wald.mainject.integrate.PersistentDocumentService
import com.wald.mainject.integrate.http.HttpClientBuilderProvider
import com.wald.mainject.integrate.http.HttpCommons
import com.wald.mainject.module.BaseModule
import com.wald.mainject.module.ModuleBuilder
import javax.ejb.TransactionAttribute
import javax.persistence.PersistenceContext

class CommonDatasources

module("documents.service.persistent") {
    beans {
        singleton<PersistentDocumentService>()
    }
}

module("commons.http") {
    beans {
        provider<HttpClientBuilderProvider> {
            name("commons.http.client.achetype")
            qualified<HttpClientArchetype>()
        }
        definitions<HttpCommons>()
    }
}

ModuleBuilder("gweg")
    .component(InstanceBeanDefinition(HttpDocumentService()))
    .component(ImplClassBeanDefinition(HttpDocumentService::class))
    .import(BaseModule()) {
        include()
        excludeThat<Comparable<*>> {
            qualifiedWith<PersistenceContext>()
            qualifiedWith<TransactionAttribute>()
        }
    }



module("docs.jpa") {
    beans {
        bean<JPADocumentRepository>()
    }
//    includes {
//        import("jpa.commons")
//    }
}

module("docs.http") {
    beans {
        singleton<HttpDocumentService> {
            name("docs.http.service")
            qualified<HttpClientArchetype>()
        }
    }
//    imports {
//        import("commons.http")
//    }
//    propertg {
//        plain("http.connectionTimeout.default" to 20)
//        classPath("/config/proxy.properies", optional = true)
//    }
}