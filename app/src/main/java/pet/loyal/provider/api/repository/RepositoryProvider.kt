package pet.loyal.provider.api.repository

object RepositoryProvider {

    fun provideRepository(): ProviderRepository {
        return ProviderRepositoryImpl()
    }
}