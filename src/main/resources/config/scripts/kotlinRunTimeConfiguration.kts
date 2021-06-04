import com.wald.mainject.inject.BeanDefinitionRegistry
import com.wald.mainject.draft.CombinedModuleConfiguration
import com.wald.mainject.module.Module
import com.wald.mainject.module.ModulesSource
import com.wald.mainject.config.property.PropertySource
import javax.transaction.Transactional

@Transactional
abstract class TheTimedModule : Module {

}

object : CombinedModuleConfiguration() {
    override val registeredBeans: BeanDefinitionRegistry
        get() = TODO("Not yet implemented")
    override val importedModules: Set<Module>
        get() = TODO("Not yet implemented")
    override val configurationSources: Set<ModulesSource>
        get() = TODO("Not yet implemented")
    override val propertySources: Set<PropertySource>
        get() = TODO("Not yet implemented")
}