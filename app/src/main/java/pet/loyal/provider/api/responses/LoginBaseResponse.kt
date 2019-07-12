package pet.loyal.provider.api.responses

class LoginBaseResponse {
    lateinit var loginResponse: LoginResponse
    var throwable: Throwable? = null
}