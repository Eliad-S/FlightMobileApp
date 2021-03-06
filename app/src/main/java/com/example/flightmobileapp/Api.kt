import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Api {
    @GET("/api/Screenshot")
    fun getImg(): Call<ResponseBody>
    @POST("/api/Commands")
    fun post(@Body rb: RequestBody) : Call<ResponseBody>
}