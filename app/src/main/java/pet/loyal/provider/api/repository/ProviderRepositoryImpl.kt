package pet.loyal.provider.api.repository

import pet.loyal.provider.BuildConfig
import pet.loyal.provider.api.service.ProviderAPIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProviderRepositoryImpl: ProviderRepository {

    var apiService: ProviderAPIService

    var retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.BASE_URL)
        .build()

    init {
        apiService = retrofit.create(ProviderAPIService::class.java)
    }
}