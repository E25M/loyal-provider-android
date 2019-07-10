package pet.loyal.provider.api.repository

object RepositoryProvider {

    fun provideProviderRepository(): ProviderRepository {
        return ProviderRepositoryImpl()
    }
}